package cn.voicecomm.ai.voicesagex.console.knowledge.handle;

import com.alibaba.excel.write.handler.AbstractCellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CustomCellWriteHandler extends AbstractCellWriteHandler {

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // 只处理表头
        if (Boolean.TRUE.equals(context.getHead()) && context.getRowIndex() == 0) {
            // 获取工作簿和表单
            Workbook workbook = context.getWriteSheetHolder().getSheet().getWorkbook();
            Sheet sheet = context.getWriteSheetHolder().getSheet();

            // 创建一个新的单元格样式
            CellStyle cellStyle = workbook.createCellStyle();

            // 创建并设置字体
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 1); // 设置字体大小，例如10
            cellStyle.setFont(font);

            // 设置单元格样式
            context.getCell().setCellStyle(cellStyle);

            // 设置行高（可选，根据需要调整）
            sheet.getRow(context.getRowIndex()).setHeightInPoints(75); // 设置行高为 15（可根据需要调整）
        }

    }
    }