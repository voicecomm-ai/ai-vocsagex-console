package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import com.alibaba.fastjson.JSONObject;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽取参数信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KGExtractReq {

  /**
   * 需要抽取的关系标签
   */
  private List<String> relations;


  /**
   * 需要抽取的本体标签
   */
  private List<String> tags;


  /**
   * 需要抽取的文本内容
   */
  private String chunk;


  /**
   * 元数据信息
   */
  private JSONObject metadata = new JSONObject();

  /**
   * 解析后回调地址
   */
  private String callbackUrl;

  private String re_prompt;

  private String ner_prompt;

  private String ner_model;

  private String re_model;
}
