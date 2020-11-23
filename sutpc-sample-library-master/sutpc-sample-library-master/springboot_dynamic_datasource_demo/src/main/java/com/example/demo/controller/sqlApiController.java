package com.example.demo.controller;

import com.example.demo.service.SqlApiService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 19:00$ 20200704$.
 * @Description
 * @Modified By:
 */
@RestController
@RequestMapping(value = "sql")
public class sqlApiController {

  @Autowired
  private SqlApiService sqlApiService;

  @GetMapping(value = "/build")
  public void findObjects(@RequestParam Map<String, Object> map) {
    sqlApiService.buildTable();
  }
}
