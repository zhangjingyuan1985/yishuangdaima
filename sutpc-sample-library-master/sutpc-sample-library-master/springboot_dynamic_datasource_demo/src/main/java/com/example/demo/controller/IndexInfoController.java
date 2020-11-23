package com.example.demo.controller;

import com.example.demo.model.HttpResult;
import com.example.demo.service.IndexInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 08:41$ 20200424$.
 * @Description
 * @Modified By:
 */
@RestController
@RequestMapping(value = "/index")
public class IndexInfoController {

  @Autowired
  private IndexInfoService indexInfoService;


  @PostMapping(value = "/findObjects")
  public HttpResult findObjects() {
    //统一结果返回
    return HttpResult.ok(indexInfoService.findObjects());
  }
}
