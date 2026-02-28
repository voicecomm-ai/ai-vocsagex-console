package cn.voicecomm.ai.voicesagex.console.knowledge.handle;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

public class CustomColumnWidthHandler implements CellWriteHandler {

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // 获取当前的sheet
        WriteSheetHolder writeSheetHolder = context.getWriteSheetHolder();
        Sheet sheet = writeSheetHolder.getSheet();
        Cell cell = context.getCell();

        // 如果是第一列，则设置列宽
        if (cell.getColumnIndex() == 0) {
            sheet.setColumnWidth(0, 300 * 100); // 设置第一列宽度为50个字符宽
        }
    }
}