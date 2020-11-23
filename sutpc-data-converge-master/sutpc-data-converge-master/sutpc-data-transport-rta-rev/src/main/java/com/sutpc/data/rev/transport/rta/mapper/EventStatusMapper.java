package com.sutpc.data.rev.transport.rta.mapper;

import com.sutpc.data.rev.transport.rta.entity.EventStatus;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 剩余车位仓库.
 *
 * @author admin
 * @date 2020/6/23 9:02
 */
@Repository
@Mapper
public interface EventStatusMapper {

  int insert(@Param("statuses") List<EventStatus> statuses);

}
