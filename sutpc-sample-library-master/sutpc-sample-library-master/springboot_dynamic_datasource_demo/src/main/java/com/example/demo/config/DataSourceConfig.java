package com.example.demo.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 08:13$ 20200424$.
 * @Description
 * @Modified By:
 */
@Configuration
public class DataSourceConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.default-datasource")
  public DataSource defaultDataSource() {
    return DruidDataSourceBuilder.create().build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.target-datasources.datasource1")
  public DataSource dataSource1() {
    return DruidDataSourceBuilder.create().build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.target-datasources.datasource2")
  public DataSource dataSource2() {
    return DruidDataSourceBuilder.create().build();
  }

  @Bean
  @Primary
  public DataSource dynamicDataSource(DataSource defaultDataSource, DataSource dataSource1,
      DataSource dataSource2) {
    // 注意：该方法的参数名称要和前面前面三个datasource对象在Spring容器中的bean名称一样
    // 或者使用 @Qualifier 指定具体的bean
    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put(DataSourceType.DEFAULT_DATASOURCE.name(), defaultDataSource);
    targetDataSources.put(DataSourceType.DATASOURCE1.name(), dataSource1);
    targetDataSources.put(DataSourceType.DATASOURCE2.name(), dataSource2);
    return new DynamicDataSource(defaultDataSource, targetDataSources);
  }
}
