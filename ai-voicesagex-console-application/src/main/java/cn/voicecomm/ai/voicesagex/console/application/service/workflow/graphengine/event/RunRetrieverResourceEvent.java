package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities.RetrievalSourceMetadata;
import java.util.List;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 运行检索器资源事件
 */
@Data
@SuperBuilder
public class RunRetrieverResourceEvent {

  /**
   * 检索器资源
   */
  private List<RetrievalSourceMetadata> retrieverResources;

  /**
   * 上下文
   */
  private String context;
}