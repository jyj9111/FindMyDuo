package com.idle.fmd.domain.user.repo;

import com.idle.fmd.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Boolean existsByAccountId(String accountId);
}
