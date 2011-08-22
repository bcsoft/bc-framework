/**
 * 
 */
package cn.bc.docs.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 图片剪切处理Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ImageAction extends ActionSupport {
	// private static Log logger = LogFactory.getLog(ImageCropAction.class);
	private static final long serialVersionUID = 1L;
	public PageOption pageOption;// 页面的data-optio属性的n配置
	public int preWidth = 110;
	public int preHeight = 140;
	public String puid;
	public String ptype;

	// 返回到裁剪上传图片的页面
	public String crop() throws Exception {
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

		return SUCCESS;
	}
}
