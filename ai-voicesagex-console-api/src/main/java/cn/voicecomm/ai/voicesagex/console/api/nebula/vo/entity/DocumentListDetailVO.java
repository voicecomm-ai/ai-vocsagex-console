package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "文档列表详细信息", description = "文档列表")
public class DocumentListDetailVO {
//
//    @Schema(description = "图空间id",requiredMode = Schema.RequiredMode.REQUIRED,example = "1719736987010" )
//    private Long spaceId;

  @Schema(description = "文档抽取id", example = "demo")
  private String documentId;


  @Schema(description = "文档名称", example = "demo")
  private String documentName;


  @Schema(description = "文档状态", example = "0 解析中  1 解析成功  2 解析失败  3 抽取中  4 抽取成功")
  private Integer documentStatus;


  @Schema(description = "文档总数", example = "100")
  private Integer documentTotal;


  @Schema(description = "已解析文档数量", example = "35")
  private Integer analysisNumber;


  @Schema(description = "是否已经开始抽取该文档", example = "true")
  private boolean status;

  @Schema(description = "排队数量", example = "100")
  private Integer lineUpNumber;


  @Schema(description = "解析排队数量", example = "100")
  private Integer parseLineUpNumber;


  private Date updateTime;
  private Date createTime;


  private Integer extractionId;


}
