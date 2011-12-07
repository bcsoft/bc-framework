package cn.bc.sync.service;

import java.util.List;

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

	public int updateStatus(Long id, int toStatus) {
		if (id == null)
			return 0;
		return this.syncBaseDao.updateStatus(new Long[] { id }, toStatus);
	}

	public int updateStatus(Long[] ids, int toStatus) {
		return this.syncBaseDao.updateStatus(ids, toStatus);
	}

	public int updateStatus2New(String syncType, List<String> syncCodes) {
		return this.syncBaseDao.updateStatus2New(syncType, syncCodes);
	}

	public int updateNewStatus2Done4ExcludeCode(String syncType,
			List<String> excludeSyncCodes) {
		return this.syncBaseDao.updateNewStatus2Done4ExcludeCode(syncType,
				excludeSyncCodes);
	}
}
