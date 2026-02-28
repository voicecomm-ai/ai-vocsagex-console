package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: gaox
 * @date: 2026/1/28 15:31
 */
@Slf4j
public class HutoolJsonUtil {

  /**
   * 对象转json字符串(不忽略null值)
   *
   * @param obj
   * @return
   */
  public static String toJsonStr(Object obj) {
    return JSONUtil.toJsonStr(obj, JSONConfig.create().setIgnoreNullValue(false));
  }

  /**
   * 对象转json数组
   *
   * @return
   */
  public static JSONArray parseArray(Object arrayOrCollection) {
    return JSONUtil.parseArray(arrayOrCollection, false);
  }

  /**
   * 对象转json对象
   *
   * @param obj
   * @return
   */
  public static JSONObject parseObj(Object obj) {
    return JSONUtil.parseObj(obj, false);
  }
}
