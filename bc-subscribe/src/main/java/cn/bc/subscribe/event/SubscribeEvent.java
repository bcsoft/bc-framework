package cn.bc.subscribe.event;

import org.springframework.context.ApplicationEvent;

/**
 * 用户订阅事件类
 * 
 * @author lbj
 * 
 */
public class SubscribeEvent extends ApplicationEvent {
	private static final long serialVersionUID = 4588278294816284230L;

	private final String code;//事件编码
	private final Object content;//内容

	public SubscribeEvent(Object source,String code,Object content) {
		super(source);
		this.code=code;
		this.content=content;
	}

	public String getCode() {
		return code;
	}

	public Object getContent() {
		return content;
	}

	
	
}
