package com.sutpc.data.aviation.rta.utilize.dto.mapper;

import com.sutpc.data.aviation.rta.utilize.dto.PlainDynamicDto;
import com.sutpc.data.aviation.rta.utilize.entity.FlightInfo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 *.
 * @Auth smilesnake minyikun
 * @Create 2019/12/30 14:30 
 */
@Mapper(componentModel = "spring")
public interface PlainDynamicDtoMapper {

  /**
   *.
   */
  @Mappings({
      @Mapping(source = "fltno", target = "flightno"),
      @Mapping(source = "deptime", target = "deptime"),
      @Mapping(source = "arrtime", target = "arrtime"),
      @Mapping(source = "flightState", target = "fstatus"),
      @Mapping(source = "fromCity", target = "fromcity"),
      @Mapping(source = "endCity", target = "tocity")
  })
  FlightInfo dtoToFlightInfo(PlainDynamicDto plainDynamicDto);

  List<FlightInfo> dtoToFlightInfo(List<PlainDynamicDto> plainDynamicDto);
}
