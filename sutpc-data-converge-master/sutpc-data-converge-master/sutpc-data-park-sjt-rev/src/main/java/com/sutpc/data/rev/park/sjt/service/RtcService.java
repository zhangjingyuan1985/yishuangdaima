package com.sutpc.data.rev.park.sjt.service;

import com.sutpc.data.rev.park.sjt.entity.AreaRes;
import com.sutpc.data.rev.park.sjt.entity.BerthRes;
import com.sutpc.data.rev.park.sjt.entity.CantonRes;
import com.sutpc.data.rev.park.sjt.entity.ConstantlyRes;
import com.sutpc.data.rev.park.sjt.entity.PriceRes;
import com.sutpc.data.rev.park.sjt.entity.SectionRes;
import java.util.List;

/**
 * 接口.
 *
 * @author admin
 * @date 2020/6/22 14:12
 */
public interface RtcService {

  List<CantonRes> getCantonList();

  List<AreaRes> getAreaList(String cantonId);

  List<SectionRes> getSectionList(String areaId);

  List<BerthRes> getBerthList(int pageIndex, int pageSize);

  PriceRes getRtcPrice(String berthCode);

  List<ConstantlyRes> getConstantlyBerth();

}
