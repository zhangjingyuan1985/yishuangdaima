package com.sutpc.bigdata.utils

object Spatial {
  private val EARTH_RADIUS = 6378.137 // 地球半径

  // 使用经纬度估算两点之间的距离(米)
  private[bigdata] val distance = (lon1: Float, lat1: Float, lon2: Float, lat2: Float) => {
    val (l1, l2) = (rad(lat1), rad(lat2))
    val a = l1 - l2
    val b = rad(lon1) - rad(lon2)

    val d = EARTH_RADIUS * 2 * math.asin(math.sqrt(math.pow(math.sin(a / 2), 2) +
      math.cos(l1) * math.cos(l2) * math.pow(math.sin(b / 2), 2)))
    math.round(d * 10000) / 10
  }

  // 弧度到角度(PI=180°)
  private def rad(d: Float): Float = d * math.Pi.toFloat / 180
}
