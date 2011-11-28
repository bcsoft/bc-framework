package cn.bc.sync.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;

/**
 * 同步服务的默认实现
 * 
 * @author rongjih
 * 
 */
public class SyncBaseServiceImpl extends DefaultCrudService<SyncBase> implements
		SyncBaseService {
	private SyncBaseDao syncBaseDao;

	public SyncBaseDao getSyncBaseDao() {
		return syncBaseDao;
	}

	public void setSyncBaseDao(SyncBaseDao syncBaseDao) {
		this.syncBaseDao = syncBaseDao;
		this.setCrudDao(syncBaseDao);
	}

	public boolean hadSync(String syncType, String syncCode) {
		return this.syncBaseDao.hadSync(syncType, syncCode);
	}

	public SyncBase load(String syncType, String syncCode) {
		return this.syncBaseDao.load(syncType, syncCode);
	}

	public boolean hadGenerate(String syncTo, Long id) {
		return this.syncBaseDao.hadGenerate(syncTo, id);
	}

	public int updateStatus(Long[] _ids, int toStatus) {
		return this.syncBaseDao.updateStatus(_ids, toStatus);
	}
}
