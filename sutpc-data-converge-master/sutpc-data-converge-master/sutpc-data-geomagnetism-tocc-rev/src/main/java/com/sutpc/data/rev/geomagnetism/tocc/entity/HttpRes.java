package com.sutpc.data.rev.geomagnetism.tocc.entity;

import com.alibaba.fastjson.JSON;
import java.util.List;
import lombok.Data;

/**
 * 接口返回.
 *
 * @author admin
 * @date 2020/6/17 9:32
 */
@Data
public class HttpRes {

  private String status;
  private String msg;
  private Object data;

  /**
   * 是否成功.
   */
  public boolean isSuccess() {
    return "200".equals(status);
  }

  /**
   * 转换为列表.
   */
  public <T> List<T> parseToList(Class<T> cls) {
    try {
      return JSON.parseArray(JSON.toJSONString(data), cls);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 转换为对象.
   */
  public <T> T parseToObject(Class<T> cls) {
    try {
      return JSON.parseObject(JSON.toJSONString(data), cls);
    } catch (Exception e) {
      return null;
    }
  }

}
