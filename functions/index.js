const functions = require('firebase-functions');
const admin = require('firebase-admin');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const JWT_SECRET = process.env.JWT_SECRET || 'J1Xad/oNW8hV7lODHKl+ufFZiK1wUFwa3pNqO1NgQps=';


admin.initializeApp();
const db = admin.firestore();

// // JWT 비밀 키 가져오기
// const JWT_SECRET = functions.config().jwt.secret;

// 저속 노화 점수 계산 함수
const calculateAgingScore = (nutrients) => {
    let score = 0;
    if (nutrients.vitaminC > 50) score += 10;
    if (nutrients.protein > 20) score += 20;
    if (nutrients.totalDietaryFiber > 10) score += 15;
    if (nutrients.energy < 500) score += 5; // 칼로리가 적으면 추가 점수
    return score;
};

const cors = require('cors')({ origin: true });

// 사용자 회원가입
exports.signup = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
    
        // const cors = require('cors');
        // app.use(cors({ origin: true }));  

        // API 테스트 오류 -> req.body가 제대로 전달되는지 확인 목적 로그
        console.log('Signup Request Body:', req.body);

        const { email, password, name } = req.body;

        try {
            const userSnapshot = await db.collection('users').where('email', '==', email).get();
            if (!userSnapshot.empty) {
                return res.status(400).send({ message: '이미 존재하는 이메일입니다.' });
            }

            const hashedPassword = await bcrypt.hash(password, 10);
            const userRef = await db.collection('users').add({
                email,
                password: hashedPassword,
                name,
                createdAt: admin.firestore.FieldValue.serverTimestamp()
            });

            res.status(201).send({ message: '회원가입 성공', userId: userRef.id });
        } catch (error) {
            console.error('회원가입 오류:', error);
            res.status(500).send({ message: '회원가입 실패', error });
        }
    });
});

// 사용자 로그인
exports.login = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {

        // const cors = require('cors');
        // app.use(cors({ origin: true }));

        // API 테스트 오류 -> req.body가 제대로 전달되는지 확인 목적 로그
        console.log('Login Request Body:', req.body);
        console.log('JWT_SECRET:', JWT_SECRET); // 추가된 부분

        const { email, password } = req.body;

        try {
            const userSnapshot = await db.collection('users').where('email', '==', email).get();
            if (userSnapshot.empty) {
                return res.status(404).send({ message: '사용자를 찾을 수 없습니다.' });
            }

            const user = userSnapshot.docs[0].data();

            // 비밀번호 비교 전 로그 추가
            console.log('입력된 비밀번호:', password);
            console.log('DB에 저장된 암호화 비밀번호:', user.password);
            
            const isMatch = await bcrypt.compare(password, user.password);
            if (!isMatch) {
                return res.status(401).send({ message: '비밀번호가 일치하지 않습니다.' });
            }

            const token = jwt.sign({ userId: userSnapshot.docs[0].id }, JWT_SECRET, { expiresIn: '1h' });
            await db.collection('sessions').add({
                token,
                userId: userSnapshot.docs[0].id,
                createdAt: admin.firestore.FieldValue.serverTimestamp()
            });

            res.status(200).send({ message: '로그인 성공', token });
        } catch (error) {
            // 에러 메시지와 스택 정보를 클라이언트에 전달
            res.status(500).send({
                message: '로그인 실패',
                error: {
                    message: error.message || 'Unknown error occurred',
                    stack: error.stack, // 디버깅용 스택 정보 포함
                },
            });
        }
    });
});

// 저속 노화 점수 저장 및 계산
exports.saveMealAndCalculateScore = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {
    
        // const cors = require('cors');
        // app.use(cors({ origin: true }));

        const { token, date, mealType, foods } = req.body;

        try {
            const decoded = jwt.verify(token, JWT_SECRET);
            const userId = decoded.userId;

            // 영양소 데이터 처리
            let totalNutrients = {
                vitaminC: 0,
                protein: 0,
                totalDietaryFiber: 0,
                energy: 0
            };

            foods.forEach(food => {
                totalNutrients.vitaminC += food.vitaminC || 0;
                totalNutrients.protein += food.protein || 0;
                totalNutrients.totalDietaryFiber += food.totalDietaryFiber || 0;
                totalNutrients.energy += food.energy || 0;
            });

            const score = calculateAgingScore(totalNutrients);

            // Firebase에 저장
            const mealRef = await db.collection('users').doc(userId).collection('meals').add({
                date,
                mealType,
                foods,
                slowAgingScore: score,
                createdAt: admin.firestore.FieldValue.serverTimestamp()
            });

            res.status(200).send({ message: '식사 기록 및 점수 저장 완료', mealId: mealRef.id, slowAgingScore: score });
        } catch (error) {
            // 에러 메시지와 스택 정보를 클라이언트에 전달
            res.status(500).send({
                message: '점수 저장 실패',
                error: {
                    message: error.message || 'Unknown error occurred',
                    stack: error.stack, // 디버깅용 스택 정보 포함
                },
            });
        }
    });
});

// 일간 저속 노화 점수 계산
exports.calculateDailyScore = functions.https.onRequest(async (req, res) => {
    cors(req, res, async () => {

        // const cors = require('cors');
        // app.use(cors({ origin: true }));
    
        const { token, date } = req.body;

        try {
            const decoded = jwt.verify(token, JWT_SECRET);
            const userId = decoded.userId;

            const mealSnapshots = await db.collection('users').doc(userId).collection('meals')
                .where('date', '==', date).get();

            if (mealSnapshots.empty) {
                return res.status(404).send({ message: '해당 날짜의 식사 기록이 없습니다.' });
            }

            let dailyScore = 0;
            mealSnapshots.forEach(doc => dailyScore += doc.data().slowAgingScore);

            await db.collection('users').doc(userId).collection('dailyScores').doc(date).set({
                date,
                dailyScore
            });

            res.status(200).send({ message: '일간 저속 노화 점수 계산 및 저장 완료', dailyScore });
        } catch (error) {
            // 에러 메시지와 스택 정보를 클라이언트에 전달
            res.status(500).send({
              message: '일간 점수 계산 실패',
                error: {
                    message: error.message || 'Unknown error occurred',
                    stack: error.stack, // 디버깅용 스택 정보 포함
                },
            });
        }  
    });
});
