/**
 * 
 */
package cn.bc.desktop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.core.RichEntityImpl;

/**
 * 个性化设置
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_DESKTOP_PERSONAL")
public class Personal extends RichEntityImpl {
	private static final long serialVersionUID = 1L;

	private String font;// 字体大小
	private String theme;// 主题名称
	private Long actorId;// 所属参与者
	private boolean inner = false;// 是否为内置对象，内置对象不允许删除

	@Column(name = "INNER_")
	public boolean isInner() {
		return inner;
	}

	public void setInner(boolean inner) {
		this.inner = inner;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	@Column(name = "AID")
	public Long getActorId() {
		return actorId;
	}

	public void setActorId(Long actorId) {
		this.actorId = actorId;
	}
}
