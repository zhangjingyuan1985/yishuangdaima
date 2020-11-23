package com.sutpc.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HtmlController {


    @GetMapping(value = "/error")
    public String error() {

        //失败返回页面
        return "error";
    }

    @GetMapping(value = "/success")
    public String success() {

        //成功返回页面
        return "success";
    }


}
