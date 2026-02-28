package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

import cn.hutool.core.util.StrUtil;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * @Descriptin:
 * @ClassName: AttributeBean
 */
@Data
public class AttributeBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1519249436445107834L;
    /**
     * 属性名称
     **/
    private String propertyName;
    /**
     * 属性类型 (int bool string double .........)
     **/
    private String indexLength;

    public String getPropertyName() {
        return "`"+propertyName +"`"+ getIndexLength();
    }

    private String getIndexLength() {
        if (StrUtil.isNotBlank(indexLength)) {
            return "(" + indexLength + ")";
        }
        return "";
    }

    private String getIndexFull() {
        if (StrUtil.isNotBlank(indexLength)) {
            return "(" + indexLength + ")";
        }
        return "";
    }
}
