package cn.bc.web.ui.html.grid;

/**
 * 文本列
 * 
 * @author dragon
 * 
 */
public class TextColumn4MapKey extends AbstractColumn {
	public TextColumn4MapKey(String id, String label) {
		this.setId(id);
		this.setValueExpression(wrap4ve(id));//默认表达式与id相同
		this.setLabel(label);
	}

	private String wrap4ve(String field) {
		return "['" + field + "']";
	}

	public TextColumn4MapKey(String id, String label, int width) {
		this.setId(id);
		this.setValueExpression(wrap4ve(id));//默认表达式与id相同
		this.setLabel(label);
		this.setWidth(width);
	}
}
