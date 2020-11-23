package com.sutpc.data.send.bus.gps.tencent.listening;

import com.alibaba.fastjson.JSONObject;
import com.sutpc.data.send.bus.gps.tencent.cache.DataCache;
import com.sutpc.data.send.bus.gps.tencent.dto.SendEnum;
import com.sutpc.data.send.bus.gps.tencent.service.SendMessageService;
import com.sutpc.data.util.RetorfitUtils;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 发送消息处理器.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/1 15:20
 */
@Slf4j
@Component
public class SendMessageHandler {

  /**
   * 临界点.
   */
  private static final double CRITICAL_POINT_THRESHOLD = (double) 2000 / (double) 400000;

  private DataCache cache = DataCache.getInstance();

  /**
   * .
   */
  @PostConstruct
  public void valid() {
    ScheduledExecutorService scheduledThreadPool = new ScheduledThreadPoolExecutor(1,
        new BasicThreadFactory.Builder().namingPattern("validate-data-pool-%d").daemon(false)
            .build());

    scheduledThreadPool.scheduleAtFixedRate(() -> {
      log.error("total:{},loss to queue {}, loss to net {}",
          cache.getTotal().getAndSet(0),
          cache.getLossCount().getAndSet(0),
          cache.getNetCount().getAndSet(0));
      log.info("startFailCount:{}", cache.getStartFailCount().get());
      log.info("startTotalCount:{}", cache.getStartTotalCount().get());
      log.info("endFailCount:{}", cache.getEndFailCount().get());
      log.info("endTotalCount:{}", cache.getEndTotalCount().get());

      /**
       * 入队列失败的百分比.
       */
      double startPercent =
          (double) cache.getStartFailCount().getAndSet(0) / (double) cache.getStartTotalCount()
              .getAndSet(0);
      log.info("startPercent:{}", startPercent);
      if (startPercent <= CRITICAL_POINT_THRESHOLD) {
        log.info("get startFailCount：{}", DataCache.getInstance().getStartFailCount().get());
        Call<JSONObject> call = RetorfitUtils.createApi(SendMessageService.class)
            .updateTime(SendEnum.Kafka.getType());
        call.enqueue(new Callback<JSONObject>() {
          @Override
          public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            log.info("send StartFailCount message success:{}", "sz_bus_from_kafka");
          }

          @Override
          public void onFailure(Call<JSONObject> call, Throwable t) {
            log.info("send {} StartFailCount message error:{}", "sz_bus_from_kafka",
                t.getMessage());
          }
        });
      }

      /**
       * 入队列失败的百分比.
       */
      double endPercent =
          (double) cache.getEndFailCount().getAndSet(0) / (double) cache.getEndTotalCount()
              .getAndSet(0);
      log.info("endPercent:{}", endPercent);
      if (endPercent <= CRITICAL_POINT_THRESHOLD) {
        log.info("get endFailCount：{}", DataCache.getInstance().getEndFailCount().get());
        Call<JSONObject> call = RetorfitUtils.createApi(SendMessageService.class)
            .updateTime(SendEnum.Kafka.getType());
        call.enqueue(new Callback<JSONObject>() {
          @Override
          public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            log.info("send EndFailCount message success:{}", "sz_bus_from_kafka");
          }

          @Override
          public void onFailure(Call<JSONObject> call, Throwable t) {
            log.info("send {} EndFailCount message error:{}", "sz_bus_from_kafka", t.getMessage());
          }
        });
      }
      cache.getLoss().forEach((k, v) -> {
        log.info("ip:{},loss:{}", k, v.get());
        if (v.getAndSet(0) == 0) {
          log.info("loss message ip:{}", k);
          Call<JSONObject> call = RetorfitUtils.createApi(SendMessageService.class)
              .updateTime(SendEnum.Tencent.getType());
          call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
              log.info("send Loss message success:{}", "sz_bus_to_tencent");
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
              log.info("address :{} Loss send {} message error:{}", k, "sz_bus_to_tencent",
                  t.getMessage());
            }
          });
        }
      });
    }, 30, 600, TimeUnit.SECONDS);
  }
}


