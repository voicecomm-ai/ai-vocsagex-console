package cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityExport implements BaseExport{
    /**
     * 实体名称
     */
    @ExcelProperty(value = "实体名称")
    private String entityName;

    @ExcelProperty(value = "VID")
    private String vid ;

    /**
     * 本体名称
     */
    @ExcelProperty(value = "本体名称")
    private String tagName;



    // 用于存储动态属性的 Map
    private Map<String, Object> dynamicProperties;



}
