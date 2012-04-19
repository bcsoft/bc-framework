package cn.bc.template.web.struts2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.docs.domain.Attach;
import cn.bc.docs.web.AttachUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
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
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.templateForm.save")
						.setId("templateSave"));
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(560)
				.setMinHeight(200).setMinWidth(300);
	}

	@Override
	protected void afterCreate(Template entity){
		super.afterCreate(entity);
		entity.setType(Template.TYPE_EXCEL);
		//内置 默认为否
		entity.setInner(false);
	}
	
	public Integer type;
	public Long tid;
	public String code;
	//检查编码是否唯一
	public String isUniqueCode(){
		Json json=new Json();
		boolean flag=this.templateService.isUnique(this.tid, code);
		if(flag){
			json.put("result", getText("template.save.code"));
			this.json=json.toString();
			return "json";
		}else{
			json.put("result", "save");
			this.json=json.toString();
			return "json";
		}
	}

	
	//下载
	public String filename;
	public String contentType;
	public long contentLength;
	public InputStream inputStream;
	public String path;
	
	public String download() throws Exception{
		Template template=this.templateService.load(this.getId());
		if(template.isFile()){
			downloadTemplate(template);
		}
		return SUCCESS;
	}
		
	private void downloadTemplate(Template template) throws FileNotFoundException {
		// 获取附件的物理文件路径
		String path=Attach.DATA_REAL_PATH+Template.DATA_SUB_PATH+File.separator+template.getPath();
		int indexD=template.getPath().indexOf(".");
		int indexX=template.getPath().indexOf("/");
		String ext=template.getPath().substring(indexD+1);
		String fName=template.getPath().substring(indexX+1,indexD);
		this.downloadFile(ext, path,fName);
	}
	
	private void downloadFile(String ext, String path, String filename)
			throws FileNotFoundException {
		contentType = AttachUtils.getContentType(ext);
		filename = WebUtils.encodeFileName(ServletActionContext.getRequest(),
				filename);
		File file = new File(path);
		contentLength = file.length();
		inputStream = new FileInputStream(file);
	}
	
	//在线查看
	public String to;
	static final int BUFFER = 4096;
	
	// 支持在线打开文档查看的文件下载
	public String inline() throws Exception {
		Template template=this.templateService.load(this.getId());
		String path=Attach.DATA_REAL_PATH+Template.DATA_SUB_PATH+File.separator+template.getPath();
		int indexD=template.getPath().indexOf(".");
		int indexX=template.getPath().indexOf("/");
		String ext=template.getPath().substring(indexD+1);
		String fName=template.getPath().substring(indexX+1,indexD);
		
		if (isConvertFile(ext)) {
			// 调用jodconvert将附件转换为pdf文档后再下载
			FileInputStream inputStream = new FileInputStream(new File(path));
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
					BUFFER);

			// connect to an OpenOffice.org instance running on port 8100
			OpenOfficeConnection connection = new SocketOpenOfficeConnection(
					getText("jodconverter.soffice.host"),
					Integer.parseInt(getText("jodconverter.soffice.port")));
			connection.connect();

			DocumentFormatRegistry formaters = new DefaultDocumentFormatRegistry();

			// convert
			DocumentConverter converter = new OpenOfficeDocumentConverter(
					connection);
			String from = ext;
			// if("docx".equalsIgnoreCase(from))
			// from = "doc";
			// else if("xlsx".equalsIgnoreCase(from))
			// from = "xls";
			// else if("pptx".equalsIgnoreCase(from))
			// from = "ppt";
			if (this.to == null || this.to.length() == 0)
				this.to = getText("jodconverter.to.extension");// 没有指定就是用系统默认的配置转换为pdf
			converter.convert(inputStream,
					formaters.getFormatByFileExtension(from), outputStream,
					formaters.getFormatByFileExtension(this.to));

			// close the connection
			connection.disconnect();
			byte[] bs = outputStream.toByteArray();
			this.inputStream = new ByteArrayInputStream(bs);

			inputStream.close();

			// 设置下载文件的参数（设置不对的话，浏览器是不会直接打开的）
			contentType = AttachUtils.getContentType(this.to);
			contentLength = bs.length;
			this.filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(), fName
							+ "." + this.to);
		} else {
			// 设置下载文件的参数
			contentType = AttachUtils.getContentType(ext);
			filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(), fName);

			// 无需转换的文档直接下载处理
			File file = new File(path);
			contentLength = file.length();
			inputStream = new FileInputStream(file);
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
