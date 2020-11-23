package com.sutpc.bigdata.utils

import java.text.ParseException

import org.apache.commons.lang3.time.DateUtils

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/8/9  9:21
  */
object JobSchedulerUtils {

  //  /**
  //    * 功能：判断字符串是否为日期格式
  //    *
  //    * @return
  //    */
  //  def isDate(strDate: String): Boolean = {
  //    val pattern = Pattern.compile(
  //      "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$"
  //    )
  //    val m = pattern.matcher(strDate)
  //    if (m.matches) true
  //    else false
  //  }

  def isMonth(str: String): Boolean = {

    val parsePatterns = Array("yyyy/MM")

    return isTime(str, parsePatterns)
  }


  //  val parsePatterns = Array("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd")

  def isDate(str: String): Boolean = {

    val parsePatterns = Array("yyyy/MM/dd")

    return isTime(str, parsePatterns)

  }

  def isHour(str: String): Boolean = {

    val parsePatterns = Array("yyyy/MM/dd/HH")

    return isTime(str, parsePatterns)
  }


  private def isTime(str: String, parsePatterns: Array[String]): Boolean = {
    try {
      DateUtils.parseDate(str, null, parsePatterns: _*)
    } catch {
      case e: Exception => return false
    }

    return true;
  }

  /**
    * 根据路径识别按天 还是 按月 还是 按小时处理
    *
    * @param inputPath
    * @param process
    */
  def smartCycleRun(inputPath: String, process: String => Unit, isRecur: Boolean = false) = {

    val inputPath = "/data/gps/taxi/shenzhen/2019/07"

    val month = inputPath.substring(inputPath.length - 7)

    if (isMonth(month)) {

      Range(1, 32).foreach(date =>
        try {
          //按月
          process(inputPath + "/" + StdUtils.fillZero(2, date.toString))
        } catch {
          case ex: Exception => println(ex)
        }
      )
    }

    val date = inputPath.substring(inputPath.length - 10)
    if (isDate(date)) {
      process(inputPath)
    }


    val hour = inputPath.substring(inputPath.length - 13)

    if (isHour(hour)) {
      process(inputPath + "/" + StdUtils.fillZero(2, date.toString))
    }


  }


  def main(args: Array[String]): Unit = {


  }


}
