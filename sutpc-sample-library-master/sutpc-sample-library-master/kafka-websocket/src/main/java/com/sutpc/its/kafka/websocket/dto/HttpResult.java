package com.sutpc.its.kafka.websocket.dto;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 15:14$ 2019/10/28$.
 * @Description
 * @Modified By:
 */
public class HttpResult<T> {

  private String code;
  private String msg;
  private Object data;


  public HttpResult() {
    code = "200";
    msg = "success";
  }

  /**
   * 返回消息 .
   * @param success .
   * @param data .
   * @param code .
   */
  public HttpResult(boolean success, Object data, String code) {
    this(success, data);
    this.code = code;
  }

  /**
   * 返回消息 .
   * @param success .
   * @param data .
   */
  public HttpResult(boolean success, Object data) {
    this.code = success ? "200" : "500";
    this.data = data;
  }

  /**
   * 返回消息 .
   * @param msg .
   * @param data .
   * @param code .
   */
  public HttpResult(String msg, Object data, String code) {
    this.code = code;
    this.data = data;
    this.msg = msg;
  }

  /**
   *  .
   * @return
   */
  public static HttpResult ok() {
    HttpResult r = new HttpResult();
    return r;
  }

  /**
   *  .
   * @param data .
   * @return
   */
  public static HttpResult ok(Object data) {
    HttpResult r = new HttpResult();
    r.setData(data);
    return r;
  }

  /**
   *  .
   * @param data .
   * @param msg .
   * @return
   */
  public static HttpResult ok(Object data, String msg) {
    HttpResult r = new HttpResult();
    r.setData(data);
    r.setMsg(msg);
    return r;
  }

  /**
   *  .
   * @param code .
   * @param data .
   * @param msg .
   * @return
   */
  public static HttpResult ok(String code, Object data, String msg) {
    HttpResult r = new HttpResult();
    r.setCode(code);
    r.setData(data);
    r.setMsg(msg);
    return r;
  }

  public static HttpResult created(Object data) {
    return new HttpResult(true, data, "201");
  }

  /**
   *  .
   * @return
   */
  public static HttpResult error() {
    HttpResult r = new HttpResult();
    r.setCode("500");
    r.setMsg("未知异常，请联系管理员");
    return r;
  }

  /**
   *  .
   * @param msg .
   * @return
   */
  public static HttpResult error(String msg) {
    HttpResult r = new HttpResult();
    r.setCode("500");
    r.setMsg(msg);
    return r;
  }

  /**
   *  .
   * @param data .
   * @param msg .
   * @return
   */
  public static HttpResult error(Object data, String msg) {
    HttpResult r = new HttpResult();
    r.setCode("500");
    r.setData(data);
    r.setMsg(msg);
    return r;
  }

  /**
   *  .
   * @param code .
   * @param data .
   * @param msg .
   * @return
   */
  public static HttpResult error(String code, Object data, String msg) {
    HttpResult r = new HttpResult();
    r.setCode(code);
    r.setData(data);
    r.setMsg(msg);
    return r;
  }


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

}
