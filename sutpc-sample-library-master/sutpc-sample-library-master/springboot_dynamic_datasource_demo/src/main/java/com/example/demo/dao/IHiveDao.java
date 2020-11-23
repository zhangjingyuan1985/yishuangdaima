package com.example.demo.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;



@Mapper
public interface IHiveDao {
	
	
	List<Map<String,Object>> findObjects(Map<String, Object> map);
	
	List<Map<String,Object>> findDataBases();	

	List<Map<String,Object>> findTableDesc(Map<String, Object> map);
	
	int createTable(Map<String, Object> map);
	
	int batchInsert(Map<String, Object> map);
	
	int saveObject(String f);
}
