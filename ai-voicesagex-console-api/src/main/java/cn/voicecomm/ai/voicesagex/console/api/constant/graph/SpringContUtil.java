package cn.voicecomm.ai.voicesagex.console.api.constant.graph;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring 上下文工具类
 *
 * @author adminst
 */
public class SpringContUtil implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    SpringContUtil.applicationContext = applicationContext;
  }

  /**
   * 获取 Spring 容器中的 Bean
   */
  public static <T> T getBean(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }

  /**
   * 通过 Bean 名称获取 Bean
   */
  public static Object getBean(String name) {
    return applicationContext.getBean(name);
  }
}