package com.idle.fmd.global.config.etc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionManagerConfig {
    @Bean
    public PlatformTransactionManager transactionManager() {
        // JPA 트랙잭션 매니저 사용
        return new JpaTransactionManager();
    }
}
