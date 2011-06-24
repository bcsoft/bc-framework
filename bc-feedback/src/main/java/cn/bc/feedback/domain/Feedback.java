/**
 * 
 */
package cn.bc.feedback.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntity;

/**
 * 电子公告
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_FEEDBACK")
public class Feedback extends FileEntity {
	private static final long serialVersionUID = 1L;
	/** 状态：草稿或待提交 */
	public static final int STATUS_DRAFT = 0;
	/** 状态：已提交 */
	public static final int STATUS_SUMMIT = 1;
	/** 状态：已受理 */
	public static final int STATUS_ACCEPT = 2;
	/** 状态：被驳回 */
	public static final int STATUS_REJECT = 3;
	/** 状态：已删除 */
	public static final int STATUS_DELETED = 4;//普通用户的删除操作将只是标记删除

	private String content;// 详细内容

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}