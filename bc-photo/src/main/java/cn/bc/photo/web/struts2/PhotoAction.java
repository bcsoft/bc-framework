/**
 *
 */
package cn.bc.photo.web.struts2;

import cn.bc.core.exception.CoreException;
import cn.bc.docs.domain.Attach;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.util.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;
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

    /**
     * 1）关联平台的附件时格式为：a:[附件主键]
     * 2）关联流程的附件时格式为：p:[附件主键]
     * 3）指定文件相对于bcdata目录下的相对路径，如"201310/201310110130220001.jpg"
     */
    public String id;

    private Map<String, PhotoExecutor> executors;

    public void setExecutors(Map<String, PhotoExecutor> executors) {
        this.executors = executors;
    }

    @Override
    public String execute() throws Exception {
        // 页面参数
        pageOption = new PageOption();
        pageOption.setWidth(600).setHeight(400).setMinWidth(450).setMinHeight(350);

        // 添加操作按钮
        //-- 拍照按钮
        pageOption.addButton(new ButtonOption("拍照", null, "bc.photo.captureCamera").setId("captureCameraBtn"));
        //-- 打开图片按钮
        pageOption.addButton(new ButtonOption("打开图片", null, "jQuery.noop").setId("openImageBtn"));
        //-- 下载按钮
        pageOption.addButton(new ButtonOption("下载", null, "bc.photo.download").setId("downloadBtn"));
        //-- 完成按钮
        pageOption.addButton(new ButtonOption("完成", null, "bc.photo.ok").setId("okBtn"));

        // 编辑现有附件的处理
        if (id != null && !id.isEmpty()) {
            String[] tid = id.split(":");
            if (tid.length > 1) {
                PhotoExecutor executor = executors.get(tid[0]);
                if ("a".equals(tid[0])) {// Attach附件的编辑

                } else if ("p".equals(tid[0])) {// 流程附件的编辑

                } else {// 指定文件路径的处理

                }
            } else {// 指定文件路径的处理

            }
        }

        return super.execute();
    }

    public JSONObject json;
    public String type;// 图片类型
    public String name;// 图片原始名称
    public String path;// 图片文件(含相对路径和扩展名)，如果没有指定则按时间错自动生成
    public String dir;// 指定的子路径（相对于bcdata路径）
    public String data;// 图片的八base64编码数据

    /**
     * 上传文件
     *
     * @return
     * @throws Exception
     */
    public String upload() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("type=" + type);
            logger.debug("name=" + name);
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
                newName = DateUtils.format(now, "yyyyMMddHHmmssSSSS") + "." + this.type;
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
            json.put("file", file);
            json.put("name", this.name);
            json.put("type", this.type);
            json.put("dir", this.dir);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            json.put("success", false);
            json.put("msg", e.getMessage());
        }
        return "json";
    }
}