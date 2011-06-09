package cn.bc.web.ui.html.grid;

/**
 * id列
 * 
 * @author dragon
 * 
 */
public class IdColumn extends AbstractColumn {
	private boolean canCheckedAll;
	private String nameExpression;
	
	public IdColumn(){
		this.setWidth(0);//覆盖默认的配置，设为0相当于不设置宽度
	}

	/**
	 * @param canCheckedAll
	 *            是否含全选和反选功能
	 */
	public IdColumn(boolean canCheckedAll) {
		this();
		this.canCheckedAll = canCheckedAll;
		this.setId("id");
		this.setExpression("id");
	}
	public IdColumn(boolean canCheckedAll, String nameExpression) {
		this(canCheckedAll);
		this.nameExpression = nameExpression;
	}

	public boolean isCanCheckedAll() {
		return canCheckedAll;
	}

	public IdColumn setCanCheckedAll(boolean canCheckedAll) {
		this.canCheckedAll = canCheckedAll;
		return this;
	}

	public String getNameExpression() {
		return nameExpression;
	}

	public IdColumn setNameExpression(String nameExpression) {
		this.nameExpression = nameExpression;
		return this;
	}

	public static IdColumn DEFAULT() {
		return new IdColumn(true,"name");
	}
}
