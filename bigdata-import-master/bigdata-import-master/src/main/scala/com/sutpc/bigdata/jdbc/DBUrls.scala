package com.sutpc.bigdata.jdbc

import java.util.Properties

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/8/13  17:56
  */
object DBUrls {

  val phone_db = "guangdong"
  val phone_url = s"jdbc:postgresql://10.10.2.62:5432/$phone_db"
  val phone_prop = new Properties()
  phone_prop.put("user", "test")
  phone_prop.put("password", "Sutpc518057")
  phone_prop.put("driver", "org.postgresql.Driver")

  val transpass_db = "stdbase"
  val transpass_std_url = s"jdbc:postgresql://10.10.201.28:5432/$transpass_db"
  val transpass_url = s"jdbc:postgresql://10.10.201.28:5432/postgres"
  val transpass_prop = new Properties()
  transpass_prop.put("user", "postgres")
  transpass_prop.put("password", "123456")
  transpass_prop.put("driver", "org.postgresql.Driver")

  //  select ROUTECODE,ROUTEID,ROUTENAME from route_bd;
  //  select PRODUCTID,CARDID from bus_bd;
  //  select RBUSRID,ROUTEID from busroute_bd;

  val bus_info_url = "jdbc:oracle:thin:@10.10.2.153:1521/GBusIndex"
  val bus_info_prop = new Properties()
  bus_info_prop.put("user", "bus")
  bus_info_prop.put("password", "bus123")
  bus_info_prop.put("driver", "oracle.jdbc.driver.OracleDriver")
}
