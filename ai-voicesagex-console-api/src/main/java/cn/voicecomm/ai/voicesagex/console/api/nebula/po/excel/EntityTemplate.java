package cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityTemplate {
    @ExcelProperty("实体名称(必填)")
    private String entityName;

    private List<String> dataRows;


}

