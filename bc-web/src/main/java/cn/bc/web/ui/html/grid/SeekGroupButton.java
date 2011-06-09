package cn.bc.web.ui.html.grid;

import cn.bc.web.ui.html.A;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Text;

/**
 * Grid底部工具条的分页按钮
 * 
 * @author dragon
 * 
 */
public class SeekGroupButton extends Button {
	private int pageNo;
	private int pageCount;

	public String getTag() {
		return "li";
	}

	protected void init() {
		this.addClazz("pagerIconGroup seek ui-state-default ui-corner-all");
	}

	public SeekGroupButton() {
		super();
	}

	public int getPageNo() {
		return pageNo;
	}

	public SeekGroupButton setPageNo(int pageNo) {
		this.pageNo = pageNo;
		return this;
	}

	public int getPageCount() {
		return pageCount;
	}

	public SeekGroupButton setPageCount(int pageCount) {
		this.pageCount = pageCount;
		return this;
	}

	public void renderButton(StringBuffer main) {
		this.addChild(new A()
				.setId("toFirstPage")
				.addClazz("pagerIcon ui-state-default ui-corner-all")
				.addChild(
						new Span().addClazz("ui-icon ui-icon-seek-first")
								.setTitle("首页")));

		this.addChild(new A()
				.setId("toPrevPage")
				.addClazz("pagerIcon ui-state-default ui-corner-all")
				.addChild(
						new Span().addClazz("ui-icon ui-icon-seek-prev")
								.setTitle("上一页")));

		this.addChild(new Span()
				.addClazz("pageNo")
				.setTitle("点击选择页码")
				.addChild(
						new Span().setId("pageNo").addChild(
								new Text(this.pageNo + "")))
				.addChild(new Text("/"))
				.addChild(
						new Span().setId("pageCount").addChild(
								new Text(this.pageCount + ""))));

		this.addChild(new A()
				.setId("toNextPage")
				.addClazz("pagerIcon ui-state-default ui-corner-all")
				.addChild(
						new Span().addClazz("ui-icon ui-icon-seek-next")
								.setTitle("下一页")));

		this.addChild(new A()
				.setId("toLastPage")
				.addClazz("pagerIcon ui-state-default ui-corner-all")
				.addChild(
						new Span().addClazz("ui-icon ui-icon-seek-end")
								.setTitle("尾页")));
	}
}
