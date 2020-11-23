package com.sutpc.data.rev.bus.gps.parse;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sutpc.data.rev.bus.gps.message.ProtoBufMsg;
import com.sutpc.data.rev.bus.gps.model.GpsVo;
import com.sutpc.data.rev.bus.gps.sink.SutpcKafkaSink;
import com.sutpc.data.rev.bus.gps.util.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息解析类.
 */
@Component
public class GpsMessageParser {

    private static final Logger logger = LoggerFactory.getLogger(GpsMessageParser.class);

    @Autowired
    SutpcKafkaSink sutpcKafkaSink;


    /**
     *  .
     */
    public static GpsVo parse(byte[] body) throws InvalidProtocolBufferException {

        //proto （原数组， 原数组的开始位置， 目标数组， 目标数组的开始位置， 拷贝个数）
        int protoLength = body.length - 4;
        byte[] protoByte = new byte[protoLength];
        System.arraycopy(body, 4, protoByte, 0, protoLength);

        GpsVo gpsvo = null;

        //消息类型
        byte[] msgType = new byte[2];
        msgType[0] = body[2];
        msgType[1] = body[3];

        int type = ByteUtils.byteArrayToInt(msgType);
        if (type == 2) {
            ProtoBufMsg.BusGpsMsg busGpsMsg = ProtoBufMsg.BusGpsMsg.parseFrom(protoByte);
            String id = busGpsMsg.getProductID();
            double latitude = busGpsMsg.getLatitude();
            double longitude = busGpsMsg.getLongitude();
            double driveSpeed = busGpsMsg.getSpeed();
            String locationTime = busGpsMsg.getMsgTime();
            double angle = busGpsMsg.getAngle();
            String routeId = busGpsMsg.getRouteID();
            String subRouteId = busGpsMsg.getSubRouteID();
            gpsvo = GpsVo.getGpsBean(id, latitude, longitude, driveSpeed, locationTime, angle, routeId,
                subRouteId);


        }

        if (type == 3) {
            ProtoBufMsg.BusArrLeftMsg busArrLeftMsg = ProtoBufMsg.BusArrLeftMsg.parseFrom(protoByte);
            logger.info("类型3 消息：{}", busArrLeftMsg.toString());
        }

        return gpsvo;
    }


    /**
     *  .
     */
    public static GpsVo parse(Message msg) throws InvalidProtocolBufferException {

        byte[] body = msg.getBody();

        return parse(body);
    }
}
