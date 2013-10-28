/**
 *
 */
package cn.bc.photo.web.struts2;

import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 图片处理Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PhotoAction extends ActionSupport {
    // private static Log logger = LogFactory.getLog(PhotoAction.class);
    private static final long serialVersionUID = 1L;
    public PageOption pageOption;

    @Override
    public String execute() throws Exception {
        // 页面参数
        pageOption = new PageOption();
        pageOption.setWidth(800).setHeight(600).setMinWidth(500).setMinHeight(400);

        // 添加操作按钮
        //-- 拍照按钮
        pageOption.addButton(new ButtonOption("拍照", null, "bc.photo.captureCamera").setId("captureCameraBtn"));
        //-- 打开图片按钮
        pageOption.addButton(new ButtonOption("打开图片", null, "jQuery.noop").setId("openImageBtn"));
        //-- 下载按钮
        pageOption.addButton(new ButtonOption("下载", null, "bc.photo.download").setId("downloadBtn"));
        //-- 完成按钮
        pageOption.addButton(new ButtonOption("完成", null, "bc.photo.ok").setId("okBtn"));

        return super.execute();
    }
}