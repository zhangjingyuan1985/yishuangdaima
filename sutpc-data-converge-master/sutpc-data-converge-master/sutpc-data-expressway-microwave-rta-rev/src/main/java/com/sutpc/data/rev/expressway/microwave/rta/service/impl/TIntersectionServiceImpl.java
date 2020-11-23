package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TIntersection;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TIntersectionMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TIntersectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/14 13:38
 */

@Service
public class TIntersectionServiceImpl implements TIntersectionService {
  @Autowired
  private TIntersectionMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TIntersection> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TIntersection> page = mapper.findAll();
    return page;
  }
}
