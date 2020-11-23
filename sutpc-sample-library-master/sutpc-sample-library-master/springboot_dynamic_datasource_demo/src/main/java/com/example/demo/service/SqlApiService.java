package com.example.demo.service;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.demo.dao.ISqlApiDao;
import com.example.demo.model.SqlApiEntity;
import com.example.demo.util.TableNameParser;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 20:03$ 20200704$.
 * @Description
 * @Modified By:
 */
@Service
@Slf4j
public class SqlApiService {

  @Autowired
  private ISqlApiDao sqlApiDao;


  public void buildTable() {
    List<SqlApiEntity> sqlApiEntityList = sqlApiDao.findObjects();
    List<SqlApiEntity> exportList = new ArrayList<>();
    for (SqlApiEntity sqlApiEntity : sqlApiEntityList) {
      if (!StringUtils.isEmpty(sqlApiEntity.getContent()) &&
          sqlApiEntity.getContent().contains("select ")) {
        try {
          Collection<String> tables = new TableNameParser(sqlApiEntity.getContent()).tables();
          String result = Joiner.on(",").join(tables);
          sqlApiEntity.setContent(result);
          exportList.add(sqlApiEntity);
        } catch (Exception ex) {
          continue;
        }
      }
    }
    ExcelWriter writer = ExcelUtil.getWriter("D:\\table.xlsx");
    writer.addHeaderAlias("title", "接口名称");
    writer.addHeaderAlias("endpoint", "方法名");
    writer.addHeaderAlias("content", "表名");
    writer.write(exportList, true);
    writer.close();
  }
}
