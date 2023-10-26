package com.showback.repository;

import com.showback.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    @Query("SELECT ua FROM UserAuth ua WHERE ua.user.userId = :userId")
    UserAuth findByUserId(@Param("userId") Long userId);

}
