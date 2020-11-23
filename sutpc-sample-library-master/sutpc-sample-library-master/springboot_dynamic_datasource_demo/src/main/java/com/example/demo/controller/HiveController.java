package com.example.demo.controller;

import com.example.demo.service.HiveService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/hive")
public class HiveController {
	
	@Autowired
	private HiveService hiveService;
	
	@RequestMapping(value = "/findObjects")
	public Object findObjects(@RequestParam Map<String, Object> map){
		
		return hiveService.findObjects(map);
	}
	
	@RequestMapping(value = "/findDataBases")
	public Object findDataBases(@RequestParam Map<String, Object> map){
		
		return hiveService.findDataBases(map);
	}

}
