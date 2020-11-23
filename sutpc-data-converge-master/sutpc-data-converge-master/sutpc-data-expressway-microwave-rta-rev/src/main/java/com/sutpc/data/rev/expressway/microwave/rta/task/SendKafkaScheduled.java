package com.sutpc.data.rev.expressway.microwave.rta.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TableNameEnum;
import com.sutpc.data.rev.expressway.microwave.rta.service.TDrivewaydataService;
import com.sutpc.data.rev.expressway.microwave.rta.service.TIntersectionService;
import com.sutpc.data.rev.expressway.microwave.rta.service.TManagerRoleService;
import com.sutpc.data.rev.expressway.microwave.rta.service.TManagerService;
import com.sutpc.data.rev.expressway.microwave.rta.service.TMwLaneInfoService;
import com.sutpc.data.rev.expressway.microwave.rta.service.TMwReportService;
import com.sutpc.data.rev.expressway.microwave.rta.service.TMwTerminalDataStaService;
import com.sutpc.data.rev.expressway.microwave.rta.service.TMwzTerminalInfoService;
import com.sutpc.data.rev.expressway.microwave.rta.service.TSeQueueHisService;
import com.sutpc.data.rev.expressway.microwave.rta.service.TSeReportService;
import com.sutpc.data.util.KafkaProducerUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 同步定时任务.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 15:38
 */
@Slf4j
@Component
public class SendKafkaScheduled {

  private static final Integer PAGE_SIZE = 1000;
  @Autowired
  private TMwTerminalDataStaService tmwTerminalDataStaService;
  @Autowired
  private TIntersectionService tintersectionService;
  @Autowired
  private TDrivewaydataService tdrivewaydataService;
  @Autowired
  private TMwzTerminalInfoService tmwzTerminalInfoService;
  @Autowired
  private TMwLaneInfoService tmwLaneInfoService;
  @Autowired
  private TManagerService tmanagerService;
  @Autowired
  private TManagerRoleService tmanagerRoleService;
  @Autowired
  private TSeReportService tseReportService;
  @Autowired
  private TMwReportService tmwReportService;
  @Autowired
  private TSeQueueHisService tseQueueHisService;
  private KafkaProducerUtils producer = new KafkaProducerUtils();

  /**
   * .
   */
  @Scheduled(cron = "${send.kafka.cron}")
  public void start() {
    foreach(TableNameEnum.TINTERSECTION.getName(),
        (pagenum, pagesize) -> tintersectionService.findAll(pagenum, pagesize));
    foreach(TableNameEnum.TDRIVEWAYDATA.getName(),
        (pagenum, pagesize) -> tdrivewaydataService.findAll(pagenum, pagesize));
    foreach(TableNameEnum.TMWZTERMINALINFO.getName(),
        (pagenum, pagesize) -> tmwzTerminalInfoService.findAll(pagenum, pagesize));
    foreach(TableNameEnum.TMWLANEINFO.getName(),
        (pagenum, pagesize) -> tmwLaneInfoService.findAll(pagenum, pagesize));
    foreach(TableNameEnum.TMANAGER.getName(),
        (pagenum, pagesize) -> tmanagerService.findAll(pagenum, pagesize));
    foreach(TableNameEnum.TMANAGERROLE.getName(),
        (pagenum, pagesize) -> tmanagerRoleService.findAll(pagenum, pagesize));
    foreach(TableNameEnum.TSEREPORT.getName(),
        (pagenum, pagesize) -> tseReportService.findAll(pagenum, pagesize));
  }

  @Scheduled(cron = "${send.kafka.cron}")
  public void startTmwreport() {
    foreach(TableNameEnum.TMWREPORT.getName(),
        (pagenum, pagesize) -> tmwReportService.findAll(pagenum, pagesize));
  }


  @Scheduled(cron = "${send.kafka.cron}")
  public void startTsequeuehis() {
    foreach(TableNameEnum.TSEQUEUEHIS.getName(),
        (pagenum, pagesize) -> tseQueueHisService.findAll(pagenum, pagesize));
  }

  //@Scheduled(cron = "${send.kafka.cron-his}")
  public void startTmwterminaldatasta() {
    foreach(TableNameEnum.TMWTERMINALDATASTA.getName(),
        (pagenum, pagesize) -> tmwTerminalDataStaService.findAll(pagenum, pagesize));
  }

  /**
   * startTmwterminaldatastaReal.
   */
  @Scheduled(cron = "${send.kafka.cron-sta}")
  public void startTmwterminaldatastaReal() {
    LocalDateTime endTime = LocalDateTime.now();
    LocalDateTime startTime = endTime.minusMinutes(1);
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    int pageCount = 2;
    //log.info("findByCollectionTime -> start");
    for (int pageIndex = 1; pageIndex < pageCount; pageIndex++) {
      Page<?> page = tmwTerminalDataStaService
          .findByCollectionTime(pageIndex, PAGE_SIZE, dtf.format(startTime), dtf.format(endTime));
      //log.info("findByCollectionTime ->", JSON.toJSONString(page));
      if (page == null || page.size() < 1) {
        log.debug("size is zero");
      } else {
        log.debug("----start -----page:{}----------", pageIndex);
        page.forEach(t -> {
          log.info(JSONObject.toJSONString(t));
          Future<RecordMetadata> send = producer
              .send(TableNameEnum.TMWTERMINALDATASTA.getName(), JSONObject.toJSONString(t));
          log.info("future:{}", JSONObject.toJSONString(send));
        });
        pageCount = page.getPages() + 1;
        log.debug("----end -----page:{}----------", pageIndex);
      }
    }
    System.out.println();
  }

  private void foreach(String topic, PagePrintTemplate template) {
    int pageCount = 2;
    for (int pageIndex = 1; pageIndex < pageCount; pageIndex++) {
      Page<?> page = template.findAll(pageIndex, PAGE_SIZE);
      if (page == null || page.size() < 1) {
        log.debug("size is zero");
      } else {
        log.debug("----start -----page:{}----------", pageIndex);
        page.forEach(t -> {
          log.info(JSONObject.toJSONString(t));
          Future<RecordMetadata> send = producer.send(topic, JSONObject.toJSONString(t));
          log.info("future:{}", JSONObject.toJSONString(send));
        });
        pageCount = page.getPages() + 1;
        log.debug("----end -----page:{}----------", pageIndex);
      }
    }
    System.out.println();
  }

  @FunctionalInterface
  interface PagePrintTemplate {

    Page<?> findAll(Integer pagenum, Integer pagesize);

  }

  @FunctionalInterface
  interface PageStaTemplate {

    Page<?> findByCollectionTime(Integer pagenum, Integer pagesize, Date startTime, Date endTime);

  }
}
