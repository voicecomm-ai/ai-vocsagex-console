package cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.graph;

import lombok.Getter;

@Getter
public enum TagEdgeEnums {


    INDEX_VALUE_ONE(0),
    INDEX_VALUE_TWO(1),
    INDEX_VALUE_THE(2);

    TagEdgeEnums(Integer status) {
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
