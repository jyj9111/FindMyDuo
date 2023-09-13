package com.idle.fmd.domain.user.repo;

import com.idle.fmd.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByAccountId(String accountId);
    Boolean existsByAccountId(String accountId);
    Boolean existsByEmail(String email);
    UserEntity findByNickname(String nickname);
    Boolean existsByNickname(String nickname);
}
