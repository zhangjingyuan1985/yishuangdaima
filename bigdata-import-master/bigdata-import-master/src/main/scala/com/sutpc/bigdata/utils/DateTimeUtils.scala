package com.sutpc.bigdata.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.util.{Calendar, TimeZone}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object DateTimeUtils {
  private val ft = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  private[bigdata] val millis = (s: String) => {
    DateTime.parse(s, ft).getMillis / 1000
  }

  private[bigdata] val inHour = (millis: Long, sh: Int, eh: Int) => {
    val date = new DateTime(millis)
    val hour = date.hourOfDay.get
    hour >= sh && hour < eh
  }

  //每天的毫秒数
  private val day = 1000 * 60 * 60 * 24
  //日期字符串的格式
  private val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  /**
    * 返回后一天的 00:00:00 字符串格式
    *
    * @return
    */
  def getAfterDayStart: String = format.format(getTodayStartMills + day)

  /**
    * @param specifiedDay 输入的日期: year/month/day
    * @return 返回指定日期的后一天 year/month/day
    */
  def getSpecifiedDayAfter(specifiedDay: String) = {
    val c = Calendar.getInstance()
    val date= new SimpleDateFormat("yyyy/MM/dd").parse(specifiedDay)
    c.setTime(date)

    val month: Int = c.get(Calendar.MONTH)

    val day= c.get(Calendar.DATE)
    c.set(Calendar.DATE,day+1)
    val dayAfter=new SimpleDateFormat("yyyy/MM/dd").format(c.getTime())
    dayAfter
  }

  /**
    * 返回指定日期的年
    * @param specifiedDay 输入的指定日期
    * @return
    */
  def getYears(specifiedDay: String) = {
    val c = Calendar.getInstance()
    val date= new SimpleDateFormat("yyyy/MM/dd").parse(specifiedDay)
    c.setTime(date)
    c.get(Calendar.YEAR)
  }

  /**
    * 返回指定日期的月份
    * @param specifiedDay 输入的指定日期
    * @return
    */
  def getMonth(specifiedDay: String) = {
    val c = Calendar.getInstance()
    val date= new SimpleDateFormat("yyyy/MM/dd").parse(specifiedDay)
    c.setTime(date)
    c.get(Calendar.MONTH)
  }

  /**
    * 返回前一天的 00:00:00 字符串格式
    *
    * @return
    */
  def getLastDayStart: String = format.format(getTodayStartMills - day)

  /**
    * 返回前一天的 23:59:59 字符串格式
    *
    * @return
    */
  def getLastDayEnd: String = format.format(getTodayStartMills - 1)

  /**
    * 返回当日的 00:00:00 字符串格式
    *
    * @return
    */
  def getTodayStart: String = {
    val zero = getTodayStartMills
    format.format(zero)
  }

  /**
    * 返回当日的 23:59:59 字符串格式
    *
    * @return
    */
  def getTodayEnd: String = format.format(getTodayStartMills + day - 1)

  /**
    * 返回当日的 00:00:00 毫秒格式
    *
    * @return
    */
  def getTodayStartMills: Long = {
    val current = System.currentTimeMillis
    val zero = ((current + TimeZone.getDefault.getRawOffset) / day * day) - TimeZone.getDefault.getRawOffset
    zero
  }

  /**
    * 返回前一天的 00:00:00 毫秒格式
    *
    * @return
    */
  def getLastDayStartMills: Long = getTodayStartMills - day

  /**
    * 判断日期是否是指定格式的
    * @param str 日期 例如: 2017-10-01 00:05:00
    * @param pattern 日期格式 例如: yyyy-MM-dd HH:mm:ss
    * @return boolean
    */
  def validateDf(str: String, pattern: String): Boolean = try {
    LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern))
    true
  } catch {
    case e: DateTimeParseException => {
      false
    }
  }

  def main(args: Array[String]): Unit = {
//    val inputPath = "/data/origin/vehicle/gps/taxi/440300/jiaowei/2019/10/08"
//    //    println(path.substring(path.lastIndexOf("/")-7))
//
//    //    val inputPath="/data/origin/vehicle/gps/taxi/440300/jiaowei/2019/10/09"
//    //    val date = DateTime.parse(inputPath.substring(inputPath.lastIndexOf("/") - 7), DateTimeFormat.forPattern("yyyy/MM/dd")).plusDays(1)
//    //    println(date.toString("yyyyMMdd HH:mm:ss"))
//
//    val today = DateTime.parse(inputPath.substring(inputPath.lastIndexOf("/") - 7), DateTimeFormat.forPattern("yyyy/MM/dd")).plusDays(1)
//
//    val time_tmp = "2019-10-07 23:59:22"
//    //loc_time	定位日期	整型	10	否		unix10位时间戳
//    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
//
//    val isOk = dataTime.getMillis >= today.minusDays(1).getMillis && dataTime.getMillis < today.getMillis
//
//    println(isOk)


    println(getSpecifiedDayAfter("2020/01/31"))
    println(getMonth("2020/02/01"))

    println(validateDf("2017-10-01 00:05:00", "yyyy-MM-dd HH:mm-ss"))

  }
}
