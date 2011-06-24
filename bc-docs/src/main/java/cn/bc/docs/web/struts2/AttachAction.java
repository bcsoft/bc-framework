/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.AttachUtils;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.FileSizeFormater;
import cn.bc.web.struts2.CrudAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;
import cn.bc.web.util.WebUtils;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

/**
 * 附件Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AttachAction extends CrudAction<Long, Attach> implements
		SessionAware {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = "R_MANAGER_ATTACH";// 附件管理角色的编码

	private AttachService attachService;

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
		this.setCrudService(attachService);
	}

	public String editableAttachsUI;
	public String readonlyAttachsUI;

	// 用于附件控件的设计
	public String design() throws Exception {
		String ptype = "test.main";
		String puid = "test.uid.1";

		// 构建可编辑附件控件
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(this.isFlashUpload());
		attachsUI.addClazz("formAttachs ui-widget-content");
		attachsUI.addAttach(this.attachService.findByPtype(ptype, puid));
		attachsUI.setPuid(puid).setPtype(ptype);
		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));

		editableAttachsUI = attachsUI.toString();

		// 构建不可编辑附件控件
		attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(this.isFlashUpload());
		attachsUI.addClazz("formAttachs ui-widget-content");
		attachsUI.addAttach(this.attachService.findByPtype(ptype, puid));
		attachsUI.setPuid(puid).setPtype(ptype);
		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
		attachsUI.setReadOnly(true);
		readonlyAttachsUI = attachsUI.toString();

		return SUCCESS;
	}

	@Override
	public String create() throws Exception {
		SystemContext context = (SystemContext) this.getContext();
		Attach e = this.getCrudService().create();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUser());
		e.setDepartId(context.getBelong().getId());
		e.setDepartName(context.getBelong().getName());
		e.setUnitId(context.getUnit().getId());
		e.setUnitName(context.getUnit().getName());

		this.setE(e);
		return "form";
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("subject");
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(900).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();

		// 是否附件管理员
		boolean isManager = ((SystemContext) this.getContext())
				.hasAnyRole(MANAGER_KEY);

		if (isManager) {
			// 删除按钮
			tb.addButton(getDefaultDeleteToolbarButton());
		} else {// 普通用户
			tb.addButton(getDefaultOpenToolbarButton());
		}

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "subject", "path", "extension", "ptype", "puid",
				"authorName" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("fileDate", getText("attach.fileDate"), 130)
				.setSortable(true).setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn("authorName", getText("attach.authorName"),
				80).setSortable(true));
		columns.add(new TextColumn("size", getText("attach.size"), 80)
				.setSortable(true).setValueFormater(new FileSizeFormater()));
		columns.add(new TextColumn("extension", getText("attach.extension"), 50)
				.setSortable(true));
		columns.add(new TextColumn("subject", getText("attach.subject"))
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("path", getText("attach.path"), 120)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("ptype", getText("attach.ptype"), 100)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("puid", getText("attach.puid"), 100)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("appPath", getText("attach.appPath"), 100)
				.setSortable(true).setValueFormater(
						new BooleanFormater(getText("label.yes"),
								getText("label.no"))));
		return columns;
	}

	public String filename;
	public String contentType;
	public InputStream inputStream;

	// 下载附件
	public String download() throws Exception {
		Attach attach = this.getCrudService().load(this.getId());
		contentType = AttachUtils.getContentType(attach.getExtension());
		filename = WebUtils.encodeFileName(ServletActionContext.getRequest(),
				attach.getSubject());
		String path;
		if (attach.isAppPath())
			path = WebUtils.rootPath + File.separator
					+ getText("app.data.subPath") + File.separator
					+ attach.getPath();
		else
			path = getText("app.data.realPath") + File.separator
					+ attach.getPath();

		inputStream = new FileInputStream(new File(path));

		// 累计下载次数
		attach.setCount(attach.getCount() + 1);
		this.getCrudService().save(attach);

		// TODO 记录一条下载痕迹
		// SystemContext context = (SystemContext) this.getContext();

		return SUCCESS;
	}

	static final int BUFFER = 4096;

	// 下载所有附件
	public String downloadAll() throws Exception {
		try {
			// 下载参数设置
			contentType = AttachUtils.getContentType("zip");
			filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(), "package." + ptype + "."
							+ DateUtils.formatDateTime(new Date()) + ".zip");

			// 获取所有附件
			List<Attach> attachs = this.getCrudService().createQuery()
					.condition(new EqualsCondition("ptype", ptype)).list();
			String path;

			// 开始打包
			BufferedInputStream origin = null;
			ByteArrayOutputStream dest = new ByteArrayOutputStream(BUFFER);
			ZipOutputStream zip = new ZipOutputStream(dest);
			for (int i = 0; i < attachs.size(); i++) {
				Attach attach = attachs.get(i);
				// 累计下载次数
				attach.setCount(attach.getCount() + 1);

				// 获取文件路径
				if (attach.isAppPath())
					path = WebUtils.rootPath + File.separator
							+ getText("app.data.subPath") + File.separator
							+ attach.getPath();
				else
					path = getText("app.data.realPath") + File.separator
							+ attach.getPath();

				File file = new File(path);
				if (file.exists()) {
					// 添加到压缩文件
					byte data[] = new byte[BUFFER];
					FileInputStream fi = new FileInputStream(file);
					origin = new BufferedInputStream(fi, BUFFER);
					ZipEntry entry = new ZipEntry((i + 1) + "_"
							+ attach.getSubject());
					zip.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER)) != -1) {
						zip.write(data, 0, count);
					}
					// int j = 0;
					// while (j < i) {
					// int k = request.getInputStream().read(buffer, j, i - j);
					// j += k;
					// }
					origin.close();
				} else {
					logger.error("丢失文件:" + path);
				}
			}
			zip.close();
			inputStream = new ByteArrayInputStream(dest.toByteArray());
			this.getCrudService().save(attachs);

			// TODO 记录下载痕迹
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return SUCCESS;
	}

	public String json;

	// 删除附件
	public String delete() {
		Json _json = new Json();
		try {
			Attach attach = this.getCrudService().load(this.getId());
			this.getCrudService().delete(this.getId());
			String path;
			if (attach.isAppPath())
				path = WebUtils.rootPath + File.separator
						+ getText("app.data.subPath") + File.separator
						+ attach.getPath();
			else
				path = getText("app.data.realPath") + File.separator
						+ attach.getPath();

			File file = new File(path);
			if (file.exists())
				file.delete();

			// TODO 记录一条删除痕迹
			// SystemContext context = (SystemContext) this.getContext();

			// 返回删除成功信息
			_json.put("success", true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			_json.put("success", false);
			_json.put("msg", e.getMessage());
		}

		json = _json.toString();
		return "json";
	}

	public String ptype;

	// 删除所有附件
	public String deleteAll() {
		Json _json = new Json();
		try {
			List<Attach> attachs = this.getCrudService().createQuery()
					.condition(new EqualsCondition("ptype", ptype)).list();
			this.getCrudService().delete(this.getId());
			String path;
			Long[] ids = new Long[attachs.size()];
			for (int i = 0; i < attachs.size(); i++) {
				Attach attach = attachs.get(i);
				ids[i] = attach.getId();
				if (attach.isAppPath())
					path = WebUtils.rootPath + File.separator
							+ getText("app.data.subPath") + File.separator
							+ attach.getPath();
				else
					path = getText("app.data.realPath") + File.separator
							+ attach.getPath();

				File file = new File(path);
				if (file.exists())
					file.delete();
			}
			this.getCrudService().delete(ids);

			// TODO 记录删除痕迹

			// 返回删除成功信息
			_json.put("success", true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			_json.put("success", false);
			_json.put("msg", e.getMessage());
		}

		json = _json.toString();
		return "json";
	}

	public String to;

	// 支持在线打开文档查看的文件下载
	public String inline() throws Exception {
		Attach attach = this.getCrudService().load(this.getId());
		String path;
		if (attach.isAppPath())
			path = WebUtils.rootPath + File.separator
					+ getText("app.data.subPath") + File.separator
					+ attach.getPath();
		else
			path = getText("app.data.realPath") + File.separator
					+ attach.getPath();

		if (isConvertFile(attach.getExtension())) {
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
			String from = attach.getExtension();
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
			this.inputStream = new ByteArrayInputStream(
					outputStream.toByteArray());

			inputStream.close();

			// 设置下载文件的参数（设置不对的话，浏览器是不会直接打开的）
			contentType = AttachUtils.getContentType(this.to);
			this.filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(), attach.getSubject()
							+ "." + this.to);
		} else {
			// 设置下载文件的参数
			contentType = AttachUtils.getContentType(attach.getExtension());
			filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(), attach.getSubject());

			// 无需转换的文档直接下载处理
			inputStream = new FileInputStream(new File(path));
		}

		// 累计下载次数
		attach.setCount(attach.getCount() + 1);
		this.getCrudService().save(attach);

		// TODO 记录一条下载痕迹
		// SystemContext context = (SystemContext) this.getContext();

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

	protected boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		if (header != null && "XMLHttpRequest".equals(header))
			return true;
		else
			return false;
	}
}
