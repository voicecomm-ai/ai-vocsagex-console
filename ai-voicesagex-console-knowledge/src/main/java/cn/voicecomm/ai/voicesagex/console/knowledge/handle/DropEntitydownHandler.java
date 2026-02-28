package cn.voicecomm.ai.voicesagex.console.knowledge.handle;

import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class DropEntitydownHandler extends AbstractRowWriteHandler {

    private CellStyle dateCellStyle;
    private CellStyle dateOnlyCellStyle;
    private CellStyle timeOnlyCellStyle;
    private static final int DEFAULT_ROW_COUNT = 5000; // 默认创建 100 行

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer rowIndex, Boolean isHead) {
        Sheet sheet = writeSheetHolder.getSheet();
        Workbook workbook = sheet.getWorkbook();

        // 初始化 TIMESTAMP 日期格式样式
        if (dateCellStyle == null) {
            dateCellStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
        }

        // 初始化 DATE 日期格式样式
        if (dateOnlyCellStyle == null) {
            dateOnlyCellStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            dateOnlyCellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-dd"));
        }

        // 初始化 TIME 时间格式样式
        if (timeOnlyCellStyle == null) {
            timeOnlyCellStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            timeOnlyCellStyle.setDataFormat(dataFormat.getFormat("HH:mm:ss"));
        }

        // 如果是表头，找到 TIMESTAMP、DATE 或 TIME 列的索引
        if (Boolean.TRUE.equals(isHead)) {
            List<Integer> timestampColumns = new ArrayList<>(); // 存储 TIMESTAMP 列的索引
            List<Integer> dateColumns = new ArrayList<>(); // 存储 DATE 列的索引
            List<Integer> timeColumns = new ArrayList<>(); // 存储 TIME 列的索引

            for (Cell cell : row) {
                String cellValue = cell.getStringCellValue();
                if (cellValue.contains("TIMESTAMP") || cellValue.contains("DATETIME")) {
                    timestampColumns.add(cell.getColumnIndex()); // 记录 TIMESTAMP 列索引
                } else if (cellValue.contains("DATE")) {
                    dateColumns.add(cell.getColumnIndex()); // 记录 DATE 列索引
                } else if (cellValue.contains("TIME")) {
                    timeColumns.add(cell.getColumnIndex()); // 记录 TIME 列索引
                }
            }

            // 为所有 TIMESTAMP、DATE 和 TIME 列设置格式
            for (int i = SpaceConstant.REPLICA_FACTOR; i <= DEFAULT_ROW_COUNT; i++) { // 从第 1 行开始
                Row dataRow = sheet.getRow(i);
                if (dataRow == null) {
                    dataRow = sheet.createRow(i); // 如果行不存在，创建新行
                }

                // 遍历 TIMESTAMP 列并设置样式
                for (int columnIndex : timestampColumns) {
                    Cell cell = dataRow.getCell(columnIndex);
                    if (cell == null) {
                        cell = dataRow.createCell(columnIndex); // 创建空单元格
                    }
                    cell.setCellStyle(dateCellStyle); // 设置 TIMESTAMP 日期格式
                }

                // 遍历 DATE 列并设置样式
                for (int columnIndex : dateColumns) {
                    Cell cell = dataRow.getCell(columnIndex);
                    if (cell == null) {
                        cell = dataRow.createCell(columnIndex); // 创建空单元格
                    }
                    cell.setCellStyle(dateOnlyCellStyle); // 设置 DATE 日期格式
                }

                // 遍历 TIME 列并设置样式
                for (int columnIndex : timeColumns) {
                    Cell cell = dataRow.getCell(columnIndex);
                    if (cell == null) {
                        cell = dataRow.createCell(columnIndex); // 创建空单元格
                    }
                    cell.setCellStyle(timeOnlyCellStyle); // 设置 TIME 时间格式
                }
            }
        }
    }





    }