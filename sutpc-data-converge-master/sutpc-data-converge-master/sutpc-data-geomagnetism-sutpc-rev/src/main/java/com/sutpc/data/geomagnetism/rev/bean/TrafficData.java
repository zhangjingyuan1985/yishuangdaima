package com.sutpc.data.geomagnetism.rev.bean;

import static com.sutpc.data.geomagnetism.rev.constant.Constant.TRAFFIC_DATA_BYTE_LENGTH;

import com.alibaba.fastjson.JSON;
import com.sutpc.data.geomagnetism.rev.util.DataTransferUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Description:定时交通数据结构.
 *
 * @author zhy
 * @date 2019/7/31
 */
@Data
@ApiModel("定时交通数据结构")
public class TrafficData {

  @ApiModelProperty("流水号")
  private Long recordNo;
  @ApiModelProperty("上报时间")
  private Long sendTime;
  @ApiModelProperty("一分钟内通过的车辆")
  private Long vehicleCount;
  @ApiModelProperty("一分钟内通过的大车车辆数")
  private Long largerVehicleCount;
  @ApiModelProperty("中型车")
  private Long midVehicleCount;
  @ApiModelProperty("小型车")
  private Long smallVehicleCount;
  @ApiModelProperty("微型车")
  private Long miniVehicleCount;
  @ApiModelProperty("摩托车")
  private Long moto;
  @ApiModelProperty("预留车型1")
  private Long reserverType1;
  @ApiModelProperty("预留车型2")
  private Long reserverType2;
  @ApiModelProperty("预留车型3")
  private Long reserverType3;
  @ApiModelProperty("预留车型4")
  private Long reserverType4;
  @ApiModelProperty("预留车型5")
  private Long reserverType5;
  @ApiModelProperty("预留车型6")
  private Long reserverType6;
  @ApiModelProperty("最大车头时距")
  private Long maxHeadLongerval;
  @ApiModelProperty("最小车头时距")
  private Long minHeadLongerval;
  @ApiModelProperty("累计车头时距")
  private Long accumulateHeadLongerval;
  @ApiModelProperty("最大车速")
  private Double maxVelocity;
  @ApiModelProperty("最小车速")
  private Double minVelocity;
  @ApiModelProperty("累计速度")
  private Double accumulateVelocity;
  @ApiModelProperty("最大占有时间")
  private Long maxOccupancy;
  @ApiModelProperty("最小占有时间")
  private Long minOccupancy;
  @ApiModelProperty("累计占有时间")
  private Long accumulateOccupancy;
  @ApiModelProperty("最大间隔时间")
  private Long maxLongerval;
  @ApiModelProperty("最小间隔时间")
  private Long minLongerval;
  @ApiModelProperty("累计间隔时间")
  private Long accumulateLongerval;
  @ApiModelProperty("最大车长")
  private Double maxVehicleLength;
  @ApiModelProperty("最小车长")
  private Double minVehicleLength;
  @ApiModelProperty("累计车长")
  private Double accumulateVehicleLength;
  @ApiModelProperty("最大排队长度")
  private Long maxQueueLength;
  @ApiModelProperty("最小排队长度")
  private Long minQueueLength;
  @ApiModelProperty("累计排队长度")
  private Long accumulateQueueLength;
  @ApiModelProperty("排队时间累计(秒)")
  private Long accumulateQueueTime;
  @ApiModelProperty("闯红灯触发次数")
  private Long runRedLightCount;
  @ApiModelProperty("前方车道满累计时间")
  private Long precedingWayFullTime;
  @ApiModelProperty("车道代码")
  private String drivewayCode;
  @ApiModelProperty("状态：0-未启用，1-启用")
  private int status;
  @ApiModelProperty("状态：0-未启用，1-启用")
  private String nullValue;

  /**
   * byte数组数据解析成定时交通数据列表.
   *
   * @param data 上报数据
   * @return 定时交通数据列表
   */
  public static List<TrafficData> getListFromByteArray(byte[] data) {
    List<TrafficData> trafficDataList = new ArrayList<>();
    int index = 1;
    // 还足够解析出一帧数据
    byte[] dataList = new byte[data.length - 1];
    System.arraycopy(data, 1, dataList, 0, dataList.length);
    while (dataList.length >= index * TRAFFIC_DATA_BYTE_LENGTH) {
      byte[] bytes = new byte[TRAFFIC_DATA_BYTE_LENGTH];
      System.arraycopy(dataList, (index - 1) * TRAFFIC_DATA_BYTE_LENGTH, bytes, 0, bytes.length);
      TrafficData trafficData = getFromByteArray(bytes);
      if (trafficData != null) {
        trafficDataList.add(trafficData);
      }
      index++;
    }
    return trafficDataList;
  }

  private static TrafficData getFromByteArray(byte[] bytes) {
    if (bytes.length < TRAFFIC_DATA_BYTE_LENGTH) {
      return null;
    } else {
      try {
        TrafficData trafficData = new TrafficData();
        trafficData.setRecordNo(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 0, 4)));
        trafficData.setSendTime(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 4, 4)));
        //车辆数量
        trafficData.setVehicleCount(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 8, 2)));
        trafficData
            .setLargerVehicleCount(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 10, 2)));
        trafficData.setMidVehicleCount(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 12, 2)));
        trafficData
            .setSmallVehicleCount(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 14, 2)));
        trafficData
            .setMiniVehicleCount(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 16, 2)));
        trafficData.setMoto(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 18, 2)));
        //预留字段
        trafficData.setReserverType1(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 20, 2)));
        trafficData.setReserverType2(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 22, 2)));
        trafficData.setReserverType3(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 24, 2)));
        trafficData.setReserverType4(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 26, 2)));
        trafficData.setReserverType5(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 28, 2)));
        trafficData.setReserverType6(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 30, 2)));
        //车头时距
        trafficData
            .setMaxHeadLongerval(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 32, 4)));
        trafficData
            .setMinHeadLongerval(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 36, 4)));
        trafficData
            .setAccumulateHeadLongerval(
                DataTransferUtil.byteArrayToLongLe(intercept(bytes, 40, 4)));
        //车速
        trafficData.setMaxVelocity(DataTransferUtil.byteArrayToDoubleLe(intercept(bytes, 44, 4)));
        trafficData.setMinVelocity(DataTransferUtil.byteArrayToDoubleLe(intercept(bytes, 48, 4)));
        trafficData
            .setAccumulateVelocity(DataTransferUtil.byteArrayToDoubleLe(intercept(bytes, 52, 4)));
        //占有时间
        trafficData.setMaxOccupancy(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 56, 4)));
        trafficData.setMinOccupancy(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 60, 4)));
        trafficData
            .setAccumulateOccupancy(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 64, 4)));
        //间隔时间
        trafficData.setMaxLongerval(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 68, 4)));
        trafficData.setMinLongerval(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 72, 4)));
        trafficData
            .setAccumulateLongerval(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 76, 4)));
        //车长
        trafficData
            .setMaxVehicleLength(DataTransferUtil.byteArrayToDoubleLe(intercept(bytes, 80, 4)));
        trafficData
            .setMinVehicleLength(DataTransferUtil.byteArrayToDoubleLe(intercept(bytes, 84, 4)));
        trafficData
            .setAccumulateVehicleLength(
                DataTransferUtil.byteArrayToDoubleLe(intercept(bytes, 88, 4)));
        //排队长度
        trafficData.setMaxQueueLength(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 92, 2)));
        trafficData.setMinQueueLength(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 94, 2)));
        trafficData
            .setAccumulateQueueLength(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 96, 4)));
        //排队时间累计（秒）
        trafficData
            .setAccumulateQueueTime(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 100, 2)));
        //闯红灯触发次数
        trafficData
            .setRunRedLightCount(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 102, 2)));
        //前方车道满累计时间
        trafficData
            .setPrecedingWayFullTime(DataTransferUtil.byteArrayToLongLe(intercept(bytes, 104, 2)));
        //车道代码
        trafficData.setDrivewayCode(new String(intercept(bytes, 106, 3)));
        //状态：0-未启用，1-启用
        byte[] bytes2 = new byte[1];
        bytes2[0] = bytes[109];
        trafficData.setStatus(DataTransferUtil.byteArrayToInt(bytes2));
        trafficData.setNullValue(new String(intercept(bytes, 110, 2)));
        System.out.println(JSON.toJSONString(trafficData));
        return trafficData;
      } catch (Exception e) {
        return null;
      }
    }
  }

  private static byte[] intercept(byte[] src, int start, int length) {
    byte[] dest = new byte[length];
    System.arraycopy(src, start, dest, 0, length);
    return dest;
  }

}
