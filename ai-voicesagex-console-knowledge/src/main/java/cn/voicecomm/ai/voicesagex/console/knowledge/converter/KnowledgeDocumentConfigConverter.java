package cn.voicecomm.ai.voicesagex.console.knowledge.converter;

import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentConfigInfoVO;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.DocumentConfigPo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface KnowledgeDocumentConfigConverter {

  DocumentConfigInfoVO poToVo(DocumentConfigPo config);
}
