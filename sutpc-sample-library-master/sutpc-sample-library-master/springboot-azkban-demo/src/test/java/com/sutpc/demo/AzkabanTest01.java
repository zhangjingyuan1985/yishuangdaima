package com.sutpc.demo;

import cn.hutool.Hutool;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.ho.yaml.Yaml;
import org.ho.yaml.YamlConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;


public class AzkabanTest01 {
  private static final String API = "http://10.10.201.64:8081";

  private static String SESSION_ID = "c79f3f8c-298e-48e7-bb12-0c0ad716069a";

  private static final String PROJECT = "test";

  private static final String PROJECT_ID = "12";

  private static final String SCHEDULE_ID = "13";

  private static RestTemplate restTemplate = null;

  static {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(2000);
    requestFactory.setReadTimeout(2000);
    restTemplate = new RestTemplate(requestFactory);
  }

  public static void main(String[] args) throws Exception {
    String project = "test529";
    loginTest(); // 登录
//    executeFlow(project,"basic",null);
   createProTest(project,"testDesc"); // 创建Project
    // deleteProTest(); // 删除Project
//     uploadZip(project); // 上传zip
    // scheduleEXEaFlowTest(); // 创建定时任务
    // scheduleByCronEXEaFlowTest(); // 创建定时任务cron
    // unscheduleFlowTest(); // 取消定时任务
    String zip = packageZip(project,project);
    uploadZip(project,zip);
    executeFlow(project,project,null);
  }

  /**
   * 登录测试 登录调度系统
   */
  public static void loginTest() throws Exception {
    SSLUtil.turnOffSslChecking();
    HttpHeaders hs = new HttpHeaders();
    hs.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    hs.add("X-Requested-With", "XMLHttpRequest");
    LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
    linkedMultiValueMap.add("action", "login");
    linkedMultiValueMap.add("username", "azkaban");
    linkedMultiValueMap.add("password", "Sutpc123");
    
    HttpEntity<LinkedMultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
    String postForObject = restTemplate.postForObject(API, httpEntity, String.class);
    System.out.println(postForObject);
    JSONObject json = JSON.parseObject(postForObject);
    if(null==json || !"success".equals(json.getString("status"))) return;
    SESSION_ID = json.getString("session.id");
  }

  /**
   * 创建任务测试 创建一个project
   */
  public static void createProTest(String project,String projectDesc) throws Exception {
    SSLUtil.turnOffSslChecking();
    HttpHeaders hs = new HttpHeaders();
    hs.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    hs.add("X-Requested-With", "XMLHttpRequest");
    LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
    linkedMultiValueMap.add("session.id", SESSION_ID);
    linkedMultiValueMap.add("action", "create");
    linkedMultiValueMap.add("name", project);
    linkedMultiValueMap.add("description", projectDesc);

    HttpEntity<LinkedMultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
    String postForObject = restTemplate.postForObject(API + "/manager", httpEntity, String.class);
    System.out.println(postForObject);
    JSONObject json = JSON.parseObject(postForObject);
    if(null==json || !"success".equals(json.getString("status"))) {
      System.out.println(json.getString("message"));
      return;
    }
  }

  /**
   * 删除任务测试 删除一个project
   */
  public static void deleteProTest(String project) throws Exception {

    SSLUtil.turnOffSslChecking();

    HttpHeaders hs = new HttpHeaders();
    hs.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    hs.add("X-Requested-With", "XMLHttpRequest");
    hs.add("Accept", "text/plain;charset=utf-8");

    Map<String, String> map = new HashMap<>();

    map.put("id", SESSION_ID);
    map.put("project", project);

    ResponseEntity<String> exchange = restTemplate.exchange(API + "/manager?session.id={id}&delete=true&project={project}", HttpMethod.GET, new HttpEntity<String>(hs), String.class, map);

    System.out.println(exchange.getBody());
    System.out.println(exchange.getStatusCode());
    System.out.println(exchange.getStatusCodeValue());
  }

  public static String packageZip(String project,String flow) throws Exception{
    File dir = new File("E:\\test\\azkaban\\test05");
    File projectFile = buildProjectFile(dir,project);
    File flowFile = buildFlowFile(dir,flow);
   // String fileName = "E:\\test\\azkaban\\test04\\test04.zip";
    File fileName=ZipUtil.zip(dir);
    return fileName.getAbsolutePath();
  }
  
  public static File buildProjectFile(File dir,String project)throws IOException {
    File projectFile = new File(dir,project+".project");
    if(projectFile.exists()){
      projectFile.delete();
    }
    projectFile.createNewFile();
    
    FileUtils.write(projectFile,"azkaban-flow-version: 2.0", Charset.forName("UTF-8"));
    return projectFile; 
  }

  public static File buildFlowFile(File dir,String flow)throws IOException {
    File flowFile = new File(dir,flow+".flow");
    if(flowFile.exists()){
      flowFile.delete();
    }
    flowFile.createNewFile();
    YamlConfig yamlConfig = YamlConfig.getDefaultConfig();
    yamlConfig.setMinimalOutput(true);
    AzFlow az = new AzFlow();
    Map<String,String> config = new HashMap<>();
    config.put("param1","xxxxxxxxx");
    az.setConfig(config);
    List<AzJob> nodes = new ArrayList<>();
    
    String python = "python3 main.py --params \"{'input': {'1': {'db_id': 1,'table_name':'t_bus_passenger_subtrip','where':{'city':'440300','date':'2019-09-05'}}},'output': {'1': {'db_id': 99, 'table_name': 't_bus_expand_od_volume_test'}}}\"";
    
    nodes.add(buildAzJob("init","echo \"start!\"",null));
    nodes.add(buildAzJob("bus_stop_od",python, Arrays.asList("init")));
    //nodes.add(buildAzJob("shell_3","echo \"shell3!\"",Arrays.asList("start_1")));
    nodes.add(buildAzJob("finish","echo \"finish!\"",Arrays.asList("bus_stop_od")));
    az.setNodes(nodes.toArray(new AzJob[nodes.size()]));

    Yaml.dump(az,flowFile,true);
    return flowFile;
  }
  
  private static AzJob buildAzJob(String name,String command,List<String> deps){
    AzJob job = new AzJob();
    job.setName(name);
    job.setType("command");
    AzJobConfig azJobConfig = new AzJobConfig();
    azJobConfig.setCommand(command);
    job.setConfig(azJobConfig);
    job.setDependsOn(deps);
    return job;
  }
  
  /**
   * 上传zip 上传依赖文件 zip包
   */
  public static void uploadZip(String project,String filePath) throws Exception {
    SSLUtil.turnOffSslChecking();
    FileSystemResource resource = new FileSystemResource(new File(filePath));
    LinkedMultiValueMap<String, Object> linkedMultiValueMap = new LinkedMultiValueMap<String, Object>();
    linkedMultiValueMap.add("session.id", SESSION_ID);
    linkedMultiValueMap.add("ajax", "upload");
    linkedMultiValueMap.add("project", project);
    linkedMultiValueMap.add("file", resource);
    String postForObject = restTemplate.postForObject(API + "/manager", linkedMultiValueMap, String.class);
    System.out.println(postForObject);
  }

  
  public static void executeFlow(String project,String flow,String disabled)throws KeyManagementException, NoSuchAlgorithmException {
    SSLUtil.turnOffSslChecking();
    HttpHeaders hs = new HttpHeaders();
    hs.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    hs.add("X-Requested-With", "XMLHttpRequest");
    LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
    linkedMultiValueMap.add("session.id", SESSION_ID);
    linkedMultiValueMap.add("ajax", "executeFlow");
    linkedMultiValueMap.add("project", project);
    linkedMultiValueMap.add("flow", flow);
    if(null!=disabled){
      linkedMultiValueMap.add("disabled", disabled);  
    }
    String res = restTemplate.postForObject(API+"/executor",linkedMultiValueMap,String.class);
    System.out.println(res);
    JSONObject json = JSON.parseObject(res);
    int execid = json.getInteger("execid");
    
  }
  
  /**
   * Schedule a period-based Flow 根据时间 创建调度任务
   */
  public static void scheduleEXEaFlowTest() throws KeyManagementException, NoSuchAlgorithmException {
    SSLUtil.turnOffSslChecking();
    HttpHeaders hs = new HttpHeaders();
    hs.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    hs.add("X-Requested-With", "XMLHttpRequest");
    LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
    linkedMultiValueMap.add("session.id", SESSION_ID);
    linkedMultiValueMap.add("ajax", "scheduleFlow");
    linkedMultiValueMap.add("projectName", PROJECT);
    linkedMultiValueMap.add("projectId", PROJECT_ID);

    linkedMultiValueMap.add("flow", "2");
    // linkedMultiValueMap.add("scheduleTime", "10,28,am,EDT");
    linkedMultiValueMap.add("scheduleTime", "15,08,pm,PDT");
    linkedMultiValueMap.add("scheduleDate", "12/1/2017");
    linkedMultiValueMap.add("flowName", "test01 description");

    // 是否循环
    linkedMultiValueMap.add("is_recurring", "on");

    // 循环周期 天 年 月等
    // M Months
    // w Weeks
    // d Days
    // h Hours
    // m Minutes
    // s Seconds
    linkedMultiValueMap.add("period", "30s"); // 经测试，定时任务支持至少是60秒或其整数倍

    HttpEntity<LinkedMultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
    String postForObject = restTemplate.postForObject(API + "/schedule", httpEntity, String.class);
    System.out.println(postForObject);
  }

  /**
   * Flexible scheduling using Cron 通过cron表达式调度执行 创建调度任务
   */
  public static void scheduleByCronEXEaFlowTest() throws KeyManagementException, NoSuchAlgorithmException {
    SSLUtil.turnOffSslChecking();
    HttpHeaders hs = new HttpHeaders();
    hs.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    hs.add("X-Requested-With", "XMLHttpRequest");
    LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
    linkedMultiValueMap.add("session.id", SESSION_ID);
    linkedMultiValueMap.add("ajax", "scheduleCronFlow");
    linkedMultiValueMap.add("projectName", PROJECT);
    linkedMultiValueMap.add("cronExpression", "* */1 * * * ?");
    linkedMultiValueMap.add("flow", "中文");
    linkedMultiValueMap.add("flowName", "dsaf");

    HttpEntity<LinkedMultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
    String postForObject = restTemplate.postForObject(API + "/schedule", httpEntity, String.class);
    System.out.println(postForObject);
  }

  /**
   * Unschedule a Flow 取消一个流的调度
   */
  public static void unscheduleFlowTest() throws KeyManagementException, NoSuchAlgorithmException {
    SSLUtil.turnOffSslChecking();
    HttpHeaders hs = new HttpHeaders();
    hs.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    hs.add("X-Requested-With", "XMLHttpRequest");
    LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
    linkedMultiValueMap.add("session.id", SESSION_ID);
    linkedMultiValueMap.add("action", "removeSched");
    linkedMultiValueMap.add("scheduleId", SCHEDULE_ID);

    HttpEntity<LinkedMultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
    String postForObject = restTemplate.postForObject(API + "/schedule", httpEntity, String.class);
    System.out.println(postForObject);
  }
}
