package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwzTerminalInfo;

public interface TMwzTerminalInfoMapper {
  Page<TMwzTerminalInfo> findAll();

  int deleteByPrimaryKey(Long terminalid);

  int insert(TMwzTerminalInfo record);

  int insertSelective(TMwzTerminalInfo record);

  TMwzTerminalInfo selectByPrimaryKey(Long terminalid);

  int updateByPrimaryKeySelective(TMwzTerminalInfo record);

  int updateByPrimaryKey(TMwzTerminalInfo record);
}