package cn.bc.web.ui.html.grid;

/**
 * 文本列
 * 
 * @author dragon
 * 
 */
public class TextColumn extends AbstractColumn {
	public TextColumn(String id, String label) {
		this.setId(id);
		this.setExpression(id);//默认表达式与id相同
		this.setLabel(label);
	}

	public TextColumn(String id, String label, int width) {
		this.setId(id);
		this.setExpression(id);//默认表达式与id相同
		this.setLabel(label);
		this.setWidth(width);
	}
}
