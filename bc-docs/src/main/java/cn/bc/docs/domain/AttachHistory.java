/**
 *
 */
package cn.bc.docs.domain;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.ActorHistory;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 附件处理的痕迹记录
 * <p>
 * 记录谁什么时候成功查看或下载了附件
 * </p>
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_DOCS_ATTACH_HISTORY")
public class AttachHistory extends EntityImpl {
  private static final long serialVersionUID = 1L;
  /**
   * 操作类型：下载
   */
  public static final int TYPE_DOWNLOAD = 0;
  /**
   * 操作类型：在线查看
   */
  public static final int TYPE_INLINE = 1;
  /**
   * 操作类型：打包下载
   */
  public static final int TYPE_ZIP = 2;
  /**
   * 操作类型：格式转换
   */
  public static final int TYPE_CONVERT = 3;
  /**
   * 操作类型：删除
   */
  public static final int TYPE_DELETED = 4;
  /**
   * 操作类型：上传
   */
  public static final int TYPE_UPLOAD = 5;

  private int type;// 操作类型,详见TYPE_常数
  private String ptype;// 所关联文档的分类,如果是Attach的操作记录，值为"Attach"
  private String puid;// 所关联文档的UID,如果是Attach的操作记录，值为attach的id值
  private Calendar fileDate;// 创建时间
  private ActorHistory author;// 创建人
  private String subject;// 标题
  private String format;// 下载的文件格式或转换后的文件格式
  private String memo;// 备注
  private String clientIp; // 用户机器的IP地址
  private String clientInfo; // 用户浏览器的信息：User-Agent
  private String path;// 物理文件保存的相对路径（相对于全局配置的app.data.realPath或app.data.subPath目录下的子路径，如"2011/bulletin/xxxx.doc"）
  /**
   * path的值是相对于app.data.realPath目录下的路径还是相对于app.data.subPath目录下的路径：
   * false：相对于app.data.realPath目录下的路径， true：相对于app.data.subPath目录下的路径
   */
  private boolean appPath;

  @Column(name = "FILE_DATE")
  public Calendar getFileDate() {
    return fileDate;
  }

  public void setFileDate(Calendar fileDate) {
    this.fileDate = fileDate;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID")
  public ActorHistory getAuthor() {
    return author;
  }

  public void setAuthor(ActorHistory author) {
    this.author = author;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  @Column(name = "TYPE_")
  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "C_IP")
  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  @Column(name = "C_INFO")
  public String getClientInfo() {
    return clientInfo;
  }

  public void setClientInfo(String clientBrowser) {
    this.clientInfo = clientBrowser;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public boolean isAppPath() {
    return appPath;
  }

  public void setAppPath(boolean appPath) {
    this.appPath = appPath;
  }

  public String getPtype() {
    return ptype;
  }

  public void setPtype(String ptype) {
    this.ptype = ptype;
  }

  public String getPuid() {
    return puid;
  }

  public void setPuid(String euid) {
    this.puid = euid;
  }
}