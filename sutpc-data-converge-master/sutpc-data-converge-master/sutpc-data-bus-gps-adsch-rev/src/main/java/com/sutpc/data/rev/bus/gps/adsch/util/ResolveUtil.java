package com.sutpc.data.rev.bus.gps.adsch.util;

import com.sutpc.data.rev.bus.gps.adsch.bean.GpsBean;
import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;


public class ResolveUtil {

  /**
   * .
   */
  public static GpsBean getGpsBean(ByteBuf msg) throws UnsupportedEncodingException {

    byte[] terminalIdByte = new byte[32];
    byte[] vehicleIdByte = new byte[8];
    byte[] lineIdByte = new byte[8];
    byte[] subLineIdByte = new byte[8];
    byte[] nameCodeByte = new byte[1];
    byte[] statusByte = new byte[1];
    byte[] longitudeByte = new byte[4];
    byte[] latitudeByte = new byte[4];
    byte[] elevationByte = new byte[4];
    byte[] timeByte = new byte[6];
    byte[] speedByte = new byte[4];
    byte[] directionByte = new byte[4];
    byte[] recordSpeedByte = new byte[4];
    byte[] recordMileageByte = new byte[4];

    msg.readBytes(terminalIdByte);
    msg.readBytes(vehicleIdByte);
    msg.readBytes(lineIdByte);
    msg.readBytes(subLineIdByte);
    msg.readBytes(nameCodeByte);
    msg.readBytes(statusByte);
    msg.readBytes(longitudeByte);
    msg.readBytes(latitudeByte);
    msg.readBytes(elevationByte);
    msg.readBytes(timeByte);
    msg.readBytes(speedByte);
    msg.readBytes(directionByte);
    msg.readBytes(recordSpeedByte);
    msg.readBytes(recordMileageByte);

    //byte[] to Object
    String terminalId = new String(terminalIdByte, "UTF-8");
    String vehicleId = new String(vehicleIdByte, "UTF-8");

    String time = BcdUtil.bcdToString(timeByte); //BCD转换 需要重写
    float speed = DataTransferUtil.bytes2Float(speedByte);
    float direction = DataTransferUtil.bytes2Float(directionByte);
    float recordSpeed = DataTransferUtil.bytes2Float(recordSpeedByte);
    float recordMileage = DataTransferUtil.bytes2Float(recordMileageByte);

    GpsBean gpsBean = new GpsBean();
    String subLineId = new String(subLineIdByte, "UTF-8");
    int nameCode = DataTransferUtil.byteToInt(nameCodeByte[0]);
    int status = DataTransferUtil.byteToInt(statusByte[0]);
    float longitude = DataTransferUtil.bytes2Float(longitudeByte);
    float latitude = DataTransferUtil.bytes2Float(latitudeByte);
    float elevation = DataTransferUtil.bytes2Float(elevationByte);
    String dateTime = "20" + time.substring(0, 2) + " " + time.substring(2, 4) + " "
        + time.substring(4, 6) + " " + time.substring(6, 8) + " "
        + " " + time.substring(8, 10) + " " + time.substring(10, 12);
    String lineId = new String(lineIdByte, "UTF-8");
    gpsBean.setTerminalId(terminalId);
    gpsBean.setVehicleId(vehicleId);
    gpsBean.setLineId(lineId);
    gpsBean.setSubLineId(subLineId);
    gpsBean.setNameCode(nameCode);
    gpsBean.setStatus(status);
    gpsBean.setLongitude(longitude);
    gpsBean.setLatitude(latitude);
    gpsBean.setElevation(elevation);
    gpsBean.setTime(dateTime);
    gpsBean.setSpeed(speed);
    gpsBean.setDirection(direction);
    gpsBean.setRecordSpeed(recordSpeed);
    gpsBean.setRecordMileage(recordMileage);

    return gpsBean;

  }


}
