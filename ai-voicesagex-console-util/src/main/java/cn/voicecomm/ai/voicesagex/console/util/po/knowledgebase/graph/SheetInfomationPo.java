package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName SheetInfomationPo
 * @Author wangyang
 * @Date 2025/9/19
 */
@Data
@TableName("knowledge_sheet_information")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SheetInfomationPo{

    @TableId(value = "\"sheet_id\"", type = IdType.AUTO)
    private Integer sheetId;

    @TableField(value = "\"document_id\"")
    private Integer documentId;

    @TableField(value = "\"sheet_name\"")
    private String  sheetName;


}
