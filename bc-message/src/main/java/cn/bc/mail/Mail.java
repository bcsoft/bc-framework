package cn.bc.mail;


/**
 * 邮件信息封装
 *
 * @author dragon
 */
public class Mail {
  private boolean html;// 是否是纯文本内容的邮件
  private String subject;// 标题
  private String content;// 内容
  private String[] to;// 主送
  private String[] cc;// 抄送
  private String[] bcc;// 密送

  public boolean isHtml() {
    return html;
  }

  public void setHtml(boolean html) {
    this.html = html;
  }

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

  public void addTo(String[] to) {
    if (to == null || to.length == 0)
      return;

    if (this.to != null) {
      String[] newTo = new String[this.to.length + to.length];
      for (int i = 0; i < this.to.length; i++) {
        newTo[i] = this.to[i];
      }
      for (int i = 0; i < to.length; i++) {
        newTo[this.to.length + i] = to[i];
      }
      this.to = newTo;
    } else {
      this.to = to;
    }
  }
}
