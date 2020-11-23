package com.sutpc.bigdata.utils

import java.io.{IOException, InputStream}
import java.security.MessageDigest

/**
  * <p>Title: MD5Utils</p>
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/7/18  19:37
  */

object MD5Utils {

  def encode32(text: String): String = {
    val md = MessageDigest.getInstance("MD5")
    md.update(text.getBytes)
    val b = md.digest
    var i = 0
    val buf = new StringBuffer("")
    var offset = 0
    while ( {
      offset < b.length
    }) {
      i = b(offset)
      if (i < 0) i += 256
      if (i < 16) buf.append("0")
      buf.append(Integer.toHexString(i))

      {
        offset += 1;
        offset - 1
      }
    }
    buf.toString
  }

  def encode16(text: String): String = {
    encode32(text).substring(8, 24)
  }

  /** *
    * 任意文件转换成Md5
    * Can convert a text to MD5
    *
    * @param inputStream
    * @return md5
    */
  def encode(inputStream: InputStream): String = {
    var in = inputStream
    try {
      val digester = MessageDigest.getInstance("MD5")
      val bytes = new Array[Byte](8192)
      var byteCount = 0
      while ( {
        (byteCount = in.read(bytes));
        byteCount > 0
      }) digester.update(bytes, 0, byteCount)
      val digest = digester.digest
      val sb = new StringBuffer
      for (b <- digest) {
        val a = b & 0xff
        var hex = Integer.toHexString(a)
        if (hex.length == 1) hex = 0 + hex
        sb.append(hex)
      }
      return sb.toString
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally if (in != null) {
      try
        in.close
      catch {
        case e: IOException =>
          e.printStackTrace()
      }
      in = null
    }
    null
  }

}
