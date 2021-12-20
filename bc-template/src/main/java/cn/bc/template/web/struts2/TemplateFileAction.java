/**
 *
 */
package cn.bc.template.web.struts2;

import cn.bc.core.exception.CoreException;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.FreeMarkerUtils;
import cn.bc.core.util.TemplateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.domain.AttachHistory;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.util.OfficeUtils;
import cn.bc.docs.web.AttachUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import cn.bc.template.util.DocxUtils;
import cn.bc.template.util.XlsUtils;
import cn.bc.template.util.XlsxUtils;
import cn.bc.web.util.WebUtils;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.*;

/**
 * 模板file处理Action
 *
 * @author lbj
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplateFileAction extends ActionSupport {
  private static Logger logger = LoggerFactory.getLogger(TemplateFileAction.class);
  private static final long serialVersionUID = 1L;
  private AttachService attachService;
  private TemplateService templateService;

  @Autowired
  public void setAttachService(AttachService attachService) {
    this.attachService = attachService;
  }

  @Autowired
  public void setTemplateService(TemplateService templateService) {
    this.templateService = templateService;
  }

  public Long id;// 模板id
  public String code; // 模板编码
  public String filename;
  public String contentType;
  public long contentLength;
  public InputStream inputStream;


  private static final int BUFFER = 4096;
  public String from;// 指定原始文件的类型，默认为文件扩展名
  public String to;// 预览时转换到的文件类型，默认为pdf
  public String path;// 附件的绝对路径名
  private String typeCode;// 模板类型编码

  public boolean custom = false;//自定义文本直接查看和下载的控制 ture表示直接,默认为flase

  // 下载附件
  public String download() throws Exception {
    Template template;

    // 加载一个流程附件对象
    if (id != null) {
      template = templateService.load(id);
      if (template == null)
        throw new CoreException("id:\"" + id + "\",not found template!");
    } else if (code != null && code.length() > 0) {
      template = templateService.loadByCode(code);
      if (template == null)
        throw new CoreException("code:\"" + code + "\",not found template!");
    } else {
      throw new CoreException("id or code is null!");
    }

    typeCode = template.getTemplateType().getCode();
    Date startTime = new Date();
    // 自定义文本下载处理
    if (typeCode.equals("custom")) {
      customDownload(template, startTime);
      return SUCCESS;
    }

    if (!template.isPureText() && (path == null || path.length() == 0))
      path = Attach.DATA_REAL_PATH + File.separator
        + Template.DATA_SUB_PATH + File.separator
        + template.getPath();

    // debug
    if (logger.isDebugEnabled()) {
      logger.debug("path=" + path);
      logger.debug("extension=" + template.getTemplateType().getExtension());
    }

    // 设置下载文件的参数
    this.contentType = AttachUtils.getContentType(template.getTemplateType().getExtension());
    this.filename = WebUtils.encodeFileName(
      ServletActionContext.getRequest(),
      template.getSubject().lastIndexOf(".") == -1 ? template
        .getSubject() + "." + template.getTemplateType().getExtension() : template
        .getSubject());
    // 获取文件流
    InputStream inputStream = template.getInputStream();
    ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER);
    //格式化操作
    if (template.isFormatted()) {
      // 声明格式化参数
      Map<String, Object> params;
      //docx
      if (typeCode.equals("word-docx")) {
        params = getParams(template);
        XWPFDocument docx = DocxUtils.format(inputStream, params);
        docx.write(out);
        this.inputStream = new ByteArrayInputStream(out.toByteArray());
        out.close();
        this.contentLength = this.inputStream.available();
        //xls
      } else if (typeCode.equals("xls")) {
        params = getParams(template);
        XlsUtils.formatTo(inputStream, out, params);
        this.inputStream = new ByteArrayInputStream(out.toByteArray());
        out.close();
        this.contentLength = this.inputStream.available();
        //xlsx
      } else if (typeCode.equals("xlsx")) {
        params = getParams(template);
        XlsxUtils.formatTo(inputStream, out, params);
        this.inputStream = new ByteArrayInputStream(out.toByteArray());
        out.close();
        this.contentLength = this.inputStream.available();
      } else {
        this.contentLength = inputStream.available();
        this.inputStream = inputStream;
      }
    } else {
      this.contentLength = inputStream.available();
      this.inputStream = inputStream;
    }

    if (logger.isDebugEnabled())
      logger.debug("download:" + DateUtils.getWasteTime(startTime));

    //附件保存相对路径
    String resolatePath = Template.DATA_SUB_PATH + File.separator
      + template.getPath();

    // 创建文件上传日志
    saveAttachHistory(template.getSubject(), AttachHistory.TYPE_DOWNLOAD, resolatePath,
      template.getTemplateType().getExtension(), Template.ATTACH_TYPE, template.getUid());
    return SUCCESS;
  }

  private void customDownload(Template template, Date startTime) throws Exception {
    // 设置下载文件的参数
    this.contentType = AttachUtils.getContentType("txt");
    this.filename = WebUtils.encodeFileName(ServletActionContext.getRequest(), template.getSubject() + ".txt");
    // 获取文件流
    InputStream inputStream = template.getInputStream();
    ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER);
    //格式化操作
    if (template.isFormatted() && !custom) {
      // 声明格式化参数
      Map<String, Object> params = getParams(template);
      String source = template.getContentEx(params);
      TemplateUtils.copy(source, out);
      this.inputStream = new ByteArrayInputStream(out.toByteArray());
      out.close();
      this.contentLength = this.inputStream.available();
    } else {
      this.contentLength = inputStream.available();//template.getSize();
      this.inputStream = inputStream;
    }

    if (logger.isDebugEnabled())
      logger.debug("download:" + DateUtils.getWasteTime(startTime));

    saveAttachHistory(template.getSubject(), AttachHistory.TYPE_DOWNLOAD, "",
      "txt", Template.ATTACH_TYPE + ".custom", template.getUid());
  }

  // 创建文件上传日志
  private void saveAttachHistory(String subject, int type, String resolatePat, String extension, String ptype, String puid) {
    if (ptype != null && puid != null) {
      AttachHistory history = new AttachHistory();
      history.setPtype(ptype);
      history.setPuid(puid);
      history.setType(type);
      history.setAuthor(SystemContextHolder.get().getUserHistory());
      history.setFileDate(Calendar.getInstance());
      history.setPath(resolatePat);
      history.setAppPath(false);
      history.setFormat(extension);
      history.setSubject(subject);
      String[] c = WebUtils.getClient(ServletActionContext.getRequest());
      history.setClientIp(c[0]);
      history.setClientInfo(c[2]);
      this.attachService.saveHistory(history);
    } else {
      logger.warn("没有指定ptype、puid参数，不保存文件上传记录");
    }
  }

  // 支持在线打开文档查看的文件下载
  public String inline() throws Exception {
    Template template;

    // 加载一个流程附件对象
    if (id != null) {
      template = templateService.load(id);
      if (template == null)
        throw new CoreException("id:\"" + id + "\",not found template!");
    } else if (code != null && code.length() > 0) {
      template = templateService.loadByCode(code);
      if (template == null)
        throw new CoreException("code:\"" + code + "\",not found template!");
    } else {
      throw new CoreException("id or code is null!");
    }

    typeCode = template.getTemplateType().getCode();
    Date startTime = new Date();

    // 自定义文本下载处理
    if (typeCode.equals("custom")) {
      customInline(template, startTime);
      return SUCCESS;
    }

    if (path == null || path.length() == 0)
      path = Attach.DATA_REAL_PATH + File.separator
        + Template.DATA_SUB_PATH + File.separator
        + template.getPath();

    // debug
    if (logger.isDebugEnabled()) {
      logger.debug("path=" + path);
      logger.debug("extension=" + template.getTemplateType().getExtension());
      logger.debug("to=" + to);
    }

    if (isConvertFile(template.getTemplateType().getExtension())) {
      // 获取文件流
      InputStream inputStream = template.getInputStream();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
        BUFFER);

      if (this.from == null || this.from.length() == 0)
        this.from = template.getTemplateType().getExtension();
      if (this.to == null || this.to.length() == 0)
        this.to = getText("jodconverter.to.extension");// 没有指定就是用系统默认的配置转换为pdf

      // 声明下载文件的参数
      byte[] bs = null;

      // 声明文件转换服务的控制 默认使用
      boolean officeConvert = true;
      // 声明需要转换的流
      InputStream is = null;
      // 声明格式化参数
      Map<String, Object> params;
      // 判断是否为可格式化类型
      //格式化操作
      if (template.isFormatted()) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //docx
        if (typeCode.equals("word-docx")) {
          params = getParams(template);
          XWPFDocument docx = DocxUtils.format(inputStream, params);
          docx.write(out);
          is = new ByteArrayInputStream(out.toByteArray());
          out.close();
          //xls
        } else if (typeCode.equals("xls")) {
          params = getParams(template);
          XlsUtils.formatTo(inputStream, out, params);
          is = new ByteArrayInputStream(out.toByteArray());
          out.close();
          //xlsx
        } else if (typeCode.equals("xlsx")) {
          params = getParams(template);
          XlsxUtils.formatTo(inputStream, out, params);
          is = new ByteArrayInputStream(out.toByteArray());
          out.close();
          //html
        } else if (typeCode.equals("html")) {
          this.to = template.getTemplateType().getExtension();
          params = getParams(template);
          // 添加系统上下文和时间戳的路径到替换参数
          SystemContext context = SystemContextHolder.get();
          params.put("htmlPageNamespace",
            context.getAttr(SystemContext.KEY_HTMLPAGENAMESPACE));
          params.put("appTs", context.getAttr(SystemContext.KEY_APPTS));
          bs = FreeMarkerUtils.format(TemplateUtils.loadText(inputStream), params).getBytes();
          // 不使用office转换服务
          officeConvert = false;
        } else
          is = inputStream;
      } else
        is = inputStream;


      if (officeConvert) {
        // convert
        OfficeUtils.convert(is, this.from, outputStream, this.to);
        // 设置下载文件的参数（设置不对的话，浏览器是不会直接打开的）
        bs = outputStream.toByteArray();
      }


      if (logger.isDebugEnabled())
        logger.debug("convert:" + DateUtils.getWasteTime(startTime));


      this.inputStream = new ByteArrayInputStream(bs);
      this.inputStream.close();
      this.contentType = AttachUtils.getContentType(this.to);
      this.contentLength = bs.length;
      this.filename = WebUtils.encodeFileName(
        ServletActionContext.getRequest(),
        (template.getSubject().lastIndexOf(".") == -1 ? template
          .getSubject() + "." + template.getTemplateType().getExtension()
          : template.getSubject()) + "." + this.to);
    } else {
      // 设置下载文件的参数
      this.contentType = AttachUtils.getContentType(template.getTemplateType().getExtension());
      this.filename = WebUtils.encodeFileName(
        ServletActionContext.getRequest(),
        template.getSubject().lastIndexOf(".") == -1 ? template
          .getSubject() + "." + template.getTemplateType().getExtension()
          : template.getSubject());

      // 无需转换的文档直接下载处理
      File file = new File(path);
      this.contentLength = file.length();
      this.inputStream = new FileInputStream(file);
    }

    //附件保存相对路径
    String resolatePath = Template.DATA_SUB_PATH + File.separator
      + template.getPath();
    // 创建文件上传日志
    saveAttachHistory(template.getSubject(), AttachHistory.TYPE_INLINE, resolatePath,
      template.getTemplateType().getExtension(), Template.ATTACH_TYPE, template.getUid());

    return SUCCESS;
  }

  private void customInline(Template template, Date startTime) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(BUFFER);

    if (this.from == null || this.from.length() == 0)
      this.from = "txt";
    if (this.to == null || this.to.length() == 0)
      this.to = getText("jodconverter.to.extension");// 没有指定就是用系统默认的配置转换为pdf

    InputStream is = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    //格式化操作
    if (template.isFormatted() && !custom) {
      // 声明格式化参数
      Map<String, Object> params = getParams(template);
      String source = template.getContentEx(params);
      TemplateUtils.copy(source, out);
      is = new ByteArrayInputStream(out.toByteArray());
      out.close();
    } else {
      TemplateUtils.copy(template.getContent(), out);
      is = new ByteArrayInputStream(out.toByteArray());
      out.close();
    }
    // convert
    OfficeUtils.convert(is, this.from, outputStream, this.to);

    if (logger.isDebugEnabled())
      logger.debug("convert:" + DateUtils.getWasteTime(startTime));

    // 设置下载文件的参数（设置不对的话，浏览器是不会直接打开的）
    byte[] bs = outputStream.toByteArray();
    this.inputStream = new ByteArrayInputStream(bs);
    this.inputStream.close();
    this.contentType = AttachUtils.getContentType(this.to);
    this.contentLength = bs.length;
    this.filename = WebUtils.encodeFileName(ServletActionContext.getRequest(), template.getSubject() + ".txt");

    saveAttachHistory(template.getSubject(), AttachHistory.TYPE_INLINE, "",
      "txt", Template.ATTACH_TYPE + ".custom", template.getUid());
  }

  // 判断指定的扩展名是否为配置的要转换的文件类型
  private boolean isConvertFile(String extension) {
    String[] extensions = getText("jodconverter.from.extensions")
      .split(",");
    for (String ext : extensions) {
      if (ext.equalsIgnoreCase(extension))
        return true;
    }
    return false;
  }

  public String markerValueJsons;//格式化测试中的格式化参数列的json字符串

  public String formatSqlJsons;//格式化模板参数sql的json字符串 [{"key":"id","value":123123}]

  // 获取替换参数
  private Map<String, Object> getParams(Template template) throws Exception {
    if (!template.isFormatted())
      return null;
    // 声明格式化参数
    Map<String, Object> params = new HashMap<String, Object>();
    // 声明格式化参数
    Map<String, Object> mapFormatSql = new HashMap<String, Object>();
    // 获取替换参数
    if (formatSqlJsons != null && formatSqlJsons.length() > 0) {
      mapFormatSql = convert2Map(new JSONObject(this.formatSqlJsons));
      Map<String, Object> p = templateService.getMapParams(template.getId(), mapFormatSql);
      if (p != null)
        params.putAll(p);
    } else if (markerValueJsons != null && markerValueJsons.length() > 0) {
      params = convert2Map(new JSONObject(this.markerValueJsons));
    }

    return params;
  }

  private Map<String, Object> convert2Map(JSONObject json)
    throws JSONException {
    Map<String, Object> map = new HashMap<String, Object>();
    Object v;
    String k;
    @SuppressWarnings("unchecked")
    Iterator<String> itor = json.keys();
    while (itor.hasNext()) {
      k = itor.next();
      v = json.get(k);
      if (v instanceof JSONArray) {
        map.put(k, convert2Collection((JSONArray) v));
      } else if (v instanceof JSONObject) {
        map.put(k, convert2Map((JSONObject) v));
      } else {
        map.put(k, v);
      }
    }
    return map;
  }

  private Collection<Object> convert2Collection(JSONArray jsons)
    throws JSONException {
    List<Object> list = new ArrayList<Object>();
    Object v;
    for (int i = 0; i < jsons.length(); i++) {
      v = jsons.get(i);
      if (v instanceof JSONArray) {
        list.add(convert2Collection((JSONArray) v));
      } else if (v instanceof JSONObject) {
        list.add(convert2Map((JSONObject) v));
      } else {
        list.add(v);
      }
    }
    return list;
  }
}
