package cn.bc.netdisk.dao;

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
	 * @return
	 */
	NetdiskFile findNetdiskFileByName(String name, Long pid, Object typeFolder);
}
