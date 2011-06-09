package cn.bc.web.ui.html.page;

import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.toolbar.Toolbar;


/**
 * 列表页面
 * 
 * @author dragon
 * 
 */
public class ListPage extends HtmlPage {
	public ListPage() {
		setType("list");
	}
	
	public String getDeleteUrl() {
		return getAttr("data-deleteUrl");
	}
	public ListPage setDeleteUrl(String action) {
		setAttr("data-deleteUrl", action);
		return this;
	}
	public String getEditUrl() {
		return getAttr("data-editUrl");
	}
	public ListPage setEditUrl(String action) {
		setAttr("data-editUrl", action);
		return this;
	}
	public String getCreateUrl() {
		return getAttr("data-createUrl");
	}
	public ListPage setCreateUrl(String action) {
		setAttr("data-createUrl", action);
		return this;
	}

	public ListPage setToolbar(Toolbar toolbar) {
		this.addChild(toolbar);
		return this;
	}

	public ListPage setGrid(Grid grid) {
		this.addChild(grid);
		return this;
	}
}
