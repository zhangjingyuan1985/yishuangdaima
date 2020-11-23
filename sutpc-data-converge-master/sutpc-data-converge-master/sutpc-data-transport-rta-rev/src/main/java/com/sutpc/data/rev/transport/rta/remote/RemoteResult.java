package com.sutpc.data.rev.transport.rta.remote;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 远程结果.
 *
 * @author admin
 * @date 2020/8/4 15:24
 */
@Data
public class RemoteResult {

  private boolean state;
  private String message;
  private Object result;

  /**
   * 解析为列表.
   */
  public <T> List<T> parse(Class<T> cls) {
    try {
      return JSON.parseArray(JSON.toJSONString(result), cls);
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

}
