package cn.bc.web.struts2.event;

import org.springframework.context.ApplicationEvent;

/**
 * 用户导出视图数据的事件
 *
 * @author dragon
 */
public class ExportViewDataEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;
  private String subject;// 标题
  private String pid;// 文档标识，通常使用文档的id、uid或批号
  private String ptype;// 所属模块：如User、Role，一般为类名
  private String fileType;// 类型：一般为文件的扩展名
  private byte[] data;// 导出的数据

  /**
   * @param source   事件源
   * @param subject  标题
   * @param ptype    所属模块，不能为空，如User、Role，一般为类名
   * @param pid      文档标识号
   * @param fileType 导出的文件类型
   * @param data     导出的数据
   */
  public ExportViewDataEvent(Object source, String ptype, String pid,
                             String subject, String fileType, byte[] data) {
    super(source);
    this.subject = subject;
    this.ptype = ptype;
    this.pid = pid;
    this.fileType = fileType;
    this.data = data;
  }

  public String getSubject() {
    return subject;
  }

  public String getPid() {
    return pid;
  }

  public String getPtype() {
    return ptype;
  }

  public String getFileType() {
    return fileType;
  }

  public byte[] getData() {
    return data;
  }
}
