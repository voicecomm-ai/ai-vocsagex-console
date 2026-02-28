package cn.voicecomm.ai.voicesagex.console.knowledge.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphFusionManageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.fusion.AffirmFusionVO;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName KnowledgeGraphFusionManageController
 * @Author wangyang
 * @Date 2026/1/4
 */

@RestController
@Tag(name = "知识融合控制器")
@RequestMapping("/fusion")
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphFusionManageController {

  private final KnowledgeGraphFusionManageService knowledgeExtractionManageService;

  @PostMapping("/affirmFusion")
  @Operation(summary = "知识确认融合", description = "知识确认融合")
  public Result<Boolean> insertExtractionJob(
      @RequestBody @Validated AffirmFusionVO affirmFusionVO) throws UnsupportedEncodingException {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.affirmFusion(
        affirmFusionVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
}
