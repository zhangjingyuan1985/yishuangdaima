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
public class VideoAiDataStandardProtobufInterceptor implements Interceptor {

  public static void main(String[] args) {
    String line = "{\"area_num\":12,\"bayonet_direction\":1,\"delta_time\":5.14,\"end_time\":1589183625,\"event_fid\":\"\",\"flow_direction\":2,\"id\":null,\"in_num\":0,\"lane\":3,\"occupancy\":22.52,\"out_num\":4,\"queue_num\":6,\"sample_dura\":25,\"speed\":37.81,\"start_time\":1589183600,\"type\":5,\"vehicle_type\":1,\"wait_num\":5}";
    JSONObject jsonObject = JSON.parseObject(line);
    System.out.println(Integer.parseInt(jsonObject.get("type").toString()));
  }

  @Override
  public void initialize() {

  }

  @Override
  public Event intercept(Event event) {
    String line = new String(event.getBody(), Charsets.UTF_8);
    JSONObject jsonObject = JSON.parseObject(line);
    System.out.println("---------------->"+jsonObject.get("type").toString());
    /**
     * 1					primary							视频ai识别信息主表
     * 2					flowRider						骑行流量识别事件子表
     * 3					flowPedestrian					行人流量识别事件子表
     * 4					countBicycle					自行车数量识别事件子表
     * 5					flowVehicle						交通车辆流量识别事件子表
     * 6					flowBusStop						公交站台人流量识别事件子表
     * 7					flowBusVehicle					公交站台车辆上下车流量识别事件子表
     * 8					flowMetro						地铁出入口人流量识别事件子表
     * 9					accident						交通事件识别子表
     * 10					accidentLane					交通事件识别影响路段子表
     * 11					pedestrianFeature				行人特征识别子表
     * 12					vehicleFeature					车辆特征识别子表
     * 13					roadBroken						道路病害识别子表
     * 14					pedestrianTrack					行人轨迹识别子表
     * 15					vehicleTrack					车辆轨迹识别子表
     */
    if(jsonObject.getInteger("type") == 1){
      event.getHeaders().put("type", "primary");
      return event;
    } else if(jsonObject.getInteger("type") == 2){
      event.getHeaders().put("type", "flowRider");
      return event;
    } else if(jsonObject.getInteger("type") == 3){
      event.getHeaders().put("type", "flowPedestrian");
      return event;
    } else if(jsonObject.getInteger("type") == 4){
      event.getHeaders().put("type", "countBicycle");
      return event;
    } else if(jsonObject.getInteger("type") == 5){
      event.getHeaders().put("type", "flowVehicle");
      return event;
    } else if(jsonObject.getInteger("type") == 6){
      event.getHeaders().put("type", "flowBusStop");
      return event;
    } else if(jsonObject.getInteger("type") == 7){
      event.getHeaders().put("type", "flowBusVehicle");
      return event;
    } else if(jsonObject.getInteger("type") == 8){
      event.getHeaders().put("type", "flowMetro");
      return event;
    } else if(jsonObject.getInteger("type") == 9){
      event.getHeaders().put("type", "accident");
      return event;
    } else if(jsonObject.getInteger("type") == 10){
      event.getHeaders().put("type", "accidentLane");
      return event;
    } else if(jsonObject.getInteger("type") == 11){
      event.getHeaders().put("type", "pedestrianFeature");
      return event;
    } else if(jsonObject.getInteger("type") == 12){
      event.getHeaders().put("type", "vehicleFeature");
      return event;
    } else if(jsonObject.getInteger("type") == 13){
      event.getHeaders().put("type", "roadBroken");
      return event;
    } else if(jsonObject.getInteger("type") == 14){
      event.getHeaders().put("type", "pedestrianTrack");
      return event;
    } else if(jsonObject.getInteger("type") == 15){
      event.getHeaders().put("type", "vehicleTrack");
      return event;
    } else {
      event.getHeaders().put("type", "others");
      return event;
    }
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
      return new VideoAiDataStandardProtobufInterceptor();
    }

    @Override
    public void configure(Context context) {
    }
  }
}
