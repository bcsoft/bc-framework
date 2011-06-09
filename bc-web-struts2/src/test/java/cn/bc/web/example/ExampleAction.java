/**
 * 
 */
package cn.bc.web.example;

import cn.bc.test.Example;
import cn.bc.web.struts2.StrutsCRUDAction;

/**
 * @author dragon
 * 
 */
public class ExampleAction extends StrutsCRUDAction<Example> {
	private static final long serialVersionUID = 1L;
	private String name;

	@Override
	protected Class<Example> getEntityClass() {
		return Example.class;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String execute() {
		setName(getId() + "|" + getName());
		return SUCCESS;
	}

	// public void validate() {
	// logger.debug("In method validate. accountBean's state is "
	// + accountBean);
	//
	// if (accountBean.length() == 0) {
	// addFieldError("accountBean", "accountBean is required.");
	// }
	// }

}
