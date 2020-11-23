package com.sutpc.data.rev.phone.signaling.rta.service;

import com.sutpc.data.rev.phone.signaling.rta.entity.TzPfPdSaturation;
import java.util.List;

/**
 * .
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/19 10:02
 */
public interface TzPfPdSaturationService {


  List<TzPfPdSaturation> queryForCurrent();
}
