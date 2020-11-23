package com.sutpc.data.geomagnetism.rev.codec;

import static com.sutpc.data.geomagnetism.rev.constant.Constant.BASE_BYTE_LENGTH;
import static com.sutpc.data.geomagnetism.rev.constant.Constant.CONSTANT_END_BYTE;
import static com.sutpc.data.geomagnetism.rev.constant.Constant.CONSTANT_START_BYTE;
import static com.sutpc.data.geomagnetism.rev.constant.Constant.END_TO_START_INTERVAL;

import com.sutpc.data.geomagnetism.rev.bean.TcpBean;
import com.sutpc.data.geomagnetism.rev.util.DataTransferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


/**
 * Description:Spider解码器.
 *
 * @author zhy
 * @date 2019/7/31
 */
@Slf4j
public class SpiderDecoder extends ByteToMessageDecoder {


  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    // 找到固定开始字节匹配的坐标
    int startIndex = getIndex(in, CONSTANT_START_BYTE);
    // 找不到数据，数据不足，退出
    if (startIndex == -1) {
      return;
    }
    // 找到固定结束字节匹配的坐标
    int endIndex = getIndex(in, CONSTANT_END_BYTE);
    // 找不到数据，数据不足，退出
    if (endIndex == -1 || startIndex >= endIndex) {
      return;
    }
    // 数据丢包，丢弃错误部分数据
    if (endIndex - startIndex != END_TO_START_INTERVAL) {
      in.skipBytes(endIndex - in.readerIndex() + 1);
      return;
    }
    byte[] lengthBytes = new byte[2];
    lengthBytes[0] = in.getByte(startIndex + 11);
    lengthBytes[1] = in.getByte(startIndex + 10);
    int dataLength = DataTransferUtil.byteArrayToInt(lengthBytes);
    int allLength = BASE_BYTE_LENGTH + dataLength;
    // 不足一个数据包长 即基础字节长和数据字节长度，退出
    if (in.readableBytes() < allLength) {
      return;
    }
    byte[] bytes = new byte[allLength];
    in.getBytes(startIndex, bytes, 0, allLength);
    TcpBean byteBean = TcpBean.fromBytes(bytes);
    if (byteBean != null) {
      log.info("receive Message type is : {}", Arrays.toString(byteBean.toBytes()));
      out.add(byteBean);
    }
    in.skipBytes(endIndex - in.readerIndex() + 1);
  }

  private int getIndex(ByteBuf buf, byte byt) {
    return buf.indexOf(buf.readerIndex(), buf.writerIndex(), byt);
  }


}
