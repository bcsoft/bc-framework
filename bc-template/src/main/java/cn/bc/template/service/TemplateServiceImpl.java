package cn.bc.template.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.docs.domain.Attach;
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

	public boolean isUnique(Long currentId, String code) {
		return this.templateDao.isUnique(currentId, code);
	}

	public String getContent(String code) {
		Template tpl = loadByCode(code);
		if (tpl == null) {
			logger.warn("没有找到编码为'" + code + "'的模板!");
			return null;
		}
		//自定义文本
		if(tpl.isCustomText())
			return tpl.getContent();

		// 不处理非纯文本类型的
		if (!tpl.isPureText())
			return null;
		
		return tpl.getContent();
	}

	public InputStream getInputStream(String code) {
		Template tpl = loadByCode(code);
		if (tpl == null) {
			logger.warn("没有找到编码为'" + code + "'的模板!");
			return null;
		}

		if(tpl.isFile()){
			try {
				// 文件名称
				String filename = tpl.getPath();
				File fileDir = new File(Attach.DATA_REAL_PATH
						+ Template.DATA_SUB_PATH + "/" + filename);
				// 没有此文件
				if (!fileDir.exists())
				return null;
				
				InputStream is = new FileInputStream(fileDir);
				return is;
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
				// 报错返回空
				return null;
			}
		}
		
		// TODO 生成文件流
		return null;
	}

	public String format(String code, Map<String, Object> args) {
		Template tpl = loadByCode(code);
		if (tpl == null) {
			logger.warn("没有找到编码为'" + code + "'的模板!");
			return null;
		}

		// TODO
		return null;
	}

	public void formatTo(String code, Map<String, Object> args, OutputStream out) {
		Template tpl = loadByCode(code);
		if (tpl == null) {
			logger.warn("没有找到编码为'" + code + "'的模板!");
			return;
		}

		// TODO
	}

	// == 以下为待清理的接口 ==

	
}
