package org.github.clockworm.dynamic.datasource.util;

import mybatis.mate.sharding.ShardingKey;

public class SiteDataSourceUtil {

    public static void change(String site) {
        ShardingKey.change(site);
    }

}

