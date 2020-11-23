package com.sutpc.data.send.bus.gps.tencent.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

  /**
   * .
   */
  public static boolean writeListAppend(List<String> list, String fileName) {

    File file = new File(fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        log.error(e.getMessage());
      }
    }
    try {
      Files.write(Paths.get(fileName), list, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
      //log.info( fileName + " write success");
      return true;
    } catch (IOException e) {
      log.error(e.getMessage());
      return false;
    }
  }

  /**
   *  .
   */
  public static boolean writeListCover(List<String> strings, String fileName) {
    try (PrintWriter w = new PrintWriter(fileName)) {
      for (String e : strings) {
        w.println(e);
      }
      return true;
    } catch (IOException e) {
      log.error("file write fail");
      return false;
    }
  }


  //遍历文件夹
  /**
   *  .
   */
  public static List<String> traverseFolder(String path) {

    List<String> listf = new ArrayList<String>();

    int fileNum = 0;
    int folderNum = 0;
    File file = new File(path);
    if (file.exists()) {
      LinkedList<File> list = new LinkedList<File>();
      File[] files = file.listFiles();
      for (File file2 : files) {
        if (file2.isDirectory()) {
          log.info("文件夹:" + file2.getAbsolutePath());
          list.add(file2);
          folderNum++;
        } else {
          listf.add(file2.getAbsolutePath());
          fileNum++;
        }
      }

    } else {
      log.error("文件不存在!");
    }
    log.info("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
    return listf;
  }
}

