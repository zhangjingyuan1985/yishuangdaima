package com.sutpc.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.sutpc.demo.model.DemoModel;
import com.sutpc.demo.model.HttpResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/demo")
@Api(tags = "Demo接口测试")
public class DemoController {

    @PostMapping(value = "/successReq")
    @ApiOperation(value = "Demo请求成功接口", notes = "Demo请求成功接口")
    public HttpResult successReq(@RequestBody String request) throws Exception {
        DemoModel requestDto = JSONObject.parseObject(request, DemoModel.class);
        System.out.println(requestDto.toString());
        return HttpResult.ok(requestDto);
    }

    @PostMapping(value = "/errorReq")
    @ApiOperation(value = "Demo请求失败接口", notes = "Demo请求失败接口")
    public HttpResult errorReq(@RequestBody String request) throws Exception {
        DemoModel requestDto = JSONObject.parseObject(request, DemoModel.class);
        System.out.println(requestDto.toString());
        int i = 1 / 0;
        System.out.println(i);
        return HttpResult.ok(requestDto);
    }

}
