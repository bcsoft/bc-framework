package cn.bc.template.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.TemplateUtils;
import cn.bc.template.dao.TemplateDao;
import cn.bc.template.domain.Template;

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

		return tpl.getContent();
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
		String source = this.getContent(code);
		String r = TemplateUtils.format(source, args);
		try {
			out.write(r.getBytes());
			out.close();
		} catch (IOException e) {
			logger.warn("formatTo 写入数据到流错误：" + e.getMessage());
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
