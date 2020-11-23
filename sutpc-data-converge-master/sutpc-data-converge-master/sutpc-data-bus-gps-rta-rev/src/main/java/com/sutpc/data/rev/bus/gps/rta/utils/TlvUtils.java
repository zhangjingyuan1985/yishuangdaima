package com.sutpc.data.rev.bus.gps.rta.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.util.StringUtils;

public class TlvUtils {

  /**
   * .
   *
   * @param str 字节数组
   * @param len 整型
   * @param big 布尔值
   */
  public static byte[] reversEndian(byte[] str, int len, boolean big) {
    byte b;
    byte[] res = new byte[len];
    for (int i = 0; i < len; i++) {
      res[i] = str[i];
    }
    if (big == false) {
      for (int i = 0; i < len; i++) {
        b = str[i];
        res[len - i - 1] = b;
      }
    }
    return res;
  }

  /**
   * .
   *
   * @param str 字节数组
   * @param start 开始点
   * @param end 结束点
   * @param big 布尔值
   */
  public static byte[] reversEndian(byte[] str, int start, int end, boolean big) {
    byte b;
    byte[] res = new byte[end - start];
    int j = 0;
    for (int i = start; i < end; i++) {
      res[j] = str[i];
      j++;
    }
    if (big == false) {
      for (int i = start; i < end; i++) {
        b = str[i];
        res[end - i - 1] = b;
      }
    }
    return res;
  }

  /**
   * 字节数组转16进制字符串.
   */
  public static String bytes2HexString(byte[] b) {
    StringBuffer result = new StringBuffer();
    String hex;
    for (int i = 0; i < b.length; i++) {
      hex = Integer.toHexString(b[i] & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      result.append(hex.toUpperCase());
    }
    return result.toString();
  }

  /**
   * 16进制字符串转换为字符串.
   */
  public static String hexStringToString(String s) {
    if (StringUtils.isEmpty(s)) {
      return null;
    }
    s = s.replace(" ", "");
    byte[] baKeyword = new byte[s.length() / 2];
    for (int i = 0; i < baKeyword.length; i++) {
      try {
        baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try {
      s = new String(baKeyword, "gbk");
      new String();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return s;
  }

  // 使用1字节就可以表示b
  public static String numToHex8(int b) {
    return String.format("%02x", b);// 2表示需要两个16进行数
  }

  // 需要使用2字节表示b
  public static String numToHex16(int b) {
    return String.format("%04x", b);
  }

  // 需要使用4字节表示b
  public static String numToHex32(int b) {
    return String.format("%08x", b);
  }

  /**
   * Convert hex string to byte[].
   *
   * @param hexString the hex string
   * @return byte[]
   */
  public static byte[] hexStringToBytes(String hexString) {
    if (StringUtils.isEmpty(hexString)) {
      return null;
    }
    hexString = hexString.toUpperCase();
    int length = hexString.length() / 2;
    char[] hexChars = hexString.toCharArray();
    byte[] d = new byte[length];
    for (int i = 0; i < length; i++) {
      int pos = i * 2;
      d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
    }
    return d;
  }

  /**
   * Convert char to byte.
   *
   * @param c char
   * @return byte
   */
  public static byte charToByte(char c) {
    return (byte) "0123456789ABCDEF".indexOf(c);
  }

  /**
   * .
   */
  public static String getTime() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time = sdf.format(date);
    return time;
  }

}
