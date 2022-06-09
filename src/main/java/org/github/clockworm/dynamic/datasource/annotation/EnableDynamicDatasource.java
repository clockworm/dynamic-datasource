package org.github.clockworm.dynamic.datasource.annotation;

import org.github.clockworm.dynamic.datasource.init.DataSourceLoadProcess;
import org.github.clockworm.dynamic.datasource.properties.DataSourceProperties;
import org.github.clockworm.dynamic.datasource.util.SpringBeanUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SpringBeanUtil.class, DataSourceProperties.class, DataSourceLoadProcess.class})
public @interface EnableDynamicDatasource {

}
