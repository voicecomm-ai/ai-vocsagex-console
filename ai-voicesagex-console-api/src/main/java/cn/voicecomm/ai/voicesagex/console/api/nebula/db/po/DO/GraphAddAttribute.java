package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphAddAttribute implements Serializable {


    @Serial
    private static final long serialVersionUID = 1670684519718301777L;
    private String space;

    private String attribute;

    private String attributeName;

    private String propertyName;

    private String propertyType;

    private String isNull;

    private Object defaultValue;

    private String common;

    public Object getDefaultValue() {
        if (!ObjectUtil.isNull(defaultValue)) {
            if (defaultValue instanceof String) {
                return "DEFAULT '" + defaultValue + "'";
            }
            return "DEFAULT " + defaultValue;
        }
        return defaultValue;
    }

    public String getCommon() {
        if (StrUtil.isNotBlank(common)) {
            return  "COMMENT '" + common + "'";
        }
        return common;
    }
}
