package cn.bc.template.web.struts2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.BCConstants;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.TemplateUtils;
import cn.bc.docs.web.AttachUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import cn.bc.template.util.DocxUtils;
import cn.bc.template.util.FreeMarkerUtils;
import cn.bc.template.util.XlsUtils;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.util.WebUtils;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

/**
 * 模板表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplateAction extends FileEntityAction<Long, Template> {
	private static final long serialVersionUID = 1L;
	private TemplateService templateService;

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.setCrudService(templateService);
		this.templateService = templateService;
	}

	@Override
	public boolean isReadonly() {

		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		return !context.hasAnyRole(getText("key.role.bc.template"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (!this.isReadonly()) {
			pageOption.addButton(new ButtonOption(getText("template.show.history.version"), null,
					"bc.templateForm.showVersion").setId("templateShowVersion"));
			pageOption.addButton(new ButtonOption(getText("label.preview.inline"), null,
					"bc.templateForm.inline").setId("templateInline"));
			if(editable)
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.templateForm.save").setId("templateSave"));
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(560)
				.setMinHeight(200).setMinWidth(300);
	}

	@Override
	protected void afterCreate(Template entity) {
		super.afterCreate(entity);
		entity.setType(Template.TYPE_EXCEL);
		// 内置 默认为否
		entity.setInner(false);
		//状态正常
		entity.setStatus(BCConstants.STATUS_ENABLED);
	}
	
	

	@Override
	public String save() throws Exception {
		Template template=this.getE();
		//状态：禁用
		if(template.getStatus()!=BCConstants.STATUS_ENABLED){
			this.beforeSave(template);
			this.templateService.getCrudDao().save(template);
			this.afterSave(template);
			return "saveSuccess";
		}
		//状态：正常
		this.beforeSave(template);
	    this.templateService.saveTpl(template);
	    this.afterSave(template);
	    return "saveSuccess";
	}



	public Integer type;//类型
	public Long tid;//模板id
	public String code;//编码
	public String version;//版本号

	// 检查编码与版本号唯一
	public String isUniqueCodeAndVersion() {
		Json json = new Json();
		boolean flag = this.templateService.isUniqueCodeAndVersion(this.tid, code,version);
		if (flag) {
			json.put("result", getText("template.save.code"));
			this.json = json.toString();
			return "json";
		} else {
			json.put("result", "save");
			this.json = json.toString();
			return "json";
		}
	}
	
	public String path;// 物理文件保存的相对路径
	public String content;//模板内容
	//---- 加载配置参数  ---开始--
	public String loadTplConfigParam(){
		Json json = new Json();
		// 附件的扩展名
		String extension = StringUtils.getFilenameExtension(path); 
		
		//自定义文本
		if(type.equals(Template.TYPE_CUSTOM)){
			//保存参数的集合	
			json.put("value", this.getParamStr(TemplateUtils.findMarkers(content)));
		}else if(type.equals(Template.TYPE_EXCEL)&&extension.equals("xls")){
			Template excel=new Template();
			excel.setPath(this.path);
			json.put("value",this.getParamStr(XlsUtils.findMarkers(excel.getInputStream())));
		}else if(type.equals(Template.TYPE_WORD)&&extension.equals("docx")){
			Template word=new Template();
			word.setPath(this.path);
			json.put("value",this.getParamStr(DocxUtils.findMarkers(word.getInputStream())));
		}else if(type.equals(Template.TYPE_TEXT)){
			Template txt=new Template();
			txt.setPath(this.path);
			json.put("value",this.getParamStr(TemplateUtils.findMarkers(txt.getInputStream())));
		}
		this.json=json.toString();
		return "json";
	}
	
	private String getParamStr(List<String> list){
		String param=null;
		for(int i=0;i<list.size();i++){
			if(i==0){
				param=list.get(i);
			}else{
				param= param+","+list.get(i);	
			}
			
		}
		return param;
	}
	
	//---- 加载配置参数  ---结束--
	
	public String filename;
	public String contentType;
	public long contentLength;
	public InputStream inputStream;
	
	//下载自定义文本
	public String download() throws Exception {
		Template coustText=this.templateService.load(tid);
		Date startTime = new Date();
		// 附件的扩展名
		String extension = "txt";
		// debug
		if (logger.isDebugEnabled()) {
			logger.debug("extension=" + extension);
		}
		// 设置下载文件的参数
		this.contentType = AttachUtils.getContentType(extension);
		this.filename = WebUtils.encodeFileName(
				ServletActionContext.getRequest(), coustText.getSubject()+"."+extension);
		this.contentLength = coustText.getContent().length();
		this.inputStream = coustText.getInputStream();
		if (logger.isDebugEnabled()) {
			logger.debug("download:" + DateUtils.getWasteTime(startTime));
		}
		return SUCCESS;
	}
	
	
	private static final int BUFFER = 4096;
	public String from;// 指定原始文件的类型，默认为文件扩展名
	public String to;// 预览时转换到的文件类型，默认为pdf
	public String f;// 要下载的文件，相对于Attach.DATA_REAL_PATH下的子路径，前后均不带/
	public String n;// [可选]指定下载为的文件名
	public String markerValueJsons;
	
	//在线查看
	public String inline() throws Exception {
		Template template=this.templateService.load(tid);		
		Date startTime = new Date();
		
		// 附件的扩展名
		String extension = StringUtils.getFilenameExtension(template.getPath()); 
		
		// debug
		if (logger.isDebugEnabled()) {
			logger.debug("path=" + template.getPath());
			logger.debug("extension=" + extension);
			logger.debug("n=" + template.getSubject());
			logger.debug("to=" + to);
		}
		
		this.n = StringUtils.getFilename(template.getSubject());
		if (isConvertFile(extension)||template.isPureText()) {
			//解释需要配置参数替换为指定的值。
			Map<String, Object> markerValues=new HashMap<String, Object>();
			JSONArray jsons = new JSONArray(this.markerValueJsons);
			JSONObject json;
			for (int i = 0; i < jsons.length(); i++) {
				json = jsons.getJSONObject(i);
				markerValues.put(json.getString("key"), json.get("value"));
			}		
			InputStream inputStream=null;
			if(template.getType()==Template.TYPE_WORD&&extension.equals("docx")){
				XWPFDocument docx=DocxUtils.format(template.getInputStream(), markerValues);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				docx.write(out);
				inputStream=new ByteArrayInputStream(out.toByteArray());
			}else if(template.getType()==Template.TYPE_EXCEL&&extension.equals("xls")){
				HSSFWorkbook xls=XlsUtils.format(template.getInputStream(), markerValues);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				xls.write(out);
				inputStream=new ByteArrayInputStream(out.toByteArray());
			}else if(template.isPureText()){
				template.setContent(FreeMarkerUtils.format(template.getContent(), markerValues));
				inputStream=template.getInputStream();
			}else{
				inputStream =template.getInputStream();
			}
			
			// 调用jodconvert将附件转换为pdf文档后再下载
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
					BUFFER);

			// connect to an OpenOffice.org instance running on port 8100
			OpenOfficeConnection connection = new SocketOpenOfficeConnection(
					getText("jodconverter.soffice.host"),
					Integer.parseInt(getText("jodconverter.soffice.port")));
			connection.connect();
			if (logger.isDebugEnabled()) {
				logger.debug("connect:" + DateUtils.getWasteTime(startTime));
			}

			DocumentFormatRegistry formaters = new DefaultDocumentFormatRegistry();

			// convert
			DocumentConverter converter = new OpenOfficeDocumentConverter(
					connection);
			if (this.from == null || this.from.length() == 0)
				this.from = extension;
			if (this.to == null || this.to.length() == 0)
				this.to = getText("jodconverter.to.extension");// 没有指定就是用系统默认的配置转换为pdf
			converter.convert(inputStream,
					formaters.getFormatByFileExtension(this.from), outputStream,
					formaters.getFormatByFileExtension(this.to));
			if (logger.isDebugEnabled()) {
				logger.debug("convert:" + DateUtils.getWasteTime(startTime));
			}

			// close the connection
			connection.disconnect();

			// 设置下载文件的参数（设置不对的话，浏览器是不会直接打开的）
			byte[] bs = outputStream.toByteArray();
			this.inputStream = new ByteArrayInputStream(bs);
			this.inputStream.close();
			this.contentType = AttachUtils.getContentType(this.to);
			this.contentLength = bs.length;
			this.filename = WebUtils.encodeFileName(ServletActionContext
					.getRequest(), this.n == null ? "bc" : this.n + "."
					+ this.to);
		} else {
			// 设置下载文件的参数
			this.filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(), this.n);
			this.contentType = AttachUtils.getContentType(extension);
			// 无需转换的文档直接下载处理
			File file = new File(template.getPath());
			this.contentLength = file.length();
			this.inputStream = new FileInputStream(file);	
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
