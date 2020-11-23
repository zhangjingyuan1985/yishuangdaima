package com.sutpc.data.send.bus.gps.tencent.codec;

import com.sutpc.data.send.bus.gps.tencent.dto.InfoUnit;
import com.sutpc.data.send.bus.gps.tencent.vo.MutilRequest;
import com.sutpc.data.send.bus.gps.tencent.vo.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GpsDataEncoder extends MessageToByteEncoder<MutilRequest> {

  private final String[] hexDigIts = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b",
      "c", "d", "e", "f"};

  @Override
  protected void encode(ChannelHandlerContext ctx, MutilRequest msg, ByteBuf out) {
    long startTime = System.currentTimeMillis();
    msg.getRequests().forEach(t -> {
      out.writeByte(0xff);
      out.writeByte(t.getHeader());
      out.writeByte(t.getLength());
      getBuf(t, out);
      out.writeByte(t.getEnd());
      /*log.info("encode consult time {}", System.currentTimeMillis() - startTime);
      byte[] temp = new byte[out.writerIndex()];
      out.getBytes(0, temp);
      log.info("msg byte :{}", bytesToHex(temp));*/

    });

  }

  /**
   * 将Request编码成ByteBuf.
   */
  private void getBuf(Request msg, ByteBuf out) {
    for (InfoUnit unit : msg.getInfoUnit()) {
      out.writeByte(unit.getIdentity());
      out.writeByte(unit.getLength());
      if (unit.getIdentity() == 0x04) {
        out.writeBytes(md5Encode(unit.getContent(), "UTF-8").getBytes());
      } else if (unit.getIdentity() == 0x05) {
        out.writeShortLE(Short.parseShort(unit.getContent()));
      } else if (unit.getIdentity() == 0x06) {
        out.writeShortLE(Short.parseShort(unit.getContent()));
      } else if (unit.getIdentity() == 0x07) {
        out.writeIntLE((int) (Double.parseDouble(unit.getContent()) * 1000000d));
      } else if (unit.getIdentity() == 0x08) {
        out.writeIntLE((int) (Double.parseDouble(unit.getContent()) * 1000000d));
      } else {
        try {
          out.writeBytes(unit.getContent().getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }

    }
  }

  /**
   * 字节转16进制.
   *
   * @param bs 字节数组
   */
  private String bytesToHex(byte[] bs) {
    char[] chars = "0123456789ABCDEF".toCharArray();
    StringBuilder sb = new StringBuilder("");
    int bit;
    for (int i = 0; i < bs.length; i++) {
      bit = (bs[i] & 0x0f0) >> 4;
      sb.append(chars[bit]);
      bit = bs[i] & 0x0f;
      sb.append(chars[bit]);
      sb.append(' ');
    }
    return sb.toString().trim();
  }

  /**
   * md5编码.
   *
   * @param origin 字符串
   * @param charsetname 编码格式
   */
  private String md5Encode(String origin, String charsetname) {
    byte[] result = null;
    String resultString = null;
    try {
      resultString = new String(origin);
      MessageDigest md = MessageDigest.getInstance("MD5");
      if (null == charsetname || "".equals(charsetname)) {
        resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
      } else {
        resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resultString;
  }

  /**
   * byte数组转16进制.
   */
  private String byteArrayToHexString(byte[] b) {
    StringBuffer resultSb = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      resultSb.append(byteToHexString(b[i]));
    }
    return resultSb.toString();
  }

  private String byteToHexString(byte b) {
    int n = b;
    if (n < 0) {
      n += 256;
    }
    int d1 = n / 16;
    int d2 = n % 16;
    return hexDigIts[d1] + hexDigIts[d2];
  }
}
