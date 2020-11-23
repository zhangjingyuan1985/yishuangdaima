package com.sutpc.bigdata.jdbc

import java.sql.{Connection, DriverManager, ResultSet, Statement}

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/8/21  11:55
  */
class JdbcHelper(driver: String, url: String, username: String, password: String, db: String) {

  lazy val statement: Statement = conn().createStatement

  def conn(): Connection = {
    println(this.driver)
    Class.forName(this.driver)
    val connection = DriverManager.getConnection(this.url, this.username, this.password)
    connection
  }

  def truncate(table: String): Unit = {
    val resDelete = statement.executeUpdate(s"DELETE FROM $table ")
    println("清空表数据，返回值=>" + resDelete)
  }


  def delete(): Unit = {
    val resDelete = statement.executeUpdate("DELETE FROM `scala_jdbc_test` WHERE `id` = 2")
    println("删除数据，返回值=>" + resDelete)
  }

  def update(): Unit = {
    val resUpdate = statement.executeUpdate("UPDATE `scala_jdbc_test` SET `name` = '张三'WHERE `id` = 1")
    println("更新数据，返回值=>" + resUpdate)
  }

  def insert(): Unit = {
    val resInsert = statement.executeUpdate("INSERT INTO `scala_jdbc_test`(`name`, `age`, `address`) VALUES ( '李四', 2, '南京')")
    println("插入数据，返回值=>" + resInsert)
  }


  def select(): Unit = {
    val res: ResultSet = statement.executeQuery("select * from scala_jdbc_test")
    while (res.next) {
      val id = res.getString("id")
      val name = res.getString("name")
      val age = res.getString("age")
      val address = res.getString("address")

      println("查询数据")
      println("id=%s,name=%s,age=%s,address=%s".format(id, name, age, address))
    }
  }


}
