package com.sutpc.bigdata.schema.hive

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/8/22  15:27
  */
case class T_TRANSIT_PEOPLE_IC(
                                card_id: String,
                                trans_type: Int,
                                trans_time: Long,
                                fdate: Int,
                                ftime: String,
                                trans_value: Int,
                                dev_no: String,
                                company_line: String,
                                line_station: String,
                                vehicle_id: String,
                                encry_id: String,
                                city: Int,
                                create_date: Int,
                                year: String,
                                month: String,
                                day: String
                              )


case class TAG_IC(
                   card_id: String,
                   trans_type: Int,
                   trans_time: Long,
                   fdate: Int,
                   ftime: String,
                   trans_value: Int,
                   dev_no: String,
                   company_line: String,
                   line_station: String,
                   vehicle_id: String,
                   encry_id: String,
                   city: Int,
                   create_date: Int,
                   year: String,
                   month: String,
                   day: String,
                   license_prefix: String
                 )

case class TAG_GPS_TAXI(
                         loc_time: Long,
                         fdate: Int,
                         ftime: String,
                         vehicle_id: String,
                         encry_id: String,
                         lng: BigDecimal,
                         lat: BigDecimal,
                         speed: BigDecimal,
                         angle: Int,
                         operation_status: Int,
                         company_code: String,
                         altitude: Int,
                         vehicle_color: Integer,
                         total_mileage: Integer,
                         city: Int,
                         year: String,
                         month: String,
                         day: String,
                         license_prefix: String
                       )


case class TAG_GPS_BUS(
                        loc_time: Long,
                        fdate: Int,
                        ftime: String,
                        vehicle_id: String,
                        encry_id: String,
                        lng: Double,
                        lat: Double,
                        speed: Double,
                        angle: Int,
                        company_code: String,
                        busline_name: String,
                        stop_announce: Integer,
                        busline_dir: Integer,
                        bus_station_order: Integer,
                        city: Int,
                        year: String,
                        month: String,
                        day: String,
                        license_prefix: String
                      )

case class TAG_WEATHER_GRID(
                             grid_fid: String,
                             period: Int,
                             t: Double,
                             slp: Double,
                             wspd: Double,
                             wdir: Double,
                             rhsfc: Double,
                             rain01m: Double,
                             rain06m: Double,
                             rain12m: Double,
                             rain30m: Double,
                             rain01h: Double,
                             rain02h: Double,
                             rain03h: Double,
                             rain06h: Double,
                             rain12h: Double,
                             rain24h: Double,
                             rain48h: Double,
                             rain72h: Double,
                             v: Double,
                             city: Int,
                             fdate: Int,
                             year: String,
                             month: String,
                             day: String
                           )

case class TAG_SECTION_FLOW(
                             fdate: Int,
                             period: Int,
                             link_fid: Int,
                             from_node: Int,
                             to_node: Int,
                             detect_volume: Int,
                             speed: Double,
                             statistic_lane_num: Int,
                             volume_factor: Int,
                             modified_volume: Int,
                             big_vehicle: Integer,
                             mid_vehicle: Integer,
                             small_vehicle: Integer,
                             mini_vehicle: Integer,
                             city: Int,
                             year: String,
                             month: String,
                             day: String
                           )

case class TAG_PHONE_HOME_DIST(
                                grid_id: String,
                                user_type: Int,
                                age: Integer,
                                sex: Integer,
                                adm_reg: Integer,
                                has_auto: Integer,
                                user_qty: Integer,
                                fdate: Int,
                                create_time: Int,
                                city: Int,
                                source: String,
                                year: String,
                                month: String,
                                day: String
                              )

case class TAG_PHONE_WORK_DIST(
                                grid_id: String,
                                age: Integer,
                                sex: Integer,
                                adm_reg: Integer,
                                has_auto: Integer,
                                work_qty: Integer,
                                fdate: Int,
                                create_time: Int,
                                city: Int,
                                source: String,

                                year: String,
                                month: String,
                                day: String

                              )

case class TAG_PHONE_HOME_WORK_DIST(
                                     home_grid_id: String,
                                     work_grid_id: String,
                                     age: Integer,
                                     sex: Integer,
                                     adm_reg: Integer,
                                     has_auto: Integer,
                                     user_qty: Integer,
                                     fdate: Int,
                                     create_time: Int,
                                     home_city: Int,
                                     work_city: Int,
                                     source: String,

                                     year: String,
                                     month: String,
                                     day: String
                                   )

case class TAG_PHONE_TRIP_DIST(
                                start_grid_id: String,
                                end_grid_id: String,
                                fdate: Int,
                                start_city: Int,
                                end_city: Int,
                                start_ptype: Int,
                                end_ptype: Int,
                                start_utype: Int,
                                end_utype: Int,
                                age: Integer,
                                sex: Integer,
                                adm_reg: Integer,
                                start_time: Int,
                                end_time: Int,
                                trip_mode: Int,
                                aggtype: Int,
                                trip_qty: Int,
                                create_time: Int,
                                source: String,
                                time_dim: Int,
                                year: String,
                                month: String,
                                day: String
                              )

case class TAG_PHONE_ACTIVITY(
                               grid_id: String,
                               user_type: Int,
                               act_time: Int,
                               time_dim: Int,
                               fdate: Int,
                               age: Int,
                               sex: Int,
                               user_qty: Int,
                               adm_reg: Int,
                               create_time: Int,
                               city: Int,
                               source: String,
                               year: String,
                               month: String,
                               day: String
                             )

case class TAG_GPS_Truck(
                          loc_time: Long,
                          fdate: Int,
                          ftime: String,
                          vehicle_id: String,
                          encry_id: String,
                          lng: BigDecimal,
                          lat: BigDecimal,
                          speed: BigDecimal,
                          angle: Int,
                          company_code: String,
                          altitude: Int,
                          vehicle_color: Integer,
                          total_mileage: Long,
                          city: Int,
                          year: String,
                          month: String,
                          day: String,
                          license_prefix: String
                        )

case class TAG_GPS_ORDER(
                          loc_time: Long,
                          vehicle_id: String,
                          encry_id: String,
                          lng: BigDecimal,
                          lat: BigDecimal,
                          speed: BigDecimal,
                          angle: Int,
                          operation_status: Int,
                          position_type: String,
                          order_id: String,
                          altitude: Int,
                          company_code: String,
                          city: Int,
                          city_name: String,
                          year: String,
                          month: String,
                          day: String,
                          license_prefix: String
                        )

