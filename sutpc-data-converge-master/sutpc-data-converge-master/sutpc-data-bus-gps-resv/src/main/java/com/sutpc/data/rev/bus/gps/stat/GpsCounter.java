package com.sutpc.data.rev.bus.gps.stat;

import java.util.concurrent.atomic.LongAdder;

/**
 * <p>Description:TODO  </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: 深圳市城市交通规划研究中心</p>.
 *
 * @author zhangyongtian
 * @version 1.0.0
 * @date 2019/8/2  9:39
 */
public class GpsCounter {

  public static volatile LongAdder longAdder = new LongAdder();
}
