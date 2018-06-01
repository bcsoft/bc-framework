/**
 *
 */
package cn.bc.identity.web;

import cn.bc.Context;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统上下文的默认实现
 *
 * @author dragon
 */
public class SystemContextImpl implements SystemContext, Serializable {
  private Map<String, Object> attrs = new HashMap<String, Object>();

  @SuppressWarnings("unchecked")
  public <T> T getAttr(String key) {
    if (attrs.containsKey(key))
      return (T) attrs.get(key);
    else
      return null;
  }

  public Context setAttr(String key, Object value) {
    if (value != null)
      attrs.put(key, value);
    else
      attrs.remove(key);
    return this;
  }

  public Actor getUser() {
    return this.getAttr(SystemContext.KEY_USER);
  }

  public ActorHistory getUserHistory() {
    return this.getAttr(SystemContext.KEY_USER_HISTORY);
  }

  public Actor getBelong() {
    return this.getAttr(SystemContext.KEY_BELONG);
  }

  public Actor getUnit() {
    return this.getAttr(SystemContext.KEY_UNIT);
  }

  @SuppressWarnings("unchecked")
  public boolean hasAnyRole(String... roles) {
    return hasAny((List<String>) this.getAttr(SystemContext.KEY_ROLES),
      roles);
  }

  @SuppressWarnings("unchecked")
  public boolean hasAnyOneRole(String roles) {
    return hasAny((List<String>) this.getAttr(SystemContext.KEY_ROLES),
      roles.split(","));
  }

  @SuppressWarnings("unchecked")
  public boolean hasAnyGroup(String... groups) {
    return hasAny((List<String>) this.getAttr(SystemContext.KEY_GROUPS),
      groups);
  }

  @SuppressWarnings("unchecked")
  public boolean hasAnyOneGroup(String groups) {
    return hasAny((List<String>) this.getAttr(SystemContext.KEY_GROUPS),
      groups.split(","));
  }

  public String getContextPath() {
    return this.getAttr(SystemContext.KEY_SYSCONTEXTPATH);
  }

  private boolean hasAny(List<String> _roles, String[] roles) {
    if (_roles != null && !_roles.isEmpty() && roles != null
      && roles.length > 0) {
      for (String role : roles) {
        if (_roles.contains(role)) {
          return true;
        }
      }
    }
    return false;
  }
}
