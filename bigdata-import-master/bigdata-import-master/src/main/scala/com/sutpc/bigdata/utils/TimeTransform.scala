package com.sutpc.bigdata.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

object TimeTransform {
  def time2UnixTime(): Int ={
     val now: Date = new Date()
     val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     val date = dateFormat.format(now)
     val dt = dateFormat.parse(date)
     val tim: Long = dt.getTime()
     var localTime = tim / 1000
     localTime.toInt
   }
  def unixTime2DateFormat(unixValue:String, format: String): String ={
    var nunixvalue = unixValue+"000"
    val fm = new SimpleDateFormat(format)
    val res = fm.format(new Date(nunixvalue.toLong))
    res
  }
  def unixTime2Date(unixValue:String): Int ={
    var nunixvalue = unixValue+"000"
    val fm = new SimpleDateFormat("yyyyMMdd")
    val res = fm.format(new Date(nunixvalue.toLong))
    res.toInt
  }
  def unixTime2Hh(unixValue:String): Int ={
    var nunixvalue = unixValue+"000"
    val fm = new SimpleDateFormat("HH")
    val res = fm.format(new Date(nunixvalue.toLong))
    res.toInt
  }
  //时间格式"20200304123708"
  //        20171011002458转化为时间戳
  def time2UnixTime2(dateTime:String):Long = {
    var dtf:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    var ldt:LocalDateTime = LocalDateTime.parse(dateTime,dtf);
    var fa:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    var datetime2:String = ldt.format(fa);

    val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = simpleDateFormat.parse(datetime2)
    val ts = date.getTime
    var res = String.valueOf(ts)

    res.toLong/1000
  }
}
