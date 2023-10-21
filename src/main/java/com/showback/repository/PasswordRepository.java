package com.showback.repository;

import com.showback.model.Password;
import com.showback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {
    Password findByUser_UserId(Long userId);
}
