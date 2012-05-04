package cn.bc.web.ui.html.grid;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.ExpressionParser;

import cn.bc.core.export.Exporter;
import cn.bc.core.util.DateUtils;

/**
 * Grid数据的Excel导出实现
 * 
 * @author dragon
 * 
 */
public class GridExporter implements Exporter {
	private static final Log logger = LogFactory.getLog(GridExporter.class);
	private InputStream templateFile; // 模板文件
	private String title; // 标题
	private String idLabel; // id列的列标题
	private List<Column> columns;
	private List<? extends Object> data;
	private ExpressionParser parser;

	public GridExporter() {

	}

	public String getIdLabel() {
		return idLabel;
	}

	public GridExporter setIdLabel(String idLabel) {
		this.idLabel = idLabel;
		return this;
	}

	public ExpressionParser getParser() {
		return parser;
	}

	public GridExporter setParser(ExpressionParser parser) {
		this.parser = parser;
		return this;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public GridExporter setColumns(List<Column> columns) {
		this.columns = columns;
		return this;
	}

	public List<? extends Object> getData() {
		return data;
	}

	public GridExporter setData(List<? extends Object> data) {
		this.data = data;
		return this;
	}

	public InputStream getTemplateFile() {
		return templateFile;
	}

	public GridExporter setTemplateFile(InputStream templateFile) {
		this.templateFile = templateFile;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public GridExporter setTitle(String title) {
		this.title = title;
		return this;
	}

	public void exportTo(OutputStream outputStream) throws Exception {
		Date startTime = new Date();
		if (logger.isDebugEnabled())
			logger.debug("templateFile=" + this.getTemplateFile());
		XLSTransformer transformer = new XLSTransformer();
		InputStream is = this.getTemplateFile();
		if (is == null)
			is = new ClassPathResource("cn/bc/web/template/export.xls")
					.getInputStream();// 使用默认的模板

		if (logger.isDebugEnabled())
			logger.debug("exportTo 1:" + DateUtils.getWasteTime(startTime));
		transformer.transformXLS(is, this.parseData()).write(outputStream);
		if (logger.isDebugEnabled())
			logger.debug("exportTo 2:" + DateUtils.getWasteTime(startTime));
		is.close();
		outputStream.flush();
		outputStream.close();
	}

	public void exportTo(String outputFile) throws Exception {
		XLSTransformer transformer = new XLSTransformer();
		Workbook workbook = transformer.transformXLS(this.getTemplateFile(),
				this.parseData());
		OutputStream os = new BufferedOutputStream(new FileOutputStream(
				outputFile));
		workbook.write(os);
		this.getTemplateFile().close();
		os.flush();
		os.close();
	}

	/**
	 * 将Grid的数据转换为默认Excel模板需要的数据
	 * 
	 * @return
	 */
	protected Map<String, Object> parseData() {
		Date startTime = new Date();
		Map<String, Object> map = new HashMap<String, Object>();

		// 导出时间
		map.put("exportTime", DateUtils.formatDateTime(new Date()));

		// 标题
		map.put("title", this.getTitle());

		// 表头
		Collection<String> columnNames = new ArrayList<String>();
		for (Column column : columns) {
			if (column instanceof IdColumn)
				columnNames.add(idLabel != null ? idLabel : "序号");
			else
				columnNames.add(column.getLabel());
		}
		map.put("columnNames", columnNames);

		// 表格数据
		Collection<Collection<Object>> rows = new ArrayList<Collection<Object>>();
		Collection<Object> row;
		Object value;
		int i = 1;
		for (Object rowData : data) {
			row = new ArrayList<Object>();
			for (Column column : columns) {
				if (column instanceof IdColumn) {
					value = i;
					row.add(value);
				} else {
					value = GridData.formatValue2Label(
							rowData,
							GridData.getValue(rowData,
									column.getValueExpression(), parser),
							column.getValueFormater());
					//row.add(value == null || value.length() == 0 ? "" : value);
					//System.out.println(value.getClass());
					row.add(value);
				}
			}
			rows.add(row);
			i++;
		}
		map.put("rowDatas", rows);

		if (logger.isDebugEnabled())
			logger.debug("parseData:" + DateUtils.getWasteTime(startTime));
		return map;
	}
}
