package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TIntersection;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/14 13:37
 */
public interface TIntersectionService {
  Page<TIntersection> findAll(Integer pagenum, Integer pagesize);
}
