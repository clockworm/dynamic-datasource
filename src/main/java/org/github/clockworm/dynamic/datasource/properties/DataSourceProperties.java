package org.github.clockworm.dynamic.datasource.properties;

import lombok.Data;
import mybatis.mate.config.HikariConfig;
import mybatis.mate.provider.HikariDataSourceProvider;
import mybatis.mate.provider.IDataSourceProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "org.github.clockworm.dynamic.datasource")
public class DataSourceProperties {
    private String primary = StringUtils.EMPTY;
    private String defaultSite = StringUtils.EMPTY;
    private String defaultSiteCode = StringUtils.EMPTY;
    private String type;
    private String driverClass;
    private String jdbcUrl;
    private String username;
    private String password;
    private HikariConfig hikari;


    @Bean
    public IDataSourceProvider dataSourceProvider() {
        return new HikariDataSourceProvider();
    }
}
