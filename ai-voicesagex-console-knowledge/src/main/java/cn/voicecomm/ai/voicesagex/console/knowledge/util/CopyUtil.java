package cn.voicecomm.ai.voicesagex.console.knowledge.util;

import cn.hutool.core.collection.CollUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;

public class CopyUtil {

  /**
   * 多个实体的复制
   *
   * @param source
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> List<T> copyList(List source, Class<T> clazz) {
    List<T> target = new ArrayList<>();
    if (!CollUtil.isEmpty(source)) {
      for (Object c : source) {
        T obj = copy(c, clazz);
        target.add(obj);
      }
    }
    return target;
  }

  /**
   * 单个实体之间的复制
   *
   * @param source
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> T copy(Object source, Class<T> clazz) {
    if (source == null) {
      return null;
    }
    T obj = null;
    try {
      obj = clazz.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
    BeanUtils.copyProperties(source, obj);
    return obj;
  }
}
