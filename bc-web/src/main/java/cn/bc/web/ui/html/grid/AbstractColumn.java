package cn.bc.web.ui.html.grid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.core.query.condition.Direction;
import cn.bc.web.formater.Formater;

/**
 * 列的抽象实现
 * 
 * @author dragon
 * 
 */
public abstract class AbstractColumn implements Column {
	protected Log logger = LogFactory.getLog(getClass());
	private int width;
	private String id;
	private String label;
	private String valueExpression;
	private boolean sortable;
	private Direction dir = Direction.None;
	private Formater<String> valueFormater;
	private boolean useTitleFromLabel;
	
	public boolean isUseTitleFromLabel() {
		return useTitleFromLabel;
	}
	public Column setUseTitleFromLabel(boolean useTitleFromLabel) {
		this.useTitleFromLabel = useTitleFromLabel;
		return this;
	}
	public boolean isSortable() {
		return sortable;
	}
	public Column setSortable(boolean sortable) {
		this.sortable = sortable;
		return this;
	}
	public Direction getDir() {
		return dir;
	}
	public Column setDir(Direction dir) {
		this.dir = dir;
		return this;
	}
	public int getWidth() {
		return width;
	}
	public Column setWidth(int width) {
		this.width = width;
		return this;
	}
	public String getId() {
		return id;
	}
	public Column setId(String id) {
		this.id = id;
		return this;
	}
	public String getLabel() {
		return label;
	}
	public Column setLabel(String label) {
		this.label = label;
		return this;
	}
	public String getValueExpression() {
		return valueExpression;
	}
	public Column setValueExpression(String valueExpression) {
		this.valueExpression = valueExpression;
		return this;
	}
	public Formater<String> getValueFormater() {
		return valueFormater;
	}
	public Column setValueFormater(Formater<String> formater) {
		this.valueFormater = formater;
		return this;
	}
}
