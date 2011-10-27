package cn.bc.web.ui.html.toolbar;

import java.util.Map;
import java.util.Map.Entry;

import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.Div;

/**
 * 工具条
 * 
 * @author dragon
 * 
 */
public class Toolbar extends Div {
	protected void init() {
		this.addClazz("bc-toolbar ui-widget-content");
	}

	public String getTag() {
		return "div";
	}

	/** 添加按钮 */
	public Toolbar addButton(Component button) {
		this.addChild(button);
		return this;
	}

	/** 默认的按钮分隔符 */
	public static Button getDefaultEmptyToolbarButton() {
		return new ToolbarEmptyButton();
	}

	/** 默认的新建按钮 */
	public static Button getDefaultCreateToolbarButton(String text) {
		return new ToolbarButton().setIcon("ui-icon-document").setText(text)
				.setAction("create");
	}

	/** 默认的查看按钮 */
	public static Button getDefaultOpenToolbarButton(String text) {
		return new ToolbarButton().setIcon("ui-icon-check").setText(text)
				.setAction("open");
	}

	/** 默认的编辑按钮 */
	public static Button getDefaultEditToolbarButton(String text) {
		return new ToolbarButton().setIcon("ui-icon-pencil").setText(text)
				.setAction("edit");
	}

	/** 默认的删除按钮 */
	public static Button getDefaultDeleteToolbarButton(String text) {
		return new ToolbarButton().setIcon("ui-icon-trash").setText(text)
				.setAction("delete");
	}

	/** 默认的搜索按钮 */
	public static Button getDefaultSearchToolbarButton(String text) {
		ToolbarSearchButton sb = new ToolbarSearchButton();
		sb.setAction("search").setTitle(text);
		return sb;
	}

	/** 默认的单选按钮组 */
	public static Button getDefaultToolbarRadioGroup(
			Map<String, String> labelValues, String key, int activeIndex, String tip) {
		ToolbarRadioGroup rg = new ToolbarRadioGroup();
		rg.setActive(activeIndex).setKey(key).setAction("reloadGrid").setTitle(tip);
		for (Entry<String, String> e : labelValues.entrySet()) {
			rg.addRadio(e.getValue(), e.getKey());
		}
		return rg;
	}
}
