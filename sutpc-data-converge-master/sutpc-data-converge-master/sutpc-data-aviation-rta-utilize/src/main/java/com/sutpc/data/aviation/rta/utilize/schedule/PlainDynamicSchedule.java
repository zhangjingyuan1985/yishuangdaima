package com.sutpc.data.aviation.rta.utilize.schedule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sutpc.data.aviation.rta.utilize.config.HttpConfig;
import com.sutpc.data.aviation.rta.utilize.dto.PlainDynamicDto;
import com.sutpc.data.aviation.rta.utilize.dto.mapper.PlainDynamicDtoMapper;
import com.sutpc.data.aviation.rta.utilize.entity.FlightInfo;
import com.sutpc.data.aviation.rta.utilize.service.FlightInfoService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 通过接口定时同步航空数据.
 * @Auth smilesnake minyikun
 * @Create 2019/12/27 16:45 
 */
@Slf4j
@Component
@EnableConfigurationProperties(HttpConfig.class)
public class PlainDynamicSchedule {

  /**
   * 页面大小.
   */
  private static final int PAGE_SIZE = 100;
  private static final int[] BACK = {0, 1};

  @Autowired
  private HttpConfig httpConfig;
  @Autowired
  private RestTemplate template;
  @Autowired
  private PlainDynamicDtoMapper mapper;
  @Autowired
  private FlightInfoService service;

  @Scheduled(cron = "${send.kafka.cron}")
  public void start() {
    Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    AtomicInteger start = new AtomicInteger(0);
    int index = 0;
    while (true) {

      if (index > 1) {
        break;
      }
      StringBuilder str = new StringBuilder();
      str.append("?opCode=" + httpConfig.getOpCode());
      str.append("&signature=" + httpConfig.getSignature(second));
      str.append("&timeStamp=" + second);
      str.append("&lat=" + httpConfig.getLat());
      str.append("&lon=" + httpConfig.getLon());
      str.append("&back=" + BACK[index]);
      str.append("&pageIndex=" + start.incrementAndGet());
      str.append("&pageSize=" + PAGE_SIZE);
      str.append("&flttype=");
      str.append("&timerange=");
      log.info("params -- timeStamp:{},back:{},pageIndex:{},signature:{}", second, BACK[index],
          start.get(), httpConfig.getSignature(second));
      //TODO back必须为0，1
      JSONObject obj;
      try {
        obj = template
            .getForObject(httpConfig.getAddress() + str.toString(), JSONObject.class);

      } catch (RestClientException e) {
        start.decrementAndGet();
        log.error("error:{}", e);
        continue;
      }
      JSONArray jsonArray = obj.getJSONArray("data");
      if (jsonArray == null || jsonArray.isEmpty()) {
        index += 1;
        start.getAndSet(0);
        continue;
      }
      List<PlainDynamicDto> list = jsonArray.toJavaList(PlainDynamicDto.class);
      List<FlightInfo> flightInfos = mapper.dtoToFlightInfo(list);
      int finalIndex = index;
      flightInfos.forEach(it -> it.setFtype(String.valueOf(BACK[finalIndex])));
      int count = service.saveOrUpdate(flightInfos);
      log.info("modify count:{}", count);

    }
    System.out.println("-----------------------------------------");


  }
}

