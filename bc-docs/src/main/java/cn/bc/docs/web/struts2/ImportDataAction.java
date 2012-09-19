package cn.bc.docs.web.struts2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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
	public int hr = 0;// 标题行的行号

	@Override
	public String execute() throws Exception {
		JsonObject json = new JsonObject();
		try {
			String fileName = Attach.DATA_REAL_PATH + "/" + file;
			if (logger.isInfoEnabled()) {
				logger.info("file=" + fileName);
			}
			String ext = StringUtils.getFilenameExtension(file);
			if ("xls".equalsIgnoreCase(ext)) {// Excel 2003 文件的处理
				executeXls(fileName);
			} else if ("xlsx".equalsIgnoreCase(ext)) {// Excel 2007+ 文件的处理
				executeXlsx(fileName);
			} else {// 不支持的文件类型
				throw new CoreException("unsupport file type: file=" + file);
			}
			json.addProperty("success", true);
			json.addProperty("msg", "数据导入成功！");
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			json.addProperty("success", false);
			json.addProperty("msg", e.getMessage());
		}

		this.json = json.toString();
		return "json";
	}

	/**
	 * 从 Excel2007+ 版文件导入数据
	 * 
	 * @param fileName
	 *            文件全路径名
	 * @throws Exception
	 */
	protected void executeXlsx(String fileName) throws Exception {
		throw new CoreException("xlsx did not support now.");
	}

	/**
	 * 从 Excel2003 版文件导入数据
	 * 
	 * @param fileName
	 *            文件全路径名
	 * @throws Exception
	 */
	protected void executeXls(String fileName) throws Exception {
		InputStream is = new FileInputStream(fileName);
		HSSFWorkbook workbook = new HSSFWorkbook(is);
		HSSFSheet sheet = workbook.getSheetAt(0);// 仅处理第一个工作表

		// 获取标题行
		HSSFRow row = sheet.getRow(hr);
		int columnNum = row.getLastCellNum();// 1-based
		if (logger.isInfoEnabled())
			logger.info("columnCount=" + columnNum);
		String[] columnNames = new String[columnNum];// 列标题的值列表：单元格的字符串值
		Cell cell;
		for (int i = 0; i < columnNum; i++) {
			cell = row.getCell(i);
			if (cell == null) {
				throw new CoreException("标题行不允许出现空的单元格!index=" + i);
			}
			columnNames[i] = cell.getStringCellValue();
		}

		// 获取数据行
		int maxRowNum = sheet.getLastRowNum();// 0-based
		if (logger.isInfoEnabled())
			logger.info("rowCount=" + (maxRowNum + 1));
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int rowNum = hr + 1; rowNum <= maxRowNum; rowNum++) {
			data.add(getXlsRowData(sheet.getRow(rowNum), columnNames));
		}

		// 处理数据
		if (logger.isInfoEnabled())
			logger.info("columns="
					+ StringUtils.arrayToCommaDelimitedString(columnNames));
		if (logger.isDebugEnabled())
			logger.debug("data=" + data);
		this.importData(data);
	}

	/**
	 * 获取行的数据
	 * 
	 * @param row
	 *            要处理的行
	 * @param heads
	 *            列标题
	 * @return
	 */
	private Map<String, Object> getXlsRowData(HSSFRow row, String[] heads) {
		int maxCellIndex = heads.length - 1;// row.getLastCellNum();
		Map<String, Object> rowData = new LinkedHashMap<String, Object>();// 值为单元格的值
		Cell cell;
		for (int i = 0; i <= maxCellIndex; i++) {
			cell = row.getCell(i);
			if (cell == null) {
				rowData.put(heads[i], null);
				continue;
			}
			rowData.put(heads[i], this.getXlsCellValue(cell, heads[i]));
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
	 * @return
	 */
	protected Object getXlsCellValue(Cell cell, String columnName) {
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
	 */
	abstract protected void importData(List<Map<String, Object>> data);
}
