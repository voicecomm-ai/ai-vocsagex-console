package cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.graph;

import lombok.Getter;

@Getter
public enum DocumentEnum {

    IN_ANALYSIS(0),
    ANALYSIS_SUCCESS(1),
    ANALYSIS_LOSE(2),
    EXTRACT(3),
    EXTRACT_SUCCESS(4);

    DocumentEnum(Integer status) {
        this.status = status;
    }

    // Getter 方法（如果使用了Lombok的@Getter注解，则Lombok会自动生成）
    public Integer getStatus() {
        return status;
    }
    /**
     * 数据项值
     */
    private  Integer status ;
}
