package cn.voicecomm.ai.voicesagex.console.api.api.file;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.FileExtractEntityDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ZipNodeDto;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ryc
 */
public interface FileService {

  CommonRespDto<String> uploadZip(String fileDir, MultipartFile file);

  CommonRespDto<String> upload(String fileDir, MultipartFile file);

  CommonRespDto<Void> downloadEntry(String zipPath, String entryPath, HttpServletResponse response);

  CommonRespDto<FileExtractEntityDto> uploadExtractEntityFiles(String fileDir, MultipartFile file);

  CommonRespDto<Set<Integer>> getUploadedChunks(String fileMd5);

  CommonRespDto<Integer> uploadChunk(String fileMd5, Integer chunkIndex, Integer totalChunks,
      String fileDir, MultipartFile file);

  CommonRespDto<FileExtractEntityDto> mergeChunks(String fileMd5, String fileName, String fileDir);

  CommonRespDto<ZipNodeDto> buildTree(String filePath);
}
