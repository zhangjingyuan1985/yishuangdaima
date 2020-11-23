package com.sutpc.data.rev.bus.gps.adsch.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**.
 * @author ZuHong Chen
 * @createTime 2019/9/24 17:11
 * @description Spring工具类
 * @version v1.0
 **/
@Component
public class SpringUtils implements ApplicationContextAware {

  /**.
   * 上下文对象
   */
  private static ApplicationContext applicationContext;

  /**.
   * 根据bean名称获取对象
   * @param name bean名称
   * @return 对象
   */
  public static Object getBean(String name) {
    return applicationContext.getBean(name);
  }

  /**.
   * 根据类获取bean对象
   * @param clazz 类
   * @param <T> 泛型
   * @return 泛型
   */
  public static <T> T getBean(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }

  /**.
   * 获取bean对象
   * @param name baen名字
   * @param clazz 类
   * @param <T> 泛型
   * @return 泛型
   */
  public static <T> T getBean(String name, Class<T> clazz) {
    return applicationContext.getBean(name, clazz);
  }

  /**.
   * 单例模式,初始化上下文对象
   * @param applicationContext  上下文对象
   * @throws BeansException 消息错误体
   */
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    if (null == SpringUtils.applicationContext) {
      SpringUtils.applicationContext = applicationContext;
    }
  }
}
