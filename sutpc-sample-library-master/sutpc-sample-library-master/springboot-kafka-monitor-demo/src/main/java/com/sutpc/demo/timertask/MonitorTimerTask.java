package com.sutpc.demo.timertask;

import com.sutpc.demo.model.CacheModel;
import com.sutpc.demo.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 定时任务模拟心跳检测
 */
@Component
public class MonitorTimerTask {

    private Logger logger = LoggerFactory.getLogger(MonitorTimerTask.class);

    private CacheModel cacheModel = CacheModel.getCacheModel();

    private static Boolean emailBusFlag = true;

    private static Boolean emailCarFlag = true;

    @Autowired
    private MailService mailService;

    @Value("${spring.mail.toEmail}")
    private String toEmail;

    @Value("${monitor.email.time.bus}")
    private Integer monitorBusTime;

    @Value("${monitor.email.time.car}")
    private Integer monitorCarTime;

    @Scheduled(cron = "*/1 * * * * ?")
    public void timeBusTask() {
        Long nowTimeMillis = System.currentTimeMillis();   //获取当前系统时间戳
        Long busTime = CacheModel.getBusTime().longValue();
        Long distanceTime = getDistanceTime(busTime, nowTimeMillis);
        if (emailBusFlag && nowTimeMillis.compareTo(busTime) >= 0 && distanceTime >= monitorBusTime) {
            String busTimeStr = timestampToString(busTime);
            String nowTimeMillisStr = timestampToString(nowTimeMillis);
            logger.info("实时公交 告警 超过20 告警 时间：" + distanceTime + ", 发送时间：" + nowTimeMillisStr + "上次接收消息时间：" + busTimeStr);
            mailService.sendSimpleMail(toEmail, "kafka 实时公交通道 " + monitorCarTime + "s 没有收到数据", "kafka 实时公交通道 " + monitorBusTime + "s 没有收到数据。 \n" + "发送时间：" + nowTimeMillisStr + " 上次接收数据时间：" + busTimeStr);
            emailBusFlag = false; // 重复发送判断
            logger.info("实时公交 告警 邮件发送成功！");
        }
        if (!emailBusFlag && distanceTime < monitorBusTime) {
            emailBusFlag = true; // 重置
        }
    }

    @Scheduled(cron = "*/1 * * * * ?")
    public void timeCarTask() {
        Long nowTimeMillis = System.currentTimeMillis();   //获取当前系统时间戳
        Long carTime = CacheModel.getCarTime().longValue();
        Long distanceTime = getDistanceTime(carTime, nowTimeMillis);
        if (emailCarFlag && nowTimeMillis.compareTo(carTime) >= 0 && distanceTime >= monitorCarTime) {
            String carTimeStr = timestampToString(carTime);
            String nowTimeMillisStr = timestampToString(nowTimeMillis);
            logger.info("实时重点车辆 告警 超过20 告警 时间：" + distanceTime + ", 发送时间：" + nowTimeMillisStr + "上次接收消息时间：" + carTimeStr);
            mailService.sendSimpleMail(toEmail, "kafka 实时重点车辆 " + monitorCarTime + "s 没有收到数据", "kafka 实时重点车辆 " + monitorCarTime + "s 没有收到数据。 \n" + "发送时间：" + nowTimeMillisStr + " 上次接收数据时间：" + carTimeStr);
            emailCarFlag = false; // 重复发送判断
            logger.info("实时重点车辆 告警 邮件发送成功！");
        }
        if (!emailCarFlag && distanceTime < monitorCarTime) {
            emailCarFlag = true;// 重置
        }
    }

    /**
     * 计算time2减去time1的差值 差值只设置 几天 几个小时 或 几分钟
     * 根据差值返回多长之间前或多长时间后
     *
     */
    public static Long getDistanceTime(long time1, long time2) {
        long time = time2 - time1;
        return time / 1000;
    }

    public static String timestampToString (Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime losAngelesTime = instant.atZone(ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return losAngelesTime.format(formatter);
    }
}
