package cn.bc.template.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.commontemplate.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.TemplateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;
import cn.bc.template.dao.TemplateDao;
import cn.bc.template.domain.Template;
import cn.bc.template.domain.TemplateParam;
import cn.bc.template.util.DocxUtils;
import cn.bc.template.util.FreeMarkerUtils;
import cn.bc.template.util.XlsUtils;
import cn.bc.template.util.XlsxUtils;

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
	private OperateLogService operateLogService;
	private ActorHistoryService actorHistoryService;
	private AttachService attachService;

	@Autowired
	public void setTemplateDao(TemplateDao templateDao) {
		this.templateDao = templateDao;
		this.setCrudDao(templateDao);
	}

	@Autowired
	public void setOperateLogService(OperateLogService operateLogService) {
		this.operateLogService = operateLogService;
	}
	
	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}
	
	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	public Template loadByCode(String code) {
		return this.templateDao.loadByCode(code);
	}

	public boolean isUniqueCodeAndVersion(Long currentId, String code,
			String version) {
		return this.templateDao
				.isUniqueCodeAndVersion(currentId, code, version);
	}

	public String getContent(String code) {
		Template tpl = loadByCode(code);
		if (tpl == null) {
			logger.warn("没有找到编码为'" + code + "'的模板!");
			return null;
		}
		// 纯文本类型
		if (tpl.isPureText())
			return tpl.getContentEx();
		// 附件的扩展名
		String extension = StringUtils.getFilenameExtension(tpl.getPath());
		if (tpl.getTemplateType().getCode().equals("word-docx")
				&& extension.equals("docx"))
			return DocxUtils.loadText(tpl.getInputStream());

		if (tpl.getTemplateType().getCode().equals("xls")
				&& extension.equals("xls"))
			return XlsUtils.loadText(tpl.getInputStream());
		
		if (tpl.getTemplateType().getCode().equals("xlsx")
				&& extension.equals("xlsx"))
			return XlsxUtils.loadText(tpl.getInputStream());
		
		if (tpl.getTemplateType().getCode().equals("html")
				&& extension.equals("html"))
			return TemplateUtils.loadText(tpl.getInputStream());

		logger.warn("文件后缀名：" + extension + ",未转换为字符串类型");
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
		if (tpl == null)
			logger.warn("没有找到编码为'" + code + "'的模板!");
		if(tpl.isFormatted())
			logger.warn("code="+code+",模板不可格式化。");
		
		// 纯文本类型
		if (tpl.isPureText()) {
			String source = this.getContent(code);
			String r = TemplateUtils.format(source, args);
			try {
				out.write(r.getBytes());
				out.flush();
			} catch (IOException e) {
				logger.warn("formatTo 写入数据到流错误：" + e.getMessage());
			} finally {
				try {
					out.close();
				} catch (IOException ex) {
				}
			}
		} else {
			InputStream is = tpl.getInputStream();
			
			// 附件的扩展名
			String extension = StringUtils.getFilenameExtension(tpl.getPath());

			if ("word-docx".equals(tpl.getTemplateType().getCode())
					&& "docx".equals(extension)) {
				XWPFDocument docx = DocxUtils
						.format(is, args);
				try {
					docx.write(out);
					out.flush();
				} catch (IOException e) {
					logger.warn("formatTo 写入数据到流错误：" + e.getMessage());
				} finally {
					try {
						is.close();
						out.close();
					} catch (IOException ex) {
					}
				}
				
			} else if ("xls".equals(tpl.getTemplateType().getCode())
					&& "xls".equals(extension)) {
				HSSFWorkbook xls = XlsUtils.format(is, args);
				try {
					xls.write(out);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
					logger.warn("formatTo 写入数据到流错误：" + e.getMessage());
				} finally {
					try {
						is.close();
						out.close();
					} catch (IOException ex) {
					}
				}
			
			} else if ("xlsx".equals(tpl.getTemplateType().getCode())
					&& "xlsx".equals(extension)) {// Excel2007+
				
				XSSFWorkbook xlsx = XlsxUtils.format(is, args);
				try {
					xlsx.write(out);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
					logger.warn("formatTo 写入数据到流错误：" + e.getMessage());
				} finally {
					try {
						is.close();
						out.close();
					} catch (IOException ex) {
					}
				}
				
			} else if ("html".equals(tpl.getTemplateType().getCode())
					&& "html".equals(extension)) {// html
				String source = FreeMarkerUtils.format(TemplateUtils.loadText(is),args);
				try {
					out.write(source.getBytes());
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
					logger.warn("formatTo 写入数据到流错误：" + e.getMessage());
				} finally {
					try {
						is.close();
						out.close();
					} catch (IOException ex) {
					}
				}
			} else {
				logger.warn("文件后缀名：" + extension + ",不能formatTo");
				try {
					is.close();
					out.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	public void saveTpl(Template template) {
		Template oldTpl = this.templateDao.loadByCodeAndId(template.getCode(),
				template.getId());
		if (oldTpl != null) {
			oldTpl.setStatus(BCConstants.STATUS_DISABLED);
			this.templateDao.save(oldTpl);
		}
		this.templateDao.save(template);
	}

	public List<Map<String, String>> findCategoryOption() {
		return this.templateDao.findCategoryOption();
	}

	private TemplateParamService templateParamService;

	@Autowired
	public void setTemplateParamService(
			TemplateParamService templateParamService) {
		this.templateParamService = templateParamService;
	}

	public Map<String, Object> getMapParams(Long id,
			Map<String, Object> mapFormatSql) {
		Template tpl = this.templateDao.load(id);
		if (tpl == null)
			throw new CoreException("template is not exists");

		// 取参数集合
		Set<TemplateParam> tplps = tpl.getParams();
		// 没配置模板参数处理
		if (tplps == null || tplps.isEmpty())
			return null;

		Map<String, Object> formattedMap = new HashMap<String, Object>();
		Map<String, Object> tempMap;
		// 遍历模板参数集合获取格式化的替换参数
		for (TemplateParam tp : tplps) {
			tempMap = templateParamService.getMapParams(tp, mapFormatSql);
			if (tempMap != null)
				formattedMap.putAll(tempMap);
		}
		return formattedMap.isEmpty() ? null : formattedMap;
	}

	@Override
	public Template save(Template entity) {
		String subject="";
		String operate=OperateLog.OPERATE_CREATE;
		if(entity.isNew()){
			subject="新建模板：" + entity.getSubject();
		}else{
			subject="更新模板：" + entity.getSubject();
			operate=OperateLog.OPERATE_UPDATE;
		}
		
		//正常
		if(entity.getStatus()==BCConstants.STATUS_ENABLED){
			Template oldTpl = this.templateDao.loadByCodeAndId(entity.getCode(),
					entity.getId());
			if (oldTpl != null) {
				this.operateLogService.saveWorkLog(Template.class.getSimpleName(),
						oldTpl.getId().toString(), "禁用旧模板：" + oldTpl.getSubject()
						,null, OperateLog.OPERATE_UPDATE);
				oldTpl.setStatus(BCConstants.STATUS_DISABLED);
				this.templateDao.save(oldTpl);
			}
		}
		entity=this.templateDao.save(entity);
		this.operateLogService.saveWorkLog(Template.class.getSimpleName(),
				entity.getId().toString(),subject,null,operate);
		return entity;
	}

	@Override
	public void delete(Serializable id) {
		Template entity=this.templateDao.load(id);
		this.operateLogService.saveWorkLog(Template.class.getSimpleName(),
				entity.getId().toString(), "删除模板：" + entity.getSubject()
						, null, OperateLog.OPERATE_DELETE);
		super.delete(id);
	}

	@Override
	public void delete(Serializable[] ids) {
		for (Serializable id : ids) {
			this.delete(id);
		}
	}

	public Attach getAttach(String subject,String code, Map<String, Object> args,
			String ptype, String puid, ActorHistory author,Map<String,Object> formatParamSql) throws Exception{
		Assert.assertNotEmpty(subject, "subject is Empty");
		Assert.assertNotEmpty(code, "code is Empty");
		Assert.assertNotEmpty(ptype, "ptype is Empty");
		Assert.assertNotEmpty(puid, "puid is Empty");
		
		Template template = this.templateDao.loadByCode(code);
		if(template == null)
			throw new CoreException("Template code:"+code+" not find entity!");
		
		if(args == null)
				args = new HashMap<String, Object>();
		
		if(author == null){
			if(SystemContextHolder.get() == null){
				author = this.actorHistoryService.loadByCode("admin");
			}else{
				author = SystemContextHolder.get().getUserHistory();
			}
		}
		
		Map<String, Object> args4Param=this.getMapParams(template.getId(), formatParamSql);
		
		if(args4Param != null)
		//最终替换参数
		args.putAll(this.getMapParams(template.getId(), formatParamSql));

		//生成附件
		Attach attach = new Attach();
		attach.setAuthor(author);
		attach.setFileDate(Calendar.getInstance());
		attach.setSubject(subject);
		attach.setAppPath(false);
		attach.setFormat(template.getTemplateType().getExtension());
		attach.setStatus(BCConstants.STATUS_ENABLED);
		attach.setPtype(ptype);
		attach.setPuid(puid);

		// 文件存储的相对路径（年月），避免超出目录内文件数的限制
		Calendar now = Calendar.getInstance();
		String datedir = new SimpleDateFormat("yyyyMM").format(now.getTime());

		// 要保存的物理文件
		String realpath;// 绝对路径名
							//uuid
		String fileName = UUID.randomUUID().toString().replace("-", "")
				+ "." + template.getTemplateType().getExtension();// 不含路径的文件名
		realpath = Attach.DATA_REAL_PATH + "/" + datedir + "/" + fileName;

		// 构建文件要保存到的目录
		File file = new File(realpath);
		if (!file.getParentFile().exists()) {
			if (logger.isInfoEnabled()) {
				logger.info("mkdir=" + file.getParentFile().getAbsolutePath());
			}
			file.getParentFile().mkdirs();
		}
		
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		
		this.formatTo(template.getCode(), args, out);
		
		// 设置附件大小
		attach.setSize(new File(realpath).length());

		// 设置附件相对路径
		attach.setPath(datedir + "/" + fileName);

		return this.attachService.save(attach);

	}

}
