package com.example.zeroCalorie.repositoryTest;

import com.example.zeroCalorie.entity.User;
import com.example.zeroCalorie.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userEmail("test@example.com")
                .password("password")
                .nickname("TestUser")
                .build();
        userRepository.save(user);
    }

    @Test
    void findByUserEmail_shouldReturnUser() {
        Optional<User> foundUser = userRepository.findByUserEmail("test@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getNickname()).isEqualTo("TestUser");
    }
}
