package com.sutpc.data.util;

import com.sutpc.framework.utils.system.PropertyUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retorfit工具类.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/1 15:04
 */
public class RetorfitUtils {
  public static <T> T createApi(Class<T> clazz) {
    String baseUrl = PropertyUtils.getProperty("base.remote.url");
    return createApi(clazz, baseUrl);
  }

  /**
   * 创建api.
   * @param clazz class
   * @param baseUrl baseUrl
   * @param <T> T
   * @return
   */
  public static <T> T createApi(Class<T> clazz, String baseUrl) {
    return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(clazz);
  }


}
