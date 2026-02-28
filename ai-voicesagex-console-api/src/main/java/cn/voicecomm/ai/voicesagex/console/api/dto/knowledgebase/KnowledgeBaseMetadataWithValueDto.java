package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.metadata.MetadataType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class KnowledgeBaseMetadataWithValueDto implements Serializable {

    private Integer id;

    private String name;

    private MetadataType type;

    private Boolean isBuiltIn;

    private Integer knowledgeBaseId;

    private Integer distinctValueCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 元数据值列表
     */
    private List<String> values;
} 