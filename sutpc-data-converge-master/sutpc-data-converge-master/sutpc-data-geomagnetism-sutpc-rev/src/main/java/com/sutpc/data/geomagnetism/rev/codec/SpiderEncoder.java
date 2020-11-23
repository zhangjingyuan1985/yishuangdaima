package com.sutpc.data.geomagnetism.rev.codec;

import com.sutpc.data.geomagnetism.rev.util.DataTransferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:Spider编码器.
 *
 * @author zhy
 * @date 2019/7/31
 */
@Slf4j
public class SpiderEncoder extends MessageToMessageEncoder<Object> {

  @Override
  protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) {
    log.info("encode");
    byte[] msgByte;
    if (msg instanceof byte[]) {
      msgByte = (byte[]) msg;
    } else {
      msgByte = msg.toString().getBytes();
    }
    ByteBuf buffer = Unpooled.wrappedBuffer(msgByte);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < msgByte.length; i++) {
      stringBuilder.append(DataTransferUtil.byteToHexString(msgByte[i]));
      if (i != msgByte.length - 1) {
        stringBuilder.append(",");
      }
    }
    log.info("Byte msg length is {} ,content is {}", msgByte.length, stringBuilder.toString());
    out.add(buffer);
  }
}
