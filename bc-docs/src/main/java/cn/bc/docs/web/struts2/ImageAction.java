/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.Context;
import cn.bc.core.exception.CoreException;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.util.ImageUtils;
import cn.bc.docs.web.AttachUtils;
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
	public int preWidth = 110;// 图片预览区的宽度
	public int preHeight = 140;// 图片预览区的高度
	public String puid;// 图片所属文档的uid
	public String ptype;// 图片所属文档的类型
	public Long id;// 附件的id
	public String empty = "/bc/docs/image/empty.jpg";
	public String subpath = "images";// 图片附件处理后保存到的相对路径(相对于${app.data.realPath}目录下的路径)
	public boolean absolute;
	protected Map<String, Object> session;

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	// 返回到裁剪上传图片的页面
	public String showCrop() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("puid=" + puid);
			logger.debug("ptype=" + ptype);
			logger.debug("id=" + id);
			logger.debug("empty=" + empty);
			logger.debug("preWidth=" + preWidth);
			logger.debug("preHeight=" + preHeight);
		}

		// 设置页面的配置参数
		pageOption = new PageOption().setWidth(545).setMinWidth(250)
				.setMinHeight(200).setModal(true);
		pageOption.addButton(new ButtonOption(getText("label.ok"), null,
				"bc.cropImage.onOk"));// 确认按钮
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
		List<Attach> list = this.attachService.findByPtype(ptype, puid);
		if (list != null && !list.isEmpty()) {
			// 附件存在
			id = list.get(0).getId();
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
			logger.debug("absolute=" + absolute);
		}
		String appRealDir = getText("app.data.realPath");
		String appSubDir = getText("app.data.subPath");

		// 加载附件
		if (id == null)
			throw new CoreException("没有指定附件id的值！");
		Attach attach = this.attachService.load(id);
		if (attach == null)
			throw new CoreException("附件不存在！id=" + id);

		// 获取原图
		String extension = attach.getExtension();
		String srcPath;
		if (attach.isAppPath())
			srcPath = WebUtils.rootPath + "/" + appSubDir + "/"
					+ attach.getPath();
		else
			srcPath = appRealDir + "/" + attach.getPath();

		logger.debug("srcPath=" + srcPath);
		File srcFile = new File(srcPath);
		InputStream srcImg = new FileInputStream(srcFile);

		// 对图片进行裁剪处理
		BufferedImage newImg = ImageUtils.cropAndZoom(srcImg, cx, cy, cw, ch,
				preWidth, preHeight, extension);

		// 保存裁剪后的图片(路径与原图相同，经文件名改为当前时间)
		Calendar now = Calendar.getInstance();
		String newImgName = new SimpleDateFormat("yyyyMMddHHmmssSSSS")
				.format(now.getTime()) + "." + extension;// 不含路径的文件名
		String newImgPath = srcFile.getParent();
		logger.debug("newImgPath=" + newImgPath);
		logger.debug("newImgName=" + newImgName);
		File newFile = new File(newImgPath + "/" + newImgName);
		ImageIO.write(newImg, extension, newFile);

		// 更新原图对应的附件记录为裁剪后的图片
		attach.setSize(newFile.length());
		String newSavePath = attach.getPath().substring(0,
				attach.getPath().lastIndexOf("/") + 1)
				+ newImgName;
		logger.debug("newSavePath=" + newSavePath);
		attach.setPath(newSavePath);
		attach.setModifiedDate(now);
		attach.setModifier(this.getContext().getUserHistory());
		this.attachService.save(attach);

		json = new Json();
		json.put("success", true);
		json.put("path", attach.getPath());

		return "json";
	}

	public SystemContext getContext() {
		return (SystemContext) this.session.get(Context.KEY);
	}

	// 裁剪图片
	public String download() throws Exception {
		// 加载附件
		if (id == null)
			throw new CoreException("没有指定附件id的值！");
		Attach attach = this.attachService.load(id);
		if (attach == null)
			throw new CoreException("附件不存在！id=" + id);

		downloadAttach(attach);
		return SUCCESS;
	}

	public String filename;
	public String contentType;
	public long contentLength;
	public InputStream inputStream;
//	public long lastModified;
//	public long expires;

	private void downloadAttach(Attach attach) throws FileNotFoundException {
		contentType = AttachUtils.getContentType(attach.getExtension());
		filename = WebUtils.encodeFileName(ServletActionContext.getRequest(),
				attach.getSubject());
		String path;
		if (attach.isAppPath())
			path = WebUtils.rootPath + "/" + getText("app.data.subPath") + "/"
					+ attach.getPath();
		else
			path = getText("app.data.realPath") + "/" + attach.getPath();

		File file = new File(path);
		contentLength = file.length();
		inputStream = new FileInputStream(file);

//		lastModified = file.lastModified();
//		expires = new Date().getTime() + (30 * 1000);// 缓存30秒
	}
}
