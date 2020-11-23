package com.sutpc.data.rev.geomagnetism.tocc.service.impl;

import com.alibaba.fastjson.JSON;
import com.sutpc.data.rev.geomagnetism.tocc.entity.Geomagnetism;
import com.sutpc.data.rev.geomagnetism.tocc.entity.HttpRes;
import com.sutpc.data.rev.geomagnetism.tocc.service.GeomagnetismService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 地磁信息服务.
 *
 * @author admin
 * @date 2020/6/16 18:42
 */
@Service
public class GeomagnetismServiceImpl implements GeomagnetismService {

  private static final Logger logger = LoggerFactory.getLogger(GeomagnetismServiceImpl.class);

  @Autowired
  private RestTemplate restTemplate;
  @Value("${url.root.api}")
  private String apiRootUrl;

  @Override
  public List<Geomagnetism> getAll() {
    List<Geomagnetism> geomagnetisms = new ArrayList<>();
    try {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
      String url = apiRootUrl + "/traffic/geomagnetismList";
      logger.info("getAll url = {}", url);
      String text = post(url, params);
      HttpRes httpRes = JSON.parseObject(text, HttpRes.class);
      if (httpRes.isSuccess()) {
        geomagnetisms = httpRes.parseToList(Geomagnetism.class);
      } else {
        logger.info("getAll failure = {}", text);
      }
    } catch (Exception e) {
      logger.info("getAll error = {}", e);
      e.printStackTrace();
    }
    return geomagnetisms;
  }

  @Override
  public List<String> getDataByCode(String code, String date) {
    List<String> values = new ArrayList<>();
    try {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
      //params.add("incode", code);
      //params.add("time", date);
      String urlPath = apiRootUrl + "/traffic/trafficList";
      String url = String.format("%s?incode=%s&time=%s", urlPath, code, date);
      logger.info("getDataByCode url = {}", url);
      String text = post(url, params);
      HttpRes httpRes = JSON.parseObject(text, HttpRes.class);
      if (httpRes.isSuccess()) {
        values = httpRes.parseToList(String.class);
      } else {
        logger.info("getDataByCode failure = {}", text);
      }
    } catch (Exception e) {
      logger.info("getDataByCode error = {}", e);
      e.printStackTrace();
    }
    return values;
  }


  private String post(String url, MultiValueMap<String, String> params) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept-Charset", "utf-8");
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
    return restTemplate.postForObject(url, entity, String.class);
  }

}
