package com.sutpc.demo.controller;

import com.sutpc.demo.vo.JobVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @Description: .
 * @Author: HuYing
 * @Date: 2020/5/18 16:03
 * @Modified By:
 */
@Slf4j
@RestController
public class DemoController {
  
  @GetMapping("/demo")
  public String demo(JobVo bean){
    log.info("Job；"+bean);
    return "";
  }
  
  
}
