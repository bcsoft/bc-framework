package cn.bc.netdisk.service;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.docs.domain.Attach;
import cn.bc.netdisk.dao.NetdiskFileDao;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.netdisk.domain.NetdiskShare;

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

	public void delete(Long id, boolean isRelevanceDelete) {
		NetdiskFile netdiskFile = this.netdiskFileDao.load(id);
		// 如果是文件就删除物理文件
		if (netdiskFile.getType() == NetdiskFile.TYPE_FILE) {
			String path = Attach.DATA_REAL_PATH + "/netdisk/"
					+ netdiskFile.getPath();
			File f = new File(path);
			f.delete();
		}
		// 若是文件夹且文件夹下的子文件都删除
		if (isRelevanceDelete) {
			// 通过id找相关的所有pid
			Serializable[] ids = this.netdiskFileDao
					.getMyselfAndChildFileId(id);
			// 如果pid中是文件类型的就删除物理文件
			for (int i = 0; i < ids.length; i++) {
				NetdiskFile df = this.netdiskFileDao.load(ids[i]);
				if (df != null) {
					if (df.getType() == NetdiskFile.TYPE_FILE) {
						String path = Attach.DATA_REAL_PATH + "/netdisk/"
								+ df.getPath();
						File f = new File(path);
						f.delete();
					}
				}
				// 批量删除文件数据
				if (i == ids.length - 1) {
					super.delete(ids);
				}
			}

		} else {
			super.delete(id);
		}

	}

	// 批量删除
	public void delete(Long[] ids, boolean isRelevanceDelete) {
		for (Long id : ids) {
			NetdiskFile netdiskFile = this.netdiskFileDao.load(id);
			if (netdiskFile.getType() == NetdiskFile.TYPE_FOLDER
					&& isRelevanceDelete) {
				this.delete(id, true);
			} else {
				this.delete(id, false);
			}
		}
	}

	public Serializable[] getMyselfAndParentsFileId(Long id) {
		return this.netdiskFileDao.getMyselfAndParentsFileId(id);
	}

	public Serializable[] getUserSharFileId(Long id) {
		return this.netdiskFileDao.getUserSharFileId(id);
	}

	public NetdiskShare getNetdiskShare(Long aid, Long pid) {
		return this.netdiskFileDao.getNetdiskShare(aid, pid);
	}

	public Serializable[] getMyselfAndChildFileId(Long id) {
		return this.netdiskFileDao.getMyselfAndChildFileId(id);
	}

	public Serializable[] getUserSharFileId2All(Long userId) {
		return this.netdiskFileDao.getUserSharFileId2All(userId);
	}

	public List<Map<String, Object>> findOwnerFolder(Long ownerId, Long pid) {
		return this.netdiskFileDao.findOwnerFolder(ownerId, pid);
	}

	public List<Map<String, Object>> findShareRootFolders(Long sharerId) {
		return this.netdiskFileDao.findShareRootFolders(sharerId);
	}

	public Long[] getUserPublicFileId() {
		return this.netdiskFileDao.getUserPublicFileId();
	}

	public List<Map<String, Object>> findChildFolder(Long pid) {
		return this.netdiskFileDao.findChildFolder(pid);
	}

	public List<Map<String, Object>> findPublicRootFolder() {
		return this.netdiskFileDao.findPublicRootFolder();
	}
}
