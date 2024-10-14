package com.example.zeroCalorie.repository;

import com.example.zeroCalorie.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public List<User> findAll();
    public Optional<User> findByUserEmail(String email);
    public Optional<User> findById(Long id);
    public User save(User user);
}
