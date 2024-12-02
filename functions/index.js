const functions = require('firebase-functions');
const admin = require('firebase-admin');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
require('dotenv').config();

const JWT_SECRET = process.env.JWT_SECRET;

if (!JWT_SECRET) {
    throw new Error('JWT_SECRET 환경 변수가 설정되지 않았습니다.');
}

admin.initializeApp();
const db = admin.firestore();
const cors = require('cors')({ origin: true });

// 저속 노화 점수 계산 함수
const calculateAgingScore = (nutrients) => {
    let score = 0;
    if (nutrients.vitaminC >= 50) score += 10;
    if (nutrients.protein >= 20) score += 20;
    if (nutrients.totalDietaryFiber >= 10) score += 15;
    if (nutrients.energy <= 500) score += 5;
    // 점수 범위를 0에서 100 사이로 제한
    return Math.min(Math.max(score, 0), 100);
};

// 끼니별 영양 성분 합산 함수
const calculateDailyNutrients = (mealData) => {
    const dailyNutrients = { vitaminC: 0, protein: 0, totalDietaryFiber: 0, energy: 0 };
    for (const mealType in mealData) {
        if (mealData[mealType]?.foods) {
            mealData[mealType].foods.forEach((food) => {
                dailyNutrients.vitaminC += food.vitaminC || 0;
                dailyNutrients.protein += food.protein || 0;
                dailyNutrients.totalDietaryFiber += food.totalDietaryFiber || 0;
                dailyNutrients.energy += food.energy || 0;
            });
        }
    }
    return dailyNutrients;
};

// JWT 토큰 검증 함수
const verifyToken = (req) => {
    const authHeader = req.headers.authorization;
    if (!authHeader) throw new Error('토큰이 제공되지 않았습니다.');
    const token = authHeader.startsWith('Bearer ') ? authHeader.split('Bearer ')[1] : authHeader;

    try {
        return jwt.verify(token, JWT_SECRET);
    } catch (error) {
        if (error.name === 'TokenExpiredError') {
            throw new Error('토큰이 만료되었습니다.');
        }
        throw new Error('유효하지 않은 토큰입니다.');
    }
};

// 사용자 회원가입
exports.signup = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const { email, password, name } = req.body;

            const existingUser = await db.collection('users').where('email', '==', email).get();
            if (!existingUser.empty) {
                return res.status(400).send({ message: '이미 존재하는 이메일입니다.' });
            }

            const hashedPassword = await bcrypt.hash(password, 10);
            const userRef = await db.collection('users').add({
                email,
                password: hashedPassword,
                name,
                createdAt: admin.firestore.FieldValue.serverTimestamp(),
            });

            res.status(201).send({ message: '회원가입 성공', userId: userRef.id });
        } catch (error) {
            res.status(500).send({ message: '회원가입 실패', error: error.message });
        }
    });
});

// 사용자 로그인
exports.login = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const { email, password } = req.body;

            const userSnapshot = await db.collection('users').where('email', '==', email).get();
            if (userSnapshot.empty) {
                return res.status(404).send({ message: '사용자를 찾을 수 없습니다.' });
            }

            const user = userSnapshot.docs[0].data();
            const isPasswordMatch = await bcrypt.compare(password, user.password);
            if (!isPasswordMatch) {
                return res.status(401).send({ message: '비밀번호가 일치하지 않습니다.' });
            }

            const token = jwt.sign({ userId: userSnapshot.docs[0].id }, JWT_SECRET, { expiresIn: '1h' });
            res.status(200).send({ message: '로그인 성공', token });
        } catch (error) {
            res.status(500).send({ message: '로그인 실패', error: error.message });
        }
    });
});

// 사용자 정보 조회
exports.getUserInfo = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const decoded = verifyToken(req);
            const userId = decoded.userId;

            const userDoc = await db.collection('users').doc(userId).get();
            if (!userDoc.exists) {
                return res.status(404).send({ message: '사용자를 찾을 수 없습니다.' });
            }

            res.status(200).send({ user: userDoc.data() });
        } catch (error) {
            console.error('회원 정보 조회 오류:', error);
            res.status(500).send({ message: '회원 정보 조회 실패', error });
        }
    });
});

// 사용자 정보 수정
exports.updateUserInfo = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const decoded = verifyToken(req);
            const userId = decoded.userId;

            const { name, email } = req.body;

            if (email) {
                const userSnapshot = await db.collection('users')
                    .where('email', '==', email)
                    .where(admin.firestore.FieldPath.documentId(), '!=', userId)
                    .limit(1)
                    .get();
                if (!userSnapshot.empty) {
                    return res.status(400).send({ message: '이미 사용 중인 이메일입니다.' });
                }
            }

            await db.collection('users').doc(userId).update({
                ...(name && { name }),
                ...(email && { email }),
            });

            res.status(200).send({ message: '회원 정보가 성공적으로 업데이트되었습니다.' });
        } catch (error) {
            console.error('회원 정보 수정 오류:', error);
            res.status(500).send({ message: '회원 정보 수정 실패' });
        }
    });
});

// 사용자 삭제
exports.deleteUser = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const decoded = verifyToken(req);
            const userId = decoded.userId;

            // 사용자 문서 삭제
            await db.collection('users').doc(userId).delete();

            // 연관된 식사 기록 삭제
            const mealsRef = db.collection('users').doc(userId).collection('meals');
            const mealsSnapshot = await mealsRef.get();
            const deletePromises = mealsSnapshot.docs.map(doc => doc.ref.delete());
            await Promise.all(deletePromises);

            res.status(200).send({ message: '회원 및 연관 데이터가 성공적으로 삭제되었습니다.' });
        } catch (error) {
            console.error('회원 삭제 오류:', error);
            res.status(500).send({ message: '회원 삭제 실패' });
        }
    });
});

// 식사 기록 조회
exports.getMealsByDate = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const decoded = verifyToken(req);
            const userId = decoded.userId;
            const { date } = req.query; // 날짜 정보

            const mealDoc = await db.collection('users').doc(userId)
                .collection('meals').doc(date).get();

            if (!mealDoc.exists) {
                return res.status(404).send({ message: '해당 날짜의 식사 기록이 없습니다.' });
            }

            const meals = mealDoc.data();
            res.status(200).send({ message: '식사 기록 조회 성공', data: meals });
        } catch (error) {
            res.status(500).send({ message: '식사 기록 조회 실패', error });
        }
    });
});

// 식사 기록 수정
exports.updateMeal = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const decoded = verifyToken(req);
            const userId = decoded.userId;
            const { date, mealType, updatedData } = req.body;

            const mealRef = db.collection('users').doc(userId)
                .collection('meals').doc(date);

            await mealRef.set(
                {
                    [mealType]: {
                        ...updatedData,
                        updatedAt: admin.firestore.FieldValue.serverTimestamp()
                    }
                },
                { merge: true }
            );

            res.status(200).send({ message: '식사 기록 수정 성공' });
        } catch (error) {
            res.status(500).send({ message: '식사 기록 수정 실패', error });
        }
    });
});

// 식사 기록 삭제
exports.deleteMeal = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const decoded = verifyToken(req);
            const userId = decoded.userId;
            const { date, mealType } = req.body;

            const mealRef = db.collection('users').doc(userId).collection('meals').doc(date);

            await mealRef.update({
                [mealType]: admin.firestore.FieldValue.delete()
            });

            // 식사 기록이 변경되었으므로 일간 점수 재계산
            const mealDoc = await mealRef.get();
            if (mealDoc.exists) {
                const dailyNutrients = calculateDailyNutrients(mealDoc.data());
                const dailySlowAgingScore = calculateAgingScore(dailyNutrients);

                await mealRef.set(
                    {
                        dailySlowAgingScore,
                        dailyNutrients,
                        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
                    },
                    { merge: true }
                );
            } else {
                // 모든 끼니가 삭제된 경우 문서 삭제
                await mealRef.delete();
            }

            res.status(200).send({ message: '식사 기록 삭제 및 일간 점수 재계산 성공' });
        } catch (error) {
            console.error('식사 기록 삭제 실패:', error);
            res.status(500).send({ message: '식사 기록 삭제 실패' });
        }
    });
});

// 일간 저속 노화 점수 계산
exports.calculateDailyScore = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const decoded = verifyToken(req);
            const userId = decoded.userId;
            const { date } = req.body;

            const mealRef = db.collection('users').doc(userId).collection('meals').doc(date);
            const mealDoc = await mealRef.get();

            if (!mealDoc.exists) {
                return res.status(404).send({ message: '해당 날짜의 식사 기록이 없습니다.' });
            }

            const dailyNutrients = calculateDailyNutrients(mealDoc.data());
            const dailySlowAgingScore = calculateAgingScore(dailyNutrients);

            await mealRef.set(
                {
                    dailySlowAgingScore,
                    dailyNutrients,
                    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
                },
                { merge: true }
            );

            res.status(200).send({
                message: '일간 저속 노화 점수 계산 및 저장 완료',
                date,
                dailySlowAgingScore,
            });
        } catch (error) {
            console.error('일간 점수 계산 오류:', error);
            res.status(500).send({ message: '일간 점수 계산 실패' });
        }
    });
});

// 끼니 점수 계산만 수행 (저장 없음)
exports.calculateMealScore = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const { foods } = req.body;

            if (!foods || !Array.isArray(foods)) {
                return res.status(400).send({ message: '유효한 foods 배열이 필요합니다.' });
            }

            // 영양소 계산
            const totalNutrients = calculateDailyNutrients({ tempMeal: { foods } });
            const mealScore = calculateAgingScore(totalNutrients);

            res.status(200).send({
                message: '점수 계산 성공',
                mealScore,
                nutrients: totalNutrients,
            });
        } catch (error) {
            console.error('점수 계산 실패:', error);
            res.status(500).send({ message: '점수 계산 실패', error });
        }
    });
});

// 기존 saveMealAndCalculateDailyScore 함수 수정
exports.saveMealAndCalculateDailyScore = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
        try {
            const decoded = verifyToken(req);
            const userId = decoded.userId;
            const { date, mealType, foods } = req.body;

            if (!Array.isArray(foods)) {
                return res.status(400).send({ message: 'foods 배열이 필요합니다.' });
            }

            // 식사 데이터를 처리하면서 영양 정보를 합산
            const dailyNutrients = { vitaminC: 0, protein: 0, totalDietaryFiber: 0, energy: 0 };

            foods.forEach(food => {
                const nutrition = food.nutritionInfo || {};
                dailyNutrients.vitaminC += nutrition.vitaminC || 0;
                dailyNutrients.protein += nutrition.protein || 0;
                dailyNutrients.totalDietaryFiber += nutrition.totalDietaryFiber || 0;
                dailyNutrients.energy += nutrition.energy || 0;
            });

            // 저속 노화 점수 계산
            const mealScore = calculateAgingScore(dailyNutrients);

            // Firestore에 저장
            const mealRef = db.collection('users').doc(userId).collection('meals').doc(date);

            await mealRef.set(
                {
                    [mealType]: {
                        foods, // food 배열 저장
                        slowAgingScore: mealScore,
                        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
                    },
                },
                { merge: true }
            );

            // 식사 기록이 추가되었으므로 일간 점수 및 영양 성분 재계산
            const mealDoc = await mealRef.get();
            const allMealsData = mealDoc.data();

            const totalDailyNutrients = calculateDailyNutrients(allMealsData);
            const dailySlowAgingScore = calculateAgingScore(totalDailyNutrients);

            await mealRef.set(
                {
                    dailySlowAgingScore,
                    dailyNutrients: totalDailyNutrients,
                    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
                },
                { merge: true }
            );

            res.status(200).send({
                message: '식사 기록 및 일간 점수 저장 완료',
                mealType,
                mealScore,
                dailySlowAgingScore,
            });
        } catch (error) {
            console.error('식사 기록 저장 실패:', error);
            res.status(500).send({ message: '식사 기록 저장 실패', error });
        }
    });
});