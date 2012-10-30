package cn.bc.netdisk.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.netdisk.dao.NetdiskFileDao;
import cn.bc.netdisk.domain.NetdiskFile;

/**
 * 网络文件接口的实现
 * 
 * @author zxr
 * 
 */
public class NetdiskFileServiceImpl extends DefaultCrudService<NetdiskFile>
		implements NetdiskFileService {
	// private static Log logger = LogFactory.getLog(NetdiskServiceImpl.class);

	private NetdiskFileDao netdiskFileDao;

	@Autowired
	public void setNetdiskFileDao(NetdiskFileDao netdiskFileDao) {
		this.netdiskFileDao = netdiskFileDao;
		this.setCrudDao(netdiskFileDao);
	}

	public NetdiskFile findNetdiskFileByName(String name, Long pid,
			Object typeFolder, String batchNo) {
		return this.netdiskFileDao.findNetdiskFileByName(name, pid, typeFolder,
				batchNo);
	}

}
