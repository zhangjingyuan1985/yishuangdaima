package com.sutpc.data.send.bus.gps.tencent.service;

import com.alibaba.fastjson.JSONObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 发送消息接口.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/1 17:10
 */
public interface SendMessageService {
  @FormUrlEncoded
  @POST("/report/updateTime")
  Call<JSONObject> updateTime(@Field("flag") String flag);
}
