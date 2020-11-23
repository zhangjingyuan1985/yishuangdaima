package com.sutpc.flume.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.List;

/**
 * @author kino
 * @version 1.0.0
 * @date 2019/12/18 17:03
 */
public class VideoAiDataStandardInterceptor implements Interceptor {

  public static void main(String[] args) {
    String line = "{\"camera_id\":\"24106\",\"event_cate\":\"traffic\",\"event_date\":\"2020-02-27 20:34:57\",\"event_refid\":\"qsymfjat6csnddl6egigq6mhcxrzaia\",\"event_type\":\"4\",\"pictures\":[],\"server_id\":\"1\",\"source_id\":\"4\",\"traffic_vehicles\":[{\"bayonet_direction\":0,\"drive_direction\":1,\"end_date\":null,\"lane\":1,\"license_plate\":\"粤B-F9999\",\"license_plate_type\":1,\"start_date\":null,\"vehicle_color\":1,\"vehicle_type\":3,\"vehicle_violation_id\":0,\"velocity\":0.0,\"violation_code\":0,\"violation_score\":0}],\"version\":\"v1.7.4\",\"videos\":[]}";
    JSONObject jsonObject = JSON.parseObject(line);
    System.out.println(Integer.parseInt(jsonObject.get("event_type").toString()));
  }

  @Override
  public void initialize() {

  }

  @Override
  public Event intercept(Event event) {
    String line = new String(event.getBody(), Charsets.UTF_8);
    JSONObject jsonObject = JSON.parseObject(line);
    System.out.println("---------------->"+jsonObject.get("event_type").toString());
    /**
     * 0、道路流量统计；
     * 1、站台流量统计；
     * 2、交通事件（机动车异常停车、车辆拥堵识别）；
     * 3、交通事件（行人、非机动车特征识别）；
     * 4、交通事件（车辆识别）
     */
    if(Integer.parseInt(jsonObject.get("event_type").toString()) == 0){ // 道路流量
      event.getHeaders().put("type", "traffic_flow");
      return event;
    } else if(Integer.parseInt(jsonObject.get("event_type").toString()) == 1){ // 站台流量
      event.getHeaders().put("type", "transit_flow");
      return event;
    } else if(Integer.parseInt(jsonObject.get("event_type").toString()) == 2){ // 交通事件（机动车异常停车、车辆拥堵识别）；
      event.getHeaders().put("type", "traffic_accidents");
      return event;
    } else if(Integer.parseInt(jsonObject.get("event_type").toString()) == 3){ // 交通事件（行人、非机动车特征识别）；
      event.getHeaders().put("type", "traffic_pedestrians");
      return event;
    } else if(Integer.parseInt(jsonObject.get("event_type").toString()) == 4){
      event.getHeaders().put("type", "traffic_vehicles");
      return event;
    } else {
      event.getHeaders().put("type", "kino");
      return event;
    }
//    return event;
  }

  @Override
  public List<Event> intercept(List<Event> list) {
    for (Event event : list) {
      intercept(event);
    }
    return list;
  }

  @Override
  public void close() {

  }

  public static class Builder implements Interceptor.Builder {

    @Override
    public Interceptor build() {
      return new VideoAiDataStandardInterceptor();
    }

    @Override
    public void configure(Context context) {
    }
  }
}
