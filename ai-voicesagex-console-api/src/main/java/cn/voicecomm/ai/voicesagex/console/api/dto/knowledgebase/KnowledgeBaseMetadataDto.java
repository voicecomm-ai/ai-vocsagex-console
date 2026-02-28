package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.metadata.MetadataType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class KnowledgeBaseMetadataDto implements Serializable {

    private Integer id;

    private String name;

    private MetadataType type;

    private Boolean isBuiltIn;

    private Integer knowledgeBaseId;

    private Integer distinctValueCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
} 