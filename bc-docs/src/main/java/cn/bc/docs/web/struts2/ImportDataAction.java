package cn.bc.docs.web.struts2;

import cn.bc.core.exception.CoreException;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.domain.Attach;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * 导入数据的Action基类
 *
 * @author dragon
 */
public abstract class ImportDataAction extends ActionSupport {
  private static final long serialVersionUID = 1L;
  private final static Logger logger = LoggerFactory.getLogger(ImportDataAction.class);
  public String file;// 用户上传数据文件的路径，相对于 Attach.DATA_REAL_PATH 目录下的子路径
  public String json;
  public int headerRowIndex = 0;// 列标题所在行的索引号，默认为第一行

  @Override
  public String execute() throws Exception {
    JSONObject json = new JSONObject();
    try {
      String fileName = Attach.DATA_REAL_PATH + "/" + file;
      if (logger.isInfoEnabled()) {
        logger.info("file=" + fileName);
      }
      String ext = StringUtils.getFilenameExtension(file);

      // 读取第一个工作表
      Sheet sheet;
      if ("xls".equalsIgnoreCase(ext)) {// Excel 2003 文件的处理
        sheet = new HSSFWorkbook(new FileInputStream(fileName)).getSheetAt(0);
      } else if ("xlsx".equalsIgnoreCase(ext)) {// Excel 2007+ 文件的处理
        sheet = new XSSFWorkbook(new FileInputStream(fileName)).getSheetAt(0);
      } else {// 不支持的文件类型
        throw new CoreException("unsupport file type: file=" + file);
      }

      // 解析工作表，获取数据
      List<Map<String, Object>> data = getSheetData(sheet, ext);

      // 记录列名
      if (!data.isEmpty()) {
        JSONArray columnNames = new JSONArray();
        for (String key : data.get(0).keySet()) {
          columnNames.put(key);
        }
        json.put("columnNames", columnNames);
      }

      // 导入数据
      importData(data, json, ext);

      // 记录详细的异常处理信息
      addErrorDetail(json);

      // 设置默认的处理结果
      if (!json.has("success"))
        json.put("success", true);
      int totalCount = data.size();
      if (!json.has("totalCount"))
        json.put("totalCount", totalCount);

      // 处理结果的描述信息
      if (!json.has("msg")) {
        json.put("msg", buildImportMessage(totalCount, this.updateCount, errorItems != null ? errorItems.size() : 0));
      }
    } catch (Exception e) {
      logger.warn(e.getMessage(), e);
      json.put("success", false);
      json.put("msg", e.getMessage());
    }

    this.json = json.toString();
    return "json";
  }

  /**
   * 构建处理结果的描述信息
   *
   * @param totalCount  总处理数
   * @param updateCount 更新的条目数
   * @param errorCount  错误条目数
   */
  protected String buildImportMessage(int totalCount, int updateCount, int errorCount) {
    String msg = "总共" + totalCount + "条数据";
    if (errorCount > 0) {
      if (updateCount > 0) {
        msg += "，成功导入" + (totalCount - updateCount - errorCount)
          + "条新数据，更新" + updateCount + "条现有数据，" + errorCount + "条数据存在异常没有导入！";
      } else {
        msg += "，成功导入" + (totalCount - errorCount) + "条新数据，" + errorCount + "条数据存在异常没有导入！";
      }
    } else {
      if (updateCount > 0) {
        msg += "，成功导入" + (totalCount - updateCount) + "条新数据，更新" + updateCount + "条现有数据！";
      } else {
        msg += "，成功导入" + totalCount + "条新数据！";
      }
    }
    return msg;
  }

  /**
   * 从 Excel2003 版文件导入数据
   *
   * @param sheet    包含数据的工作表
   * @param fileType Excel文件类型：xls或xlsx
   */
  protected List<Map<String, Object>> getSheetData(Sheet sheet, String fileType) throws Exception {
    // 获取标题行：从该行第一个单元格开始搜索直到单元格值为空止，从而确定最大的列数
    Row row = sheet.getRow(headerRowIndex);
    List<String> columnNames = new ArrayList<>();// 列标题的值列表：单元格的字符串值
    Cell cell;
    int i = 0;
    String columnName;
    boolean hasData;
    do {
      cell = row.getCell(i);
      if (cell != null) {
        if (isMergedColumn(sheet, headerRowIndex, i)) { // 属于合并行单元格
          columnName = (cell.getStringCellValue().trim().length() == 0 ? row.getCell(i - 1).getStringCellValue() : cell.getStringCellValue())
            + sheet.getRow(headerRowIndex + 1).getCell(i).getStringCellValue();
        } else {
          columnName = cell.getStringCellValue();
        }
        hasData = (columnName != null && columnName.length() > 0);
        if (hasData)
          columnNames.add(columnName.replaceAll("\\r\\n|\\r|\\n", ""));
      } else {
        hasData = false;
      }
      i++;
    } while (hasData);
    if (logger.isInfoEnabled())
      logger.info("columnCount={}", columnNames.size());

    // 验证Excel文件的标题行是否符合规范
    this.validateHeaderRow(columnNames, row, headerRowIndex);

    // 获取数据行：从该行开始搜索直到空行为空止，从而确定最大的行数
    List<Map<String, Object>> data = new ArrayList<>();
    Map<String, Object> rowData;
    i = getDataRowIndex();
    do {
      rowData = getRowData(sheet.getRow(i), columnNames, fileType);
      if (rowData != null) {
        data.add(rowData);
      }
      i++;
    } while (rowData != null);
    if (logger.isInfoEnabled())
      logger.info("dataCount={}", data.size());

    // 返回获取的数据
    if (logger.isInfoEnabled())
      logger.info("columns={}", StringUtils.collectionToCommaDelimitedString(columnNames));
    if (logger.isDebugEnabled())
      logger.debug("data={}", data);
    return data;
  }

  /**
   * 获取表格第一条数据行索引号
   *
   * @return 索引号
   */
  protected int getDataRowIndex() {
    return headerRowIndex + 1;
  }

  /**
   * 验证当前单元格是否属于合并行单元格
   *
   * @param sheet  包含数据的工作表
   * @param row    当前行
   * @param column 当前列
   * @return true-属于合并行单元格；false-不属于
   */
  protected boolean isMergedColumn(Sheet sheet, int row, int column) {
    return false;
  }


  /**
   * 验证 Excel 文件的标题行是否符合规范
   * <p>子类可以复写此方法提供自定义的验证</p>
   *
   * @param columnNames    标题行被解析出的列名列表
   * @param row            标题行
   * @param headerRowIndex 标题行所在行的索引号
   */
  protected void validateHeaderRow(List<String> columnNames, Row row, int headerRowIndex) {
    // do nothing
  }

  /**
   * 获取行的数据
   *
   * @param row         要处理的行
   * @param columnNames 列标题
   * @param fileType    Excel文件类型：xls或xlsx
   */
  protected Map<String, Object> getRowData(Row row, List<String> columnNames, String fileType) {
    if (row == null)
      return null;
    Map<String, Object> rowData = new LinkedHashMap<>();// 值为单元格的值
    Cell cell;
    Object cellValue;
    for (int i = 0; i < columnNames.size(); i++) {
      cell = row.getCell(i);
      cellValue = (cell == null ? null : getCellValue(cell, columnNames.get(i), fileType));
      if (i == 0) {// 首个单元格为空就当成是数据行的结束行而退出
        if (cellValue == null || cellValue.toString().trim().isEmpty()) {
          return null;
        }
      }
      rowData.put(columnNames.get(i), cellValue);
    }
    return rowData;
  }

  /**
   * 获取单元格的值：默认只返回字符串值
   *
   * @param cell       单元格
   * @param columnName 单元格所在列的标题名称
   * @param fileType   Excel文件类型：xls或xlsx
   */
  protected Object getCellValue(Cell cell, String columnName, String fileType) {
    if (cell == null)
      return null;
    if (cell.getCellType() == CellType.STRING) {// 字符串
      // 列名去空格
      if (cell.getStringCellValue() != null) {
        return cell.getStringCellValue().trim();
      } else {
        return cell.getStringCellValue();
      }
    } else if (cell.getCellType() == CellType.BOOLEAN) {// 布尔
      return cell.getBooleanCellValue();
    } else if (cell.getCellType() == CellType.NUMERIC) {// 数字
      return cell.getNumericCellValue();
    } else if (cell.getCellType() == CellType.BLANK) {
      return null;
    } else if (cell.getCellType() == CellType.ERROR) {
      return cell.getErrorCellValue();
    } else if (cell.getCellType() == CellType.FORMULA) {// 公式
      return cell.getStringCellValue();
    } else {// 日期
      return cell.getDateCellValue();
    }
  }

  /**
   * 导入数据
   *
   * @param data 要处理的数据
   * @param json [可选]返回结果信息的包装，用户可以自定义进行控制
   */
  abstract protected void importData(List<Map<String, Object>> data, JSONObject json, String fileType) throws JSONException;

  /**
   * 获取单元格的日期值
   */
  protected Calendar getCellCalendar(Cell cell) {
    if (cell.getCellType() == CellType.NUMERIC) {// xls中为日期类型时，jxls对应数字类型
      Date d = cell.getDateCellValue();
      if (d != null) {
        Calendar c = Calendar.getInstance();
        c.setTime(cell.getDateCellValue());
        return c;
      } else {
        return null;
      }
    } else if (cell.getCellType() == CellType.STRING) {// 将字符串转换为数字
      String d = cell.getStringCellValue();
      if (d != null && !d.isEmpty()) {
        return DateUtils.getCalendarEx(d);
      } else {
        return null;
      }
    } else {
      logger.warn("Error format to Calendar:{}", cell);
      return null;
    }
  }

  /**
   * 显示导入处理的详细异常信息
   */
  public String showDetail() throws Exception {
    return SUCCESS;
  }

  // 创建导入数据异常的详细信息
  protected void addErrorDetail(JSONObject json) throws JSONException {
    if (errorItems != null && !errorItems.isEmpty()) {
      JSONArray ejs = new JSONArray();
      for (Map<String, Object> m : errorItems) {
        ejs.put(convertErrorItem2Json(m));
      }
      json.put("detail", ejs);
      logger.debug("error:{}", ejs);
    }
  }

  /**
   * 转换异常信息对象为json对象
   */
  protected JSONObject convertErrorItem2Json(Map<String, Object> m) throws JSONException {
    JSONObject ej;
    ej = new JSONObject();
    for (Entry<String, Object> entry : m.entrySet()) {
      ej.put(entry.getKey(), formatErrorItemValue(entry.getKey(), entry.getValue()));
    }
    return ej;
  }

  /**
   * 格式化指定的值
   *
   * @param key   键
   * @param value 值
   */
  protected Object formatErrorItemValue(String key, Object value) {
    return value;
  }

  /**
   * 数据更新的条目数
   */
  protected int updateCount = 0;

  /**
   * 错误的数据处理条目
   */
  protected List<Map<String, Object>> errorItems = new ArrayList<>();

  /**
   * 与系统中现有数据完全相同而被忽略数据的条目数
   */
  protected int ignoreCount = 0;

  /**
   * 添加一条导入数据异常的信息
   *
   * @param map   要处理的数据条目
   * @param index 数据条目的索引号
   * @param msg   指定的处理异常信息
   */
  protected void addErrorItem(Map<String, Object> map, int index, String msg) {
    map.put("index", index);// 索引号
    map.put("msg", msg);
    errorItems.add(map);
  }
}