package cn.bc.netdisk.service;

import java.io.Serializable;

import cn.bc.core.service.CrudService;
import cn.bc.netdisk.domain.NetdiskFile;

/**
 * 网络文件接口
 * 
 * @author zxr
 * 
 */
public interface NetdiskFileService extends CrudService<NetdiskFile> {

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
	 * 删除文件
	 * 
	 * @param id
	 * @param isRelevanceDelete
	 */
	void delete(Long id, boolean isRelevanceDelete);

	/**
	 * 批量删除文件
	 * 
	 * @param ids
	 * @param isRelevanceDelete
	 */
	void delete(Long[] ids, boolean isRelevanceDelete);

	/**
	 * 获取当前的文件和父级文件id
	 * 
	 * @param id当前文件的id
	 * @return
	 */
	Serializable[] getMyselfAndParentsFileId(Long id);

	/**
	 * 通过用户id查找其可以访问指定文件夹以及指定文件以下的所有文件的id
	 * 
	 * @param id用户id
	 * @return
	 */
	Serializable[] getUserSharFileId(Long id);
}
