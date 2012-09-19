package cn.bc.docs.web.struts2;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import cn.bc.core.exception.CoreException;
import cn.bc.docs.domain.Attach;

import com.google.gson.JsonObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 导入数据的Action基类
 * 
 * @author dragon
 * 
 */
public abstract class ImportDataAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory.getLog(ImportDataAction.class);

	public String file;// 用户上传数据文件的路径，相对于 Attach.DATA_REAL_PATH 目录下的子路径
	public String json;
	public int headerRowIndex = 0;// 列标题所在行的索引号，默认为第一行

	@Override
	public String execute() throws Exception {
		JsonObject json = new JsonObject();
		try {
			String fileName = Attach.DATA_REAL_PATH + "/" + file;
			if (logger.isInfoEnabled()) {
				logger.info("file=" + fileName);
			}
			String ext = StringUtils.getFilenameExtension(file);

			// 读取第一个工作表
			Sheet sheet = null;
			if ("xls".equalsIgnoreCase(ext)) {// Excel 2003 文件的处理
				sheet = new HSSFWorkbook(new FileInputStream(fileName))
						.getSheetAt(0);
			} else if ("xlsx".equalsIgnoreCase(ext)) {// Excel 2007+ 文件的处理
				sheet = new XSSFWorkbook(new FileInputStream(fileName))
						.getSheetAt(0);
			} else {// 不支持的文件类型
				throw new CoreException("unsupport file type: file=" + file);
			}

			// 解析工作表，获取数据
			List<Map<String, Object>> data = getSheetData(sheet, ext);

			// 导入数据
			importData(data, json, ext);

			// 设置默认的处理结果
			if (!json.has("success"))
				json.addProperty("success", true);
			if (!json.has("msg"))
				json.addProperty("msg", "成功导入" + data.size() + "条数据！");
			if (!json.has("totalCount"))
				json.addProperty("totalCount", data.size());
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			json.addProperty("success", false);
			json.addProperty("msg", e.getMessage());
		}

		this.json = json.toString();
		return "json";
	}

	/**
	 * 从 Excel2003 版文件导入数据
	 * 
	 * @param sheet
	 *            包含数据的工作表
	 * @param fileType
	 *            Excel文件类型：xls或xlsx
	 * @throws Exception
	 */
	protected List<Map<String, Object>> getSheetData(Sheet sheet,
			String fileType) throws Exception {
		// 获取标题行：从该行第一个单元格开始搜索直到单元格值为空止，从而确定最大的列数
		Row row = sheet.getRow(headerRowIndex);
		List<String> columnNames = new ArrayList<String>();// 列标题的值列表：单元格的字符串值
		Cell cell;
		int i = 0;
		String columnName;
		boolean hasData;
		do {
			cell = row.getCell(i);
			if (cell != null) {
				columnName = cell.getStringCellValue();
				hasData = (columnName != null && columnName.length() > 0);
				if (hasData)
					columnNames.add(columnName);
			} else {
				hasData = false;
			}
			i++;
		} while (hasData);
		if (logger.isInfoEnabled())
			logger.info("columnCount=" + columnNames.size());

		// 获取数据行：从该行开始搜索直到空行为空止，从而确定最大的行数
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> rowData;
		i = headerRowIndex + 1;
		do {
			rowData = getRowData(sheet.getRow(i), columnNames, fileType);
			if (rowData != null) {
				data.add(rowData);
			}
			i++;
		} while (rowData != null);
		if (logger.isInfoEnabled())
			logger.info("dataCount=" + data.size());

		// 返回获取的数据
		if (logger.isInfoEnabled())
			logger.info("columns="
					+ StringUtils.collectionToCommaDelimitedString(columnNames));
		if (logger.isDebugEnabled())
			logger.debug("data=" + data);
		return data;
	}

	/**
	 * 获取行的数据
	 * 
	 * @param row
	 *            要处理的行
	 * @param columnNames
	 *            列标题
	 * @param fileType
	 *            Excel文件类型：xls或xlsx
	 * @return
	 */
	private Map<String, Object> getRowData(Row row, List<String> columnNames,
			String fileType) {
		if (row == null)
			return null;
		Map<String, Object> rowData = new LinkedHashMap<String, Object>();// 值为单元格的值
		Cell cell;
		Object cellValue;
		for (int i = 0; i < columnNames.size(); i++) {
			cell = row.getCell(i);
			if (cell == null) {
				rowData.put(columnNames.get(i), null);
				continue;
			} else {
				cellValue = getCellValue(cell, columnNames.get(i), fileType);
				if (i == 0) {// 首个单元格为空就当成是数据行的结束行而退出
					if (cellValue == null
							|| (cellValue instanceof String && cellValue
									.toString().isEmpty())) {
						return null;
					}
				} else {
					rowData.put(columnNames.get(i), cellValue);
				}
			}
		}
		return rowData;
	}

	/**
	 * 获取单元格的值：默认只返回字符串值
	 * 
	 * @param cell
	 *            单元格
	 * @param columnName
	 *            单元格所在列的标题名称
	 * @param fileType
	 *            Excel文件类型：xls或xlsx
	 * @return
	 */
	protected Object getCellValue(Cell cell, String columnName, String fileType) {
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {// 字符串
			return cell.getStringCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {// 布尔
			return cell.getBooleanCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {// 数字
			return cell.getNumericCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return null;
		} else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
			return cell.getErrorCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {// 公式
			return cell.getStringCellValue();
		} else {// 日期
			return cell.getDateCellValue();
		}
	}

	/**
	 * 导入数据
	 * 
	 * @param data
	 *            要处理的数据
	 * @param json
	 *            [可选]返回结果信息的包装，用户可以自定义进行控制
	 */
	abstract protected void importData(List<Map<String, Object>> data,
			JsonObject json, String fileType);
}
