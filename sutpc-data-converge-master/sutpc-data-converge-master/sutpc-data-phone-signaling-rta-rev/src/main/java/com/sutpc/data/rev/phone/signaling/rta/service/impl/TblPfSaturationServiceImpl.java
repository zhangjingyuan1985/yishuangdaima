package com.sutpc.data.rev.phone.signaling.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.phone.signaling.rta.entity.TblPfSaturation;
import com.sutpc.data.rev.phone.signaling.rta.mapper.TblPfSaturationMapper;
import com.sutpc.data.rev.phone.signaling.rta.service.TblPfSaturationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/19 10:03
 */
@Service
public class TblPfSaturationServiceImpl implements TblPfSaturationService {
  @Autowired
  private TblPfSaturationMapper mapper;

  @Override
  public Page<TblPfSaturation> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TblPfSaturation> page = mapper.findAll();
    return page;
  }
}
