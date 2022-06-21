package org.github.clockworm.dynamic.datasource.init;

import lombok.extern.slf4j.Slf4j;
import mybatis.mate.config.DataSourceProperty;
import mybatis.mate.config.HikariConfig;
import mybatis.mate.config.ShardingProperties;
import mybatis.mate.provider.IDataSourceProvider;
import mybatis.mate.sharding.ShardingDatasource;
import mybatis.mate.strategy.IShardingStrategy;
import org.github.clockworm.dynamic.datasource.entity.Site;
import org.github.clockworm.dynamic.datasource.properties.DataSourceProperties;
import org.github.clockworm.dynamic.datasource.service.SiteServiceSpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Configuration
public class DataSourceLoadProcess {

    @Autowired
    private DataSourceProperties properties;

    @Autowired(required = false)
    private IShardingStrategy shardingStrategy;

    @Autowired(required = true)
    SiteServiceSpi siteServiceSpi;
    @Autowired
    private IDataSourceProvider dataSourceProvider;

    @Primary
    @Bean(name = "dataSource")
    public ShardingDatasource shardingDatasource() {
        if (properties.getHikari() == null) {
            HikariConfig var3 = new HikariConfig();
            var3.setConnectionTimeout(60000L);
            var3.setConnectionTestQuery("select 1");
            var3.setIdleTimeout(60000L);
            var3.setValidationTimeout(3000L);
            var3.setIdleTimeout(300000L);
            var3.setMaxLifetime(600000L);
            var3.setMaximumPoolSize(200);
            var3.setMinimumIdle(10);
            properties.setHikari(var3);
        }
        ShardingProperties shardingProperties = new ShardingProperties();
        shardingProperties.setPrimary(properties.getPrimary());
        List<DataSourceProperty> dataSourceProperties = new ArrayList<>();
        DataSourceProperty primary = new DataSourceProperty();
        log.info("loading site dataSource :{}", properties.getDefaultSite());
        primary.setKey(properties.getDefaultSite());
        primary.setDriverClassName(properties.getDriverClass());
        primary.setUrl(String.format(properties.getJdbcUrl(), properties.getPrimary().concat(properties.getDefaultSiteCode())));
        primary.setUsername(properties.getUsername());
        primary.setPassword(properties.getPassword());
        primary.setHikari(properties.getHikari());
        dataSourceProperties.add(primary);

        List<Site> sites = siteServiceSpi.setSites();
        for (Site site : sites) {
            String jdbcUrl = properties.getJdbcUrl();
            DataSourceProperty odmDataSourceProperty = new DataSourceProperty();
            odmDataSourceProperty.setKey(site.getCode());
            odmDataSourceProperty.setDriverClassName(properties.getDriverClass());
            String odmJdbcUrl = String.format(properties.getJdbcUrl(), properties.getPrimary().concat(site.getCode()));
            odmDataSourceProperty.setUrl(odmJdbcUrl);
            odmDataSourceProperty.setUsername(properties.getUsername());
            odmDataSourceProperty.setPassword(properties.getPassword());
            odmDataSourceProperty.setTestQuery("select 1");
            odmDataSourceProperty.setHikari(properties.getHikari());
            dataSourceProperties.add(odmDataSourceProperty);
            log.info("loading site dataSource :{}", site.getName());
        }

        shardingProperties.setDatasource(new HashMap<String, List<DataSourceProperty>>(16) {{
            put(properties.getPrimary(), dataSourceProperties);
        }});
        Map<String, DataSource> dataSources = new HashMap<>(32);
        shardingProperties.getDatasource().forEach((k, v) -> v.forEach(d -> {
            try {
                String datasourceKey = k + d.getKey();
                dataSources.put(datasourceKey, dataSourceProvider.createDataSource(k, d));
            } catch (SQLException e) {
                log.error("init datasource error: ", e);
            }
        }));
        if (null != shardingStrategy) shardingProperties.setShardingStrategy(shardingStrategy);
        return new ShardingDatasource(dataSourceProvider, dataSources, shardingProperties);
    }
}
