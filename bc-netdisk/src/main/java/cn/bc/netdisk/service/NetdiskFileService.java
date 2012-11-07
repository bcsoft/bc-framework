package cn.bc.netdisk.service;

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
}
