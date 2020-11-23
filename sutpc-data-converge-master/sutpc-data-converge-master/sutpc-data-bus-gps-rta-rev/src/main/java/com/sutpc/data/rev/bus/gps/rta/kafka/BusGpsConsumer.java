package com.sutpc.data.rev.bus.gps.rta.kafka;

import com.sutpc.data.rev.bus.gps.rta.cache.DataStatistics;
import com.sutpc.data.rev.bus.gps.rta.utils.DataTransferUtil;
import com.sutpc.data.rev.bus.gps.rta.utils.TlvUtils;
import com.sutpc.data.util.KafkaConsumeByterUtils;
import com.sutpc.data.util.KafkaProducerUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.stereotype.Component;

/**
 * 公交gps数据消费者类.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/20 15:01
 */
@Slf4j
@Component
public class BusGpsConsumer {

  /**
   * 缓存队列.
   */
  private static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(1000000);
  /**
   * kafka生产.
   */
  private KafkaProducerUtils producer = new KafkaProducerUtils();
  /**
   * 数据分析.
   */
  private DataStatistics statistics = DataStatistics.getInstance();

  /**
   * .
   */
  @PostConstruct
  public void start() {
    ExecutorService executorService = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024),
        new BasicThreadFactory.Builder().namingPattern("bus-gps-consumer-pool-%d").daemon(false)
            .build(),new AbortPolicy());
    executorService.execute(() -> new KafkaConsumeByterUtils().receive(record -> {
      log.debug("offset = {},value = {},timestamp:{},current time:{}", record.offset(),
          record.value(), record.timestamp(), new Date());
      //总数据量
      statistics.getDataTotal().incrementAndGet();
      parse(record.value());
    }));
    executorService.submit(() -> {
      while (true) {
        if (!queue.isEmpty()) {
          log.info("size:{}", queue.size());
          int size = queue.size() > 1000 ? 1000 : queue.size();
          for (int i = 0; i < size; i++) {
            String value = queue.poll();
            //可用的数据
            statistics.getSendTotal().incrementAndGet();
            producer.send(value);
          }
        }
      }
    });


  }

  private void parse(byte[] value) {
    try {
      if (value == null || value.length < 1) {
        //空数据
        statistics.getNullTotal().incrementAndGet();
        return;
      }
      //解析卫星数据

      byte[] databyte3 = TlvUtils.reversEndian(value, 58, 66, true);
      byte[] databyte3Nozero = getnoZero(databyte3, 8);
      String data3 = new String(databyte3Nozero);
      String data15One = data3.replaceFirst("^0*", "").replace("\0", "");
      String data15 = data15One.substring(0, data15One.length() - 1);
      String last = data15One.substring(data15One.length() - 1);

      if ("5".equals(last)) {
        data15 = "高峰专线" + data15;
      }
      if ("6".equals(last)) {
        data15 = "高快巴士" + data15;
      }
      if ("7".equals(last)) {
        data15 = "假日专线" + data15;
      }
      if ("A".equals(last)) {
        data15 = "福田保税区" + data15;
      }
      if ("B".equals(last)) {
        data15 = "观光" + data15;
      }
      if ("C".equals(last)) {
        data15 = "机场巴士" + data15;
      }
      String fast = data15One.substring(0, 1);
      if ("5".equals(last) || "6".equals(last) || "7".equals(last) || "C".equals(last)) {
        data15 += "号";
      }
      if ("8".equals(last)) {
        data15 += "区间线";
      }
      if ("9".equals(last)) {
        data15 += "其它";
      }
      if ("A".equals(last)) {
        data15 += "线";
      }
      if ("B".equals(last)) {
        data15 += "号线";
      }

      byte[] databyte7 = TlvUtils.reversEndian(value, 76, 80, true);
      float data7 = DataTransferUtil.bytes2Float(databyte7);
      if (data7 >= 115 || data7 < 113) {
        //错误的数据
        statistics.getErrorTotal().incrementAndGet();
        return;
      }
      byte[] databyte8 = TlvUtils.reversEndian(value, 80, 84, true);
      float data8 = DataTransferUtil.bytes2Float(databyte8);
      if (data8 >= 24 || data8 < 22) {
        //错误的数据
        statistics.getErrorTotal().incrementAndGet();
        return;
      }
      byte[] databyte9 = TlvUtils.reversEndian(value, 84, 88, true);
      float data9 = DataTransferUtil.bytes2Float(databyte9);

      byte[] databyte10 = TlvUtils.reversEndian(value, 88, 94, true);
      byte[] databyte10Nozero = getnoZero(databyte10, 6);
      String data10 = bytesToHex(databyte10Nozero);
      String year = new SimpleDateFormat("yy").format(new Date());
      if (!data10.substring(0, 2).equals(year)) {
        //错误的数据
        statistics.getErrorTotal().incrementAndGet();
        return;
      }

      byte[] databyte11 = TlvUtils.reversEndian(value, 94, 98, true);
      float data11 = DataTransferUtil.bytes2Float(databyte11);

      byte[] databyte12 = TlvUtils.reversEndian(value, 98, 102, true);
      float data12 = DataTransferUtil.bytes2Float(databyte12);

      byte[] databyte13 = TlvUtils.reversEndian(value, 102, 106, true);
      float data13 = DataTransferUtil.bytes2Float(databyte13);

      byte[] databyte14 = TlvUtils.reversEndian(value, 106, 110, true);
      float data14 = DataTransferUtil.bytes2Float(databyte14);
      byte[] databyte1 = TlvUtils.reversEndian(value, 18, 50, true);
      byte[] databyte1Nozero = getnoZero(databyte1, 32);
      String data1 = new String(databyte1Nozero);
      byte[] databyte2 = TlvUtils.reversEndian(value, 50, 58, true);
      byte[] databyte2Nozero = getnoZero(databyte2, 8);
      String data2 = new String(databyte2Nozero);
      byte[] databyte4 = TlvUtils.reversEndian(value, 66, 74, true);
      byte[] databyte4Nozero = getnoZero(databyte4, 8);
      String data4 = new String(databyte4Nozero);
      byte[] databyte5 = TlvUtils.reversEndian(value, 74, 75, true);
      int data5 = DataTransferUtil.byteToInt(databyte5[0]);
      byte[] databyte6 = TlvUtils.reversEndian(value, 75, 76, true);

      int data6 = DataTransferUtil.byteToInt(databyte6[0]);

      String result =
          data1.replace(" ", "") + "," + data2.replace(" ", "") + "," + data3.replace(" ", "") + ","
              + data4.replace(" ", "") + "," + data5 + "," + data6 + "," + data7 + "," + data8 + ","
              + data9 + "," + data10 + "," + data11 + "," + data12 + "," + data13 + "," + data14
              + "," + data15;
      log.debug("byte[] to string:{}", result);
      statistics.getAvailableTotal().incrementAndGet();
      queue.offer(result);

    } catch (Exception e) {
      //错误的数据
      statistics.getErrorTotal().incrementAndGet();
      log.error("message:{},data:{}", e.getMessage(), value);
    }
  }

  private static byte[] getnoZero(byte[] bytes, int len) {
    int i = 0;
    while (bytes[i] == 0) {
      i++;
      if (i == len - 1) {
        break;
      }

    }
    byte[] bytesnew = new byte[len - i];
    System.arraycopy(bytes, i, bytesnew, 0, len - i);
    return bytesnew;
  }

  private static String bytesToHex(byte[] hashInBytes) {

    StringBuilder sb = new StringBuilder();
    for (byte b : hashInBytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
