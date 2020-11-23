package com.sutpc.data.rev.park.sjt.app;

import com.alibaba.fastjson.JSON;
import java.util.List;
import lombok.Data;

/**
 * HTTP相应.
 *
 * @author admin
 * @date 2020/6/22 11:28
 */
@Data
public class HttpRes {

  /**
   * 返回数据状态码：接口是否调用成功 1-查询成功 2-查询失.
   */
  private int state;
  /**
   * 返回数据状态信息.
   */
  private String msg;
  /**
   * 返回数据.
   */
  private Object data;

  public boolean isSuccess() {
    return 1 == state;
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
