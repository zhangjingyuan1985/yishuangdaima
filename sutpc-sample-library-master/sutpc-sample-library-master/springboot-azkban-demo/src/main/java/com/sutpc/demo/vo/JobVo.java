package com.sutpc.demo.vo;

import lombok.Data;
import lombok.ToString;

/**
 * .
 *
 * @Description: .
 * @Author: HuYing
 * @Date: 2020/5/18 16:04
 * @Modified By:
 */
@Data
@ToString
public class JobVo {
  
  private String msg;
  private String server;
  private String project;
  private String flow;
  private String executionId;
  private String job;
  private String status;
  
  
}
