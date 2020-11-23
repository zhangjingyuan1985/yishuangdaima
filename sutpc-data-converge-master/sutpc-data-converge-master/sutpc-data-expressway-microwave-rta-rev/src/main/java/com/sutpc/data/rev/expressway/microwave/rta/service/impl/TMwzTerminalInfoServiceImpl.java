package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwzTerminalInfo;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TMwzTerminalInfoMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TMwzTerminalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 11:00
 */
@Service
public class TMwzTerminalInfoServiceImpl implements TMwzTerminalInfoService {
  @Autowired
  private TMwzTerminalInfoMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TMwzTerminalInfo> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TMwzTerminalInfo> page = mapper.findAll();
    return page;
  }
}
