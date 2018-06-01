/**
 *
 */
package cn.bc.template.web.struts2;

import cn.bc.core.exception.CoreException;
import cn.bc.core.util.*;
import cn.bc.docs.util.OfficeUtils;
import cn.bc.docs.web.AttachUtils;
import cn.bc.template.domain.Template;
import cn.bc.template.service.ParamsExcutorService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态在线查看和下载模板格式化后的附件Action
 *
 * @author lbj
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplateDynamicViewFileAction extends ActionSupport {
  private static Logger logger = LoggerFactory.getLogger(TemplateDynamicViewFileAction.class);
  private static final long serialVersionUID = 1L;
  private TemplateService templateService;

  @Autowired
  public void setTemplateService(TemplateService templateService) {
    this.templateService = templateService;
  }

  //可通过传递 模板的id或 模板的编码获取模板
  public Long id;// 模板id
  public String code; // 模板编码

  public boolean im = true;//控制是否需要到模块中获取数据，默认需要
  public String msid;// 调用模块中service的id名
  public Long mid;// 模块的id

  // markerValueJsons格式化的模板的参数列的json字符串
  public String mvj;

  /*
   * formatSqlJsons模板带有模板参数 必须要设置此参数
   * 格式化模板参数sql的json字符串{"id":"10653169"}
   */
  public String fsj;

  /* 控制是否需要读取文件的替换key，若此key在替换参数中没有设置则默认设长度为零的字符串
   * 只支持 docx,html和纯文本类型
   */
  public boolean ir = false;
  public String rv = "";//replaceValue替换值

  //下载和在线查看文件需要读取的参数
  public String filename;
  public String contentType;
  public long contentLength;
  public InputStream inputStream;
  private static final int BUFFER = 4096;
  public String from;// 指定原始文件的类型，默认为文件扩展名,文档转换服务时使用
  public String to;// 预览时转换到的文件类型，默认为pdf

  //模板信息
  private Template template;
  private String ext;
  private String typeCode;
  private String subject;

  //初始化模板
  private void init() throws CoreException {
    // 加载一个模板对象
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

    ext = template.getTemplateType().getExtension();
    typeCode = template.getTemplateType().getCode();
    subject = template.getSubject();
  }

  private InputStream getInputStream() {
    return template.getInputStream();
  }

  private Map<String, Object> getParams() {
    Map<String, Object> params = null;
    if (im) {
      ParamsExcutorService excutor = (ParamsExcutorService) SpringUtils.getBean(msid);
      params = excutor.getParamsExcutor(mid);
    }
    if (params == null) params = new HashMap<String, Object>();

    if (mvj != null && mvj.length() > 0) {
      params.putAll(JsonUtils.toMap(mvj));
    }

    Map<String, Object> mapFormatSql = new HashMap<String, Object>();
    mapFormatSql.putAll(params);
    if (fsj != null && fsj.length() > 0) {
      mapFormatSql.putAll(JsonUtils.toMap(fsj));
    }
    Map<String, Object> p = templateService.getMapParams(template.getId(), mapFormatSql);
    if (p != null)
      params.putAll(p);

    if (!ir) return params;

    if (typeCode.equals("word-docx") || typeCode.equals("html") || template.isPureText()) {
      // 获取文件中的${XXXX}占位标记的键名列表
      List<String> markers = DocxUtils.findMarkers(template.getInputStream());
      // 占位符列表与参数列表匹配,当占位符列表值没出现在参数列表key值时，增加此key值
      for (String key : markers) {
        if (key.indexOf("?") == -1 && key.indexOf("!") == -1
          && key.indexOf(":") == -1 && !params.containsKey(key))
          params.put(key, rv);
      }
    }

    return params;
  }

  // 下载附件
  public String download() throws Exception {
    init();
    // 开始时间，计算执行完以下的代码使用
    Date startTime = new Date();
    // 判断指定的扩展名是否为配置的要转换的文件类型 或者 纯文本类型也可以
    if (isConvertFile(ext) || template.isPureText()) {
      // 判断是否为可格式化类型
      //格式化操作
      if (template.isFormatted()) {
        //获取格式化模板的参数
        Map<String, Object> params = getParams();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //docx
        if (typeCode.equals("word-docx")) {
          XWPFDocument docx = DocxUtils.format(getInputStream(), params);
          docx.write(out);
          this.inputStream = new ByteArrayInputStream(out.toByteArray());
          //xls
        } else if (typeCode.equals("xls")) {
          HSSFWorkbook xls = XlsUtils.format(getInputStream(), params);
          xls.write(out);
          this.inputStream = new ByteArrayInputStream(out.toByteArray());
          //xlsx
        } else if (typeCode.equals("xlsx")) {
          XSSFWorkbook xlsx = XlsxUtils.format(getInputStream(), params);
          xlsx.write(out);
          this.inputStream = new ByteArrayInputStream(out.toByteArray());
          //html
        } else if (typeCode.equals("html")) {
          TemplateUtils.copy(
            FreeMarkerUtils.format(TemplateUtils.loadText(getInputStream()), params), out);
          this.inputStream = new ByteArrayInputStream(out.toByteArray());
        } else if (template.isPureText()) {
          TemplateUtils.copy(FreeMarkerUtils.format(template.getContentEx(), params), out);
          this.inputStream = new ByteArrayInputStream(out.toByteArray());
        } else {
          this.inputStream = getInputStream();
        }
        //关闭资源
        out.close();
      } else {
        this.inputStream = getInputStream();
      }
      if (logger.isDebugEnabled())
        logger.debug("format:" + DateUtils.getWasteTime(startTime));
    } else {
      this.inputStream = template.getInputStream();
    }
    this.contentType = AttachUtils.getContentType(ext);
    this.contentLength = this.inputStream.available();
    this.filename = WebUtils.encodeFileName(
      ServletActionContext.getRequest(), subject + "." + ext);
    return SUCCESS;
  }

  // 支持在线打开文档查看的文件下载
  public String inline() throws Exception {
    init();
    // 开始时间，计算执行完以下的代码使用
    Date startTime = new Date();
    // 判断指定的扩展名是否为配置的要转换的文件类型 或者 纯文本类型也可以
    if (isConvertFile(ext) || template.isPureText()) {
      if (this.from == null || this.from.length() == 0)
        this.from = ext;
      if (this.to == null || this.to.length() == 0)
        this.to = getText("jodconverter.to.extension");// 没有指定就是用系统默认的配置转换为pdf

      // 声明文件转换服务的控制 默认不适用
      boolean officeConvert = true;
      // 声明下载文件的参数
      byte[] bs = null;
      // 声明需要转换的流
      InputStream is = null;
      // 判断是否为可格式化类型
      //格式化操作
      if (template.isFormatted()) {
        //获取格式化模板的参数
        Map<String, Object> params = getParams();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //docx
        if (typeCode.equals("word-docx")) {
          XWPFDocument docx = DocxUtils.format(getInputStream(), params);
          docx.write(out);
          is = new ByteArrayInputStream(out.toByteArray());
          //xls
        } else if (typeCode.equals("xls")) {
          HSSFWorkbook xls = XlsUtils.format(getInputStream(), params);
          xls.write(out);
          is = new ByteArrayInputStream(out.toByteArray());
          //xlsx
        } else if (typeCode.equals("xlsx")) {
          XSSFWorkbook xlsx = XlsxUtils.format(getInputStream(), params);
          xlsx.write(out);
          is = new ByteArrayInputStream(out.toByteArray());
          //html
        } else if (typeCode.equals("html")) {
          //设置html格式的查看
          this.to = ext;
          bs = FreeMarkerUtils.format(TemplateUtils.loadText(getInputStream()), params).getBytes();
          // 不使用office转换服务
          officeConvert = false;
        } else if (template.isPureText()) {
          bs = FreeMarkerUtils.format(template.getContentEx(), params).getBytes();
          // 不使用office转换服务
          officeConvert = false;
        } else {
          is = getInputStream();
        }
        //关闭资源
        out.close();
      } else {
        is = getInputStream();
      }

      //文件输出
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
        BUFFER);
      if (officeConvert) {
        // convert
        OfficeUtils.convert(is, this.from, outputStream, this.to);
        bs = outputStream.toByteArray();
        is.close();
        outputStream.close();
      }

      if (logger.isDebugEnabled())
        logger.debug("convert:" + DateUtils.getWasteTime(startTime));

      this.inputStream = new ByteArrayInputStream(bs);
      this.inputStream.close();
      this.contentType = AttachUtils.getContentType(this.to);
      this.contentLength = bs.length;
      this.filename = WebUtils.encodeFileName(
        ServletActionContext.getRequest(), subject + "." + this.to);
    } else {
      // 设置下载文件的参数
      this.contentType = AttachUtils.getContentType(ext);
      this.filename = WebUtils.encodeFileName(
        ServletActionContext.getRequest(), subject + "." + ext);
      this.inputStream = template.getInputStream();
      this.contentLength = template.getSize();
    }
    return SUCCESS;
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

}
