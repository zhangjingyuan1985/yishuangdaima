package com.sutpc.data.file.bus.gps.pta.cosumer;

import com.sutpc.data.file.bus.gps.pta.cache.DataStatistics;
import com.sutpc.data.util.KafkaConsumerUtils;
import com.sutpc.framework.utils.system.PropertyUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProducterAndConsumerThreadGroup {

  private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(1000000);

  private int period = getPeriod();

  private DataStatistics statistics = DataStatistics.getInstance();

  private AtomicLong row = new AtomicLong(0);//消息条数

  /**
   * .
   */
  @PostConstruct
  public void start() {
    ExecutorService executorService = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024),
        new BasicThreadFactory.Builder().namingPattern("producer-consumer-pool-%d").daemon(false)
            .build(),new AbortPolicy());
    executorService.execute(() -> new KafkaConsumerUtils().receive(record -> {
      //每一千条打印日志
      if (row.incrementAndGet() > 1000) {
        log.debug("offset = {},value = {},timestamp:{},current time:{}", record.offset(),
            record.value(), record.timestamp(), new Date());
        row.getAndSet(0);
      }
      queue.offer(record.value());
      statistics.getDataTotal().incrementAndGet();
    }));

    executorService.execute(() -> consumer());
  }

  /**
   * 消费者.
   */
  private void consumer() {
    while (true) {
      try {
        String date = LocalDate.now().toString().replace("-", "");
        String fileName = date + "_" + formatPeriod() + ".txt";
        if (period < getPeriod()) {
          date = LocalDate.now().toString().replace("-", "");
          fileName = date + "_" + formatPeriod() + ".txt";
        }

        String dirStr = PropertyUtils.getProperty("gen.dir");
        LocalDate localDate = LocalDate.now();

        dirStr += File.separator + localDate.getYear()
            + File.separator + (localDate.getMonthValue() < 10 ? ("0" + localDate.getMonthValue())
            : localDate.getMonthValue())
            + File.separator + (localDate.getDayOfMonth() < 10 ? ("0" + localDate.getDayOfMonth())
            : localDate.getDayOfMonth())
            + File.separator;
        File dir = new File(dirStr);

        if (!dir.exists()) {
          dir.mkdirs();
        }

        List<String> infoList = new ArrayList<>();
        if (!queue.isEmpty()) {
          int size = queue.size() < 1000 ? queue.size() : 1000;
          for (int i = 0; i < size; i++) {
            infoList.add(queue.poll());
          }
        }

        if (infoList.size() > 0) {
          String dire = dirStr + fileName;
          write(infoList, dire);
        }
        period = getPeriod();
      } catch (Exception e) {
        log.error("error message:{}", e.getMessage());
        statistics.getErrorTotal().incrementAndGet();
      }
    }
  }

  /**
   * 将list数据写入到指定文件.
   *
   * @param list list数据
   * @param fileName 指定文件
   */
  private boolean write(List<String> list, String fileName) {

    File file = new File(fileName);
    //判断文件是否存在，不存在创建
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        log.error("error message:{}", e.getMessage());
        statistics.getErrorTotal().incrementAndGet();
      }
    }
    //使用1.7的文件工具类追加数据
    try {
      Files.write(Paths.get(fileName), list, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
      statistics.getConsumerDataTotal().getAndAdd(list.size());
      log.debug(fileName + " write success");
      return true;
    } catch (IOException e) {
      log.error("error message:{}", e.getMessage());
      statistics.getErrorTotal().incrementAndGet();
      return false;
    }
  }

  /**
   * 格式化时间片.
   */
  private String formatPeriod() {
    int temp = getPeriod();
    String periodStr = "";
    if (temp < 10) {
      periodStr = "00" + temp;
    } else if (temp < 100) {
      periodStr = "0" + temp;
    } else {
      periodStr += temp;
    }
    return periodStr;
  }

  /**
   * 得到当前时间片.
   */
  private static int getPeriod() {
    Calendar instance = Calendar.getInstance();
    int hour = instance.get(Calendar.HOUR_OF_DAY);
    int minute = instance.get(Calendar.MINUTE);
    int passMinute = hour * 60 + minute;
    return (passMinute / 5) + 1;
  }
}
