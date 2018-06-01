package cn.bc.email.service;

import cn.bc.core.service.CrudService;
import cn.bc.email.domain.EmailTrash;

import java.util.List;

/**
 * 垃圾邮件Service接口
 *
 * @author lbj
 */
public interface EmailTrashService extends CrudService<EmailTrash> {
  /**
   * 根据操作人id 查找可还原的垃圾邮件
   *
   * @param ownerId
   * @return
   */
  List<EmailTrash> find4resumableByOwnerId(Long ownerId);
}