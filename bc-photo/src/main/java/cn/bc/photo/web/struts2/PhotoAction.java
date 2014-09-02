/**
 *
 */
package cn.bc.photo.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.photo.domain.IpCamera;
import cn.bc.photo.service.IpCameraService;
import cn.bc.photo.service.PhotoExecutor;
import cn.bc.photo.service.PhotoService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.util.DateUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 图片处理Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PhotoAction extends ActionSupport {
	private static Log logger = LogFactory.getLog(PhotoAction.class);
	private static final long serialVersionUID = 1L;
	public PageOption pageOption;
	@Autowired
	private AttachService attachService;
	@Autowired
	private PhotoService photoService;
	@Autowired
	private IpCameraService ipCameraService;

	public JSONObject json;// 图片处理完毕后返回的json数据：{success、path、url、fname、size、format [、dir、msg、id]}
	/**
	 * 1）关联平台的附件时格式为：attach:[附件主键]
	 * 2）关联流程的附件时格式为：wf:[附件主键]
	 * 3）指定文件相对于"/bcdata"目录下的相对路径，如"201310/201310110130220001.jpg"
	 */
	public String id;
	public String dir;// 指定的子路径（相对于bcdata路径）
	public String path;// 图片文件(含相对路径和扩展名)，如果没有指定则按时间错自动生成
	public String fname;// 图片原始名称
	public String format;// 图片类型
	public String data;// 图片的base64编码数据
	public String url;// 图片的web访问路径，可以直接设置为img的src的url路径
	public Integer size;// 图片大小
	public String ptype;// 创建Attach附件时使用
	public String puid;// 创建Attach附件时使用
	public List<IpCamera> ipCameras;// 可用的IP摄像头列表

	@Override
	public String execute() throws Exception {
		// 页面参数
		pageOption = new PageOption();
		pageOption.setWidth(600).setHeight(400).setMinWidth(450).setMinHeight(350);

		// 添加操作按钮
		//-- 拍照按钮
		pageOption.addButton(new ButtonOption("拍照", null, "bc.photo.handler.captureCamera").setId("captureCameraBtn"));
		//-- 打开图片按钮
		pageOption.addButton(new ButtonOption("打开图片", null, "jQuery.noop").setId("openImageBtn"));
		//-- 下载按钮
		pageOption.addButton(new ButtonOption("下载", null, "bc.photo.handler.download").setId("downloadBtn"));
		//-- 完成按钮
		pageOption.addButton(new ButtonOption("完成", null, "bc.photo.handler.ok").setId("okBtn"));

		// 编辑现有附件的处理
		if (id != null && !id.isEmpty()) {
			String[] tid = id.split(":");
			if (tid.length > 1) {// 标准附件和流程附件的处理
				PhotoExecutor photoExecutor = photoService.getPhotoExecutors().get(tid[0]);
				if (photoExecutor == null)
					throw new CoreException("undefined PhotoExecutor: code=" + tid[0]);
				Map<String, Object> info = photoExecutor.execute(tid[1]);
				this.url = (String) info.get("url");
				this.dir = (String) info.get("dir");
				this.path = (String) info.get("path");
				this.fname = (String) info.get("fname");
				this.format = (String) info.get("format");
				this.size = (Integer) info.get("size");
				//if(info.containsKey("ptype")) this.ptype = (String) info.get("ptype");
				//if(info.containsKey("puid")) this.puid = (String) info.get("puid");
			} else {// 指定文件路径的处理
				this.path = tid[0];

				// 从文件路径名解析出扩展名
				if (this.format == null || this.format.isEmpty()) {
					this.format = this.path.substring(this.path.lastIndexOf(".") + 1);
				}

				// 从文件路径名解析出文件名
				if (this.fname == null || this.fname.isEmpty()) {
					this.fname = this.path.substring(this.path.lastIndexOf("/") + 1);
					this.fname = this.fname.substring(0, this.fname.lastIndexOf("."));
				}
			}
		} else {
			// 空白窗口
		}

		// 获取可用的IP摄像头列表
		Actor user = SystemContextHolder.get().getUser();
		this.ipCameras = this.ipCameraService.findByOwner(user.getId());

		return super.execute();
	}

	/**
	 * 上传文件
	 *
	 * @return
	 * @throws Exception
	 */
	public String upload() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("id=" + this.id);
			logger.debug("dir=" + this.dir);
			logger.debug("path=" + this.path);
			logger.debug("fname=" + this.fname);
			logger.debug("format=" + this.format);
			logger.debug("ptype=" + this.ptype);
			logger.debug("puid=" + this.puid);
			logger.debug("data=" + this.data);
		}
		json = new JSONObject();
		try {
			// 相对于"/bcdata"目录下的文件全路径名
			String file = "";

			// 附加子路径
			if (this.dir != null && !this.dir.isEmpty()) {
				file += this.dir + "/";
			}

			// 附加文件相对路径
			if (this.path == null || this.path.isEmpty()) {// 自动创建文件路径
				Date now = new Date();
				this.path = DateUtils.format(now, "yyyyMM") + "/";// 年月
				this.path += DateUtils.format(now, "yyyyMMddHHmmssSSSS") + "." + this.format;// 文件名
			}
			file += this.path;
			logger.debug("file=" + file);

			// 处理base64数据
			if (this.data == null || this.data.isEmpty()) {
				throw new CoreException("没有图像数据！");
			}
			if (!this.data.startsWith("data:image/")) {
				throw new CoreException("错误的图像数据格式！");
			}
			this.data = this.data.substring(this.data.indexOf(",") + 1);

			// base64字符转换为图片文件保存
			File _file = new File(Attach.DATA_REAL_PATH + "/" + file);
			if (!_file.exists())
				_file.getParentFile().mkdirs();
			BASE64Decoder decoder = new BASE64Decoder();
			FileCopyUtils.copy(decoder.decodeBuffer(this.data), _file);

			// 保存或更新Attach
			if (this.id != null && !this.id.isEmpty()) {
				if (this.id.startsWith("attach:")) {// 更新现有Attach附件的信息
					String[] id_cfg = this.id.split(":");
					Attach attach = this.attachService.load(Long.parseLong(id_cfg[1]));
					attach.setModifier(SystemContextHolder.get().getUserHistory());
					attach.setModifiedDate(Calendar.getInstance());
					attach.setSize(_file.length());
					this.attachService.save(attach);
				}
			} else if (this.ptype != null && !this.ptype.isEmpty()) {// 创建新的Attach附件
				Attach attach = new Attach();
				attach.setAuthor(SystemContextHolder.get().getUserHistory());
				attach.setFileDate(Calendar.getInstance());
				attach.setFormat(this.format);
				attach.setSubject(this.fname);
				attach.setStatus(BCConstants.STATUS_ENABLED);
				attach.setPtype(this.ptype);
				attach.setPuid(this.puid);
				attach.setAppPath(false);
				attach.setPath(this.path);
				attach.setSize(_file.length());
				attach.setModifier(SystemContextHolder.get().getUserHistory());
				attach.setModifiedDate(Calendar.getInstance());
				this.attachService.save(attach);
				this.id = "attach:" + attach.getId();
			}

			json.put("success", true);
			json.put("id", this.id);
			json.put("dir", this.dir);
			json.put("path", this.path);
			json.put("fname", this.fname);
			json.put("format", this.format);
			json.put("ptype", this.ptype);
			json.put("puid", this.puid);
			json.put("size", _file.length());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			json.put("success", false);
			json.put("msg", e.getMessage());
		}
		return "json";
	}
}