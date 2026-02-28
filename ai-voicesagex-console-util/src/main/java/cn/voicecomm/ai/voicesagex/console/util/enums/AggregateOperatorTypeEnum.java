package cn.voicecomm.ai.voicesagex.console.util.enums;

import lombok.Getter;

/**
 * @author: gaox
 * @date: 2026/1/22 14:28
 */
@Getter
public enum AggregateOperatorTypeEnum {
    /**
     * 实体属性聚合
     */
    ENTITY_ATTRIBUTE_AGGREGATE("entity-attribute-aggregate", "实体属性聚合"),
    /**
     * 元标签聚合
     */
    LABEL_AGGREGATE("label-aggregate", "元标签聚合"),
    /**
     * 最相关实体聚合
     */
    MOST_RELEVANT_ENTITY("most-relevant-entity", "最相关实体聚合"),
    /**
     * 嵌套聚合
     */
    NESTED_AGGREGATE("nested-aggregate", "嵌套聚合");

    /**
     * 枚举值
     */
    private final String value;

    /**
     * 中文描述
     */
    private final String desc;

    // 构造方法
    AggregateOperatorTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

