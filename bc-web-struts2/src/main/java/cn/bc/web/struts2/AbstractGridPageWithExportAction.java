package cn.bc.web.struts2;

import cn.bc.Context;
import cn.bc.core.exception.CoreException;
import cn.bc.core.util.DateUtils;
import cn.bc.web.struts2.event.ExportViewDataEvent;
import cn.bc.web.ui.html.grid.*;
import cn.bc.web.util.WebUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 带导出功能的视图的抽象Action
 *
 * @author dragon
 */
public abstract class AbstractGridPageWithExportAction<T extends Object> extends AbstractGridPageAction<T> implements ApplicationEventPublisherAware {
  private static final long serialVersionUID = 1L;
  private final static Logger logger = LoggerFactory.getLogger("cn.bc.web.struts2.AbstractGridPageWithExportAction");
  private ApplicationEventPublisher eventPublisher;

  // 导出视图数据需要的变量
  public boolean exporting;// 标记当前处于导出状态
  public String exportKeys;// 要导出列的标识，用逗号连接多个
  public String fileName;// 下载的文件名
  public InputStream inputStream;// 下载文件对应的流
  public String contentType = "application/vnd.ms-excel";// 下载文件类型定义
  public int bufferSize = 4096;
  public String inputName = "inputStream";// 文件输出流定义
  public String contentDisposition;// 下载文件处理方法

  public int exportScope;
  public String exportFormat;
  public String[] field;

  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.eventPublisher = applicationEventPublisher;
  }

  // 导出表格的数据为excel文件
  public String export() throws Exception {
    Date startTime = new Date();
    // 确定下载文件的名称(解决跨浏览器中文文件名乱码问题)
    if (this.fileName == null || this.fileName.length() == 0) this.fileName = this.getDefaultExportFileName();
    String title = this.fileName;

    // 确定下载文件处理方法：以附件方式下载
    this.contentDisposition = createContentDisposition(title);

    if (logger.isDebugEnabled()) {
      logger.debug("exportKeys=" + exportKeys);
      logger.debug("fileName=" + fileName);
      logger.debug("contentType=" + contentType);
      logger.debug("bufferSize=" + bufferSize);
      logger.debug("contentDisposition=" + contentDisposition);
    }

    // TODO 根据导出条件构建
    if (this.getPage() != null) {   // 分页的处理
      this.setPage(this.findPage());
      this.es = this.getPage().getData();
    } else {                        // 非分页的处理
      this.es = this.findList();
    }

    // 导出数据到 Excel
    if (logger.isDebugEnabled()) logger.debug("1:" + DateUtils.getWasteTime(startTime));
    GridExporter exporter = buileGridExporter(title, getText("label.idLabel"));
    ByteArrayOutputStream out = new ByteArrayOutputStream(this.bufferSize);
    if (logger.isDebugEnabled()) logger.debug("2:" + DateUtils.getWasteTime(startTime));
    exporter.exportTo(out);
    if (logger.isDebugEnabled()) logger.debug("3:" + DateUtils.getWasteTime(startTime));
    byte[] data = out.toByteArray();
    this.inputStream = new ByteArrayInputStream(data);

    // 发布用户导出数据事件
    ExportViewDataEvent event = new ExportViewDataEvent(this, getModuleType(), "0", title, "xls", data);
    this.eventPublisher.publishEvent(event);

    return "export";

    // Content-Type参考:
    // 163下载.doc：application/msword
    // 163下载.xls：application/vnd.ms-excel
    // 163下载.txt：text/plain
    // 163下载.html：text/html
    // 163下载.rar|.reg：application/octet-stream
    // application/x-msdownload
  }

  protected String createContentDisposition(String fileName) {
    String name;
    if (fileName == null || fileName.isEmpty()) name = "Export.xlsx";
    else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) name = fileName;
    else name = fileName + ".xlsx";
    return WebUtils.buildContentDisposition("attachment", name);
  }

  /**
   * 获取模块的标识字符串，通常为Domain的类名
   */
  protected String getModuleType() {
    return StringUtils.capitalize(this.getFormActionName());
  }

  /**
   * 构建一个表格导出器
   *
   * @param title   标题
   * @param idLabel id列的标题名称
   */
  protected GridExporter buileGridExporter(String title, String idLabel) {
    GridExporter exporter = new GridExporter();
    exporter.setColumns(this.getExportColumns()).setTitle(title).setData(this.es).setIdLabel(idLabel);

    // 扩展额外的导出数据
    Map<String, Object> map = new HashMap<>();
    this.extendGridExportData(map);
    exporter.addExtra(map);

    exporter.setTemplateFile(getExportTemplate());
    return exporter;
  }

  /**
   * 扩展视图的导出参数
   *
   * @param extras 已经初始化好的Map
   */
  protected void extendGridExportData(Map<String, Object> extras) {
    // 默认使用视图的扩展参数
    JSONObject json = getGridExtrasData();
    if (json == null || json.length() == 0) return;
    Iterator keys = json.keys();
    String key;
    try {
      while (keys.hasNext()) {
        key = (String) keys.next();
        extras.put(key, json.get(key));
      }
    } catch (JSONException e) {
      throw new CoreException(e);
    }
  }

  protected InputStream getExportTemplate() {
    return null;
  }

  /**
   * 获取需要导出到Excel的列配置，默认使用同视图的配置一样
   */
  protected List<Column> getExportColumns() {
    return this.getGridColumns().stream()
      .filter(column -> this.isExportColumn(column.getId()) || column instanceof IdColumn)
      .collect(Collectors.toList());
  }

  /**
   * 判断指定的列是否应该导出
   * <ul>
   * 返回true的情况：
   * <li>
   * 1)非导出状态</li>
   * <li>
   * 2)导出状态，用户没有排除某些列不导出</li>
   * <li>
   * 3)导出状态，用户选定的某些列</li>
   * </ul>
   *
   * @param key 列的标识
   */
  protected boolean isExportColumn(String key) {
    return (this.exportKeys == null || this.exportKeys.isEmpty())  // 导出所有列
      || (key != null && this.exportKeys.contains(key));     // 用户指定要导出的列
  }

  /**
   * 获取默认的导出文件名
   */
  protected String getDefaultExportFileName() {
    return getText("export.default.fileName") + getText(StringUtils.uncapitalize(getModuleType()));// 默认的文件名
  }

  @Override
  protected void extendGridFooterButton(GridFooter gridFooter) {
    // 导出按钮
    if (this.canExport()) gridFooter.addButton(getDefaultExportButton());

    // 导入按钮
    gridFooter.addButton(this.getGridFooterImportButton());

    // 打印按钮
    // gridFooter.addButton(GridFooter.getDefaultPrintButton(getText("label.print")));
  }

  /**
   * 判断用户能否导出数据：非移动设备默认可以导出，对移动设备只有岗位"在移动设备中下载数据"内的人可以导出
   */
  protected boolean canExport() {
    Context c = this.getContext();
    if (Boolean.TRUE.equals(c.getAttr("mobile"))) {// 移动设备
      @SuppressWarnings("unchecked")
      List<String> groups = c.getAttr("groups");
      return groups != null && groups.contains("DownloadFromMobile");
    }
    return true;
  }

  /**
   * 默认的导出按钮
   */
  protected FooterButton getDefaultExportButton() {
    return GridFooter.getDefaultExportButton(getText("label.export"));
  }

  /**
   * 获取导入按钮的配置，默认没有导入按钮
   */
  protected FooterButton getGridFooterImportButton() {
    return null;
  }

  /**
   * 获取导入按钮的配置，默认没有导入按钮
   */
  protected FooterButton getDefaultGridFooterImportButton() {
    return GridFooter.getDefaultImportButton(getText("label.import"));
  }
}