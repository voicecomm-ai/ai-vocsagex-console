package cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
    public class RelationExport implements  BaseExport{

        @ExcelProperty(value = "主体类型",index = 0)
        private String subjectTagName;

        @ExcelProperty(value = "主体名称",index = 1)
        private String subjectName;
        @ExcelProperty(value = "关系名称",index = 2)

        private String edgeName;
        @ExcelProperty(value = "客体类型",index = 3)

        private String objectTagName;
        @ExcelProperty(value = "客体名称",index = 4)

        private String objectName;


    public RelationExport(String subjectTagName, String subjectName, String edgeName, String objectTagName, String objectName) {
        this.subjectTagName = subjectTagName;
        this.subjectName = subjectName;
        this.edgeName = edgeName;
        this.objectTagName = objectTagName;
        this.objectName = objectName;
    }

    private Map<String, Object> dynamicProperties;
}
