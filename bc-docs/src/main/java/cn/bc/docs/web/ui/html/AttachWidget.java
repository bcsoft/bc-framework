package cn.bc.docs.web.ui.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import cn.bc.docs.domain.Attach;
import cn.bc.docs.web.AttachUtils;
import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.A;
import cn.bc.web.ui.html.Div;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Table;
import cn.bc.web.ui.html.Td;
import cn.bc.web.ui.html.Text;
import cn.bc.web.ui.html.Tr;

/**
 * 附件UI组件
 * 
 * @author dragon
 * 
 */
public class AttachWidget extends Div {
	protected Log logger = LogFactory.getLog(getClass());

	private boolean readOnly;// 只读还是编辑状态
	private boolean flashUpload;// 是否使用Flash上传附件的标记
	private String summary = "&nbsp;个附件共&nbsp;";// 附件汇总信息的不变部分的字符串
	private List<String> extensions;// 上传文件的扩展名限制
	private List<Attach> attachs;// 包含的附件

	/**
	 * 附件的总操作按钮
	 * <p>
	 * 如添加附件uploadFile、打包下载downloadAll、全部删除deleteAll，用逗号连接多个；
	 * 如果要添加自定义的操作按钮，使用addHeadButton方法。
	 * </p>
	 */
	private List<Component> headButtons;

	/**
	 * 各个附件的分操作按钮
	 * <p>
	 * 如在线查看inline、下载download、删除delete，用逗号连接多个；
	 * 如果要添加自定义的操作按钮，使用addAttachButton方法。
	 * </p>
	 */
	private List<Component> attachButtons;

	public AttachWidget() {
		// 默认的容器样式
		this.addClazz("attachs");
	}

	@Override
	public StringBuffer render(StringBuffer main) {
		// 将extensions转化为属性
		if (extensions != null && !extensions.isEmpty()) {
			setAttr("data-extensions",
					StringUtils.collectionToCommaDelimitedString(extensions));
		}

		if (this.isFlashUpload())
			this.addClazz("flashUpload");

		if (this.getChildren() != null)
			this.getChildren().clear();

		// 构建head的ui
		this.addChild(buildHeadUI());

		// 构建附件列表的ui
		if (this.attachs != null) {
			for (Attach attach : this.attachs) {
				this.addChild(buildAttachUI(attach));
			}
		}

		// render it
		return super.render(main);
	}

	protected Component buildHeadUI() {
		Table headUI = new Table();
		headUI.addClazz("header").setAttr("cellpadding", "0")
				.setAttr("cellspacing", "0");
		Tr tr = new Tr();
		headUI.addChild(tr);

		// 汇总信息
		Td td = new Td();
		tr.addChild(td);
		td.addClazz("summary");
		int totalCount = this.attachs != null ? this.attachs.size() : 0;
		td.addChild(new Span().setId("totalCount").addChild(
				new Text(String.valueOf(totalCount))));
		td.addChild(new Text(this.getSummary()));
		long totalSize = 0;
		if (this.attachs != null) {
			for (Attach a : attachs)
				totalSize += a.getSize();
		}
		td.addChild(new Span().setId("totalSize")
				.setAttr("data-size", String.valueOf(totalSize))
				.addChild(new Text(AttachUtils.getSizeInfo(totalSize))));

		// 添加附件按钮
		if (!this.isReadOnly()) {
			tr.addChild(defaultHeadButton4UploadFile(null, this.isFlashUpload()));
		}

		// 如果没有自定义设置，创建默认的操作按钮
		if (this.headButtons == null || this.headButtons.isEmpty()) {
			this.addHeadButton(defaultHeadButton4DownloadAll(null));
			if (!this.isReadOnly())
				this.addHeadButton(defaultHeadButton4DeleteAll(null));
		}

		// 将额外的按钮添加到td
		td = new Td();
		tr.addChild(td);
		for (Component button : this.headButtons) {
			td.addChild(button);
		}

		return headUI;
	}

	protected Component buildAttachUI(Attach attach) {
		Table attachUI = new Table();
		attachUI.addClazz("attach").setAttr("cellpadding", "0")
				.setAttr("cellspacing", "0")
				.setAttr("data-size", String.valueOf(attach.getSize()))// 大小
				.setAttr("data-id", attach.getId().toString())// id
				.setAttr("data-name", attach.getSubject())// 文件名
				.setAttr("data-count", String.valueOf(attach.getCount()))// 下载次数
				.setAttr("data-url", attach.getPath())// 路径
				.setAttr("data-isAppPath", String.valueOf(attach.isAppPath()));

		// 汇总信息
		Tr tr = new Tr();
		attachUI.addChild(tr);
		tr.addChild(new Text("<td class=\"icon\"><span class=\"file-icon "
				+ attach.getExtension() + "\"></span></td>"));
		Td td = new Td();
		tr.addChild(td);
		td.addClazz("info").addChild(
				new Text("<div class=\"subject\">" + attach.getSubject()
						+ "</div>"));// 文件名称
		Table table = new Table();
		td.addChild(table);
		table.setAttr("cellpadding", "0").setAttr("cellspacing", "0");
		Td operations = new Td();
		table.addChild(new Tr().addChild(
				new Td().addClazz("size").addChild(
						new Text(AttachUtils.getSizeInfo(attach.getSize()))))
				.addChild(operations));
		operations.addClazz("operations");
		operations.addChild(defaultAttachButton4Inline(null));// 在线查看按钮
		operations.addChild(defaultAttachButton4Download(null));// 下载按钮
		if (!this.isReadOnly())
			operations.addChild(defaultAttachButton4Delete(null));// 删除按钮

		return attachUI;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public boolean isFlashUpload() {
		return flashUpload;
	}

	public void setFlashUpload(boolean flashUpload) {
		this.flashUpload = flashUpload;
	}

	/**
	 * 所属文档类型
	 * 
	 * @return
	 */
	public String getPtype() {
		return getAttr("data-ptype");
	}

	/**
	 * 所属文档类型
	 * 
	 * @return
	 */
	public AttachWidget setPtype(String ptype) {
		setAttr("data-ptype", ptype);
		return this;
	}

	/**
	 * 所属文档uid
	 * 
	 * @return
	 */
	public String getPuid() {
		return getAttr("data-puid");
	}

	/**
	 * 所属文档uid
	 * 
	 * @return
	 */
	public AttachWidget setPuid(String puid) {
		setAttr("data-puid", puid);
		return this;
	}

	/**
	 * 最大上传附件数，<=0为无限制
	 * 
	 * @return
	 */
	public String getMaxCount() {
		return getAttr("data-maxCount");
	}

	/**
	 * 最大上传附件数，<=0为无限制
	 * 
	 * @return
	 */
	public AttachWidget setMaxCount(int maxCount) {
		if (maxCount > 0)
			setAttr("data-maxCount", String.valueOf(maxCount));
		return this;
	}

	/**
	 * 总上传大小限制，<=0为无限制
	 * 
	 * @return
	 */
	public String getMaxSize() {
		return getAttr("data-maxSize");
	}

	/**
	 * 总上传大小限制，<=0为无限制
	 * 
	 * @return
	 */
	public AttachWidget setMaxSize(int maxSize) {
		if (maxSize > 0)
			setAttr("data-maxSize", String.valueOf(maxSize));
		return this;
	}

	/**
	 * 添加扩展名限制
	 * <p>
	 * 可用逗号连接多个扩展名一次添加多个
	 * </p>
	 * 
	 * @param extension
	 * @return
	 */
	public AttachWidget addExtension(String extension) {
		if (extensions == null)
			extensions = new ArrayList<String>();
		if (extension != null && extension.length() > 0) {
			if (extension.indexOf(",") != -1) {
				for (String e : extension.split(","))
					extensions.add(e);
			} else {
				extensions.add(extension);
			}
		}
		return this;
	}

	/**
	 * 添加一个附件
	 * 
	 * @param attach
	 * @return
	 */
	public AttachWidget addAttach(Attach attach) {
		if (attachs == null)
			attachs = new ArrayList<Attach>();
		if (attach != null) {
			attachs.add(attach);
		}
		return this;
	}

	/**
	 * 添加一批附件
	 * 
	 * @param attachs
	 * @return
	 */
	public AttachWidget addAttach(Collection<Attach> attachs) {
		if (this.attachs == null)
			this.attachs = new ArrayList<Attach>();
		if (attachs != null && !attachs.isEmpty()) {
			this.attachs.addAll(attachs);
		}
		return this;
	}

	/**
	 * 添加一个附件的总操作按钮
	 * 
	 * @param extension
	 * @return
	 */
	public AttachWidget addHeadButton(Component button) {
		if (headButtons == null)
			headButtons = new ArrayList<Component>();
		if (button != null) {
			headButtons.add(button);
		}
		return this;
	}

	/**
	 * 添加一个附件的分操作按钮
	 * 
	 * @param extension
	 * @return
	 */
	public AttachWidget addAttachButton(Component button) {
		if (attachButtons == null)
			attachButtons = new ArrayList<Component>();
		if (button != null) {
			attachButtons.add(button);
		}
		return this;
	}

	// --以下为默认的按钮构建方法

	/**
	 * 默认的"添加附件"按钮
	 * 
	 * @return
	 */
	public static Component defaultHeadButton4UploadFile(String label,
			boolean flashUpload) {
		Component c = new Td();
		c.addClazz("uploadFile");
		if (!flashUpload)
			c.addChild(new Text((label == null || label.length() == 0) ? "添加附件"
					: label));
		String id = new Date().getTime() + "";
		c.addChild(new Text(
				"<input type=\"file\" class=\"uploadFile\" id=\"fid" + id
						+ "\" name=\"uploadFile\" multiple/>"));
		return c;
	}

	public static Component createButton(String label, String action,
			String click, String callback) {
		return createButton(label, action, click, callback, null);
	}

	/**
	 * 创建一个附件的操作按钮
	 * 
	 * @param label
	 * @param action
	 * @param click
	 * @param callback
	 * @return
	 */
	public static Component createButton(String label, String action,
			String click, String callback, String title) {
		Component c = new A();
		c.addClazz("operation").setAttr("data-action", action)
				.setAttr("data-click", click)
				.setAttr("data-callback", callback);
		if (label == null)
			label = "label";
		c.addChild(new Text(label));
		if (title != null)
			c.setTitle(title);
		return c;
	}

	/**
	 * 默认的"打包下载"按钮
	 * 
	 * @return
	 */
	public static Component defaultHeadButton4DownloadAll(String label) {
		return createButton(label == null ? "打包下载" : label, "downloadAll",
				null, null);
	}

	/**
	 * 默认的"全部删除"按钮
	 * 
	 * @return
	 */
	public static Component defaultHeadButton4DeleteAll(String label) {
		return createButton(label == null ? "全部删除" : label, "deleteAll", null,
				null);
	}

	/**
	 * 默认的"在线查看"按钮
	 * 
	 * @return
	 */
	public static Component defaultAttachButton4Inline(String label) {
		return createButton(label == null ? "在线查看" : label, "inline", null,
				null,
				"在线查看需要浏览器的支持，如果是xls、xlsx、doc、docx、ppt、pptx等Office文档，会自动转换为pdf文件查看。");
	}

	/**
	 * 默认的"下载"按钮
	 * 
	 * @return
	 */
	public static Component defaultAttachButton4Download(String label) {
		return createButton(label == null ? "下载" : label, "download", null,
				null);
	}

	/**
	 * 默认的"删除"按钮
	 * 
	 * @return
	 */
	public static Component defaultAttachButton4Delete(String label) {
		return createButton(label == null ? "删除" : label, "delete", null, null);
	}
}
