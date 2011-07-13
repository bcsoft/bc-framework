/**
 * 
 */
package cn.bc.docs.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

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
public class AttachHistory extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	/** 操作类型：下载 */
	public static final int TYPE_DOWNLOAD = 0;
	/** 操作类型：在线查看 */
	public static final int TYPE_INLINE = 1;
	/** 操作类型：打包下载 */
	public static final int TYPE_ZIP = 2;
	/** 操作类型：格式转换 */
	public static final int TYPE_CONVERT = 3;
	/** 操作类型：删除 */
	public static final int TYPE_DELETED = 4;

	private int type;// 操作类型,详见TYPE_常数
	private Attach attach;// 对应的附件
	private String format;// 下载的文件格式或转换后的文件格式
	private String memo;// 备注
	private String clientIp; // 用户机器的IP地址
	private String clientInfo; // 用户浏览器的信息：User-Agent
	private String subject;// 标题

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

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "AID", referencedColumnName = "ID")
	public Attach getAttach() {
		return attach;
	}

	public void setAttach(Attach attach) {
		this.attach = attach;
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
}