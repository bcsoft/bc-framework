package cn.bc.web.ui.html.grid;

import java.util.ArrayList;
import java.util.List;

import cn.bc.web.ui.Render;
import cn.bc.web.ui.html.Div;
import cn.bc.web.ui.html.Text;

public class Grid extends Div {
	/** 空的占位单元格 */
	public static Render EMPTY_TD = new Text("<td class=\"empty\">&nbsp;</td>");

	private boolean singleSelect;// 行是否单选
	private String dblClickRow;// 双击行的处理事件
	private List<Column> columns = new ArrayList<Column>();
	private GridFooter footer;// 底部的工具条
	private GridData gridData;
	private GridHeader gridHeader;

	public List<Column> getColumns() {
		return columns;
	}

	public Grid setColumns(List<Column> columns) {
		this.columns = columns;
		return this;
	}

	public Grid() {
		this.addClazz("bc-grid");
	}

	public GridHeader getGridHeader() {
		return gridHeader;
	}

	public Grid setGridHeader(GridHeader gridHeader) {
		this.gridHeader = gridHeader;
		return this;
	}

	public GridData getGridData() {
		return gridData;
	}

	public Grid setGridData(GridData gridData) {
		this.gridData = gridData;
		return this;
	}

	public boolean isSingleSelect() {
		return singleSelect;
	}

	public Grid setSingleSelect(boolean singleSelect) {
		this.singleSelect = singleSelect;
		return this;
	}

	public GridFooter getFooter() {
		return footer;
	}

	public Grid setFooter(GridFooter footer) {
		this.footer = footer;
		return this;
	}

	public String getDblClickRow() {
		return dblClickRow;
	}

	public Grid setDblClickRow(String dblClickRow) {
		this.dblClickRow = dblClickRow;
		return this;
	}

	public StringBuffer render(StringBuffer main) {
		// 特殊属性处理
		if (isSingleSelect())
			this.addClazz("singleSelect");
		else
			this.addClazz("multipleSelect");

		// 双击表格行的事件
		if (dblClickRow != null && dblClickRow.length() > 0)
			this.setAttr("data-dblclickrow", getDblClickRow());

		// 构建表格列头
		if (gridHeader != null)
			this.addChild(this.gridHeader);

		// 构建表格列表数据
		if (gridData != null)
			this.addChild(this.gridData);

		// 构建表格的底部工具条：分页条等按钮
		if (footer != null)
			this.addChild(this.footer);

		return super.render(main);
	}

	/**
	 * 获取表格的id列定义
	 * 
	 * @return
	 */
	public static Column getIDColumn(List<Column> columns) {
		return columns.get(0);
	}

	/**
	 * 计算数据表格的宽度（忽略第一列的id列）
	 * 
	 * @return
	 */
	public static int getDataTableWidth(List<Column> columns) {
		// table的总宽度（20为留给滚动条的余量）
		int totalWidth = 20;
		// 循环添加其余列（忽略第一列的id列）
		for (int i = 1; i < columns.size(); i++) {
			if (columns.get(i).getWidth() > 0) {
				totalWidth += columns.get(i).getWidth();
			}else{
				totalWidth += 150;//默认按100的占位宽度
			}
		}
		return totalWidth;
	}
}
