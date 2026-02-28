package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.parameterextractor;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.Model;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.Vision;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 参数提取器实体类
 *
 * @author wangf
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ParameterExtractorNode extends BaseNode implements Serializable {


    /**
     * 查询参数
     */
    private List<String> query;

    /**
     * 模型信息
     */
    private Model model;

    /**
     * 推理模式
     */
    private String reasoning_mode;

    /**
     * 视觉配置
     */
    private Vision vision;


    /**
     * 指令模板
     */
    private String instruction;

    /**
     * 参数定义列表
     */
    private List<Parameter> parameters;



    /**
     * 参数定义内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Parameter {
        /**
         * 参数名称
         */
        private String name;

        /**
         * 参数类型
         */
        private String type;

        /**
         * 参数描述
         */
        private String description;

        /**
         * 是否必需
         */
        private Boolean required;
    }
}