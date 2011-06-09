package cn.bc.web.ui.html.grid;

import java.util.ArrayList;
import java.util.List;

import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Div;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Table;
import cn.bc.web.ui.html.Td;
import cn.bc.web.ui.html.Text;
import cn.bc.web.ui.html.Tr;

/**
 * 表格列头部分的html组件
 * 
 * @author dragon
 * 
 */
public class GridHeader extends Div {
	private List<Column> columns = new ArrayList<Column>();
	private String toggleSelectTitle;// 全选反选的提示信息

	public GridHeader() {
		this.addClazz("ui-state-default header");
	}

	public List<Column> getColumns() {
		return columns;
	}

	public GridHeader setColumns(List<Column> columns) {
		this.columns = columns;
		return this;
	}

	public String getToggleSelectTitle() {
		return toggleSelectTitle;
	}

	public GridHeader setToggleSelectTitle(String toggleSelectTitle) {
		this.toggleSelectTitle = toggleSelectTitle;
		return this;
	}

	public StringBuffer render(StringBuffer main) {
		buildheader();

		return super.render(main);
	}

	protected void buildheader() {
		Component left = new Div().addClazz("left");
		Component right = new Div().addClazz("right");
		this.addChild(left).addChild(right);

		// 左table
		Component leftTable = new Table().addClazz("table")
				.setAttr("cellspacing", "0").setAttr("cellpadding", "0");
		left.addChild(leftTable);
		leftTable
				.addChild(new Tr()
						.addClazz("ui-state-default row")
						.addChild(
								new Td().addClazz("id")
										.setTitle(this.getToggleSelectTitle())
										.addChild(
												new Span()
														.addClazz("ui-icon ui-icon-notice"))));

		// 右table
		Component rightTable = new Table().addClazz("table")
				.setAttr("cellspacing", "0").setAttr("cellpadding", "0");
		right.addChild(rightTable);
		Component tr = new Tr().addClazz("ui-state-default row");
		rightTable.addChild(tr);
		Component td;
		Column column;
		// table的总宽度
		int totalWidth = Grid.getDataTableWidth(this.columns);
		rightTable.addStyle("width", totalWidth + "px");

		// 循环添加其余列（第一列为id列忽略）
		for (int i = 1; i < columns.size(); i++) {
			column = columns.get(i);
			td = new Td();
			tr.addChild(td);
			td.setAttr("data-id", column.getId());
			td.setAttr("data-label", column.getLabel());

			if (i == 1) {
				td.addClazz("first");// 首列样式
			} else if (i == columns.size() - 1) {
				td.addClazz("last");// 最后列样式
			} else {
				td.addClazz("middle");// 中间列样式
			}

			if (column.getWidth() > 0) {
				td.addStyle("width", column.getWidth() + "px");
			}
			Component wrapper = new Div().addClazz("wrapper");// td内容的包装容器：相对定位的元素
			td.addChild(wrapper);
			wrapper.addChild(new Text(column.getLabel()));

			// 排序标记
			if (column.isSortable()) {
				td.addClazz("sortable");
				switch (column.getDir()) {
				case Asc:
					td.addClazz("current");// 标记当前列处于排序状态
					wrapper.addChild(new Span()
							.addClazz("sortableIcon ui-icon ui-icon-triangle-1-n"));// 正序
					break;
				case Desc:
					td.addClazz("current");// 标记当前列处于排序状态
					wrapper.addChild(new Span()
							.addClazz("sortableIcon ui-icon ui-icon-triangle-1-s"));// 逆序
					break;
				default:
					wrapper.addChild(new Span()
							.addClazz("sortableIcon ui-icon hide"));
					break;
				}
			}
		}

		// 添加一列空列
		//tr.addChild(Grid.EMPTY_TD);
	}
}
