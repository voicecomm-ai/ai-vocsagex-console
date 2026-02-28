package cn.voicecomm.ai.voicesagex.console.knowledge.util;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.graph.TagEdgeEnums;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.NgTagEdge;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取 Tag/Edge 详细信息Util
 */
public class ResultSetTagEdgeUtil {

  public static void setNgTagPropertiesFromRecord(ResultSet.Record row, NgTagEdge ngTag)
      throws UnsupportedEncodingException {
    ngTag.setField(row.get(TagEdgeEnums.INDEX_VALUE_ONE.getStatus()).asString());
    ngTag.setType(row.get(TagEdgeEnums.INDEX_VALUE_TWO.getStatus()).asString());
    ngTag.setNullable(row.get(TagEdgeEnums.INDEX_VALUE_THE.getStatus()).asString());
  }

  public static List<NgTagEdge> createNgTagList(ResultSet result)
      throws UnsupportedEncodingException {
    List<NgTagEdge> ngTagList = new ArrayList<>();
    if (result != null) {
      int size = result.rowsSize();
      for (int i = SpaceConstant.INDEX; i < size; i++) {
        ResultSet.Record row = result.rowValues(i);
        if (row != null) {
          NgTagEdge ngTag = new NgTagEdge();
          setNgTagPropertiesFromRecord(row, ngTag);
          ngTagList.add(ngTag);
        }
      }
    }
    return ngTagList;
  }

  /**
   * 对列表进行分页处理
   *
   * @param list        原始列表
   * @param pageSize    每页大小
   * @param currentPage 当前页码（从1开始）
   * @return 分页后的子列表
   */
  public static <T> List<T> paginate(List<T> list, int pageSize, int currentPage) {
    if (list == null || list.isEmpty() || pageSize <= 0 || currentPage <= 0) {
      return new ArrayList<>(); // Return an empty list if input parameters are invalid
    }

    int totalSize = list.size();
    int totalPages = (int) Math.ceil((double) totalSize / pageSize);

    if (currentPage > totalPages) {
      return new ArrayList<>(); // Return an empty list if currentPage exceeds total pages
    }

    int startIndex = (currentPage - 1) * pageSize;
    startIndex = Math.max(startIndex, 0);

    int endIndex = Math.min(startIndex + pageSize, totalSize);

    return list.subList(startIndex, endIndex);
  }
}
