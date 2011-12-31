/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.Context;
import cn.bc.core.exception.CoreException;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.util.ImageUtils;
import cn.bc.docs.web.AttachUtils;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.util.WebUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 图片剪切处理Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ImageAction extends ActionSupport implements SessionAware {
	private static Log logger = LogFactory.getLog(ImageAction.class);
	private static final long serialVersionUID = 1L;
	private AttachService attachService;
	public PageOption pageOption;// 页面的data-optio属性配置
	public int cropSize = 400;// 图片操作区的宽度和高度
	public int pageWidth = 545;// 对话框宽度
	public int preWidth = 110;// 图片预览区的宽度
	public int preHeight = 140;// 图片预览区的高度
	public String puid;// 图片所属文档的uid
	public String ptype;// 图片所属文档的类型
	public Long id;// 附件的id
	public String empty = "/bc/docs/image/empty.jpg";
	public String subpath = "images";// 图片附件处理后保存到的相对路径(相对于${app.data.realPath}目录下的路径)
	public boolean absolute;
	protected Map<String, Object> session;
	public String extensions = getText("app.attachs.images");//图片扩展名的限制，用逗号连接多个

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	public AttachWidget attachsUI;

	// 返回到裁剪上传图片的页面
	public String showCrop() throws Exception {
		// 时间戳
		ts = new Date().getTime();

		if (logger.isDebugEnabled()) {
			logger.debug("puid=" + puid);
			logger.debug("ptype=" + ptype);
			logger.debug("empty=" + empty);
			logger.debug("preWidth=" + preWidth);
			logger.debug("preHeight=" + preHeight);
		}

		// 设置页面的配置参数
		pageOption = new PageOption().setWidth(pageWidth).setMinWidth(250)
				.setMinHeight(200).setModal(true);
		pageOption.addButton(new ButtonOption(getText("label.ok"), null,
				"bc.showCrop.onOk"));// 确认按钮
		pageOption.put("srcWidth", 400);// 原始图片的宽度
		pageOption.put("srcHeight", 296);// 原始图片的高度
		pageOption.put("preWidth", preWidth);// 转换后图片的宽度
		pageOption.put("preHeight", preHeight);// 转换后图片的高度
		pageOption.put("puid", puid);// 图片所属文档的uid
		pageOption.put("ptype", ptype);// 图片所属文档的类型

		if (puid == null || puid.length() == 0) {
			throw new CoreException("puid不能为空！");
		}

		// 加载图片附件
		Attach attach = this.attachService.loadByPtype(ptype, puid);
		if (attach != null) {
			// 附件存在
			id = attach.getId();
		}

		// 构建附件控件
		// attachsUI = buildAttachsUI();

		if (logger.isDebugEnabled()) {
			logger.debug("id=" + id);
		}
		return SUCCESS;
	}

	public int cw;
	public int ch;
	public int cx;
	public int cy;
	public Json json;

	// 裁剪图片
	public String doCrop() throws Exception {
		Calendar now = Calendar.getInstance();
		if (logger.isDebugEnabled()) {
			logger.debug("puid=" + puid);
			logger.debug("ptype=" + ptype);
			logger.debug("id=" + id);
			logger.debug("empty=" + empty);
			logger.debug("cw=" + cw);
			logger.debug("ch=" + ch);
			logger.debug("cx=" + cx);
			logger.debug("cy=" + cy);
			logger.debug("preWidth=" + preWidth);
			logger.debug("preHeight=" + preHeight);
			// logger.debug("absolute=" + absolute);
		}
		String appRealDir = getText("app.data.realPath");
		String appSubDir = getText("app.data.subPath");

		// 新路径
		String subFolder = new SimpleDateFormat("yyyyMM").format(now.getTime());

		// 加载附件
		Attach attach;
		String srcPath;
		String newImgPath;
		if (id == null) {
			attach = new Attach();
			attach.setFileDate(now);
			attach.setAppPath(false);
			attach.setAuthor(this.getContext().getUserHistory());
			attach.setExtension("jpg");
			attach.setPuid(puid);
			attach.setPtype(ptype);
			attach.setStatus(BCConstants.STATUS_ENABLED);
			// attach.setPath(empty.substring(1));
			attach.setSubject("empty");
			// attach.setSize(0);
			srcPath = WebUtils.rootPath + empty;
			newImgPath = appRealDir + "/" + subFolder;
		} else {
			attach = this.attachService.load(id);
			if (attach == null)
				throw new CoreException("attach is null:id=" + id);
			if (attach.isAppPath()) {
				srcPath = WebUtils.rootPath + "/" + appSubDir + "/"
						+ attach.getPath();
				newImgPath = WebUtils.rootPath + "/" + appSubDir + "/"
						+ subFolder;
			} else {
				srcPath = appRealDir + "/" + attach.getPath();
				newImgPath = appRealDir + "/" + subFolder;
			}
		}

		// 获取原图
		String extension = attach.getExtension();

		logger.debug("srcPath=" + srcPath);
		File srcFile = new File(srcPath);
		InputStream srcImg = new FileInputStream(srcFile);

		// 对图片进行裁剪处理
		BufferedImage newImg = ImageUtils.cropAndZoom(srcImg, cx, cy, cw, ch,
				preWidth, preHeight, extension);

		// 构建文件要保存到的目录
		File _fileDir = new File(newImgPath);
		if (!_fileDir.exists()) {
			if (logger.isFatalEnabled()) {
				logger.fatal("mkdir=" + newImgPath);
			}
			_fileDir.mkdirs();
		}

		// 保存裁剪后的图片(路径与原图相同，经文件名改为当前时间)
		String newImgName = new SimpleDateFormat("yyyyMMddHHmmssSSSS")
				.format(now.getTime()) + "." + extension;// 不含路径的文件名
		String newSavePath = newImgPath + "/" + newImgName;
		logger.debug("newSavePath=" + newSavePath);
		// logger.debug("newImgPath=" + newImgPath);
		// logger.debug("newImgName=" + newImgName);
		File newFile = new File(newSavePath);
		ImageIO.write(newImg, extension, newFile);

		// 更新原图对应的附件记录为裁剪后的图片
		attach.setSize(newFile.length());
		attach.setPath(subFolder + "/" + newImgName);
		attach.setModifiedDate(now);
		attach.setModifier(this.getContext().getUserHistory());
		this.attachService.save(attach);

		json = new Json();
		json.put("success", true);
		json.put("appPath", attach.isAppPath());
		json.put("path", attach.getPath());
		json.put("id", attach.getId());
		json.put("puid", attach.getPuid());
		json.put("ptype", attach.getPtype());
		json.put("size", attach.getSize());

		return "json";
	}

	public SystemContext getContext() {
		return (SystemContext) this.session.get(Context.KEY);
	}

	// 裁剪图片
	public String download() throws Exception {
		// 加载附件
		Attach attach;
		if (id != null) {
			attach = this.attachService.load(id);
		} else {
			if (puid == null) {
				throw new CoreException("puid is null.");
			} else {
				attach = this.attachService.loadByPtype(ptype, puid);
			}
		}
		String filepath,extension;
		if (attach == null){
			//使用空白图片代替
			extension = "jpg";
			filepath = WebUtils.rootPath + empty;
		}else{
			extension = attach.getExtension();
			if (attach.isAppPath())
				filepath = WebUtils.rootPath + "/" + getText("app.data.subPath")
						+ "/" + attach.getPath();
			else
				filepath = getText("app.data.realPath") + "/" + attach.getPath();
		}

		downloadAttach(filepath,extension);
		return SUCCESS;
	}

	public String filename;
	public String contentType;
	public long contentLength;
	public InputStream inputStream;
	public long ts;// 时间戳

	private void downloadAttach(String filepath,String extension) {
		try {
			contentType = AttachUtils.getContentType(extension);
//			filename = WebUtils.encodeFileName(
//					ServletActionContext.getRequest(), attach.getSubject());
			File file = new File(filepath);
			contentLength = file.length();
			inputStream = new FileInputStream(file);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
