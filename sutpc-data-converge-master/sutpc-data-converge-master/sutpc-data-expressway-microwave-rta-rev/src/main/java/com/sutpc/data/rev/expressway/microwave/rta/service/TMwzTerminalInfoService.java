package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwzTerminalInfo;

public interface TMwzTerminalInfoService {
  Page<TMwzTerminalInfo> findAll(Integer pagenum, Integer pagesize);
}
