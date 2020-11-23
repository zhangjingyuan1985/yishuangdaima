package com.sutpc.bigdata.utils

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/5/12 15:57
  */
object StrUtils {
  def stringIsNull(s: String): String = {
    if(s == null || s.trim.isEmpty){
      "-99"
    } else {
      s
    }
  }
  def intIsNull(s: String): Int = {
    if(s == null || s.trim.isEmpty){
      -99
    } else {
      s.toInt
    }
  }
  def doubleIsNull(s: String): Double = {
    if(s == null || s.trim.isEmpty){
      0.0
    } else {
      s.toDouble
    }
  }
  def longIsNull(s: String): Long = {
    if(s == null || s.trim.isEmpty){
      0L
    } else {
      s.toLong
    }
  }
}
