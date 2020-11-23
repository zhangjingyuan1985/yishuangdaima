package com.sutpc.data.rev.vitalvehicle.gps.ctfo.schedule;

import com.alibaba.fastjson.JSONObject;
import com.sutpc.data.rev.vitalvehicle.gps.ctfo.cache.MessageCache;
import com.sutpc.data.rev.vitalvehicle.gps.ctfo.cache.MessageCounter;
import com.sutpc.data.rev.vitalvehicle.gps.ctfo.service.SendMessageService;
import com.sutpc.data.util.RetorfitUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 消息通知定时器.
 * @Auth smilesnake minyikun
 * @Create 2020/1/8 15:46 
 */
@Slf4j
@Component
public class MessageNoticeSchedule {

  private MessageCounter counter = MessageCounter.getInstance();

  @Value("${base.remote.topic}")
  private String topic;

  /**
   * 每分钟执行一次.
   */
  @Scheduled(cron = "0 0/5 * * * ?")
  public void handler() {
    MessageCache messageCache = counter.getCache();
    //接收的时间+指定的时间<当前时间，说明正常
    if (messageCache.getRevTime().plus(messageCache.getRange()[messageCache.getIndex().get()],
        ChronoUnit.MINUTES).isAfter(LocalDateTime.now())) {
      sendMessage();
    } else {
      log.info("已经{}分钟没有消息了", messageCache.getRange()[messageCache.getIndex().get()]);
    }

    if (messageCache.getIndex().incrementAndGet() > 3) {
      messageCache.getIndex().getAndSet(3);
    }
  }

  private void sendMessage() {
    Call<JSONObject> call = RetorfitUtils.createApi(SendMessageService.class)
        .updateTime(topic);
    call.enqueue(new Callback<JSONObject>() {
      @Override
      public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
        log.info("send success:{}", topic);
      }

      @Override
      public void onFailure(Call<JSONObject> call, Throwable t) {
        log.info("send error:{}", topic);
      }
    });
  }

  public static void main(String[] args) {
    System.out.println(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 55)).plus(5,
        ChronoUnit.MINUTES).isBefore(LocalDateTime.now()));
  }
}