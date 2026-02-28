package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import cn.hutool.json.JSONObject;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 记忆更新请求
 *
 * @author wangf
 * @date 2025/9/8 下午 2:36
 */
@Data
@Accessors(chain = true)
public class MemoryUpdateRequest implements Serializable {


  /**
   * 记忆内容
   */
  private String content;


  /**
   * 文本生成模型 配置信息
   */
  private JSONObject model_instance_config;

  /**
   * 文本生成模型 加载方式
   */
  private String model_instance_provider;


}



