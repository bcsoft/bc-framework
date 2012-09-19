package cn.bc.web.ui.html.grid;

import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Ul;

/**
 * Grid 底部的工具条
 * 
 * @author dragon
 * 
 */
public class GridFooter extends Ul {
	protected void init() {
		this.addClazz("pager ui-widget-content ui-widget ui-helper-clearfix");
	}

	public GridFooter addButton(Component button) {
		this.addChild(button);
		return this;
	}

	/** 刷新按钮 */
	public static FooterButton getDefaultRefreshButton(String text) {
		FooterButton fb = new FooterButton();
		fb.setIcon("ui-icon-refresh").setAction("refresh").setTitle(text);
		return fb;
	}

	/** 排序方式切换按钮 */
	public static FooterButton getDefaultSortButton(boolean remoteSort,
			String click2remoteSortText, String click2localSortText) {
		FooterButton fb = new FooterButton();
		fb.setIcon("ui-icon-transferthick-e-w");
		fb.setAction("changeSortType");
		fb.setAttr("title4clickToRemoteSort", click2remoteSortText);
		fb.setAttr("title4clickToLocalSort", click2localSortText);
		if (remoteSort) {// 远程排序
			fb.setTitle(click2localSortText).addClazz("ui-state-active");
		} else {// 本地排序
			fb.setTitle(click2remoteSortText);
		}
		return fb;
	}

	/** 导出按钮 */
	public static FooterButton getDefaultExportButton(String text) {
		FooterButton fb = new FooterButton();
		fb.setIcon("ui-icon-arrowthickstop-1-s").setAction("export")
				.setTitle(text);
		return fb;
	}

	/** 导入按钮 */
	public static FooterButton getDefaultImportButton(String text) {
		FooterButton fb = new FooterButton();
		fb.setIcon("ui-icon-arrowthickstop-1-n").setAction("import")
				.setTitle(text);
		return fb;
	}

	/** 打印按钮 */
	public static FooterButton getDefaultPrintButton(String text) {
		FooterButton fb = new FooterButton();
		fb.setIcon("ui-icon-print").setAction("print").setTitle(text);
		return fb;
	}
}
