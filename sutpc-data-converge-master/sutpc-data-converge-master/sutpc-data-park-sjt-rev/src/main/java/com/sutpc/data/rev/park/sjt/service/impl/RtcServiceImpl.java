package com.sutpc.data.rev.park.sjt.service.impl;

import com.alibaba.fastjson.JSON;
import com.sutpc.data.rev.park.sjt.app.AppHelper;
import com.sutpc.data.rev.park.sjt.app.HttpRes;
import com.sutpc.data.rev.park.sjt.entity.AreaRes;
import com.sutpc.data.rev.park.sjt.entity.BerthRes;
import com.sutpc.data.rev.park.sjt.entity.CantonRes;
import com.sutpc.data.rev.park.sjt.entity.ConstantlyRes;
import com.sutpc.data.rev.park.sjt.entity.PriceItem;
import com.sutpc.data.rev.park.sjt.entity.PriceRes;
import com.sutpc.data.rev.park.sjt.entity.SectionRes;
import com.sutpc.data.rev.park.sjt.service.RtcService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 宜停车接口.
 *
 * @author admin
 * @date 2020/6/22 16:39
 */
@Slf4j
@Service
public class RtcServiceImpl implements RtcService {

  @Autowired
  private AppHelper helper;

  private static final Logger logger = LoggerFactory.getLogger(RtcServiceImpl.class);

  @Override
  public List<CantonRes> getCantonList() {
    try {
      String text = helper.get("/rtc/getCantonList", null);
      HttpRes res = JSON.parseObject(text, HttpRes.class);
      if (res.isSuccess()) {
        return res.parseToList(CantonRes.class);
      }
    } catch (Exception e) {
      logger.info("error = {}", e);
      e.printStackTrace();
      return new ArrayList<>();
    }
    return new ArrayList<>();
  }

  @Override
  public List<AreaRes> getAreaList(String cantonId) {
    try {
      Map<String, String> params = new HashMap<>();
      params.put("cantonId", cantonId);
      String text = helper.get("/rtc/getAreaList", params);
      HttpRes res = JSON.parseObject(text, HttpRes.class);
      if (res.isSuccess()) {
        return res.parseToList(AreaRes.class);
      }
    } catch (Exception e) {
      logger.info("error = {}", e);
      e.printStackTrace();
      return new ArrayList<>();
    }
    return new ArrayList<>();
  }

  @Override
  public List<SectionRes> getSectionList(String areaId) {
    try {
      Map<String, String> params = new HashMap<>();
      params.put("areaId", areaId);
      String text = helper.get("/rtc/getSectionList", params);
      HttpRes res = JSON.parseObject(text, HttpRes.class);
      if (res.isSuccess()) {
        return res.parseToList(SectionRes.class);
      }
    } catch (Exception e) {
      logger.info("error = {}", e);
      e.printStackTrace();
      return new ArrayList<>();
    }
    return new ArrayList<>();
  }

  @Override
  public List<BerthRes> getBerthList(int pageIndex, int pageSize) {
    try {
      Map<String, String> params = new HashMap<>();
      params.put("pageIndex", String.valueOf(pageIndex));
      params.put("pageSize", String.valueOf(pageSize));
      String text = helper.get("/rtc/getBerthList", params);
      HttpRes res = JSON.parseObject(text, HttpRes.class);
      if (res.isSuccess()) {
        return res.parseToList(BerthRes.class);
      }
    } catch (Exception e) {
      logger.info("error = {}", e);
      e.printStackTrace();
      return new ArrayList<>();
    }
    return new ArrayList<>();
  }

  @Override
  public PriceRes getRtcPrice(String berthCode) {
    try {
      Map<String, String> params = new HashMap<>();
      params.put("berthCode", berthCode);
      String text = helper.get("/rtc/getRTCPrice", params);
      HttpRes res = JSON.parseObject(text, HttpRes.class);
      if (res.isSuccess()) {
        List<PriceItem> items = res.parseToList(PriceItem.class);
        PriceRes data = JSON.parseObject(text, PriceRes.class);
        data.setItems(items);
        return data;
      }
    } catch (Exception e) {
      logger.info("error = {}", e);
      e.printStackTrace();
      return null;
    }
    return null;
  }

  @Override
  public List<ConstantlyRes> getConstantlyBerth() {
    try {
      String text = helper.get("/rtc/getConstantlyBerth", null);
      HttpRes res = JSON.parseObject(text, HttpRes.class);
      if (res.isSuccess()) {
        return res.parseToList(ConstantlyRes.class);
      }
    } catch (Exception e) {
      logger.info("error = {}", e);
      e.printStackTrace();
      return new ArrayList<>();
    }
    return new ArrayList<>();
  }
}
