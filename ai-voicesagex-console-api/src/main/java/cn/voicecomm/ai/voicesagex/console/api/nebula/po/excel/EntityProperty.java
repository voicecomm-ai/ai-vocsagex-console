package cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityProperty {

    private String propertyName;

    private String propertyType;

    private String extra;

    private String defaultValueAsString;

    private Integer tagRequired;

}
