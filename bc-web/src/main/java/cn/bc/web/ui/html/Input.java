package cn.bc.web.ui.html;

import cn.bc.web.ui.AbstractComponent;

/**
 * input
 * 
 * @author dragon
 * 
 */
public class Input extends AbstractComponent {
	public String getType() {
		return getAttr("type");
	}
	public Input setType(String type) {
		setAttr("type", type);
		return this;
	}
	
	public String getTag() {
		return "input";
	}
}
