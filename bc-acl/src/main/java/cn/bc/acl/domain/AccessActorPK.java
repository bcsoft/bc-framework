/**
 *
 */
package cn.bc.acl.domain;

import cn.bc.identity.domain.Actor;

import java.io.Serializable;

/**
 * 复合主键类
 *
 * @author LBJ
 */

public class AccessActorPK implements Serializable {
  private static final long serialVersionUID = 1L;
  private AccessDoc accessDoc;// 访问对象
  private Actor actor;// 访问者


  public AccessActorPK() {

  }

  public AccessActorPK(AccessDoc accessDoc, Actor actor) {
    this.accessDoc = accessDoc;
    this.actor = actor;
  }

  public AccessDoc getAccessDoc() {
    return accessDoc;
  }

  public void setAccessDoc(AccessDoc accessDoc) {
    this.accessDoc = accessDoc;
  }

  public Actor getActor() {
    return actor;
  }

  public void setActor(Actor actor) {
    this.actor = actor;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (accessDoc == null ? 0 : accessDoc.hashCode());
    result = prime * result + (actor == null ? 0 : actor.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;

    final AccessActorPK other = (AccessActorPK) obj;
    if (accessDoc == null) {
      if (other.accessDoc != null)
        return false;
    } else if (!accessDoc.equals(other.accessDoc)) {
      return false;
    }
    if (actor == null) {
      if (other.actor != null)
        return false;
    } else if (!actor.equals(other.actor)) {
      return false;
    }
    return true;
  }
}
