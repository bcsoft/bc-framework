package cn.bc.photo.domain;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.Actor;

import javax.persistence.*;

/**
 * IP摄像头
 * Created by dragon on 2014/8/27.
 */
@Entity
@Table(name = "BC_IPCAMERA")
public class IpCamera extends EntityImpl {
  private Actor owner;  // 所有者
  private String name;  // 名称
  private String url;    // 地址

  @ManyToOne(fetch = FetchType.EAGER, optional = true)
  @JoinColumn(name = "OWNER_ID", referencedColumnName = "ID")
  public Actor getOwner() {
    return owner;
  }

  public void setOwner(Actor owner) {
    this.owner = owner;
  }

  @Column(name = "NAME_")
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
}
