package com.sutpc.data.rev.bus.gps.adsch.util;

public class BcdUtil {

  private static String cTBCDSymbolString = "0123456789*#abc";
  private static char[] cTBCDSymbols = cTBCDSymbolString.toCharArray();

  /**
   * .
   *
   * @param num long值
   */
  public static byte[] decToBcdArray(long num) {
    int digits = 0;

    long temp = num;
    while (temp != 0) {
      digits++;
      temp /= 10;
    }

    int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;
    boolean isOdd = digits % 2 != 0;

    byte[] bcd = new byte[byteLen];

    for (int i = 0; i < digits; i++) {
      byte tmp = (byte) (num % 10);

      if (i == digits - 1 && isOdd) {
        bcd[i / 2] = tmp;
      } else if (i % 2 == 0) {
        bcd[i / 2] = tmp;
      } else {
        byte foo = (byte) (tmp << 4);
        bcd[i / 2] |= foo;
      }

      num /= 10;
    }

    for (int i = 0; i < byteLen / 2; i++) {
      byte tmp = bcd[i];
      bcd[i] = bcd[byteLen - i - 1];
      bcd[byteLen - i - 1] = tmp;
    }

    return bcd;
  }

  /**
   * .
   *
   * @param bcd byte
   */
  public static String bcdToString(byte bcd) {
    StringBuffer sb = new StringBuffer();

    byte high = (byte) (bcd & 0xf0);
    high >>>= (byte) 4;
    high = (byte) (high & 0x0f);
    byte low = (byte) (bcd & 0x0f);

    sb.append(high);
    sb.append(low);

    return sb.toString();
  }

  /**
   * .
   *
   * @param bcd bytes数组
   */
  public static String bcdToString(byte[] bcd) {

    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < bcd.length; i++) {
      sb.append(bcdToString(bcd[i]));
    }

    return sb.toString();
  }

  /**
   * .
   *
   * @param bytes 字节数组
   */
  public static String bcd2Str(byte[] bytes) {
    StringBuffer temp = new StringBuffer(bytes.length * 2);

    for (int i = 0; i < bytes.length; i++) {
      temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
      temp.append((byte) (bytes[i] & 0x0f));
    }
    return "0".equalsIgnoreCase(temp.toString().substring(0, 1)) ? temp.toString().substring(1)
        : temp.toString();
  }


  /**
   * .
   *
   * @param tbcd 字节数组
   */
  public static String toBcd(byte[] tbcd) {

    int size = (tbcd == null ? 0 : tbcd.length);
    StringBuffer buffer = new StringBuffer(2 * size);
    for (int i = 0; i < size; ++i) {
      int octet = tbcd[i];
      int n2 = (octet >> 4) & 0xF;
      int n1 = octet & 0xF;

      if (n1 == 15) {
        throw new NumberFormatException("Illegal filler in octet n=" + i);
      }
      buffer.append(cTBCDSymbols[n1]);

      if (n2 == 15) {
        if (i != size - 1) {
          throw new NumberFormatException("Illegal filler in octet n=" + i);
        }
      } else {
        buffer.append(cTBCDSymbols[n2]);
      }
    }

    return buffer.toString();
  }

}
