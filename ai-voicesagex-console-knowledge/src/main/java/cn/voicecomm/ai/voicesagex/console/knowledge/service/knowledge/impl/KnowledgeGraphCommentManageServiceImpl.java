package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphCommentManageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图知识库本体关系接口实现类
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphCommentManageServiceImpl implements KnowledgeGraphCommentManageService {

  /**
   * 文件上传路径前缀
   */
  @Value("${file.upload}")
  private String uploadDir;

  @Override
  public CommonRespDto<String> upload(String fileDir, MultipartFile file) {
    if (file.isEmpty()) {
      return CommonRespDto.error("请选择文件");
    }
    if (file.getSize() > 200 * 1024 * 1024) {
      return CommonRespDto.error("文件大小不能超过200MB");
    }
    if (CharSequenceUtil.isBlank(fileDir)) {
      fileDir = "other";
    }
    try {
      // 保存到文件服务器
      String dataFormat = DatePattern.PURE_DATE_FORMAT.format(new Date());
      String formatted = DatePattern.PURE_DATETIME_MS_FORMAT.format(new Date());
      String randomString = RandomUtil.randomString(4);
      // 保存到文件服务器
      String filePath = String.join("/", fileDir, dataFormat, formatted + randomString,
          file.getOriginalFilename());
      String path = uploadDir + filePath;
      FileUtil.touch(path);
      FileUtil.writeBytes(file.getBytes(), path);
      String realPath = StrUtil.replaceFirst(path, "/data1", "/file");
      return CommonRespDto.success(realPath);
    } catch (IOException e) {
      log.error("上传文件失败，文件名: {}", file.getOriginalFilename(), e);
      return CommonRespDto.error("上传失败");
    }
  }
}