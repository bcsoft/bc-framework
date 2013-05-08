package cn.bc.subscribe.event;

import org.springframework.context.ApplicationEvent;

import cn.bc.docs.domain.Attach;

/**
 * 用户订阅事件类
 * 
 * @author lbj
 * 
 */
public class SubscribeEvent extends ApplicationEvent {
	private static final long serialVersionUID = 4588278294816284230L;

	private final String code;// 事件编码
	private final String subject;// 主题
	private final String content;// 详细内容
	private final Attach attach;// 附件，监听者只准复制内容 不能直接引用内容

	public SubscribeEvent(Object source, String code, String subject,
			String content,Attach attach) {
		super(source);
		this.code = code;
		this.subject = subject;
		this.content = content;
		this.attach = attach;
	}

	public String getCode() {
		return code;
	}

	public String getContent() {
		return content;
	}

	public String getSubject() {
		return subject;
	}

	/**
	 * 附件，监听者复制内容 
	 * 		建议：不直接引用内容，直接引用可能出现风险
	 * @return
	 */
	public Attach getAttach() {
		return attach;
	}
	
	

}
