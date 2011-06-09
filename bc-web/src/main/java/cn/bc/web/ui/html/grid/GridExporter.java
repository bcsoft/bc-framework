package cn.bc.web.ui.html.grid;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
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
	private String templateFile; // 模板文件
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

	public String getTemplateFile() {
		return templateFile;
	}

	public GridExporter setTemplateFile(String templateFile) {
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
		if (logger.isDebugEnabled())
			logger.debug("templateFile=" + this.getTemplateFile());
		XLSTransformer transformer = new XLSTransformer();
		InputStream is;
		if (this.getTemplateFile() == null)
			is = new ClassPathResource("cn/bc/web/template/export.xls")
					.getInputStream();// 使用默认的模板
		else
			is = new BufferedInputStream(new FileInputStream(
					this.getTemplateFile()));

		transformer.transformXLS(is, this.parseData()).write(outputStream);
		is.close();
		outputStream.flush();
		outputStream.close();
	}

	public void exportTo(String outputFile) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("templateFile=" + this.getTemplateFile());
		XLSTransformer transformer = new XLSTransformer();
		transformer.transformXLS(this.getTemplateFile(), this.parseData(),
				outputFile);
	}

	/**
	 * 将Grid的数据转换为默认Excel模板需要的数据
	 * 
	 * @return
	 */
	protected Map<String, Object> parseData() {
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
		Collection<Collection<String>> rows = new ArrayList<Collection<String>>();
		Collection<String> row;
		String value;
		int i = 1;
		for (Object obj : data) {
			row = new ArrayList<String>();
			for (Column column : columns) {
				if (column instanceof IdColumn) {
					value = i + "";
					row.add(value);
				} else {
					value = GridData.getValue(obj, column.getExpression(),
							parser, column.getFormater());
					row.add(value == null || value.length() == 0 ? "" : value);
				}
			}
			rows.add(row);
			i++;
		}
		map.put("rowDatas", rows);

		return map;
	}
}
