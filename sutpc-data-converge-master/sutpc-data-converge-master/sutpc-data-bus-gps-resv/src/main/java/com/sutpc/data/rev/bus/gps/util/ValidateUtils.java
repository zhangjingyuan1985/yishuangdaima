package com.sutpc.data.rev.bus.gps.util;

import com.sutpc.data.rev.bus.gps.model.GpsVo;
import java.text.SimpleDateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * <p>Description:TODO  </p>.
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: 深圳市城市交通规划研究中心</p>
 *
 * @author zhangyongtian
 * @version 1.0.0
 * @date 2019/8/1  18:39
 */
@Slf4j
public class ValidateUtils {

  private static final ThreadLocal<SimpleDateFormat> TL = new ThreadLocal<SimpleDateFormat>() {
    @Override
    protected SimpleDateFormat initialValue() {
      return new SimpleDateFormat("yyyyMMdd");
    }
  };

  /**
   *  .
   */
  public static boolean gbsBus(GpsVo gpsvo) {

    if (!isDate(String.valueOf(gpsvo.getDate()))) {
      log.error("无效数据：" + gpsvo.toString());
      return false;
    }

    return true;
  }

  /**
   *  .
   */
  public static boolean isDate(String str) {
    SimpleDateFormat format = TL.get();
    try {
      // 设置lenient为false.
      // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
      format.setLenient(false);
      format.parse(str);
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  /**
   * 判断参数的格式是否为“yyyyMMdd”格式的合法日期字符串.
   */
  public static boolean isValidDate(String str) {
    try {
      if (!StringUtils.isEmpty(str)) {
        if (str.length() == 8) {
          // 闰年标志
          boolean isLeapYear = false;
          String year = str.substring(0, 4);
          String month = str.substring(4, 6);
          String day = str.substring(6, 8);
          int vyear = Integer.parseInt(year);
          // 判断年份是否合法
          if (vyear < 1900 || vyear > 2200) {
            return false;
          }
          // 判断是否为闰年
          if (vyear % 4 == 0 && vyear % 100 != 0 || vyear % 400 == 0) {
            isLeapYear = true;
          }
          // 判断月份
          // 1.判断月份
          if (month.startsWith("0")) {
            String units4Month = month.substring(1, 2);
            int vunits4Month = Integer.parseInt(units4Month);
            if (vunits4Month == 0) {
              return false;
            }
            if (vunits4Month == 2) {
              // 获取2月的天数
              int vdays4February = Integer.parseInt(day);
              if (isLeapYear) {
                if (vdays4February > 29) {
                  return false;
                }
              } else {
                if (vdays4February > 28) {
                  return false;
                }
              }
            }
          } else {
            // 2.判断非0打头的月份是否合法
            int vmonth = Integer.parseInt(month);
            if (vmonth != 10 && vmonth != 11 && vmonth != 12) {
              return false;
            }
          }
          // 判断日期
          // 1.判断日期
          if (day.startsWith("0")) {
            String units4Day = day.substring(1, 2);
            int vunits4Day = Integer.parseInt(units4Day);
            if (vunits4Day == 0) {
              return false;
            }
          } else {
            // 2.判断非0打头的日期是否合法
            int vday = Integer.parseInt(day);
            if (vday < 10 || vday > 31) {
              return false;
            }
          }
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }
}
