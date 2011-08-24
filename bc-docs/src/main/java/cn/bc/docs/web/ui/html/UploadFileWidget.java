package cn.bc.docs.web.ui.html;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.web.ui.html.Div;

/**
 * 代替浏览器默认上传附件的UI组件
 * 
 * @author dragon
 * 
 */
public class UploadFileWidget extends Div {
	protected Log logger = LogFactory.getLog(getClass());

	private boolean flashUpload;// 是否使用Flash上传附件的标记
	private String label = "本地上传";// 按钮显示的文字
	private List<String> extensions;// 上传文件的扩展名限制

	public boolean isFlashUpload() {
		return flashUpload;
	}

	public void setFlashUpload(boolean flashUpload) {
		this.flashUpload = flashUpload;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(List<String> extensions) {
		this.extensions = extensions;
	}

	public UploadFileWidget() {
		// 默认的容器样式
		this.addClazz("attachs");
	}

	@Override
	public StringBuffer render(StringBuffer main) {
		return super.render(main);
	}
}
