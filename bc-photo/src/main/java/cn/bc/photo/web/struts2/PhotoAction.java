/**
 *
 */
package cn.bc.photo.web.struts2;

import cn.bc.core.exception.CoreException;
import cn.bc.docs.domain.Attach;
import cn.bc.photo.service.PhotoExecutor;
import cn.bc.photo.service.PhotoService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.util.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.util.Date;
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
	private PhotoService photoService;

	/**
	 * 1）关联平台的附件时格式为：attach:[附件主键]
	 * 2）关联流程的附件时格式为：wf:[附件主键]
	 * 3）指定文件相对于"/bcdata"目录下的相对路径，如"201310/201310110130220001.jpg"
	 */
	public String id;
	public JSONObject json;
	public String dir;// 指定的子路径（相对于bcdata路径）
	public String path;// 图片文件(含相对路径和扩展名)，如果没有指定则按时间错自动生成
	public String fname;// 图片原始名称
	public String format;// 图片类型
	public String data;// 图片的base64编码数据

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
				this.path = (String) info.get("path");
				this.format = (String) info.get("format");
				this.fname = (String) info.get("fname");
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
			logger.debug("id=" + id);
			logger.debug("dir=" + dir);
			logger.debug("path=" + path);
			logger.debug("fname=" + fname);
			logger.debug("format=" + format);
			logger.debug("data=" + data);
		}
		json = new JSONObject();
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			String file = "", newName;
			if (this.path != null && !this.path.isEmpty()) {// 指定目标文件：通常为编辑状态的处理
				file += this.path;
			} else {// 新建的文件
				// 附加子目录
				if (this.dir != null && !this.dir.isEmpty()) {
					file += this.dir + "/";
				}

				// 构建时间目录
				Date now = new Date();
				file += DateUtils.format(now, "yyyyMM") + "/";
				newName = DateUtils.format(now, "yyyyMMddHHmmssSSSS") + "." + this.format;
				file += newName;
			}

			// 处理base64数据
			if (this.data == null || this.data.isEmpty())
				throw new CoreException("没有图像数据！");
			this.data = this.data.substring(this.data.indexOf(",") + 1);
			if (this.data.isEmpty())
				throw new CoreException("没有图像数据！");

			// base64字符转换为图片文件保存
			logger.debug("file=" + file);
			File _file = new File(Attach.DATA_REAL_PATH + "/" + file);
			if (!_file.exists())
				_file.getParentFile().mkdirs();
			FileCopyUtils.copy(decoder.decodeBuffer(this.data), _file);

			json.put("success", true);
			json.put("id", this.id);
			json.put("dir", this.dir);
			json.put("path", file);
			json.put("fname", this.fname);
			json.put("format", this.format);
			json.put("size", 111111);// TODO
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			json.put("success", false);
			json.put("msg", e.getMessage());
		}
		return "json";
	}
}