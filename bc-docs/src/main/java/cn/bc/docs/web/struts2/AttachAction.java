/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.service.CrudService;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.domain.AttachHistory;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.util.OfficeUtils;
import cn.bc.docs.web.AttachUtils;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.json.Json;
import cn.bc.web.util.WebUtils;

/**
 * 基于Attach的附件处理Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AttachAction extends EntityAction<Long, Attach> implements
		SessionAware {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;

	private ActorHistoryService actorHistoryService;
	private AttachService attachService;
	private CrudService<AttachHistory> attachHistoryService;

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
		this.setCrudService(attachService);
	}

	@Autowired
	public void setAttachHistoryService(
			@Qualifier(value = "attachHistoryService") CrudService<AttachHistory> attachHistoryService) {
		this.attachHistoryService = attachHistoryService;
	}

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.actor"),
				getText("key.role.bc.admin"));// 组织架构管理或超级管理角色
	}

	public String editableAttachsUI;
	public String readonlyAttachsUI;

	// 用于附件控件的设计
	public String design() throws Exception {
		String ptype = "test.main";
		String puid = "test.uid.1";

		// 构建可编辑附件控件
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(isFlashUpload());
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
		attachsUI.setFlashUpload(isFlashUpload());
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
		e.setAuthor(context.getUserHistory());

		this.setE(e);
		return "form";
	}

	public String filename;
	public String contentType;
	public long contentLength;
	public InputStream inputStream;
	public String path;

	// 下载附件
	public String download() throws Exception {
		Attach attach;
		if (this.puid == null || this.puid.length() == 0) {// 下载正常的附件
			attach = this.attachService.load(this.getId());
		} else {// 下载浏览器
			attach = this.attachService.loadByPtype(null, this.puid);
			if (attach == null)
				throw new CoreException("undefined browser:puid=" + this.puid);
		}
		downloadAttach(attach);
		return SUCCESS;
	}

	// 下载物理附件
	public String file() throws Exception {
		if (this.path == null || this.path.length() == 0) {// 下载正常的附件
			throw new Exception("need path");
		}

		// 获取附件的物理文件路径
		String path = getText("app.data.realPath") + File.separator + this.path;
		String ext = StringUtils.getFilenameExtension(path);
		String fn = StringUtils.getFilename(path);

		// 处理物理文件的下载
		this.downloadFile(ext, path, fn);

		return SUCCESS;
	}

	private void downloadAttach(Attach attach) throws FileNotFoundException {
		// 获取附件的物理文件路径
		String path;
		if (attach.isAppPath())
			path = WebUtils.rootPath + File.separator
					+ getText("app.data.subPath") + File.separator
					+ attach.getPath();
		else
			path = getText("app.data.realPath") + File.separator
					+ attach.getPath();

		// 处理物理文件的下载
		this.downloadFile(attach.getFormat(), path, attach.getSubject());

		// 累计下载次数
		attach.setCount(attach.getCount() + 1);
		this.getCrudService().save(attach);

		// 记录一条下载痕迹
		this.attachHistoryService.save(buildHistory(
				AttachHistory.TYPE_DOWNLOAD, attach));
	}

	private void downloadFile(String ext, String path, String filename)
			throws FileNotFoundException {
		this.contentType = AttachUtils.getContentType(ext);
		this.filename = WebUtils.encodeFileName(
				ServletActionContext.getRequest(), filename);
		File file = new File(path);
		this.contentLength = file.length();
		this.inputStream = new FileInputStream(file);
	}

	private AttachHistory buildHistory(int type, Attach attach) {
		SystemContext context = (SystemContext) this.getContext();
		AttachHistory ah = new AttachHistory();
		ah.setFileDate(Calendar.getInstance());
		ah.setFormat(attach.getFormat());
		ah.setType(type);
		if (context == null) {
			ah.setAuthor(this.actorHistoryService.loadByCode("admin"));
			ah.setMemo("[unauth]");
		} else {
			ah.setAuthor(context.getUserHistory());
		}

		ah.setPtype(Attach.class.getSimpleName());
		ah.setPuid(attach.getId().toString());
		ah.setSubject(attach.getSubject());
		ah.setPath(attach.getPath());
		ah.setAppPath(attach.isAppPath());

		// 客户端信息
		HttpServletRequest request = ServletActionContext.getRequest();
		ah.setClientInfo(request.getHeader("User-Agent"));
		String key = "X-Forwarded-For";// 如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串Ip值,其中第一个非unknown的有效IP字符串是真正的用户端的真实IP
		String clientIp = request.getHeader(key);
		if (clientIp == null || clientIp.length() == 0
				|| "unknown".equalsIgnoreCase(clientIp)) {
			key = "Proxy-Client-IP";
			clientIp = request.getHeader(key);
		}
		if (clientIp == null || clientIp.length() == 0
				|| "unknown".equalsIgnoreCase(clientIp)) {
			key = "WL-Proxy-Client-IP"; // Weblogic集群获取客户端IP
			clientIp = request.getHeader(key);
		}
		if (clientIp == null || clientIp.length() == 0
				|| "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getRemoteAddr();// 获得客户端电脑的名字，若失败则返回客户端电脑的ip地址
		}
		ah.setClientIp(clientIp);

		return ah;
	}

	static final int BUFFER = 4096;

	// 下载所有附件
	public String downloadAll() throws Exception {
		try {
			// 下载参数设置
			contentType = AttachUtils.getContentType("zip");
			filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(),
					DateUtils.formatDateTime(new Date(), "yyyyMMddHHmmss")
							+ (ptype == null ? "" : ptype) + ".zip");

			List<Attach> attachs;
			if (this.getIds() != null && this.getIds().length() > 0) {// 指定的附件
				String[] _ids = this.getIds().split(",");
				Long[] ids = new Long[_ids.length];
				for (int i = 0; i < _ids.length; i++)
					ids[i] = new Long(_ids[i]);
				attachs = this.getCrudService().createQuery()
						.condition(new InCondition("id", ids)).list();
			} else {// 所有附件(状态为正常的)
				attachs = this
						.getCrudService()
						.createQuery()
						.condition(
								new AndCondition()
										.add(new EqualsCondition("ptype", ptype))
										.add(new EqualsCondition("puid", puid))
										.add(new EqualsCondition("status",
												BCConstants.STATUS_ENABLED)))
						.list();
			}
			String path;

			// 开始打包
			BufferedInputStream origin = null;
			ByteArrayOutputStream dest = new ByteArrayOutputStream(BUFFER);
			ZipOutputStream zip = new ZipOutputStream(dest);
			List<AttachHistory> ahs = new ArrayList<AttachHistory>();
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
				String name, extFromPath, extFromName;
				if (file.exists()) {
					// 获取压缩文件显示的文件名
					name = attach.getSubject();
					extFromPath = StringUtils.getFilenameExtension(path);

					// 附加实际的扩展名
					if (name.lastIndexOf(".") == -1) {
						if (extFromPath != null) {
							name += "." + extFromPath;
						}
					} else {
						extFromName = StringUtils.getFilenameExtension(name);
						if (!extFromName.equals(extFromPath)) {
							name += "." + extFromPath;
						}
					}

					// 添加到压缩文件
					byte data[] = new byte[BUFFER];
					FileInputStream fi = new FileInputStream(file);
					origin = new BufferedInputStream(fi, BUFFER);
					ZipEntry entry = new ZipEntry((i + 1) + "_" + name);
					zip.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER)) != -1) {
						zip.write(data, 0, count);
					}
					origin.close();

					ahs.add(buildHistory(AttachHistory.TYPE_ZIP, attach));
				} else {
					logger.error("丢失文件:" + path);
				}
			}
			zip.close();
			inputStream = new ByteArrayInputStream(dest.toByteArray());
			contentLength = dest.size();
			this.getCrudService().save(attachs);

			// 记录下载痕迹
			this.attachHistoryService.save(ahs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return SUCCESS;
	}

	// 删除附件
	public String delete() {
		Json _json = new Json();
		try {
			Attach attach = this.getCrudService().load(this.getId());
			// this.getCrudService().delete(this.getId());
			// String path;
			// if (attach.isAppPath())
			// path = WebUtils.rootPath + File.separator
			// + getText("app.data.subPath") + File.separator
			// + attach.getPath();
			// else
			// path = getText("app.data.realPath") + File.separator
			// + attach.getPath();
			//
			// File file = new File(path);
			// if (file.exists())
			// file.delete();

			// 将附件标记为删除状态
			attach.setStatus(BCConstants.STATUS_DELETED);
			this.getCrudService().save(attach);

			// 记录一条删除痕迹
			this.attachHistoryService.save(buildHistory(
					AttachHistory.TYPE_DELETED, attach));

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
	public String puid;

	// 删除所有附件
	public String deleteAll() {
		Json _json = new Json();
		try {
			List<Attach> attachs = this.getCrudService().createQuery()
					.condition(new EqualsCondition("ptype", ptype)).list();
			// String path;
			// Long[] ids = new Long[attachs.size()];
			List<AttachHistory> ahs = new ArrayList<AttachHistory>();
			for (int i = 0; i < attachs.size(); i++) {
				Attach attach = attachs.get(i);
				// ids[i] = attach.getId();
				// if (attach.isAppPath())
				// path = WebUtils.rootPath + File.separator
				// + getText("app.data.subPath") + File.separator
				// + attach.getPath();
				// else
				// path = getText("app.data.realPath") + File.separator
				// + attach.getPath();
				//
				// File file = new File(path);
				// if (file.exists())
				// file.delete();

				// 将附件标记为删除状态
				attach.setStatus(BCConstants.STATUS_DELETED);

				// 记录一条删除痕迹
				ahs.add(buildHistory(AttachHistory.TYPE_DELETED, attach));
			}
			// this.getCrudService().delete(ids);
			this.getCrudService().save(attachs);

			// 记录删除痕迹
			this.attachHistoryService.save(ahs);

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
		Date startTime = new Date();
		Attach attach = this.getCrudService().load(this.getId());
		String path;
		if (attach.isAppPath())
			path = WebUtils.rootPath + File.separator
					+ getText("app.data.subPath") + File.separator
					+ attach.getPath();
		else
			path = getText("app.data.realPath") + File.separator
					+ attach.getPath();

		if (isConvertFile(attach.getFormat())) {
			if (this.to == null || this.to.length() == 0) {
				this.to = getText("jodconverter.to.extension");// 没有指定就是用系统默认的配置转换为pdf
//				if ("xls".equalsIgnoreCase(attach.getFormat())
//						|| "xlsx".equalsIgnoreCase(attach.getFormat())
//						|| "xlsm".equalsIgnoreCase(attach.getFormat())) {
//					this.to = "html";// excel默认转换为html格式（因为转pdf的A4纸张导致大报表换页乱了）
//				} else {
//					this.to = getText("jodconverter.to.extension");// 没有指定就是用系统默认的配置转换为pdf
//				}
			}

			if (attach.isAppPath()) {
				// 转换附件格式后再下载
				FileInputStream inputStream = new FileInputStream(
						new File(path));
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
						BUFFER);

				// convert
				OfficeUtils.convert(inputStream, attach.getFormat(),
						outputStream, this.to);

				byte[] bs = outputStream.toByteArray();
				this.inputStream = new ByteArrayInputStream(bs);
			} else {// 转换文档
				this.inputStream = OfficeUtils.convert(attach.getPath(),
						this.to, true);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("convert:" + DateUtils.getWasteTime(startTime));
			}

			// 设置下载文件的参数（设置不对的话，浏览器是不会直接打开的）
			this.contentType = AttachUtils.getContentType(this.to);
			this.contentLength = this.inputStream.available();
			this.filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(), attach.getSubject()
							+ "." + this.to);
		} else {
			// 设置下载文件的参数
			contentType = AttachUtils.getContentType(attach.getFormat());
			filename = WebUtils.encodeFileName(
					ServletActionContext.getRequest(), attach.getSubject());

			// 无需转换的文档直接下载处理
			File file = new File(path);
			contentLength = file.length();
			inputStream = new FileInputStream(file);
		}

		// 累计下载次数
		attach.setCount(attach.getCount() + 1);
		this.getCrudService().save(attach);

		// 记录一条下载痕迹
		this.attachHistoryService.save(buildHistory(AttachHistory.TYPE_INLINE,
				attach));

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

	@Override
	protected String getJs() {
		return this.getContextPath() + "/bc/docs/attach/list.js";
	}

	// 返回下载系统支持的浏览器列表的页面
	public String browser() throws Exception {
		return SUCCESS;
	}
}
