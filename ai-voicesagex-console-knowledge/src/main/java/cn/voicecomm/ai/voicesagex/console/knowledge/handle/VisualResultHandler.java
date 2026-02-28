package cn.voicecomm.ai.voicesagex.console.knowledge.handle;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.VisualInfo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.VisualInfoVector;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Convert the vertex data from ResultSet to NgVertex.
 */
@Component
public class VisualResultHandler {


  public List<VisualInfo> handle(ResultSet result) throws UnsupportedEncodingException {
    List<VisualInfo> visualInfos = new ArrayList<>();
    List<String> columnNames = result.getColumnNames();
    if (columnNames.contains(SpaceConstant.DST) && columnNames.contains(SpaceConstant.EDGENAME)
        && columnNames.contains(SpaceConstant.SRC)) {
      // 获取各列的值
      List<ValueWrapper> dstValues = result.colValues(SpaceConstant.DST);
      List<ValueWrapper> srcValues = result.colValues(SpaceConstant.SRC);
      List<ValueWrapper> edgeValues = result.colValues(SpaceConstant.EDGENAME);
      List<ValueWrapper> ranks = result.colValues(SpaceConstant.RANK_NUMBER);
      // 假设所有列表的长度相同（这是处理这种情况的关键假设）
      if (dstValues.size() == edgeValues.size() && srcValues.size() == dstValues.size()) {
        for (int i = SpaceConstant.INDEX; i < dstValues.size(); i++) {
          // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
          String src = srcValues.get(i).asString();
          String dst = dstValues.get(i).asString();
          String edgeName = edgeValues.get(i).asString();
          Long rank = ranks.get(i).asLong();
          // 创建一个新的 DataEntry 对象并添加到列表中
          visualInfos.add(new VisualInfo(src, dst, edgeName, rank));
        }
      }
    }

    return visualInfos;

  }

  public List<VisualInfoVector> handleVector(ResultSet result) throws UnsupportedEncodingException {
    List<VisualInfoVector> visualInfos = new ArrayList<>();
    List<String> columnNames = result.getColumnNames();
    if (columnNames.contains(SpaceConstant.DST) && columnNames.contains(SpaceConstant.EDGENAME)
        && columnNames.contains(SpaceConstant.SRC)) {
      // 获取各列的值
      List<ValueWrapper> dstValues = result.colValues(SpaceConstant.DST);
      List<ValueWrapper> srcValues = result.colValues(SpaceConstant.SRC);
      List<ValueWrapper> edgeValues = result.colValues(SpaceConstant.EDGENAME);
      List<ValueWrapper> ranks = result.colValues(SpaceConstant.E);
      // 假设所有列表的长度相同（这是处理这种情况的关键假设）
      if (dstValues.size() == edgeValues.size() && srcValues.size() == dstValues.size()) {
        for (int i = SpaceConstant.INDEX; i < dstValues.size(); i++) {
          // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
          String src = srcValues.get(i).asString();
          String dst = dstValues.get(i).asString();
          String edgeName = edgeValues.get(i).asString();
          Map<String, ValueWrapper> properties = ranks.get(i).asMap();

          // 创建一个新的 DataEntry 对象并添加到列表中
          visualInfos.add(new VisualInfoVector(src, dst, edgeName, properties));
        }
      }
    }

    return visualInfos;

  }
}
