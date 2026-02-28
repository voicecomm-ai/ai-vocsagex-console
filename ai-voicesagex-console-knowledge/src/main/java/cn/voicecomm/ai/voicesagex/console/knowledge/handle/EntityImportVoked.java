package cn.voicecomm.ai.voicesagex.console.knowledge.handle;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityImportVoked extends AnalysisEventListener<Map<Integer, String>> {


    //表头数据（存储所有的表头数据）
    private List<Map<Integer, String>> headList = new ArrayList<>();
    //数据体
    private List<Map<Integer, String>> dataList = new ArrayList<>();


    @Override//这里会一行行的返回头
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        //存储全部表头数据
        headList.add(headMap);
    }

    @Override// 处理每一行数据
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override// 全部处理结束执行
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    public List<Map<Integer, String>> getHeadList() {
        return headList;
    }

    public List<Map<Integer, String>> getDataList() {
        return dataList;
    }

}
