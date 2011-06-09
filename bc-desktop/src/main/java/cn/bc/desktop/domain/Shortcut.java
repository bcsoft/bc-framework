/**
 * 
 */
package cn.bc.desktop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.DefaultEntity;
import cn.bc.identity.domain.Actor;
import cn.bc.security.domain.Module;

/**
 * 桌面快捷方式
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_DESKTOP_SHORTCUT")
public class Shortcut extends DefaultEntity {
	private static final long serialVersionUID = 1L;
	
	private boolean standalone;//是否在独立的浏览器窗口中打开
	private String order;//排序号
	private String name;//名称,为空则使用模块的名称
	private String url;//地址,为空则使用模块的地址
	private Module module;//对应的模块
	private Actor actor;//所属的参与者(如果为上级参与者,如单位部门,则其下的所有参与者都拥有该快捷方式)
	private String iconClass;//图标样式
	
	public String getIconClass() {
		return iconClass;
	}
	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}
	public boolean isStandalone() {
		return standalone;
	}
	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}
	@Column(name = "ORDER_")
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="MID", nullable=true, updatable=false)
	public Module getModule() {
		return module;
	}
	public void setModule(Module module) {
		this.module = module;
	}
	
	@ManyToOne(targetEntity=Actor.class,fetch=FetchType.LAZY)
	@JoinColumn(name="AID", nullable=true, updatable=false)
	public Actor getActor() {
		return actor;
	}
	public void setActor(Actor actor) {
		this.actor = actor;
	}
}
