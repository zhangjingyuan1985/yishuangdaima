package com.sutpc.data.send.bus.gps.tencent.util;

import com.sutpc.framework.utils.system.PropertyUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * .
 *
 * @Author:ShangxiuWu
 * @Description
 * @Date: Created in 10:17 2019/4/2 0002.
 * @Modified By:
 */
@Slf4j
public class DatabaseUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);

  private static final String DRIVER = PropertyUtils.getProperty("datasource.driver");
  private static final String URL = PropertyUtils.getProperty("datasource.url");
  private static final String USERNAME = PropertyUtils.getProperty("datasource.username");
  private static final String PASSWORD = PropertyUtils.getProperty("datasource.password");

  private static final String SQL = "SELECT * FROM ";// 数据库操作

  static {
    try {
      Class.forName(DRIVER);
    } catch (ClassNotFoundException e) {
      LOGGER.error("can not load jdbc driver", e);
    }
  }

  /**
   * .
   * 获取数据库连接
   */
  public static Connection getConnection() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    } catch (SQLException e) {
      LOGGER.error("get connection failure", e);
    }
    return conn;
  }

  /**
   * .
   * 关闭数据库连接
   */
  public static void closeConnection(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        LOGGER.error("close connection failure", e);
      }
    }
  }

  /**
   * .
   * 获取数据库下的所有表名
   */
  public static List<String> getTableNames() {
    List<String> tableNames = new ArrayList<>();
    Connection conn = getConnection();
    ResultSet rs = null;
    try {
      //获取数据库的元数据
      DatabaseMetaData db = conn.getMetaData();
      //从元数据中获取到所有的表名
      rs = db.getTables(null, null, null, new String[]{"TABLE"});
      while (rs.next()) {
        tableNames.add(rs.getString(3));
      }
    } catch (SQLException e) {
      LOGGER.error("getTableNames failure", e);
    } finally {
      try {
        rs.close();
        closeConnection(conn);
      } catch (SQLException e) {
        LOGGER.error("close ResultSet failure", e);
      }
    }
    return tableNames;
  }

  /**
   * .
   * 获取表中所有字段名称
   *
   * @param tableName 表名
   */
  public static List<Map<String, String>> getColumnNames(String tableName) {
    List<Map<String, String>> columnNames = new ArrayList<>();
    //与数据库的连接
    Connection conn = getConnection();
    DatabaseMetaData metaData = null;
    try {
      metaData = conn.getMetaData();
      ResultSet columns = metaData.getColumns(null, "%", tableName, "%");
      //结果集元数据
      while (columns.next()) {
        Map<String, String> map = new HashMap<>();
        String columnName = columns.getString("COLUMN_NAME");
        String typeName = columns.getString("TYPE_NAME");
        map.put(columnName, typeName);//列字段类型
        columnNames.add(map);
        System.out.println(columnName + "," + typeName);
      }
    } catch (SQLException e) {
      LOGGER.error("getColumnNames failure", e);
    } finally {

      closeConnection(conn);

    }
    return columnNames;
  }

  /**
   *  .
   */
  public static void query(String sql, Consumer<ResultSet> consumer) {
    log.info("-- start -- ");
    Connection conn = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    try {
      conn = DatabaseUtil.getConnection();
      st = conn.prepareStatement(sql);

      rs = st.executeQuery();
      while (rs.next()) {
        consumer.accept(rs);
      }
      log.info("-- end -- ");

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      DatabaseUtil.closeConnection(conn);
    }
  }

  /**
   * .
   *
   * @param args 参数
   */
  public static void main(String[] args) {
    List<String> tableNames = getTableNames();
    int i = 1;
    for (String tableName : tableNames) {
      System.out.println(i + "," + tableName);
      i++;
      getColumnNames(tableName);
    }
  }
}