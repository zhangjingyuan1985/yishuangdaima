package com.sutpc.data.geomagnetism.rev.bean;

import com.sutpc.data.geomagnetism.rev.util.DataTransferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Calendar;
import lombok.Data;

/**
 * Description: 数据包（发送包/返回包）结构.
 *
 * @author zhy
 * @date 2019/7/31
 */
@Data
@ApiModel("数据包")
public class TcpBean {

  @ApiModelProperty("起始字，固定0x88")
  private byte stx = (byte) 0x88;
  @ApiModelProperty("业务类型 0x00:注册 0x01:定时交通数据"
      + "0x0f:心跳 0x04:要求校时")
  private byte type;
  @ApiModelProperty("路段或路口代码 8 bytes")
  private byte[] hsc;
  @ApiModelProperty("data的长度 2 bytes")
  private byte[] dl;
  @ApiModelProperty("传送组号 2 bytes")
  private byte[] cn;
  @ApiModelProperty("结束字 固定0x99")
  private byte etx = (byte) 0x99;
  @ApiModelProperty("校验字 从STX到ETX的XOR校验和DATA的XOR校验。不包括STX和ETX")
  private byte crc;
  @ApiModelProperty("数据")
  private byte[] data;

  /**
   * 从byteBuf转换成TcpBean对象.
   *
   * @param in buff
   * @return TcpBean
   */
  public static TcpBean fromBytes(byte[] in) {
    try {
      int startIndex = 0;
      TcpBean tcpBean = new TcpBean();
      tcpBean.setStx(in[startIndex++]);
      tcpBean.setType(in[startIndex++]);
      byte[] newHsc = new byte[8];
      System.arraycopy(in, startIndex, newHsc, 0, newHsc.length);
      startIndex += newHsc.length;
      tcpBean.setHsc(newHsc);
      byte[] newDl = new byte[2];
      System.arraycopy(in, startIndex, newDl, 0, newDl.length);
      startIndex += newDl.length;
      tcpBean.setDl(newDl);
      byte[] newCn = new byte[2];
      System.arraycopy(in, startIndex, newCn, 0, newCn.length);
      startIndex += newCn.length;
      tcpBean.setCn(newCn);
      tcpBean.setEtx(in[startIndex++]);
      tcpBean.setCrc(in[startIndex++]);
      int dataLen = in.length - 16;
      if (dataLen > 0) {
        byte[] newDate = new byte[dataLen];
        System.arraycopy(in, startIndex, newDate, 0, newDate.length);
        tcpBean.setData(newDate);
      }
      return tcpBean;
    } catch (Exception e) {
      return null;
    }

  }

  /**
   * 普通返回数据包. 如果成功，data返回 0x99—OK 否则0x00-失败.
   *
   * @param tcpBean 请求的数据包
   * @param isSuccess 是否成功 如果成功
   * @return 返回数据包
   */
  public static TcpBean commonBack(TcpBean tcpBean, boolean isSuccess) {
    short dataLength = 1;
    tcpBean.setDl(DataTransferUtil.shortToByteArray(dataLength));
    byte[] resultData = new byte[1];
    if (isSuccess) {
      resultData[0] = (byte) 0x99;
    }
    tcpBean.setData(resultData);
    return tcpBean;
  }

  /**
   * 校时，返回数据按顺序一次年月日时分秒.
   *
   * @param tcpBean 请求的数据包
   * @return 返回数据包
   */
  public static TcpBean timingBack(TcpBean tcpBean) {
    short dataLength = 6;
    tcpBean.setDl(DataTransferUtil.shortToByteArray(dataLength));
    byte[] resultData = new byte[6];
    Calendar instance = Calendar.getInstance();
    resultData[0] = DataTransferUtil.intToByte(instance.get(Calendar.YEAR) - 2000);
    resultData[1] = DataTransferUtil.intToByte(instance.get(Calendar.MONTH) + 1);
    resultData[2] = DataTransferUtil.intToByte(instance.get(Calendar.DAY_OF_MONTH));
    resultData[3] = DataTransferUtil.intToByte(instance.get(Calendar.HOUR_OF_DAY));
    resultData[4] = DataTransferUtil.intToByte(instance.get(Calendar.MINUTE));
    resultData[5] = DataTransferUtil.intToByte(instance.get(Calendar.SECOND) + 1);
    tcpBean.setData(resultData);
    return tcpBean;
  }

  /**
   * 转字节数组.
   *
   * @return 字节数组
   */
  public byte[] toBytes() {
    ByteBuf buf = Unpooled.buffer();
    buf.writeByte(stx);
    buf.writeByte(type);
    buf.writeBytes(hsc, 0, hsc.length);
    //计算长度
    long dataLen = data == null ? 0 : data.length;
    byte[] lengthBytes = DataTransferUtil.getLengthBytes(dataLen);
    dl = new byte[2];
    dl[0] = lengthBytes[1];
    dl[1] = lengthBytes[0];
    buf.writeBytes(dl, 0, dl.length);
    buf.writeBytes(cn, 0, cn.length);
    buf.writeByte(etx);
    //从STX到ETX的XOR校验和DATA的XOR校验。不包括STX和ETX。
    byte xor = type;
    for (byte b : hsc) {
      xor ^= b;
    }
    for (byte b : dl) {
      xor ^= b;
    }
    for (byte b : cn) {
      xor ^= b;
    }
    if (data != null && data.length > 0) {
      for (byte b : data) {
        xor ^= b;
      }
    }
    buf.writeByte(xor);
    if (data != null && data.length > 0) {
      buf.writeBytes(data, 0, data.length);
    }
    byte[] result = new byte[buf.writerIndex()];
    buf.getBytes(buf.readerIndex(), result);
    return result;
  }
}
