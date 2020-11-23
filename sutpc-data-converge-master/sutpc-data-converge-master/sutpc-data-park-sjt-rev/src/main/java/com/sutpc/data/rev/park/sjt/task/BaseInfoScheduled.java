package com.sutpc.data.rev.park.sjt.task;

import com.sutpc.data.rev.park.sjt.entity.AreaRes;
import com.sutpc.data.rev.park.sjt.entity.BerthRes;
import com.sutpc.data.rev.park.sjt.entity.CantonRes;
import com.sutpc.data.rev.park.sjt.entity.PriceItem;
import com.sutpc.data.rev.park.sjt.entity.PriceRes;
import com.sutpc.data.rev.park.sjt.entity.SectionRes;
import com.sutpc.data.rev.park.sjt.service.RtcService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 同步定时任务.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 15:38
 */
@Slf4j
@Component
public class BaseInfoScheduled {

  @Autowired
  private RtcService service;
  private static final Logger logger = LoggerFactory.getLogger(BaseInfoScheduled.class);

  /**
   * 定时拉取数据.
   */
  //@Scheduled(initialDelay = 3 * 1000, fixedRate = 5000 * 60 * 1000)
  public void startData() {

    // 获取行政区
    List<CantonRes> cantons = service.getCantonList();
    // 片区
    List<AreaRes> areas = new ArrayList<>();
    Map<String, String> mapArea = new HashMap<>();
    for (CantonRes canton : cantons) {
      List<AreaRes> items = service.getAreaList(canton.getCantonId());
      for (AreaRes area : items) {
        mapArea.put(area.getAreaId(), canton.getCantonName());
      }
      areas.addAll(items);
    }

    // 路段
    for (AreaRes area : areas) {
      List<SectionRes> sections = service.getSectionList(area.getAreaId());
      String cantonName = mapArea.get(area.getAreaId());
      sections.forEach(section -> {
        section.setCantonName(cantonName);
      });
      writeSection(sections);
    }
    System.out.println("----------------------");
    // 泊车位
    int number = 1;
    int size = 500;
    boolean finished = false;
    Map<String, List<String>> mapBerth = new HashMap<>();
    do {
      System.out.println("number = " + number);
      List<BerthRes> berths = service.getBerthList(number, size);
      if (CollectionUtils.isEmpty(berths)) {
        int count = 0;
        finished = true;
        while (count < 5) {
          berths = service.getBerthList(number, size);
          if (!CollectionUtils.isEmpty(berths)) {
            finished = false;
            break;
          }
          count++;
        }
      }
      if (!CollectionUtils.isEmpty(berths)) {
        berths.forEach(berth -> {
          if (mapBerth.containsKey(berth.getSectionName())) {
            mapBerth.get(berth.getSectionName()).add(berth.getBerthCode());
          } else {
            List<String> codes = new ArrayList<>();
            codes.add(berth.getBerthCode());
            mapBerth.put(berth.getSectionName(), codes);
          }
        });
        writeBerth(berths);
      }
      number++;
    } while (!finished);
    System.out.println("----------------------");
    StringBuilder sb = new StringBuilder();
    for (String key : mapBerth.keySet()) {
      PriceRes price = null;
      for (String code : mapBerth.get(key)) {
        price = service.getRtcPrice(code);
        if (price != null) {
          break;
        }
      }
      if (price == null) {
        continue;
      }
      sb.append(String.format("%s##", key));
      sb.append(String.format("工作日（%s），非工作日（%s）##", price.getWorkPricing(),
          price.getNoWorkPricing()));
      String time = "";
      if (!CollectionUtils.isEmpty(price.getItems())) {
        for (int i = 0; i < price.getItems().size(); i++) {
          PriceItem item = price.getItems().get(i);
          time += (i == 0 ? "" : "，") + item.getDateType() + item.getParkTime();
        }
      }
      sb.append(String.format("%s", time));
      sb.append("\r\n");
    }
    write("G:\\宜停车\\路段费用.txt", sb.toString());
  }


  private void writeSection(List<SectionRes> sections) {
    StringBuilder sb = new StringBuilder();
    for (SectionRes section : sections) {
      sb.append(String.format("%s##", section.getSectionId()));
      sb.append(String.format("%s##", section.getSectionName()));
      sb.append(String.format("%s##", section.getSectionLongitude()));
      sb.append(String.format("%s##", section.getSectionLatitude()));
      sb.append(String.format("%s", section.getCantonName()));
      sb.append("\r\n");
    }
    write("G:\\宜停车\\路段列表.txt", sb.toString());
  }

  private void writeBerth(List<BerthRes> berths) {
    StringBuilder sb = new StringBuilder();
    for (BerthRes berth : berths) {
      sb.append(String.format("%s##", berth.getCantonName()));
      sb.append(String.format("%s##", berth.getSectionName()));
      sb.append(String.format("%s", berth.getParkStatus()));
      sb.append("\r\n");
    }
    write("G:\\宜停车\\泊位列表.txt", sb.toString());
  }

  private void write(String filepath, String content) {
    File file = new File(filepath);
    if (!file.exists()) {
      try {
        boolean success = file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try (FileOutputStream fos = new FileOutputStream(file, true);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
      osw.write(content);
      osw.write("\r\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
