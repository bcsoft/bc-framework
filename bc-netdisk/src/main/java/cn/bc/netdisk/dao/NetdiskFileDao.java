package cn.bc.netdisk.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.bc.core.dao.CrudDao;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.netdisk.domain.NetdiskShare;

/**
 * 网络文件Dao接口
 * 
 * @author zxr
 * 
 */
public interface NetdiskFileDao extends CrudDao<NetdiskFile> {
	/**
	 * 通过文件名称,pid,文件类型来查找文件
	 * 
	 * @param name
	 * @param typeFolder
	 *            如果为null时忽略类型
	 * @param pid
	 *            如果为null时忽略pid
	 * @param batchNo
	 *            批号
	 * @return
	 */
	NetdiskFile findNetdiskFileByName(String name, Long pid, Object typeFolder,
			String batchNo);

	/**
	 * 通过id查找指定文件夹以及指定文件夹以下的所有文件的id
	 * 
	 * @param id
	 *            文件夹id
	 * @return
	 */
	Serializable[] getMyselfAndChildFileId(Long id);

	/**
	 * 获取当前的文件和父级文件id
	 * 
	 * @param id
	 *            当前文件Id
	 * @return
	 */
	Serializable[] getMyselfAndParentsFileId(Long id);

	/**
	 * 通过用户id查找其可以访问指定文件夹以及指定文件以下的所有文件的id
	 * 
	 * @param id
	 *            用户id
	 * @return
	 */
	Serializable[] getUserSharFileId(Long id);

	/**
	 * 根据当前用户ID和文件Id查找权限信息
	 * 
	 * @param aid
	 * @param pid
	 * @return
	 */
	NetdiskShare getNetdiskShare(Long aid, Long pid);

	/**
	 * 查找当前用户能查看的所有文件(自建与别人分享)
	 * 
	 * @param userId用户ID
	 * @return
	 */
	Serializable[] getUserSharFileId2All(Long userId);

	/**
	 * 获取属主用户创建的节点信息
	 * 
	 * @param ownerId
	 *            创建者的ID，对应ActorHistory的ID
	 * @param pid
	 *            上级节点的ID，为空代表根节点
	 * @return
	 */
	List<Map<String, Object>> findOwnerFolder(Long ownerId, Long pid);

	/**
	 * 获取分享给指定用户的根节点信息
	 * 
	 * @param sharerId
	 *            指定用户的ID，对应Actor的ID
	 * @param isEdit
	 *            拥有编辑权限
	 * @return
	 */
	List<Map<String, Object>> findShareRootFolders(Long sharerId, boolean isEdit);

	/**
	 * 查找公共硬盘所有文件
	 * 
	 * @return
	 */
	Long[] getUserPublicFileId();

	/**
	 * 获取指定文件夹下的子文件夹
	 * 
	 * @param pid
	 * @param isEdit
	 *            拥有编辑权限
	 * @param userId
	 *            用户id
	 * @return
	 */
	List<Map<String, Object>> findChildFolder(Long pid, boolean isEdit,
			Long userId);

	/**
	 * 获取公共的根节点信息
	 * 
	 * @return
	 */
	List<Map<String, Object>> findPublicRootFolder();
}
