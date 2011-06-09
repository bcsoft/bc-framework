package cn.bc.desktop.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.desktop.dao.PersonalDao;
import cn.bc.desktop.domain.Personal;

/**
 * 个人设置service接口的实现
 * 
 * @author dragon
 * 
 */
public class PersonalServiceImpl extends
		DefaultCrudService<Personal> implements PersonalService {
	private PersonalDao personalDao;

	@Autowired
	public void setPersonalDao(PersonalDao personalDao) {
		this.personalDao = personalDao;
		this.setCrudDao(personalDao);
	}

	public Personal loadByActor(Long actorId) {
		return personalDao.loadByActor(actorId, false);
	}

	public Personal loadByActor(Long actorId, boolean autoUseGlobal) {
		return personalDao.loadByActor(actorId, autoUseGlobal);
	}

	public Personal loadGlobalConfig() {
		return personalDao.loadGlobalConfig();
	}
}
