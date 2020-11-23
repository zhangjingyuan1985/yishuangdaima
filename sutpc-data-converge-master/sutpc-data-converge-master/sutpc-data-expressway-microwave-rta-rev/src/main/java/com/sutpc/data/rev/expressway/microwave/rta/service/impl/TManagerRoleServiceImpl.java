package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TManagerRole;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TManagerRoleMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TManagerRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 10:58
 */
@Service
public class TManagerRoleServiceImpl implements TManagerRoleService {
  @Autowired
  private TManagerRoleMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TManagerRole> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TManagerRole> page = mapper.findAll();
    return page;
  }
}
