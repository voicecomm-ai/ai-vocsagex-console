package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;


@TableName("knowledge_graph_pattern_manager")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PatterManagerPo {

  @TableField(value = "\"space_id\"")
  private Integer spaceId;

  @TableField(value = "\"is_flush\"")
  private int isFlush;


  @TableField(value = "\"update_time\"")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;


}
