package cn.bc.template.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import cn.bc.BCConstants;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.TemplateUtils;
import cn.bc.template.dao.TemplateDao;
import cn.bc.template.domain.Template;
import cn.bc.template.util.DocxUtils;
import cn.bc.template.util.XlsUtils;

/**
 * Service接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateServiceImpl extends DefaultCrudService<Template> implements
		TemplateService {
	private static Log logger = LogFactory.getLog(TemplateServiceImpl.class);

	private TemplateDao templateDao;

	@Autowired
	public void setTemplateDao(TemplateDao templateDao) {
		this.templateDao = templateDao;
		this.setCrudDao(templateDao);
	}

	public Template loadByCode(String code) {
		return this.templateDao.loadByCode(code);
	}

	public boolean isUniqueCodeAndVersion(Long currentId, String code,String version) {
		return this.templateDao.isUniqueCodeAndVersion(currentId, code,version);
	}

	public String getContent(String code) {
		Template tpl = loadByCode(code);
		if (tpl == null) {
			logger.warn("没有找到编码为'" + code + "'的模板!");
			return null;
		}
		//纯文本类型
		if(tpl.isPureText())return tpl.getContent();
		// 附件的扩展名
		String extension = StringUtils.getFilenameExtension(tpl.getPath());
		if(tpl.getTemplateType().getCode().equals("word-docx")&&extension.equals("docx"))
			return DocxUtils.loadText(tpl.getInputStream());
		
		if(tpl.getTemplateType().getCode().equals("xls")&&extension.equals("xls"))
			return XlsUtils.loadText(tpl.getInputStream());
			
		logger.warn("文件后缀名："+extension+",未转换为字符串类型");
		return null;
	}

	public InputStream getInputStream(String code) {
		Template tpl = loadByCode(code);
		if (tpl == null) {
			logger.warn("没有找到编码为'" + code + "'的模板!");
			return null;
		}
		
		return tpl.getInputStream();
	}

	public String format(String code, Map<String, Object> args) {
		String source = this.getContent(code);
		return TemplateUtils.format(source, args);
	}

	public void formatTo(String code, Map<String, Object> args, OutputStream out) {
		Template tpl = loadByCode(code);
		if (tpl == null) logger.warn("没有找到编码为'" + code + "'的模板!");
		//纯文本类型
		if(tpl.isPureText()){
			String source = this.getContent(code);
			String r = TemplateUtils.format(source, args);
			try {
				out.write(r.getBytes());
				out.close();
			} catch (IOException e) {
				logger.warn("formatTo 写入数据到流错误：" + e.getMessage());
			}
		}else{
			// 附件的扩展名
			String extension = StringUtils.getFilenameExtension(tpl.getPath()); 
			
			if(tpl.getTemplateType().getCode().equals("word-docx")&&extension.equals("docx")){
				XWPFDocument docx=DocxUtils.format(tpl.getInputStream(), args);
				try {
					docx.write(out);
					out.close();
				} catch (IOException e) {
					logger.warn("formatTo 写入数据到流错误：" + e.getMessage());
				}
			}else if(tpl.getTemplateType().getCode().equals("xls")&&extension.equals("xls")){
				HSSFWorkbook xls=XlsUtils.format(tpl.getInputStream(), args);
				try {
					xls.write(out);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.warn("formatTo 写入数据到流错误：" + e.getMessage());
				}
			}else{
				logger.warn("文件后缀名："+extension+",不能formatTo");
			}
		}
	}

	public void saveTpl(Template template) {
		Template oldTpl= this.templateDao.loadByCode(template.getCode());
		if(oldTpl!=null){
			oldTpl.setStatus(BCConstants.STATUS_DISABLED);
			this.templateDao.save(oldTpl);
		}
		this.templateDao.save(template);
	}
}
