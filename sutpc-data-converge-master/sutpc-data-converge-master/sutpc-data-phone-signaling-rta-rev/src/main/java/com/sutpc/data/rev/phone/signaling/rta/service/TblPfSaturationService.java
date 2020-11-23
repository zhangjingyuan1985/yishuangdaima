package com.sutpc.data.rev.phone.signaling.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.phone.signaling.rta.entity.TblPfSaturation;

/**
 * .
 * @Auth smilesnake minyikun
 * @Create 2019/8/19 10:02
 */
public interface TblPfSaturationService {
  Page<TblPfSaturation> findAll(Integer pagenum, Integer pagesize);
}
