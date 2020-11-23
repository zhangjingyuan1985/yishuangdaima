package com.sutpc.data.rev.bus.gps.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

  //判断是否为写入时间
  /**
   * .
   */
  public static boolean isWriteTime() {
    LocalDateTime now = LocalDateTime.now();
    int minute = now.getMinute();
    return minute % 5 == 0;
  }

  //得到目录
  /**
   * .
   */
  public static String[] getDir() {
    LocalDateTime now = LocalDateTime.now();
    int year = now.getYear();
    int month = now.getMonthValue();
    int day = now.getDayOfMonth();
    int hour = now.getHour();
    int minute = now.getMinute();
    String timeSlice = String.valueOf((hour * 60 + minute) / 5);

    for (int i = 0; i < 3 - timeSlice.length(); i++) {
      timeSlice = '0' + timeSlice;
    }
    if ("000".equals(timeSlice)) {
      timeSlice = "288";
    }
    return new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day),
        timeSlice};
  }


  //得到时间片
  /**
   * .
   */
  public static String[] getTimeSlice() {
    LocalDateTime now = LocalDateTime.now();
    int year = now.getYear();
    int month = now.getMonthValue();
    String monthStr = String.format("%02d", month);
    int day = now.getDayOfMonth();
    String dayStr = String.format("%02d", day);

    int hour = now.getHour();
    int minute = now.getMinute();
    int second = now.getSecond();
    int timeSlice;
    if (minute % 5 == 0 && second == 0) {
      timeSlice = (hour * 60 + minute) / 5;
      if (timeSlice == 0) {
        timeSlice = 1;
      }
    } else {
      timeSlice = (hour * 60 + minute) / 5 + 1;
    }
    String timeSliceStr = String.format("%03d", timeSlice);

    return new String[]{String.valueOf(year), monthStr, dayStr, timeSliceStr};
  }

  /**
   * .
   */
  public static LocalDateTime getLocalDateTime(String date, String time) {
    time = String.format("%06d", Integer.valueOf(time));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    LocalDateTime dateTime = LocalDateTime.parse(date + time, formatter);
    return dateTime;
  }

  /**
   * .
   */
  public static String getLocalDateTimeStr(String date, String time) {
    time = String.format("%06d", Integer.valueOf(time));
    return date + time;
  }

  public static int getHour(String time) {
    time = String.format("%06d", Integer.valueOf(time));
    return Integer.valueOf(time.substring(0, 2));
  }

  /**
   * .
   */
  public static void main(String[] args) {
    String str = "20181122102545";
    System.out.println(getLocalDateTime("20181122", "90530"));
    System.out.println(getHour("153655"));
    System.out.println(getHour("211"));
  }


}
