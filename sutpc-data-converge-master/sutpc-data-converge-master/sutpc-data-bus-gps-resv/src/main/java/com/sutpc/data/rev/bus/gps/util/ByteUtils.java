package com.sutpc.data.rev.bus.gps.util;

/**
 * Created by xiaoyuzhou on 2019/7/8.
 */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @标题 DateTransferUtil.
 * @作者 tangshaofeng
 * @描述
 * @注意
 * @时间 2017年5月8日 上午9:44:34
 */
public class ByteUtils {

  /**
   * byte转int.
   */
  public static int byteToInt(byte b) {
    return b & 0xFF;
  }

  /**
   * byte[]转int，数组长度超过4，则index从0开始的多出的byte信息将会丢失.
   */
  public static int byteArrayToInt(byte[] bytes) {
    if (bytes == null) {
      return 0;
    }
    int result = 0;
    for (int i = 0; i < bytes.length; i++) {
      result += ByteUtils.byteToInt(bytes[i]) << (8 * (bytes.length - i - 1));
    }
    return result;
  }

  /**
   * byte[]转short.
   */
  public static short byteArrayToShort(byte[] bytes) {
    ByteBuffer buffer = ByteBuffer.allocate(2);
    buffer.put(bytes[0]);
    buffer.put(bytes[1]);
    buffer.flip();
    return buffer.getShort();
  }

  /**
   * int转byte.
   */
  public static byte intToByte(int i) {
    return (byte) i;
  }

  /**
   * int[]一比一转byte[].
   */
  public static byte[] intArrayToByteArray(int[] intArray) {
    byte[] result = new byte[intArray.length];

    for (int i = 0; i < result.length; i++) {
      result[i] = intToByte(intArray[i]);
    }

    return result;
  }

  /**
   * int转byte[].
   */
  public static byte[] intToByteArray(int value) {
    byte[] result = new byte[4];
    for (int i = 0; i < 4; i++) {
      result[4 - i - 1] = (byte) (value >> (8 * i));
    }
    return result;
  }

  /**
   * short转byte[].
   */
  public static byte[] shortToByteArray(short value) {
    byte[] result = new byte[2];
    for (int i = 0; i < 2; i++) {
      result[2 - i - 1] = (byte) (value >> (8 * i));
    }
    return result;
  }

  /**
   * byte to long.
   */
  public static long byteToLong(byte b) {
    return b & 0xFF;
  }

  /**
   * long to byte.
   */
  public static byte longToByte(long i) {
    return (byte) i;
  }

  /**
   * byte[]转long，数组长度超过8，则index从0开始的多出的byte信息将会丢失.
   */
  public static long byteArrayToLong(byte[] bytes) {
    long result = 0;
    for (int i = 0; i < bytes.length; i++) {
      result += ByteUtils.byteToLong(bytes[i]) << (8 * (bytes.length - i - 1));
    }
    return result;
  }

  /**
   * long to byte[8].
   */
  public static byte[] longToByteArray(long l) {
    byte[] result = new byte[8];
    for (int i = 0; i < 8; i++) {
      result[8 - i - 1] = (byte) (l >> (8 * i));
    }
    return result;
  }

  /**
   * long to byte[2] extract two byte who's index is 6 and 7.
   */
  public static byte[] getLengthBytes(long l) {
    byte[] arrays = longToByteArray(l);
    byte[] result = new byte[2];
    System.arraycopy(arrays, 6, result, 0, 2);
    return result;
  }

  /**
   * byte to HEX string.
   */
  public static String byteToHexString(byte b) {
    return String.format("%02X ", b);
  }

  /**
   * HEX string to long.
   */
  public static long hexStringToLong(String hex) {
    BigInteger bigInteger = new BigInteger(hex, 16);
    return bigInteger.longValue();
  }

  /**
   * HEX string to byte[].
   */
  public static byte[] hexStringToBytes(String hex) {
    BigInteger bigInteger = new BigInteger(hex, 16);
    return bigInteger.toByteArray();
  }

  /**
   * HEX string to byte who's index is max in byte[].
   */
  public static byte hexStringToByte(String hex) {
    byte[] array = hexStringToBytes(hex);
    return array[array.length - 1];
  }

  /**
   * .
   *
   * @param versionBytes versionBytes
   */
  public static String getVersion(byte[] versionBytes) {
    String firstVersion = byteToHexString(versionBytes[0]);
    String secondVersion = byteToHexString(versionBytes[1]);
    String version = firstVersion + "." + secondVersion;
    if (version.startsWith("0")) {
      version = version.substring(1);
    }
    return version;
  }

  /**
   * .
   *
   * @Description 将7位的byte数组转换成yyyyMMddhhmmss的时间
   * @notice
   */
  public static String byteArrayToDateString(byte[] bytes) {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    byte[] year = new byte[2];
    buffer.get(year);
    return "" + ByteUtils.byteArrayToInt(year) + String.format("%02d", bytes[2])
        + String.format("%02d", bytes[3]) + String.format("%02d", bytes[4]) + String
        .format("%02d", bytes[5])
        + String.format("%02d", bytes[6]);
  }

  /**
   * .
   *
   * @param date date
   */
  public static byte[] dateStringToByteArray(String date) {
    byte[] yearLong = ByteUtils.longToByteArray(Long.parseLong(date.substring(0, 4)));
    byte[] yearSmall = new byte[2];
    yearSmall[0] = yearLong[6];
    yearSmall[1] = yearLong[7];
    byte month = ByteUtils.longToByte(Long.parseLong(date.substring(4, 6)));
    byte day = ByteUtils.longToByte(Long.parseLong(date.substring(6, 8)));
    byte hour = ByteUtils.longToByte(Long.parseLong(date.substring(8, 10)));
    byte minute = ByteUtils.longToByte(Long.parseLong(date.substring(10, 12)));
    byte second = ByteUtils.longToByte(Long.parseLong(date.substring(12, 14)));
    byte[] dateByte = new byte[7];
    ByteBuffer buffer2 = ByteBuffer.allocate(7);
    buffer2.put(yearSmall);
    buffer2.put(month);
    buffer2.put(day);
    buffer2.put(hour);
    buffer2.put(minute);
    buffer2.put(second);
    buffer2.flip();
    buffer2.get(dateByte);
    return dateByte;
  }

  /**
   * 时间格式 HHmm.
   */
  public static byte[] timeStringToByteArray(String time) {
    ByteBuffer buffer = ByteBuffer.allocate(2);
    if (time != null && time.trim().length() == 4) {
      buffer.put((byte) Integer.parseInt(time.trim().substring(2, 4)));
      buffer.put((byte) Integer.parseInt(time.trim().substring(0, 2)));
    } else {
      throw new IllegalArgumentException("StartTime格式不正确！正确格式为HHmm");
    }

    return buffer.array();
  }

  /**
   * .
   */
  public static String byteArrayToTimeString(byte[] timeByte) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(ByteUtils.byteToInt(timeByte[1]));
    buffer.append(":");
    buffer.append(ByteUtils.byteToInt(timeByte[0]));
    return buffer.toString();
  }

  /**
   * .
   *
   * @param signalInstallationCross signalInstallationCross
   * @param length length
   */
  public static byte[] str2ByteArray(String signalInstallationCross, int length) {
    ByteBuffer buffer = ByteBuffer.allocate(length);
    try {
      buffer.put(signalInstallationCross.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      //logger.error(ExceptionUtil.getStachTraceFromException(e));
      e.printStackTrace();
    }
    return buffer.array();
  }

  /**
   * Byte_Array[] to int[]
   * 将长度为8的byte[]中不为0的bit所在位置统计到int[]中
   * byte[0]对应的bit1-bit8.
   */
  public static int[] byteArrayToNumberArray(byte[] byteArray) {
    List<Integer> list = new ArrayList<>();
    for (int j = 0; j < byteArray.length; j++) {
      byte tmp = byteArray[7 - j];
      for (byte i = 1; i < 9; i++) {
        int test = tmp & (int) Math.pow(2, i - 1);
        if (test != 0) {
          list.add(i + j * 8);
        }
      }
    }
    int[] result = new int[list.size()];

    for (int i = 0; i < result.length; i++) {
      result[i] = list.get(i);
    }
    return result;
  }

  /**
   * int[] to Byte_Array[].
   */
  public static byte[] numberArrayToByteArray(int[] numberArray) {
    int[] sum = new int[8];
    if (numberArray != null) {

      for (int i = 0; i < numberArray.length; i++) {
        int temp = numberArray[i];
        int mod = temp % 8;
        int quotients = temp / 8;
        sum[8 - quotients - (mod == 0 ? 0 : 1)] += Math.pow(2, (mod != 0 ? mod : 8) - 1);
      }
    }
    ByteBuffer buffer = ByteBuffer.allocate(8);
    for (int i = 0; i < sum.length; i++) {
      buffer.put((byte) sum[i]);
    }
    return buffer.array();
  }

  /**
   *  .
   * @param dateList dateList
   * @return
   */
  public static byte[] dateListToByte(int[] dateList) {
    long result = 0;
    for (int i : dateList) {
      double pow = Math.pow(2, i - 1);
      long powInt = (long) pow;
      result = result | powInt;
    }
    byte[] temp = longToByteArray(result);
    byte[] dest = new byte[4];
    System.arraycopy(temp, 4, dest, 0, 4);
    return dest;
  }

  /**
   *  .
   * @param weekList weekList
   * @return
   */
  public static byte weekListToByte(int[] weekList) {
    int result = 0;
    for (int i : weekList) {
      double pow = Math.pow(2, i - 1);
      int powInt = (int) pow;
      result = result | powInt;
    }
    return (byte) result;
  }

  /**
   *  .
   * @param monthList monthList
   * @return
   */
  public static byte[] monthListToByte(int[] monthList) {
    int result = 0;
    for (int i : monthList) {
      double pow = Math.pow(2, i);
      int powInt = (int) pow;
      result = result | powInt;
    }
    byte[] dest = new byte[2];
    System.arraycopy(intToByteArray(result), 2, dest, 0, 2);
    return dest;
  }

  /**
   *  .
   * @param dateBytes dateBytes
   * @return
   */
  public static int[] byteToDateList(byte[] dateBytes) {
    List<Integer> list = new ArrayList<>();
    for (int j = 0; j < dateBytes.length; j++) {
      byte tmp = dateBytes[3 - j];
      for (int i = 0; i < 8; i++) {
        int test = tmp & (int) Math.pow(2, i);
        if (test != 0) {
          list.add(i + j * 8 + 1);
        }
      }
    }
    int[] result = new int[list.size()];

    for (int i = 0; i < result.length; i++) {
      result[i] = list.get(i);
    }
    return result;
  }

  /**
   *  .
   * @param weekByte weekByte
   * @return
   */
  public static int[] byteToWeekList(byte weekByte) {
    List<Integer> list = new ArrayList<>();
    for (int i = 1; i < 8; i++) {
      int test = weekByte & (int) Math.pow(2, i - 1);
      if (test != 0) {
        list.add(i);
      }
    }
    int[] result = new int[list.size()];

    for (int i = 0; i < result.length; i++) {
      result[i] = list.get(i);
    }
    return result;
  }

  /**
   * .
   */
  public static int[] byteToMonthList(byte[] monthBytes) {
    List<Integer> list = new ArrayList<>();
    int temp = byteArrayToShort(monthBytes);
    for (int i = 1; i < 16; i++) {
      int test = temp & (int) Math.pow(2, i);
      if (test != 0) {
        list.add(i);
      }
    }
    int[] result = new int[list.size()];

    for (int i = 0; i < result.length; i++) {
      result[i] = list.get(i);
    }
    return result;
  }

  /**
   * .
   */
  public static byte[] ipv4StringToByteArray(String ipv4) {
    ByteBuffer buffer = ByteBuffer.allocate(4);
    if (ipv4 != null) {
      String[] ip = ipv4.split("\\.");
      if (ip.length == 4) {
        for (int j = 0; j < ip.length; j++) {
          buffer.put((byte) Integer.parseInt(ip[j]));
        }
      } else {
        throw new IllegalArgumentException("ipv4格式不正确！正确格式为xxx.xxx.xxx.xxx");
      }
    }
    return buffer.array();
  }

  /**
   * .
   */
  public static String byteArrayToIpv4String(byte[] ipv4Byte) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < ipv4Byte.length; i++) {
      buffer.append(ipv4Byte[i]);
      if (i != (ipv4Byte.length - 1)) {
        buffer.append(".");
      }
    }
    return buffer.toString();
  }

  /**
   * .
   */
  public static byte[] ipv6StringToByteArray(String ipv6) {
    ByteBuffer buffer = ByteBuffer.allocate(16);
    if (ipv6 != null) {
      String[] ip = ipv6.split("\\:");
      if (ip.length == 8) {
        for (int j = 0; j < ip.length; j++) {
          buffer.put(ByteUtils.shortToByteArray(Short.parseShort(ip[j])));
        }
      }
    }
    return buffer.array();
  }

  /**
   * .
   */
  public static String byteArrayToIpv6String(byte[] ipv6Byte) {
    StringBuffer buffer = new StringBuffer();
    byte[] dest = new byte[2];
    for (int i = 0; i < ipv6Byte.length / 2; i++) {
      System.arraycopy(ipv6Byte, i * 2, dest, 0, 2);
      buffer.append(ByteUtils.byteArrayToShort(dest));
      if (i != 7) {
        buffer.append(":");
      }
    }
    return buffer.toString();
  }

  /**
   * .
   */
  public static String byteToBinaryString(byte byteArr) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 7; i >= 0; i--) {
      int pow = (int) Math.pow(2, i);
      buffer.append((byteArr & pow) != 0 ? 1 : 0);
    }
    return buffer.toString();
  }

  /**
   * .
   */
  public static String byteArrayToBinaryString(byte[] byteArr) {
    StringBuffer buffer = new StringBuffer();
    for (byte b : byteArr) {
      for (int i = 7; i >= 0; i--) {
        int pow = (int) Math.pow(2, i);
        buffer.append((b & pow) != 0 ? 1 : 0);
      }
      buffer.append(" ");
    }
    return buffer.toString();
  }

  /**
   * .
   */
  public static byte binaryStringToByte(String byteStr) {
    int endIndex = byteStr.length() < 8 ? 0 : byteStr.length() - 8;
    char[] byteCharArr = byteStr.toCharArray();
    int result = 0;
    for (int i = byteStr.length() - 1, j = 0; i >= endIndex; i--, j++) {
      result |= (Character.getNumericValue(byteCharArr[i]) * (int) Math.pow(2, j));
    }
    return (byte) result;
  }

  /**
   * .
   */
  public static byte[] binaryStringToByteArray(String byteArrStr) {
    char[] byteCharArr = byteArrStr.toCharArray();
    int lenght = byteCharArr.length / 8 + (byteCharArr.length % 8 == 0 ? 0 : 1);
    byte[] result = new byte[lenght];
    for (int i = byteArrStr.length() - 1, j = 0; i >= 0; i--, j++) {
      result[(lenght - 1) - (j / 8)] |=
          (Character.getNumericValue(byteCharArr[i]) * (int) Math.pow(2, j)) >> ((j / 8) * 8);
    }
    return result;
  }

  public static void main(String[] args) throws UnsupportedEncodingException {
    System.out.println(charToByte('A'));

  }

  /**
   * .
   */
  public static String byteArrayToHexString(byte[] msgLength, boolean isSeparator, char separator) {
    StringBuilder sb = new StringBuilder();
    for (byte msg : msgLength) {
      sb.append(byteToHexString(msg));
      if (isSeparator) {
        sb.append(separator);
      }
    }
    if (msgLength.length > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  /**
   * .
   */
  public static byte charToByte(char c) {
    Charset cs = Charset.forName("UTF-8");
    CharBuffer cb = CharBuffer.allocate(1);
    cb.put(c);
    cb.flip();
    ByteBuffer bb = cs.encode(cb);

    byte[] tmp = bb.array();
    return tmp[0];
  }

  //8字节转double
  /**
   * .
   */
  public static double bytes2Double(byte[] arr) {
    long value = 0;
    for (int i = 0; i < 8; i++) {
      value |= ((long) (arr[i] & 0xff)) << (8 * i);
    }
    return Double.longBitsToDouble(value);
  }

  // 4 bytes 转float
  /**
   * .
   */
  public static float bytes2Float(byte[] b) {
    int accum = 0;
    for (int shiftBy = 0; shiftBy < 4; shiftBy++) {
      accum |= (b[shiftBy] & 0xff) << shiftBy * 8;
    }
    return Float.intBitsToFloat(accum);
  }

  public static int convertTwoBytesToUint(byte b1, byte b2) {
    return (b2 & 0xFF) << 8 | (b1 & 0xFF);
  }
}

