package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

import cn.hutool.core.util.ObjectUtil;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * @author adminst
 * @Descriptin:
 * @ClassName: PropertyBean
 */
@Data
public class PropertyBean implements Serializable {

    @Serial
    private static final long serialVersionUID = -8449098194862237846L;
    /**
     * 属性名称
     **/
    private String propertyName;
    /**
     * 属性类型 (int bool string double .........)
     **/
    private String propertyType;
    /**
     * 属性描述
     **/
    private String propertyComment;

    /**
     * 是否可为空 (NOT NULL 或者 NULL)
     **/
    private String isNull;

    /**
     * 默认值
     **/
    private Object defaultValue;

    public Object getDefaultValue() {
        if (!ObjectUtil.isNull(defaultValue)) {
            if (defaultValue instanceof String) {
                return "DEFAULT '" + defaultValue + "'";
            }
            return "DEFAULT " + defaultValue;
        }
        return defaultValue;
    }
}
