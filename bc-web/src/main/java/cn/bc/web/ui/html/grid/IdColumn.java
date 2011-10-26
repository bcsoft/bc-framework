package cn.bc.web.ui.html.grid;

/**
 * id列
 * 
 * @author dragon
 * 
 */
public class IdColumn extends AbstractColumn {
	public static String DEFAULT_ID = "id";
	private boolean canCheckedAll = true;// 是否含全选和反选功能

	public IdColumn() {
		this.setWidth(0);// 覆盖默认的配置，设为0相当于不设置宽度
		this.setId(DEFAULT_ID);
		this.setValueExpression(DEFAULT_ID);
	}

	public boolean isCanCheckedAll() {
		return canCheckedAll;
	}

	/**
	 * 设置id列是否显示全选和反选功能
	 * 
	 * @param canCheckedAll
	 * @return
	 */
	public IdColumn setCanCheckedAll(boolean canCheckedAll) {
		this.canCheckedAll = canCheckedAll;
		return this;
	}
}
