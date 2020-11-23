package com.sutpc.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 22:29$ 20200404$.
 * @Description
 * @Modified By:
 */
public class PythonTest {


  public static void main(String[] args) throws IOException, InterruptedException {
    // TODO Auto-generated method stub
    int month = 5;
    Process proc;
    String lastLine = null;
    try {
      //String[] strings = {"D:\\python3\\python.exe", "E:\\test\\azkaban\\test04\\main.py","24234234"};
      //String[] strings2 = {"D:\\python3\\python.exe", "D:\\add.py"};
      // 执行py文件\\
      String python = "D:\\python3\\python.exe E:\\test\\azkaban\\test04\\main.py --params \"{'input': {'1': {'db_id': 1,'table_name':'t_bus_passenger_subtrip','where':{'city':'440300','date':'2019-09-05'}}},'output': {'1': {'db_id': 99, 'table_name': 't_bus_expand_od_volume_test'}}}\"";
      //String python = "D:\\python3\\python.exe D:\\simple_kernel.py --params print(\"Hello, Python!\")";
      //String[] python = {"D:\\python3\\python.exe", "D:\\simple_kernel.py",
      //    "print(\"Hello, Python!\")"};
      System.out.println(python);
      proc = Runtime.getRuntime().exec(python);
      //用输入输出流来截取结果
      BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      String content = null;
      while (true) {
        lastLine = content;
        if ((content = in.readLine()) == null) {
          System.out.println(lastLine);
          break;
        }
      }
      in.close();
      int re = proc.waitFor();
      System.out.println(re);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
