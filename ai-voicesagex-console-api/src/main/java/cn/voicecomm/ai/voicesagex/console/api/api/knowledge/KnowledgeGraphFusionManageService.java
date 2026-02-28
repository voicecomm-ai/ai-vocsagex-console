package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.fusion.AffirmFusionVO;
import java.io.UnsupportedEncodingException;

/**
 * @ClassName KnowledgeGraphFusionManageService
 * @Author wangyang
 * @Date 2026/1/5
 */
public interface KnowledgeGraphFusionManageService {

  /**
   * 知识融合接口
   *
   * @param affirmFusionVO
   * @return
   */
  CommonRespDto<Boolean> affirmFusion(AffirmFusionVO affirmFusionVO)
      throws UnsupportedEncodingException;
}
