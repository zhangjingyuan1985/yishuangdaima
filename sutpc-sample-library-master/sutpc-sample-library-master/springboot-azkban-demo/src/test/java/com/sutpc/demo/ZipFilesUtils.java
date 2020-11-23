package com.sutpc.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * .
 *
 * @Description: .
 * @Author: HuYing
 * @Date: 2020/5/14 8:56
 * @Modified By:
 */
public class ZipFilesUtils {
  private static final Logger log = LoggerFactory.getLogger(ZipFilesUtils.class);

  private static final byte[] BUF = new byte[1024];

  private ZipFilesUtils() {};

  public static boolean isBlank(CharSequence cs) {
    int strLen;
    if (cs != null && (strLen = cs.length()) != 0) {
      for(int i = 0; i < strLen; ++i) {
        if (!Character.isWhitespace(cs.charAt(i))) {
          return false;
        }
      }

      return true;
    } else {
      return true;
    }
  }
  
  /**
   * 压缩
   *
   * @param files 多文件
   * @param zipFilePath 目标压缩文件路径
   * @throws IOException
   */
  public static void zip(List<File> files, String zipFilePath) throws IOException {
    log.info("启用ZIP压缩工具 >>>>>>>>>> ");
    if(files == null || files.size() == 0) return;
    if(isBlank(zipFilePath)) return;
    File zipFile = createFile(zipFilePath);
    FileOutputStream fileOutputStream = null;
    ZipOutputStream zipOutputStream = null;
    FileInputStream inputStream = null;
    try {
      fileOutputStream = new FileOutputStream(zipFile);
      zipOutputStream = new ZipOutputStream(fileOutputStream);
      for(File file : files) {
        log.info("正在打包文件 -> " + file.getAbsolutePath());
        if(file.exists()) {
          zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
          inputStream = new FileInputStream(file);
          int len;
          while((len = inputStream.read(BUF)) > 0) {
            zipOutputStream.write(BUF, 0, len);
          }
          zipOutputStream.closeEntry();
        }
      }
      log.info("压缩完成 <<<<<<<<<< " + zipFile.getAbsolutePath());
    } finally {
      if(inputStream != null) {
        inputStream.close();
      }
      if(zipOutputStream != null) {
        zipOutputStream.close();
      }
      if(fileOutputStream != null) {
        fileOutputStream.close();
      }
    }
  }

  /**
   * 压缩（将文件夹打包）
   *
   * @param fileDir 文件夹目录路径
   * @param zipFilePath 目标压缩文件路径
   * @throws IOException
   */
  public static void zipByFolder(String fileDir, String zipFilePath) throws IOException {
    File folder = new File(fileDir);
    if(folder.exists() && folder.isDirectory()) {
      File[] files = folder.listFiles();
      List<File> filesList = Arrays.asList(files);
      zip(filesList, zipFilePath);
    }
  }

  /**
   * 解压
   *
   * @param zipFile 压缩文件
   * @param descDir 目标文件路径
   * @return
   * @throws IOException
   */
  public static List<File> unzip(File zipFile, String descDir) throws IOException {
    List<File> files = new ArrayList<>();
    if(!isBlank(descDir)) {
      log.info("启用ZIP解压工具 >>>>>>>>>> ");
      if(zipFile.exists() && zipFile.getName().endsWith(".zip")) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
          ZipFile zf = new ZipFile(zipFile);
          Enumeration entries = zf.entries();
          while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            String zipEntryName = zipEntry.getName();
            log.info("正在解压文件 -> " + zipEntryName);
            inputStream = zf.getInputStream(zipEntry);
            String descFilePath = descDir + File.separator + zipEntryName;
            File descFile = createFile(descFilePath);
            files.add(descFile);
            outputStream = new FileOutputStream(descFilePath);
            int len;
            while((len = inputStream.read(BUF)) > 0) {
              outputStream.write(BUF, 0, len);
            }
          }
          log.info("解压完成 <<<<<<<<<< " + descDir);
        } finally {
          if(null != inputStream) {
            inputStream.close();
          }
          if(null != outputStream) {
            outputStream.close();
          }
        }
      }
    }
    return files;
  }

  private static File createFile(String filePath) throws IOException {
    File file = new File(filePath);
    File parentFile = file.getParentFile();
    if(!parentFile.exists()) {
      parentFile.mkdirs();
    }
    if(!file.exists()) {
      file.createNewFile();
    }
    return file;
  }
}
