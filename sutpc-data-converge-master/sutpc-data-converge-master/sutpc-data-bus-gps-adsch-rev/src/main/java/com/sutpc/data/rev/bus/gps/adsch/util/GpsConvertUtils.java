package com.sutpc.data.rev.bus.gps.adsch.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description .
 * @Author:ShangxiuWu
 * @Date: 10:06 2020/7/3.
 * @Modified By:
 */
public class GpsConvertUtils {

  public static void main(String[] args) {
    String[] values = new String[]{
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00230,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00231,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00232,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00233,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00234,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00235,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00236,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00237,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00238,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,00239,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,0023A,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,0023B,0,20200521 15:02:37",
        "20200521,150237,H,54800,114.007452,22.535215,0,0,1,1,0023C,0,20200521 15:02:37"

    };
    Stopwatch stopWatch = Stopwatch.createStarted();
    for (int i = 0; i < 10000_0; i++) {
      for (String value : values) {
        System.out.println(convert(value));
      }
    }

    stopWatch.stop();
    System.out.println(stopWatch.toString());

  }

  public static String convert(String value) {
    List<String> strings = Splitter.on(",").splitToList(value);
    strings = new ArrayList<>(strings);
    String s = strings.get(10);
    if (s.endsWith("0") || s.endsWith("1")
        || s.endsWith("2") || s.endsWith("3")
        || s.endsWith("4") || s.endsWith("5")) {
      strings.set(10,
          s.substring(0, s.length() - 1).replaceFirst("^0*", ""));
    } else if (s.endsWith("6")) {
      strings.set(10,
          String.format("高快巴士%s号", s.substring(0, s.length() - 1).replaceFirst("^0*", "")));
    } else if (s.endsWith("7")) {
      strings.set(10,
          String.format("假日%s号线", s.substring(0, s.length() - 1).replaceFirst("^0*", "")));
    } else if (s.endsWith("8")) {
      strings.set(10,
          String.format("区间线%s", s.substring(0, s.length() - 1).replaceFirst("^0*", "")));
    } else if (s.endsWith("9")) {
      strings.set(10,
          String.format("其他%s", s.substring(0, s.length() - 1).replaceFirst("^0*", "")));
    } else if (s.endsWith("A")) {
      strings.set(10,
          String.format("福田保税区%s线", s.substring(0, s.length() - 1).replaceFirst("^0*", "")));
    } else if (s.endsWith("B")) {
      strings.set(10,
          String.format("观光%s号线", s.substring(0, s.length() - 1).replaceFirst("^0*", "")));
    } else if (s.endsWith("C")) {
      strings.set(10,
          String.format("机场巴士%s号", s.substring(0, s.length() - 1).replaceFirst("^0*", "")));
    }
    return Joiner.on(",").join(strings);
  }

}
