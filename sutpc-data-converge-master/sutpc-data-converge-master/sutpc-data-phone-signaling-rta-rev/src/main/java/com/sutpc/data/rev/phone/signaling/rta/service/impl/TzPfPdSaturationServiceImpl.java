package com.sutpc.data.rev.phone.signaling.rta.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sutpc.data.rev.phone.signaling.rta.entity.TzPfPdSaturation;
import com.sutpc.data.rev.phone.signaling.rta.service.TzPfPdSaturationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * .
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/19 10:03
 */
@Service
public class TzPfPdSaturationServiceImpl implements TzPfPdSaturationService {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${url.root.tzpfpd}")
  private String urlRoot;

  private static final Logger logger = LoggerFactory.getLogger(TzPfPdSaturationServiceImpl.class);

  @Override
  public List<TzPfPdSaturation> queryForCurrent() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept-Charset", "utf-8");
    headers.set("Content-type", "application/json; charset=utf-8");
    HttpEntity<String> entity = new HttpEntity<String>(headers);
    String text = restTemplate.getForObject(urlRoot, String.class, entity);
    logger.info("text = {}", text);
    final String dataKey = "data";
    try {
      JSONObject sources = JSON.parseObject(text);
      if (sources != null && sources.containsKey(dataKey)) {
        JSONArray data = sources.getJSONArray(dataKey);
        return parse(data).toJavaList(TzPfPdSaturation.class);
      }
    } catch (Exception e) {
      return new ArrayList<>();
    }
    return new ArrayList<>();
  }

  /**
   * 时间字符串转对象.
   *
   * @param sources 处理前数据
   * @return 处理后数据
   */
  private JSONArray parse(JSONArray sources) {
    final String key = "TIMESTAMP";
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a", Locale.ENGLISH);
    for (Object source : sources) {
      JSONObject object = (JSONObject) source;
      String timestamp = object.getString(key);
      LocalDateTime time = LocalDateTime.parse(timestamp, dtf);
      object.put(key, time);
    }
    return sources;
  }
}
