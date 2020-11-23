package com.sutpc.data.rev.bus.gps.adsch.codec;

import com.sutpc.data.rev.bus.gps.adsch.util.DataTransferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/6 14:58
 */
@Slf4j
public class BusGpsDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    int bufferSize = in.readableBytes();
    byte[] start = new byte[2];
    in.markReaderIndex();
    if (bufferSize < 20) {
      return;
    }
    in.getBytes(in.readerIndex(), start);
    while (!"faf5".equals(bytesToHex(start))) {
      bufferSize = in.readableBytes();
      if (bufferSize < 20) {
        return;
      }
      in.skipBytes(1);
      in.markReaderIndex();
      in.getBytes(in.readerIndex(), start);
    }
    in.skipBytes(2);
    bufferSize = in.readableBytes();
    if (bufferSize < 18) {
      return;
    }
    byte[] version = new byte[2];
    in.getBytes(in.readerIndex(), version);
    if (!"1000".equals(bytesToHex(version))) {
      return;
    }
    in.skipBytes(2);
    //length
    bufferSize = in.readableBytes();
    if (bufferSize < 16) {
      return;
    }

    byte[] bytes = new byte[2];
    in.readBytes(bytes);
    int dataLength = DataTransferUtil.convertTwoBytesToUInt(bytes[1], bytes[0]);   //换位
    //    log.info("data length:{}", dataLength);

    if (dataLength == 104) {
      log.info("------");
    }

    if (in.readableBytes() <= dataLength) {
      in.resetReaderIndex();
      return;
    }

    in.skipBytes(1);//103
    byte conmmode = in.readByte();//102
    in.skipBytes(10);//92
    out.add(in.readBytes(92));
    in.skipBytes(2);

  }


  private String bytesToHex(byte[] hashInBytes) {

    StringBuilder sb = new StringBuilder();
    for (byte b : hashInBytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();

  }
}
