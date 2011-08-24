package cn.bc.docs.service;

import java.util.List;

import cn.bc.core.service.CrudService;
import cn.bc.docs.domain.Attach;

/**
 * 附件Service接口
 * 
 * @author dragon
 * 
 */
public interface AttachService extends CrudService<Attach> {

	/**
	 * 查找指定文档的附件
	 * 
	 * @param ptype
	 *            附件所属文档的类型
	 * @param puid
	 *            附件所属文档的uid
	 * @return
	 */
	List<Attach> findByPtype(String ptype, String puid);

	/**
	 * 查找指定文档的单一附件
	 * 
	 * @param ptype
	 *            附件所属文档的类型
	 * @param puid
	 *            附件所属文档的uid
	 * @return
	 */
	Attach loadByPtype(String ptype, String puid);
}
