package cn.bc.netdisk.dao;

import java.io.Serializable;

import cn.bc.core.dao.CrudDao;
import cn.bc.netdisk.domain.NetdiskFile;

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
	 * @param id 文件夹id
	 * @return
	 */
	Serializable[] getChildIdsById(Long id);
}
