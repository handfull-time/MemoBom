package com.utime.memoBom.common.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.utime.memoBom.common.schemasync.MyBatisMapperDdlSource;
import com.utime.memoBom.common.schemasync.SchemaSyncProperties;
import com.utime.memoBom.common.schemasync.SchemaSynchronizer;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(SchemaSyncProperties.class)
public class SchemaSyncConfig {

    @Bean
    public ApplicationRunner schemaSyncRunner(SchemaSyncProperties props,
                                             javax.sql.DataSource dataSource) {
        return args -> {
            if (!props.isEnabled()) return;

            // 컬럼 체크는 나중에...
//            MyBatisMapperDdlSource source = new MyBatisMapperDdlSource();
//            var ddls = source.loadCreateTableDdls(props.getMapperLocations());
//
//            SchemaSynchronizer sync = new SchemaSynchronizer(dataSource, props);
//            sync.syncAll(ddls);
        };
    }
}
