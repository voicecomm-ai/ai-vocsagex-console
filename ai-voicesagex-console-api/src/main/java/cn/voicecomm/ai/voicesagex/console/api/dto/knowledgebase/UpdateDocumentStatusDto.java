package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.DocumentStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UpdateDocumentStatusDto implements Serializable {

    /**
     * 文档ID列表
     */
    @NotEmpty(message = "文档ID列表不能为空")
    private List<Integer> documentIds;

    /**
     * 目标状态
     */
    private DocumentStatus status;

    private Boolean isArchived;
} 
