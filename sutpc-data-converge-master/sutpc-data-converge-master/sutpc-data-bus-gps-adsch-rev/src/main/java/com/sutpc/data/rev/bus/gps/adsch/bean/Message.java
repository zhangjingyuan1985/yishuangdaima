package com.sutpc.data.rev.bus.gps.adsch.bean;

import lombok.Data;

/**
 *
 */
@Data
public class Message {

  private int head = 0xfaf5;
  private int version;
  private int length;
  private int frameNum;
  private int cmd;
  private int receiveType;
  private int sendType;
  private long receiverAddress;
  private long senderAddress;

  private GpsBean data;

  private int chk;

}
