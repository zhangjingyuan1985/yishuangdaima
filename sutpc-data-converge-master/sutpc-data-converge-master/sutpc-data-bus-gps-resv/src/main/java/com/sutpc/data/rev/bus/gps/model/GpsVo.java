package com.sutpc.data.rev.bus.gps.model;

import java.math.BigDecimal;
import lombok.Data;
import org.apache.commons.lang3.time.FastDateFormat;

@Data
public class GpsVo {

  private int date;
  private String time;
  private String companyCode;
  private String id; //车牌
  private double longitude;
  private double latitude;
  private int driveSpeed;
  private int angle;
  private int operationState;
  private int dataState;
  private String routeId;
  private String subRouteId;
  private String mqRevTime;

  /**
   * .
   */
  public static GpsVo getGpsBean(String id, double latitude, double longitude, double driveSpeed,
      String locationTime, double angle, String routeId, String subRouteId) {

    String[] dateAndTime = locationTime.split(" ");
    if (dateAndTime.length != 2) {
      return null;
    }

    int date = Integer.valueOf(dateAndTime[0].replaceAll("-", ""));

    String time = dateAndTime[1].replaceAll(":", "");

    double longitudeInt = new BigDecimal(longitude).setScale(6, BigDecimal.ROUND_DOWN)
        .doubleValue();
    double latitudeInt = new BigDecimal(latitude).setScale(6, BigDecimal.ROUND_DOWN).doubleValue();
    int speed = (int) driveSpeed;
    int angleInt = (int) angle;
    String companyCode = "H";
    int operationState = 1;
    int dataState = 1;

    String mqRevTime = FastDateFormat.getInstance("yyyyMMdd HH:mm:ss")
        .format(System.currentTimeMillis());

    return new GpsVo(date, time, companyCode, id, longitudeInt, latitudeInt, speed, angleInt,
        operationState, dataState, routeId, subRouteId, mqRevTime);

  }

  /**
   * .
   */
  public GpsVo(int date, String time, String companyCode, String id, double longitude,
      double latitude, int driveSpeed, int angle, int operationState, int dataState, String routeId,
      String subRouteId, String mqRevTime) {
    this.date = date;
    this.time = time;
    this.companyCode = companyCode;
    this.id = id;
    this.longitude = longitude;
    this.latitude = latitude;
    this.driveSpeed = driveSpeed;
    this.angle = angle;
    this.operationState = operationState;
    this.dataState = dataState;
    this.routeId = routeId;
    this.subRouteId = subRouteId;
    this.mqRevTime = mqRevTime;
  }

  @Override
  public String toString() {
    return date + "," + time + "," + companyCode + "," + id + ","
        + longitude + "," + latitude + "," + driveSpeed + "," + angle + ","
        + operationState + "," + dataState + "," + routeId + "," + subRouteId + "," + mqRevTime;
  }
}
