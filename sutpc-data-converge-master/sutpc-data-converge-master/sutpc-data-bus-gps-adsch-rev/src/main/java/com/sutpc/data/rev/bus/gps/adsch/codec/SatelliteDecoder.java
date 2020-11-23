package com.sutpc.data.rev.bus.gps.adsch.codec;


import com.sutpc.data.rev.bus.gps.adsch.bean.GpsBean;
import com.sutpc.data.rev.bus.gps.adsch.bean.Message;
import com.sutpc.data.rev.bus.gps.adsch.util.ResolveUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

/**
 *
 */
public class SatelliteDecoder extends MessageToMessageDecoder<ByteBuf> {

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
      List<Object> list) throws Exception {
    Message message = new Message();
    byteBuf.readShort();
    message.setVersion(byteBuf.readShort());
    message.setLength(byteBuf.readShort());
    message.setFrameNum(byteBuf.readByte());
    message.setCmd(byteBuf.readByte());
    message.setReceiveType(byteBuf.readByte());
    message.setSendType(byteBuf.readByte());
    message.setReceiverAddress(byteBuf.readInt());
    message.setSenderAddress(byteBuf.readInt());

    ByteBuf byteBuf92 = Unpooled.buffer(92);
    byteBuf.readBytes(byteBuf92, 92);
    GpsBean gpsBean= ResolveUtil.getGpsBean(byteBuf92);
    message.setData(gpsBean);
    message.setChk(byteBuf.readShort());
    list.add(message);
  }
}
