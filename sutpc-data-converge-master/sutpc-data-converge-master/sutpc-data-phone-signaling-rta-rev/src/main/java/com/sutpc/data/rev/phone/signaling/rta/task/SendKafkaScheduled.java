package com.sutpc.data.rev.phone.signaling.rta.task;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.sutpc.data.rev.phone.signaling.rta.entity.TableNameEnum;
import com.sutpc.data.rev.phone.signaling.rta.service.TblPfSaturationService;
import com.sutpc.data.util.KafkaProducerUtils;
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
  private TblPfSaturationService tblPfSaturationService;

  private KafkaProducerUtils producer = new KafkaProducerUtils();

  @Scheduled(cron = "${send.kafka.cron}")
  public void start() {
    foreach(TableNameEnum.TBL_PF_SATURATION.getName(),
        (pagenum, pagesize) -> tblPfSaturationService.findAll(pagenum, pagesize));
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
}
