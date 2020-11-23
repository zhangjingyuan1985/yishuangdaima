package com.sutpc.bigdata.utils

import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec
import javax.crypto.{Cipher, KeyGenerator, SecretKey}
import org.apache.commons.codec.binary.Base64


/**
  * <p>Title: DESUtils</p>
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/7/18  18:53
  */
object DesUtils {

  def bytes2hex(bytes: Array[Byte]): String = {
    val hex = new StringBuilder()
    for (i <- 0 to bytes.length - 1) {
      val b = bytes(i)
      var negative = false
      if (b < 0) {
        negative = true
      }
      val inte = Math.abs(b)
      val temp = Integer.toHexString(inte & 0xFF)
      if (temp.length() == 1) {
        hex.append("0")
      }
      // hex.append(temp.toLowerCase())
      hex.append(temp)
    }
    hex.toString
  }

  // 生成AES密鈅
  @throws(classOf[Exception])
  def genKeyAES(): String = {
    val pkey = "123"
    val kgen = KeyGenerator.getInstance("DES")
    kgen.init(56, new SecureRandom(pkey.getBytes))
    val secretKey = kgen.generateKey
    val enCodeFormat = secretKey.getEncoded
    val key = new SecretKeySpec(enCodeFormat, "DES")
    //    val cipher = Cipher.getInstance("DES") // 创建密码器


    //    val keyGen = KeyGenerator.getInstance("DES")
    //    keyGen.init(56)
    //    val key = keyGen.generateKey()
    val base64Str = Base64.encodeBase64String(key.getEncoded())
    base64Str
  }

  @throws(classOf[Exception])
  def loadKeyAES(base64Key: String): SecretKey = {
    val bytes = Base64.decodeBase64(base64Key)
    val key = new SecretKeySpec(bytes, "DES")
    return key
  }

  def encryt(content: String): Array[Byte] = {
    encrytAES(content.getBytes(), loadKeyAES(genKeyAES))
    //    bytes2hex(encrytAES(content.getBytes(), loadKeyAES(genKeyAES)))
  }

  def encryt2Str(content: String): String = {
    bytes2hex(encrytAES(content.getBytes(), loadKeyAES(genKeyAES)))
  }

  @throws(classOf[Exception])
  def encrytAES(source: Array[Byte], key: SecretKey): Array[Byte] = {
    val cipher = Cipher.getInstance("DES")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    cipher.doFinal(source)
  }

  def decry(source: Array[Byte]): String = {
    new String(decryptAES(source, loadKeyAES(genKeyAES)))
  }

  //  def decry2(source: String): String = {
  //    new String(decryptAES(hexStr2Byte(source), loadKeyAES(genKeyAES)))
  //  }

  @throws(classOf[Exception])
  def decryptAES(source: Array[Byte], key: SecretKey): Array[Byte] = {
    val cipher = Cipher.getInstance("DES")
    cipher.init(Cipher.DECRYPT_MODE, key)
    cipher.doFinal(source)
  }


  def main(args: Array[String]): Unit = {

    val content = "张三"
    println(decry(encryt(content)))


  }
}