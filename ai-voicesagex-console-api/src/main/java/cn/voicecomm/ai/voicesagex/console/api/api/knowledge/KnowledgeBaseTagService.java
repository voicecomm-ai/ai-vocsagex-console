package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseTagDto;

import java.util.List;

public interface KnowledgeBaseTagService {

  /**
   * 创建标签
   *
   * @param name 标签名称
   */
  CommonRespDto<Integer> createTag(String name);

  CommonRespDto<Void> deleteTag(Integer id);

  boolean isDeletable(Integer id);

  CommonRespDto<Void> bindTag(List<Integer> tagIds, Integer knowledgeBaseId);

  CommonRespDto<Void> editTag(Integer id, String name);

  CommonRespDto<List<KnowledgeBaseTagDto>> list(String name);
}
