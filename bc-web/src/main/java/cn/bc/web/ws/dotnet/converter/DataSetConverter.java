package cn.bc.web.ws.dotnet.converter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import cn.bc.core.exception.CoreException;
import cn.bc.web.formater.Formater;
import cn.bc.web.ws.converter.WSConverter;
import cn.bc.web.ws.dotnet.Cell;
import cn.bc.web.ws.dotnet.Column;
import cn.bc.web.ws.dotnet.DataSet;
import cn.bc.web.ws.dotnet.Row;

/**
 * 调用WebService接口返回的XML信息到对象的转换器
 * 
 * @author dragon
 * 
 */
public class DataSetConverter implements WSConverter<DataSet> {
	private static final Log logger = LogFactory.getLog(DataSetConverter.class);
	private String columnSelector = "[minOccurs]";
	private String rowSelector = "Table";
	private String msgSelector = "strMsg";
	private Map<String, Formater<?>> formaters;// 单元格值的格式化处理器

	public Map<String, Formater<?>> getFormaters() {
		return formaters;
	}

	public void setFormaters(Map<String, Formater<?>> formaters) {
		this.formaters = formaters;
	}

	public DataSetConverter addFormater(String key, Formater<?> formater) {
		if (this.formaters == null)
			this.formaters = new HashMap<String, Formater<?>>();
		this.formaters.put(key, formater);
		return this;
	}

	public String getMsgSelector() {
		return msgSelector;
	}

	public void setMsgSelector(String msgSelector) {
		this.msgSelector = msgSelector;
	}

	public String getColumnSelector() {
		return columnSelector;
	}

	public void setColumnSelector(String columnSelector) {
		this.columnSelector = columnSelector;
	}

	public String getRowSelector() {
		return rowSelector;
	}

	public void setRowSelector(String rowSelector) {
		this.rowSelector = rowSelector;
	}

	public DataSet convert(String xml) {
		if (logger.isDebugEnabled()){
			logger.debug("----xml----start");
			logger.debug(xml);
			logger.debug("----xml----end");
		}
		DataSet dataSet = new DataSet();
		Document doc = Jsoup.parse(xml);

		// 列头转换
		Column column;
		Elements headerEls = doc.select(getColumnSelector());
		// System.out.println(headerEls.size());
		String type;
		int index;
		for (Element rowEl : headerEls) {
			type = rowEl.attr("type");
			index = type.indexOf(":");
			column = new Column(rowEl.attr("name").toLowerCase(),
					index != -1 ? type.substring(index + 1) : type);// 去除前缀xs
			dataSet.addColumn(column);

			// 默认的值转换器
			if (this.getFormaters() != null) {
				Formater<?> formater = this.getFormaters()
						.get(column.getType());
				if (formater != null) {
					column.setFormater(formater);
				}
			}
		}

		// 数据转换
		Elements rowEls = doc.select(getRowSelector());
		// System.out.println(rowEls.size());
		Row row;
		Cell cell;
		String cellName;
		Object value;
		for (Element rowEl : rowEls) {
			Elements cellEls = rowEl.children();
			row = new Row();
			for (Element cellEl : cellEls) {
				cellName = cellEl.tagName().toLowerCase();
				column = dataSet.getColumn(cellName);
				value = cellEl.html();
				if (column == null) {
					throw new CoreException("undenfined column:name="
							+ cellName);
				}
				if (column.getFormater() != null) {
					value = column.getFormater().format(rowEl, value);
				}
				cell = new Cell(cellName, value);
				row.addCell(cell);
			}
			dataSet.addRow(row);
		}

		// 错误信息
		if (StringUtils.hasLength(getMsgSelector())) {
			Elements msgEls = doc.select(getMsgSelector());
			if (!msgEls.isEmpty()) {
				dataSet.setMsg(msgEls.get(0).html());
			}
		}
		return dataSet;
	}
}
