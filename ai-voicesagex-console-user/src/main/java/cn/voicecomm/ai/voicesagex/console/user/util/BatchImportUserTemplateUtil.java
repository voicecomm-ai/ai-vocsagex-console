package cn.voicecomm.ai.voicesagex.console.user.util;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.ReadingOrder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class BatchImportUserTemplateUtil {

  private static final int FONT_SIZE = 11;
  private static final int FONT_WIDTH = 256;
  private static final String FONT_NAME = "宋体";
  private static final String TEMPLATE_SHEET_NAME = "模板";
  private static final String REQUIREMENTS_SHEET_NAME = "说明";
  private static final String HIDDEN_SHEET_NAME = "hidden";
  private static final String[] REQUIREMENTS = {
    "以下均为必填字段：",
    "1、用户名：不超过10个字符",
    "2、登录账号：输入数字/英文字母，不超过15个字符",
    "3、初始密码：6-20位，需包含大小写英文和数字，不允许中文"
  };

  private static final String USERNAME = "用户名";
  private static final String ACCOUNT = "登录账号";
  private static final String PASSWORD = "初始密码";

  public static void getDownloadTemplate(String filePath) {
    try {
      XSSFWorkbook template = generateDownloadTemplate();
      saveTemplate(template, filePath);
      template.close();
    } catch (IOException e) {
      throw new RuntimeException("关闭模板失败", e);
    }
  }

  private static XSSFWorkbook generateDownloadTemplate() {
    XSSFWorkbook workbook = new XSSFWorkbook();
    createTemplateSheet(workbook);
    createRequirementsSheet(workbook);
    return workbook;
  }

  private static void saveTemplate(XSSFWorkbook workbook, String filePath) {
    try {
      Path path = Paths.get(filePath);
      if (Files.exists(path)) {
        Files.delete(path);
      }

      Files.createDirectories(path.getParent());
      Path file = Files.createFile(path);
      FileOutputStream outputStream = new FileOutputStream(file.toFile());
      workbook.write(outputStream);
      outputStream.close();
    } catch (IOException e) {
      throw new RuntimeException("生成模板失败", e);
    }
  }

  public static List<TemplateSheetCell> readTemplate(String filePath) {
    try (XSSFWorkbook workbook = new XSSFWorkbook(filePath)) {
      XSSFSheet templateSheet = workbook.getSheet(TEMPLATE_SHEET_NAME);
      if (templateSheet == null) {
        throw new RuntimeException("模板文件格式错误");
      }

      List<TemplateSheetCell> cells = new ArrayList<>();

      int minRowIx = templateSheet.getFirstRowNum();
      int maxRowIx = templateSheet.getLastRowNum();

      XSSFRow headerRow = templateSheet.getRow(minRowIx);
      short minColIx = headerRow.getFirstCellNum();
      short maxColIx = headerRow.getLastCellNum();

      for (int rowIx = minRowIx + 1; rowIx <= maxRowIx; rowIx++) {
        XSSFRow row = templateSheet.getRow(rowIx);

        if (Objects.isNull(row)) {
          continue;
        }

        TemplateSheetCell templateSheetCell = new TemplateSheetCell();
        boolean isEmptyRow = true;
        for (short colIx = minColIx; colIx < maxColIx; colIx++) {
          XSSFCell headerCell = headerRow.getCell(colIx);
          String headerName = headerCell.getStringCellValue();
          XSSFCell cell = row.getCell(colIx);

          if (Objects.isNull(cell)) {
            continue;
          }

          String cellValue = readCellValue(cell);

          isEmptyRow = StringUtils.isEmpty(cellValue);

          switch (headerName) {
            case USERNAME:
              templateSheetCell.setUsername(cellValue);
              break;
            case ACCOUNT:
              templateSheetCell.setAccount(cellValue);
              break;
            case PASSWORD:
              templateSheetCell.setPassword(cellValue);
              break;
            default:
          }
        }

        if (!isEmptyRow) {
          cells.add(templateSheetCell);
        }
      }

      return cells;
    } catch (IOException e) {
      throw new RuntimeException("读取模板失败", e);
    }
  }

  private static String readCellValue(XSSFCell cell) {
    CellType cellType = cell.getCellType();
    if (Objects.equals(CellType.STRING, cellType)) {
      String stringCellValue;
      try {
        stringCellValue = cell.getStringCellValue();
      } catch (Exception e) {
        stringCellValue = "";
      }
      return stringCellValue;
    }

    String rawValue;
    try {
      rawValue = cell.getRawValue();
    } catch (Exception e) {
      rawValue = "";
    }
    return rawValue;
  }

  public static void generateFailedImportedUserTemplate(List<TemplateSheetCell> cells,
    Path filePath) {
    try (XSSFWorkbook sheets = generateDownloadTemplate()
    ) {
      XSSFSheet sheet = sheets.getSheet(TEMPLATE_SHEET_NAME);
      XSSFRow headerRow = sheet.getRow(0);
      Cell roleCell = headerRow.createCell(Header.values().length);
      roleCell.setCellValue("失败原因");
      CellStyle cellStyle = createTemplateSheetHeaderStyles(sheets);
      int fontIndexAsInt = cellStyle.getFontIndexAsInt();
      Font fontAt = sheets.getFontAt(fontIndexAsInt);
      fontAt.setColor(IndexedColors.RED.getIndex());
      cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
      cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      roleCell.setCellStyle(cellStyle);

      for (int i = 1; i <= cells.size(); i++) {
        XSSFRow row = sheet.createRow(i);
        TemplateSheetCell templateSheetCell = cells.get(i - 1);

        row.createCell(Header.USERNAME_HEADER.getIdx())
          .setCellValue(templateSheetCell.getUsername());
        row.createCell(Header.ACCOUNT_HEADER.getIdx()).setCellValue(templateSheetCell.getAccount());
        row.createCell(Header.PASSWORD_HEADER.getIdx())
          .setCellValue(templateSheetCell.getPassword());
        row.createCell(Header.values().length).setCellValue(StringUtils.join(
          templateSheetCell.getFailureReasons(), "，"));
      }
      saveTemplate(sheets, filePath.toString());
    } catch (IOException e) {
      throw new RuntimeException("关闭模板失败", e);
    }
  }

  private static void createTemplateSheet(XSSFWorkbook workbook) {
    XSSFSheet sheet = workbook.createSheet(TEMPLATE_SHEET_NAME);

    setTemplateSheetHeaderColumn(sheet);

    CellStyle headerStyle = createTemplateSheetHeaderStyles(workbook);

    createTemplateSheetHeaders(sheet, headerStyle);
  }

  private static CellStyle createTemplateSheetHeaderStyles(XSSFWorkbook workbook) {
    XSSFCellStyle cellStyle = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setFontName(FONT_NAME);
    font.setFontHeightInPoints((short) FONT_SIZE);
    cellStyle.setFont(font);
    // 水平居中
    cellStyle.setAlignment(HorizontalAlignment.GENERAL);
    return cellStyle;
  }

  private static void setTemplateSheetHeaderColumn(Sheet sheet) {
    for (Header header : Header.values()) {
      sheet.setColumnWidth(header.getIdx(), header.getName().getBytes().length * FONT_WIDTH);
      CellStyle columnStyle = sheet.getWorkbook().createCellStyle();
      columnStyle.setDataFormat((short) header.getFmt());
      sheet.setDefaultColumnStyle(header.getIdx(), columnStyle);
    }
  }

  private static void createTemplateSheetHeaders(Sheet sheet, CellStyle headerStyle) {
    Row headerRow = sheet.createRow(0);
    for (Header header : Header.values()) {
      Cell roleCell = headerRow.createCell(header.getIdx());
      roleCell.setCellValue(header.getName());
      roleCell.setCellStyle(headerStyle);
    }
  }


  private static void createRequirementsSheet(XSSFWorkbook workbook) {
    XSSFSheet requirementsSheet = workbook.createSheet(REQUIREMENTS_SHEET_NAME);

    List<String> requirements = getRequirements();
    for (int i = 0; i < requirements.size(); i++) {
      XSSFRow row = requirementsSheet.createRow(i);
      row.createCell(0);
    }
    CellRangeAddress cellAddresses = new CellRangeAddress(0, requirements.size(), 0,
      SpreadsheetVersion.EXCEL2007.getLastColumnIndex());
    requirementsSheet.addMergedRegion(cellAddresses);

    XSSFRow row = requirementsSheet.getRow(0);
    row.setHeight((short) (requirements.size() * FONT_SIZE));

    // 设置样式
    XSSFCell cell = row.getCell(0);
    cell.setCellValue(StringUtils.join(requirements, StringUtils.LF + StringUtils.CR));
    XSSFCellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setAlignment(HorizontalAlignment.GENERAL);
    cellStyle.setReadingOrder(ReadingOrder.LEFT_TO_RIGHT);
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    cellStyle.setWrapText(true);
    XSSFFont font = workbook.createFont();
    font.setFontName(FONT_NAME);
    font.setFontHeightInPoints((short) FONT_SIZE);
    cellStyle.setFont(font);
    cell.setCellStyle(cellStyle);

    requirementsSheet.setColumnWidth(0,
      requirements.stream().mapToInt(r -> r.getBytes().length).max().orElse(0) * FONT_WIDTH);
  }

  private static List<String> getRequirements() {
    return Arrays.asList(REQUIREMENTS);
  }

  @Getter
  public enum Header {
    USERNAME_HEADER(0, USERNAME, BuiltinFormats.getBuiltinFormat("@")),
    ACCOUNT_HEADER(1, ACCOUNT, BuiltinFormats.getBuiltinFormat("@")),
    PASSWORD_HEADER(2, PASSWORD, BuiltinFormats.getBuiltinFormat("@"));


    private final int idx;
    private final String name;
    /**
     * 参考：org.apache.poi.ss.usermodel.BuiltinFormats 设定的格式
     */
    private final int fmt;

    Header(int idx, String name, int fmt) {
      this.idx = idx;
      this.name = name;
      this.fmt = fmt;
    }
  }

  @Data
  public static class TemplateSheetCell {

    private String username;
    private String account;
    private String password;

    private List<String> failureReasons;
  }
}
