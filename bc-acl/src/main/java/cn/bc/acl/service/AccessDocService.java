package cn.bc.acl.service;

import cn.bc.acl.domain.AccessActor;
import cn.bc.acl.domain.AccessDoc;
import cn.bc.core.service.CrudService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 访问对象Service接口
 *
 * @author lbj
 */
public interface AccessDocService extends CrudService<AccessDoc> {

  /**
   * 保存访问对象与访问者
   *
   * @param entity
   * @param accessActors
   */
  AccessDoc save4AccessActors(AccessDoc entity, List<AccessActor> accessActors);

  /**
   * 获取访问对象
   *
   * @param docId   文档标识
   * @param docType 文档类型
   * @return
   */
  AccessDoc load(String docId, String docType);

  /**
   * 保存 ACL 配置信息
   *
   * @param docType 文档类型
   * @param docId   文档ID
   * @param docName 文档的描述名
   * @param details 访问权限配置列表：[{id: 【actor的ID】, role: 【访问控制的值】}, ...]
   * @return 有变动方返回true
   */
  boolean saveConfig(String docType, String docId, String docName, Collection<Map<String, Object>> details);
}