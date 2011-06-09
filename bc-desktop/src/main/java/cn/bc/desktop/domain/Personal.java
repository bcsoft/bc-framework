/**
 * 
 */
package cn.bc.desktop.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.DefaultEntity;
import cn.bc.identity.domain.Actor;

/**
 * 个性化设置
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_DESKTOP_PERSONAL")
public class Personal extends DefaultEntity {
	private static final long serialVersionUID = 1L;

	private String font;// 字体大小
	private String theme;// 主题名称
	private Actor actor;// 所属参与者

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

	@ManyToOne(targetEntity = Actor.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "AID", nullable = true, updatable = false)
	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}
}
