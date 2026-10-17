package com.devguna.curriculum.finalproject.common;

import java.util.Properties;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DatabaseIdConfig {

    /**
     * 접속한 DB 제품 이름을 _databaseId 값으로 바꿔 준다. XML에서 DB마다 다른 JSON 조건을 고르는 데 쓴다.
     * 이 설정이 없으면 _databaseId는 항상 null이다.
     */
    @Bean
    DatabaseIdProvider databaseIdProvider() {
        Properties vendors = new Properties();
        vendors.setProperty("H2", "h2");
        vendors.setProperty("Oracle", "oracle");
        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider();
        provider.setProperties(vendors);
        return provider;
    }
}
