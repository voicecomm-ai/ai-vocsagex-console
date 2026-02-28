package cn.voicecomm.ai.voicesagex.console.knowledge.handle;

import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePo;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * 导出模板支持下拉框处理类
 */
public class DropdownHandler implements SheetWriteHandler {


  private Long spaceId;

  private KnowledgeGraphTagEdgeMapper knowledgeGraphTagEdgeMapper;


  public DropdownHandler(Long spaceId, KnowledgeGraphTagEdgeMapper knowledgeGraphTagEdgeMapper) {
    this.spaceId = spaceId;
    this.knowledgeGraphTagEdgeMapper = knowledgeGraphTagEdgeMapper;
  }

  @Override
  public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder,
      WriteSheetHolder writeSheetHolder) {
    Map<Integer, String[]> mapDropDown = new HashMap<>();
    //获取工作簿
    Sheet sheet = writeSheetHolder.getSheet();
    // 执行查询
    List<KnowledgeGraphTagEdgePo> mapSpaces = knowledgeGraphTagEdgeMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.INDEX)
            .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));

    if (!CollectionUtils.isEmpty(mapSpaces)) {
      List<String> collect = (List<String>) mapSpaces.stream()
          .map(KnowledgeGraphTagEdgePo::getTagName).collect(Collectors.toList());
      String[] array = collect.toArray(new String[collect.size()]);
      mapDropDown.put(SpaceConstant.INDEX, array);
      mapDropDown.put(SpaceConstant.TWO, array);
    }

    ///开始设置下拉框
    DataValidationHelper helper = sheet.getDataValidationHelper();
    //设置下拉框
    for (Map.Entry<Integer, String[]> entry : mapDropDown.entrySet()) {
      /*起始行、终止行、起始列、终止列  起始行为1即表示表头不设置**/
      CellRangeAddressList addressList = new CellRangeAddressList(SpaceConstant.REPLICA_FACTOR,
          SpaceConstant.EXCEL_NUMBER, entry.getKey(), entry.getKey());
      /*设置下拉框数据**/
      DataValidationConstraint constraint = helper.createExplicitListConstraint(entry.getValue());
      DataValidation dataValidation = helper.createValidation(constraint, addressList);
      sheet.addValidationData(dataValidation);
    }


  }
}
