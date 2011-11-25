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

	public boolean hadSync(String syncType, String syncId) {
		return this.syncBaseDao.hadSync(syncType, syncId);
	}

	public SyncBase load(String syncType, String syncId) {
		return this.syncBaseDao.load(syncType, syncId);
	}
}
