/**
 * 
 */
package cn.bc.work.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.core.DefaultEntity;

/**
 * 工作事项信息
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_WORK")
public class Work extends DefaultEntity {
	private static final long serialVersionUID = 1L;

	private String classifier; // 分类词,可多级分类,级间使用/连接,如"发文类/正式发文"
	private String subject;// 标题
	private String content;// 内容
	private Long fromId; // 来源标识
	private Integer fromType; // 来源类型
	private String fromInfo; // 来源描述
	private String openUrl; // 打开的Url模板

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
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

	public Long getFromId() {
		return fromId;
	}

	@Column(name = "FROM_ID")
	public void setFromId(Long fromId) {
		this.fromId = fromId;
	}

	@Column(name = "FROM_TYPE")
	public Integer getFromType() {
		return fromType;
	}

	public void setFromType(Integer fromType) {
		this.fromType = fromType;
	}

	@Column(name = "FROM_INFO")
	public String getFromInfo() {
		return fromInfo;
	}

	public void setFromInfo(String fromInfo) {
		this.fromInfo = fromInfo;
	}

	@Column(name = "OPEN_URL")
	public String getOpenUrl() {
		return openUrl;
	}

	public void setOpenUrl(String openUrl) {
		this.openUrl = openUrl;
	}
}
