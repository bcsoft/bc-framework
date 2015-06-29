package cn.bc.web.ws.dotnet.converter;

import cn.bc.core.exception.CoreException;
import cn.bc.web.formater.Formater;
import cn.bc.web.ws.converter.WSConverter;
import cn.bc.web.ws.dotnet.Cell;
import cn.bc.web.ws.dotnet.Column;
import cn.bc.web.ws.dotnet.DataSet;
import cn.bc.web.ws.dotnet.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用WebService接口返回的XML信息到对象的转换器
 *
 * @author dragon
 */
public class DataSetConverter implements WSConverter<DataSet> {
	private static final Logger logger = LoggerFactory.getLogger(DataSetConverter.class);
	private String columnSelector = "//*[@minOccurs]";
	private String rowSelector = "//Table";
	private String msgSelector = "//strMsg";
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
		if (logger.isDebugEnabled()) {
			logger.debug("----xml----start");
			logger.debug(xml);
			logger.debug("----xml----end");
		}
		try {
			DataSet dataSet = new DataSet();

			// 使用 xpath 解析 xml文档 （使用 jsoup1.8.2 无法解析中文节点名称）
			InputSource source = new InputSource(new StringReader(xml));
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			//domFactory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(source);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr;

			// 列头转换
			Column column;
			expr = xpath.compile(this.getColumnSelector());
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			String type;
			int index;
			Node n;
			NamedNodeMap attrs;
			for (int i = 0; i < nodes.getLength(); i++) {
				n = nodes.item(i);
				attrs = n.getAttributes();
				type = attrs.getNamedItem("type").getTextContent();
				index = type.indexOf(":");
				column = new Column(attrs.getNamedItem("name").getTextContent().toLowerCase(),
						index != -1 ? type.substring(index + 1) : type);// 去除前缀xs
				dataSet.addColumn(column);

				// 默认的值转换器
				if (this.getFormaters() != null) {
					Formater<?> formater = this.getFormaters().get(column.getType());
					if (formater != null) {
						column.setFormater(formater);
					}
				}
			}

			// 数据转换
			expr = xpath.compile(this.getRowSelector());
			nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			logger.debug("rows: size={}, html={}", nodes.getLength(), nodes);
			Row row;
			Cell cell;
			String cellName;
			Object value;
			NodeList cellNodes;
			Node cellNode;
			for (int i = 0; i < nodes.getLength(); i++) {
				n = nodes.item(i);
				cellNodes = n.getChildNodes();
				row = new Row();
				for (int j = 0; j < cellNodes.getLength(); j++) {
					cellNode = cellNodes.item(j);
					cellName = cellNode.getNodeName().toLowerCase();
					column = dataSet.getColumn(cellName);
					value = cellNode.getTextContent();
					if (column == null) {
						throw new CoreException("undefined column: name=" + cellName);
					}
					if (column.getFormater() != null) {
						value = column.getFormater().format(n, value);
					}
					cell = new Cell(cellName, value);
					row.addCell(cell);
				}
				dataSet.addRow(row);
			}

			// 错误信息
			if (StringUtils.hasLength(getMsgSelector())) {
				expr = xpath.compile(this.getMsgSelector());
				Node msgNode = (Node) expr.evaluate(doc, XPathConstants.NODE);
				String msg = msgNode.getTextContent();
				if (msg != null && !msg.isEmpty()) dataSet.setMsg(msg);
			}
			return dataSet;
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new CoreException(e.getMessage(), e);
		}
	}
}
