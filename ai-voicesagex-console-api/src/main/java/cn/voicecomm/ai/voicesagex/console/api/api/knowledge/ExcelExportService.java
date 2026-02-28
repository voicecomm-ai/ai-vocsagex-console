package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel.BaseExport;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.ExportAllDataVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.ExportDataVO;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelExportService {
    
    List<BaseExport> getData(ExportAllDataVO exportDataVO);

    List<BaseExport> getSelectData(ExportDataVO exportDataVO);

    List<String> getTagData(Long spaceId, String tagName, int type);

    String processData(String propertyType, String propertyValue);

    CommonRespDto<Boolean> importEntity(MultipartFile file, String spaceId, int type);

    List<List<String>> generateDynamicHeaders(List<String> dynamicKeys,int type);

    List<Map<Integer, String>> convertToExcelData(List<BaseExport> list, List<String> dynamicKeys,int type);
}

