package cn.bc.subscribe.event;

import java.util.List;
import java.util.Map;

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
	private final List<Attach> attachs;// 多个附件，监听者只准复制内容 不能直接引用内容
	private final Map<String,Object> params;

	/**
	 * @param source 
	 * @param code    事件编码
	 * @param subject 主题
	 * @param content 详细内容
	 * @param attachs 多个附件，监听者只准复制内容 不能直接引用内容
	 * @param params  参数
	 */
	public SubscribeEvent(Object source, String code, String subject,
			String content,List<Attach> attachs, Map<String,Object> params) {
		super(source);
		this.code = code;
		this.subject = subject;
		this.content = content;
		this.attachs = attachs;
		this.params = params;
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
	public List<Attach> getAttachs() {
		return attachs;
	}

	public Map<String, Object> getParams() {
		return params;
	}
	
	
	

}
