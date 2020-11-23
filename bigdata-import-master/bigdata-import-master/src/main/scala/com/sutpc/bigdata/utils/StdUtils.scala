package com.sutpc.bigdata.utils

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.joda.time.DateTime

/**
  * <p>Title: StdUtils</p>
  * <p>Description:TODO 标准化工具类 </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/7/18  10:59
  */
object StdUtils {


  def isCnLngLat(lng: Double, lat: Double): Boolean = {

    //纬度3.86~53.55，经度73.66~135.05

    lng >= 73.66 && lng <= 135.05 && lat >= 3.86 && lat <= 53.55

  }

  def fillTime(date: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): String = {

    if (StdUtils.isTime(date, Array(pattern))) {
      return date
    }

    var time1 = date

    if (date.length > 19) {
      time1 = date.substring(0, 19)
      return time1
    }

    val time2 = "1971-01-01 00:00:00"

    time1 + time2.substring(time1.length)

  }

  def main(args: Array[String]): Unit = {
    println(fillTime("2018-10-01 00:02:35.000"))
  }


  def isMonth(str: String): Boolean = {

    val parsePatterns = Array("yyyy/MM", "yyyy-MM", "yyyyMM")

    return isTime(str, parsePatterns)
  }


  //  val parsePatterns = Array("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd")

  def isDate(str: String): Boolean = {

    val parsePatterns = Array("yyyy/MM/dd", "yyyy-MM-dd", "yyyyMMdd")

    return isTime(str, parsePatterns)

  }

  def isHour(str: String): Boolean = {

    val parsePatterns = Array("yyyy/MM/dd/HH", "yyyy-MM-dd HH", "yyyyMMdd HH")

    return isTime(str, parsePatterns)
  }

  //  def main(args: Array[String]): Unit = {
  //    val res = isTime("20571501040057", Array("yyyyMMddHHmmss"))
  //    println(res)
  //  }


  def isTime(str: String, parsePatterns: Array[String]): Boolean = {
    try {
      val date = DateUtils.parseDate(str, null, parsePatterns: _*)
      if (date.getTime > DateTime.now().getMillis) {
        return false;
      }
    } catch {
      case e: Exception => return false
    }

    return true;
  }


  def replaceBlank(str: String) = {
    str.replaceAll(" ", "")
  }

  /**
    * 临时替代数据年份
    *
    * @param date
    * @param year
    * @return
    */
  def replaceYear(date: String, year: String = "2017"): String = {
    if (!date.startsWith(year)) {
      year + date.substring(4)
    } else {
      date
    }
  }

  /**
    * 同济需求
    *
    * @param date
    * @param yearMonth
    * @return
    */
  def replaceYearMonth(date: String, yearMonth: String = "201710"): String = {
    if (date.startsWith("20190312")) {
      return "20171023"
    }
    if (!date.startsWith(yearMonth)) {
      yearMonth + date.substring(6)
    } else {
      date
    }
  }


  //  def main(args: Array[String]): Unit = {
  //   val arr = "2939131247,19980077,-15093343,201,2017-01-01 15:34:23,1,0,0,1,0,0,0,0,0,0,0,0,131856,131856,131856,131856,16.987055,16.987055,16.987055,16.987055,866,866,866,866,130896,130896,130896,130896,4.746764,4.746764,4.746764,4.746764,0,0,0,0,0,0,1".split(",")
  //
  //    println(arr)
  //    println(arr.length)
  //  }

  /**
    * 字符串前补0
    *
    * @param fillsize
    * @param data
    * @return
    */
  def fillZero(fillsize: Int, data: String): String = {


    val prefix = new StringBuilder

    Range(0, fillsize - data.trim.length).foreach(x => {
      prefix.append("0")
    })

    prefix.append(data.trim).toString()
  }


  /**
    * 判断全不为空
    *
    * @param arr
    * @return
    */
  def allNotEmpty(arr: Array[String]): Boolean = {
    var res = true
    for (e <- arr if res) {
      res = StringUtils.isNotEmpty(e)
    }
    res
  }


}
