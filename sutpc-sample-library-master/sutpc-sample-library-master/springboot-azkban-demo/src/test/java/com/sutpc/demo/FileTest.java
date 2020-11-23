package com.sutpc.demo;


import cn.hutool.core.io.FileUtil;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 10:42$ 2020-09-21$.
 * @Description
 * @Modified By:
 */
public class FileTest {

  public static void main(String[] args) throws Exception {
    String fileName = "20200920_";
    long count = 0;
    for (int i = 1; i <= 288; i++) {
      if (i < 10) {
        fileName = fileName + "00" + i;
      } else if (i < 100) {
        fileName = fileName + "0" + i;
      }
      count = count + Files.lines(Paths.get(fileName + ".txt")).count();
    }
    System.out.println(count);
  }

}
