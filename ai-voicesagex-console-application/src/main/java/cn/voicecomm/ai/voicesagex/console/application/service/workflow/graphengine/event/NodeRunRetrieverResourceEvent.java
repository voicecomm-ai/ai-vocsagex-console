package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities.RetrievalSourceMetadata;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 节点运行检索资源事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class NodeRunRetrieverResourceEvent extends BaseNodeEvent {
    private List<RetrievalSourceMetadata> retrieverResources;
    private String context;
    
}