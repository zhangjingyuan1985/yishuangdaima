package com.sutpc.demo.model;

public class HttpResult<T> {

    private String code;

    private String msg;

    private Object data;


    public HttpResult() {
        code = "200";
        msg = "success";
    }

    public static HttpResult ok() {
        HttpResult r = new HttpResult();
        return r;
    }

    public static HttpResult ok(Object data) {
        HttpResult r = new HttpResult();
        r.setData(data);
        return r;
    }

    public static HttpResult ok(Object data, String msg) {
        HttpResult r = new HttpResult();
        r.setData(data);
        r.setMsg(msg);
        return r;
    }

    public static HttpResult ok(String code, Object data, String msg) {
        HttpResult r = new HttpResult();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }

    public static HttpResult error() {
        HttpResult r = new HttpResult();
        r.setCode("500");
        r.setMsg("未知异常，请联系管理员");
        return r;
    }

    public static HttpResult error(String msg) {
        HttpResult r = new HttpResult();
        r.setCode("500");
        r.setMsg(msg);
        return r;
    }

    public static HttpResult error(Object data, String msg) {
        HttpResult r = new HttpResult();
        r.setCode("500");
        r.setData(data);
        r.setMsg(msg);
        return r;
    }

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
