package cn.bc.web.ui.html.grid;

import cn.bc.core.export.Exporter;
import cn.bc.core.util.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.ExpressionParser;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Grid数据的Excel导出实现
 *
 * @author dragon
 */
public class GridExporter implements Exporter {
  private static final Log logger = LogFactory.getLog(GridExporter.class);
  private InputStream templateFile; // 模板文件
  private String title; // 标题
  private String idLabel; // id列的列标题
  private List<Column> columns;
  private List<? extends Object> data;
  private ExpressionParser parser;
  private Map<String, Object> extras;// 额外的数据

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

  /**
   * 获取以添加的额外数据
   *
   * @return
   */
  public Map<String, Object> getExtras() {
    return extras;
  }

  /**
   * 添加一个额外数据
   *
   * @param key   键
   * @param value 值
   */
  public GridExporter addExtra(String key, Object value) {
    if (this.extras == null) this.extras = new HashMap<String, Object>();
    this.extras.put(key, value);
    return this;
  }

  /**
   * 添加一份额外数据
   *
   * @param extras
   */
  public GridExporter addExtra(Map<String, Object> extras) {
    if (this.extras == null) this.extras = new HashMap<String, Object>();
    this.extras.putAll(extras);
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
    InputStream is = this.getTemplateFile();
    if (is == null)
      is = new ClassPathResource("cn/bc/web/template/export.xlsx")
        .getInputStream();// 使用默认的模板

    if (logger.isDebugEnabled())
      logger.debug("exportTo 1:" + DateUtils.getWasteTime(startTime));

    JxlsHelper.getInstance().processTemplate(is, outputStream, new Context(this.parseData()));
    if (logger.isDebugEnabled())
      logger.debug("exportTo 2:" + DateUtils.getWasteTime(startTime));
    is.close();
    outputStream.flush();
    outputStream.close();
  }

  public void exportTo(String outputFile) throws Exception {
    FileOutputStream targetStream = new FileOutputStream(outputFile);
    JxlsHelper.getInstance().processTemplate(
      this.getTemplateFile(),
      targetStream,
      new Context(this.parseData())
    );
    this.getTemplateFile().close();
    targetStream.flush();
    targetStream.close();
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
    Date now = new Date();
    map.put("exportTime", DateUtils.formatDateTime(now));
    map.put("exportTime_", now);

    // 标题
    map.put("title", this.getTitle());

    // 表头
    Collection<String> columnNames = new ArrayList<String>();
    for (Column column : columns) {
      if (column instanceof IdColumn) {
        columnNames.add(idLabel != null ? idLabel : "序号");
      } else if (column instanceof HiddenColumn) {
        columnNames.add("");// 空白
      } else {
        columnNames.add(column.getLabel());
      }
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
        if (column instanceof HiddenColumn) {// 忽略隐藏列
          continue;
        } else if (column instanceof IdColumn) {
          value = i;
        } else {
          Object srcValue = GridData.getValue(rowData, column.getValueExpression(), parser);
          value = GridData.formatValue2Label(rowData, srcValue, column.getValueFormater());
        }
        row.add(value);
      }
      rows.add(row);
      i++;
    }
    map.put("rowDatas", rows);

    if (this.extras != null) {
      map.putAll(this.extras);
    }

    if (logger.isDebugEnabled())
      logger.debug("parseData:" + DateUtils.getWasteTime(startTime));
    return map;
  }
}
