package com.sutpc.data.send.bus.gps.tencent.dto;

public class InfoUnit {
  private int identity;
  private int length;
  private String content;

  public InfoUnit() {
  }

  /**
   *  .
   * @param identity id
   * @param length 长度
   * @param content 内容
   */
  public InfoUnit(int identity, int length, String content) {
    this.identity = identity;
    this.length = length;
    this.content = content;
  }

  public int getIdentity() {
    return identity;
  }

  public void setIdentity(byte identity) {
    this.identity = identity;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
