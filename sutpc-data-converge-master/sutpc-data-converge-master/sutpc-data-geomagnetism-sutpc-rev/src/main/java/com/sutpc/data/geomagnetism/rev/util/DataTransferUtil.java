package com.sutpc.data.geomagnetism.rev.util;

/**
 * Description:数据转换工具类.
 *
 * @author zhy
 * @date 2019/7/31
 */
public class DataTransferUtil {

  private DataTransferUtil() {

  }

  /**
   * byte转int.
   */
  private static int byteToInt(byte b) {
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
      result += DataTransferUtil.byteToInt(bytes[i]) << (8 * (bytes.length - i - 1));
    }
    return result;
  }


  /**
   * int转byte.
   */
  public static byte intToByte(int i) {
    return (byte) i;
  }


  /**
   * short转byte[].
   */
  public static byte[] shortToByteArray(short value) {
    int shortByteCount = 2;
    byte[] result = new byte[shortByteCount];
    for (int i = 0; i < shortByteCount; i++) {
      result[2 - i - 1] = (byte) (value >> (8 * i));
    }
    return result;
  }

  /**
   * byte to long.
   */
  private static long byteToLong(byte b) {
    return b & 0xFF;
  }


  /**
   * byte[]转long，数组长度超过8，则index从0开始的多出的byte信息将会丢失.
   */
  public static long byteArrayToLongLe(byte[] bytes) {
    long result = 0;
    for (int i = 0; i < bytes.length; i++) {
      result += DataTransferUtil.byteToLong(bytes[i]) << (8 * i);
    }
    return result;
  }

  /**
   * long to byte[8].
   */
  private static byte[] longToByteArray(long l) {
    int longByteCount = 8;
    byte[] result = new byte[longByteCount];
    for (int i = 0; i < longByteCount; i++) {
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


  public static double byteArrayToDoubleLe(byte[] arr) {
    return Double.longBitsToDouble(byteArrayToLongLe(arr));
  }

}