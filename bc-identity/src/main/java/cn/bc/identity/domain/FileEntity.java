/**
 *
 */
package cn.bc.identity.domain;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 基本文档实体接口：定义id字段
 *
 * @author dragon
 */
public interface FileEntity<ID extends Serializable> extends
  cn.bc.core.Entity<ID> {
  /**
   * 获取文档的创建时间
   *
   * @return
   */
  Calendar getFileDate();

  /**
   * 设置文档的创建时间
   *
   * @param fileDate
   */
  void setFileDate(Calendar fileDate);

  /**
   * 获取文档的创建人信息
   *
   * @return
   */
  ActorHistory getAuthor();

  /**
   * 设置文档的创建人信息
   *
   * @param author
   */
  void setAuthor(ActorHistory author);

  /**
   * 获取文档的最后修改时间
   *
   * @return
   */
  Calendar getModifiedDate();

  /**
   * 设置文档的最后修改时间
   *
   * @param fileDate
   */
  void setModifiedDate(Calendar modifiedDate);

  /**
   * 获取文档的最后修改人信息
   *
   * @return
   */
  ActorHistory getModifier();

  /**
   * 设置文档的最后修改人信息
   *
   * @param modifier
   */
  void setModifier(ActorHistory modifier);
}
