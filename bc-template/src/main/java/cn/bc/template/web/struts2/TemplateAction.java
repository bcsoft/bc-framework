package cn.bc.template.web.struts2;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.util.TemplateUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.option.domain.OptionItem;
import cn.bc.template.domain.Template;
import cn.bc.template.domain.TemplateParam;
import cn.bc.template.service.TemplateParamService;
import cn.bc.template.service.TemplateService;
import cn.bc.template.service.TemplateTypeService;
import cn.bc.template.util.DocxUtils;
import cn.bc.template.util.XlsUtils;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

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
	private TemplateTypeService templateTypeService;
	private TemplateParamService templateParamService;

	// 模板类型集合
	public List<Map<String, String>> typeList;
	
	//模板参数
	public String templateParamIds;
	public Set<TemplateParam> templateParams;

	@Autowired
	public void setTemplateTypeService(TemplateTypeService templateTypeService) {
		this.templateTypeService = templateTypeService;
	}

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.setCrudService(templateService);
		this.templateService = templateService;
	}
	
	@Autowired
	public void setTemplateParamService(TemplateParamService templateParamService) {
		this.templateParamService = templateParamService;
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
			pageOption.addButton(new ButtonOption(
					getText("template.preview.test"), null,
					"bc.templateForm.inline").setId("templateInline"));
			pageOption
					.addButton(new ButtonOption(
							getText("template.show.history.version"), null,
							"bc.templateForm.showVersion")
							.setId("templateShowVersion"));
			if (editable)
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.templateForm.save").setId("templateSave"));
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(545)
				.setMinHeight(200).setMinWidth(300).setMaxHeight(800)
				.setHelp("mubanguanli");
	}

	@Override
	protected void afterCreate(Template entity) {
		super.afterCreate(entity);
		// 内置 默认为否
		entity.setInner(false);
		// 状态正常
		entity.setStatus(BCConstants.STATUS_ENABLED);
		// 默认模板类型为自定义文本
		entity.setTemplateType(this.templateTypeService.loadByCode("custom"));
		// 默认模板不可格式化
		entity.setFormatted(false);

		// uid
		entity.setUid(this.getIdGeneratorService().next(Template.ATTACH_TYPE));

		this.typeList = this.templateTypeService.findTemplateTypeOption(true);
	}

	@Override
	protected void afterEdit(Template entity) {
		super.afterEdit(entity);

		this.typeList = this.templateTypeService.findTemplateTypeOption(true);
		OptionItem.insertIfNotExist(typeList, entity.getTemplateType().getId()
				.toString(), entity.getTemplateType().getName());
		this.templateParams=entity.getParams();
	}

	@Override
	protected void afterOpen(Template entity) {
		super.afterOpen(entity);
		this.typeList = this.templateTypeService.findTemplateTypeOption(true);
		OptionItem.insertIfNotExist(typeList, entity.getTemplateType().getId()
				.toString(), entity.getTemplateType().getName());
		this.templateParams=entity.getParams();
	}
	
	
	@Override
	protected void beforeSave(Template entity) {
		super.beforeSave(entity);
		
		Long[] ids = null;
		if (this.templateParamIds != null && this.templateParamIds.length() > 0) {
			String[] tpIds = this.templateParamIds.split(",");
			ids = new Long[tpIds.length];
			for (int i = 0; i < tpIds.length; i++) 
				ids[i] = new Long(tpIds[i]);
		}

		if(ids!=null&&ids.length>0){
			Set<TemplateParam> params=null;
			TemplateParam templateParam=null;
			for(int i=0;i<ids.length;i++){
				if(i==0)
					params=new HashSet<TemplateParam>();
				templateParam=this.templateParamService.load(ids[i]);
				params.add(templateParam);
			}
			if(this.getE().getParams()!=null){
				this.getE().getParams().clear();
				this.getE().getParams().addAll(params);
			}else
				this.getE().setParams(params);
			
		}
	}

	@Override
	public String save() throws Exception {
		Template template = this.getE();
		template.setTemplateType(this.templateTypeService.load(template
				.getTemplateType().getId()));
		// 设置保存文件大小 获取文件大小
		template.setSize(template.getSizeEx());

		// 状态：禁用
		if (template.getStatus() != BCConstants.STATUS_ENABLED) {
			this.beforeSave(template);
			this.templateService.save(template);
			this.afterSave(template);
			return "saveSuccess";
		}
		// 状态：正常
		this.beforeSave(template);
		this.templateService.saveTpl(template);
		this.afterSave(template);
		return "saveSuccess";
	}

	public Long tid;// 模板id
	public String code;// 编码
	public String version;// 版本号

	// 检查编码与版本号唯一
	public String isUniqueCodeAndVersion() {
		Json json = new Json();
		boolean flag = this.templateService.isUniqueCodeAndVersion(this.tid,
				code, version);
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

	// ---- 加载配置参数 ---开始--
	public String loadTplConfigParam() throws Exception {
		Json json = new Json();
		Template tpl = this.templateService.load(tid);
		// 附件的扩展名
		InputStream is = tpl.getInputStream();
		List<String> markers;
		if (tpl.isPureText()) {
			markers = TemplateUtils.findMarkers(is);
			// 保存参数的集合
			json.put("value", this.getParamStr(markers));
		} else if (tpl.getTemplateType().getCode().equals("xls")) {
			markers = XlsUtils.findMarkers(is);
			json.put("value", this.getParamStr(markers));
		} else if (tpl.getTemplateType().getCode().equals("word-docx")) {
			markers = DocxUtils.findMarkers(is);
			json.put("value", this.getParamStr(markers));
		}
		is.close();
		this.json = json.toString();
		return "json";
	}

	private String getParamStr(List<String> list) {
		String param = null;
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				param = list.get(i);
			} else {
				param = param + "," + list.get(i);
			}
		}
		return param;
	}


	public String filename;
	public String contentType;
	public long contentLength;
	public InputStream inputStream;

	// 下载自定义文本
	public String download() throws Exception {
		Template coustText = this.templateService.load(tid);
		if (coustText.getTemplateType().getCode().equals("custom")
				&& content != null && content.length() > 0) {
			coustText.setContent(content);
		}

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
				ServletActionContext.getRequest(), coustText.getSubject() + "."
						+ extension);
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

	// 在线查看
	public String inline() throws Exception {
		Template template = this.templateService.load(tid);
		if (template.getTemplateType().getCode().equals("custom")
				&& content != null && content.length() > 0) {
			template.setContent(content);
		}

		Date startTime = new Date();

		// 附件的扩展名
		String extension = template.getTemplateType().getExtension();

		// debug
		if (logger.isDebugEnabled()) {
			logger.debug("path=" + template.getPath());
			logger.debug("extension=" + extension);
			logger.debug("n=" + template.getSubject());
			logger.debug("to=" + to);
		}

		this.n = StringUtils.getFilename(template.getSubject());
		if (isConvertFile(extension) || template.isPureText()) {
			// 解释需要配置参数替换为指定的值。
			Map<String, Object> markerValues = null;
			JSONArray jsons = null;
			if (this.markerValueJsons != null
					&& this.markerValueJsons.length() > 0) {
				markerValues = new HashMap<String, Object>();
				jsons = new JSONArray(this.markerValueJsons);
				JSONObject json;
				Object v;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					v = json.get("value");
					if (v instanceof JSONArray) {
						v = convert2Collection((JSONArray) v);
					} else if (v instanceof JSONObject) {
						v = convert2Map((JSONObject) v);
					}
					markerValues.put(json.getString("key"), v);
				}
			}
			InputStream is;
			if (template.getTemplateType().getCode().equals("word-docx")
					&& extension.equals("docx")) {
				XWPFDocument docx = DocxUtils.format(template.getInputStream(),
						markerValues);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				docx.write(out);
				is = new ByteArrayInputStream(out.toByteArray());
				out.close();
			} else if (template.getTemplateType().getCode().equals("xls")
					&& extension.equals("xls")) {
				HSSFWorkbook xls = XlsUtils.format(template.getInputStream(),
						markerValues);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				xls.write(out);
				is = new ByteArrayInputStream(out.toByteArray());
				out.close();
			} else if (template.isPureText()) {
				if (markerValues != null && !markerValues.isEmpty()) {
					if (template.getTemplateType().getCode().equals("custom")) {
						String format = FreeMarkerUtils.format(
								template.getContent(), markerValues);
						template.setContent(format);
						is = template.getInputStream();
					} else {
						is = new ByteArrayInputStream(template.getContentEx(
								markerValues).getBytes());
					}
				} else {
					is = template.getInputStream();
				}

				if (extension == null)
					extension = "txt";
			} else {
				is = template.getInputStream();
			}

			if (this.from == null || this.from.length() == 0)
				this.from = extension;
			if (this.to == null || this.to.length() == 0) {
				if ("xls".equalsIgnoreCase(this.from)
						|| "xlsx".equalsIgnoreCase(this.from)) {
					this.to = "html";// excel默认转换为html格式（因为转pdf的A4纸张导致大报表换页乱了）
				} else {
					this.to = getText("jodconverter.to.extension");// 没有指定就是用系统默认的配置转换为pdf
				}
			}

			// 调用jodconvert将附件转换为pdf文档后再下载
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
					BUFFER);
			OfficeUtils.convert(is, this.from, outputStream, this.to);
			is.close();
			if (logger.isDebugEnabled()) {
				logger.debug("convert:" + DateUtils.getWasteTime(startTime));
			}

			// 设置下载文件的参数（设置不对的话，浏览器是不会直接打开的）
			byte[] bs = outputStream.toByteArray();
			this.inputStream = new ByteArrayInputStream(bs);
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
