package com.example.demo.config;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 08:10$ 20200424$.
 * @Description
 * @Modified By:
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

  public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
    super.setDefaultTargetDataSource(defaultTargetDataSource);
    super.setTargetDataSources(targetDataSources);
    super.afterPropertiesSet();
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return DataSourceContextHolder.getDataSourceType();
  }
}
