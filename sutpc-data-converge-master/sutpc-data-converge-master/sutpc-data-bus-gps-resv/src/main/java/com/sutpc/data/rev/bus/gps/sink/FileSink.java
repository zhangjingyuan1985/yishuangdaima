package com.sutpc.data.rev.bus.gps.sink;

import com.sutpc.data.rev.bus.gps.config.Constant;
import com.sutpc.data.rev.bus.gps.util.DateUtil;
import com.sutpc.data.rev.bus.gps.util.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
//@EnableScheduling
public class FileSink {

  private ConcurrentLinkedQueue<String> queue;

  public FileSink() {
    queue = new ConcurrentLinkedQueue<String>();
  }


  public void addQueue(String content) {
    this.queue.add(content);
  }

  /**
   *  .
   */
  //定时任务 每秒写入一次
  //@Scheduled(fixedRate = 1000)
  public void write() {
    log.info("写入文件...");
    String[] curDir = DateUtil.getTimeSlice();
    String testDir =
        Constant.FILE_OUTPUT_BASE_DIR + "\\DATA\\" + curDir[0] + "\\" + curDir[1] + "\\"
            + curDir[2];
    if (!new File(testDir).exists()) {
      new File(testDir).mkdirs();
    }
    String fileName = curDir[0] + curDir[1] + curDir[2] + "_" + curDir[3] + ".txt";

    List<String> infoList = new ArrayList<>();
    while (!queue.isEmpty()) {
      infoList.add(queue.poll());
    }

    if (!CollectionUtils.isEmpty(infoList)) {
      FileUtils.writeListAppend(infoList, testDir + "//" + fileName);
    }

  }


}
