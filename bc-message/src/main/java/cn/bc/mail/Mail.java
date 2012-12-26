package cn.bc.mail;

/**
 * 邮件信息封装
 * 
 * @author dragon
 * 
 */
public class Mail {
	private String subject;// 标题
	private String content;// 内容
	private String[] to;// 主送
	private String[] cc;// 抄送
	private String[] bcc;// 密送

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public String[] getBcc() {
		return bcc;
	}

	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}
}
