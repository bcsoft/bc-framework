package cn.bc.docs.service;

import java.util.List;

import cn.bc.core.service.CrudService;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.domain.AttachHistory;

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
	 * @return
	 */
	List<Attach> findByPtype(String ptype);

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

	/**
	 * 复制附件的处理:仅支持复制app.data.realPath目录下的附件
	 * 
	 * @param fromPtype
	 *            原附件的ptype
	 * @param fromPuid
	 *            原附件的puid
	 * @param toPtype
	 *            新附件的ptype
	 * @param toPuid
	 *            新附件的puid
	 * @param keepAuthorInfo
	 *            是否保留源附件的创建信息不变，设为false将创建时间和创建人设为当前用户的信息
	 * @return 复制出来的附件列表
	 */
	List<Attach> doCopy(String fromPtype, String fromPuid, String toPtype,
			String toPuid, boolean keepAuthorInfo);

	/**
	 * 保存一个附件操作日志
	 */
	AttachHistory saveHistory(AttachHistory history);
}
