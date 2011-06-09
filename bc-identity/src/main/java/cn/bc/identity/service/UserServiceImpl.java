package cn.bc.identity.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.DigestUtils;

import cn.bc.identity.dao.ActorRelationDao;
import cn.bc.identity.dao.AuthDataDao;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.domain.AuthData;

/**
 * 用户Service接口的实现
 * 
 * @author dragon
 * 
 */
public class UserServiceImpl extends ActorServiceImpl implements UserService {
	private static final Log logger = LogFactory.getLog(UserServiceImpl.class);
	private AuthDataDao authDataDao;
	private ActorRelationDao actorRelationDao;

	public void setActorRelationDao(ActorRelationDao actorRelationDao) {
		this.actorRelationDao = actorRelationDao;
	}

	public void setAuthDataDao(AuthDataDao authDataDao) {
		this.authDataDao = authDataDao;
	}

	public int updatePassword(Long[] ids, String password) {
		return this.authDataDao.updatePassword(ids, password);
	}

	public Actor save(Actor user, Actor belong, Long[] groupIds) {
		boolean isNew = user.isNew();
		// 先保存获取id值
		user = super.save4belong(user, belong);// 这里已经处理了上级关系的保存

		// 如果是新建用户，须新建AuthData对象
		if (isNew) {
			AuthData authData = new AuthData();
			authData.setId(user.getId());
			try {
				// 设置默认密码(32位md5加密)
				authData.setPassword(DigestUtils.md5DigestAsHex("888888"
						.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			}
			this.authDataDao.save(authData);
		}

		// 处理分派的岗位信息
		List<ActorRelation> curArs;
		curArs = this.actorRelationDao.findByFollower(
				ActorRelation.TYPE_BELONG, user.getId(),
				new Integer[] { Actor.TYPE_GROUP });
		Map<String, ActorRelation> gmap = new HashMap<String, ActorRelation>();
		for (ActorRelation ar : curArs)
			gmap.put(ar.getMaster().getId().toString(), ar);
		if (groupIds != null && groupIds.length > 0) {
			ActorRelation ar;
			List<ActorRelation> newArs = new ArrayList<ActorRelation>();
			for (Long gId : groupIds) {
				ar = gmap.get(gId.toString());
				if(ar == null){
					//创建新的关系保存
					ar = new ActorRelation();
					ar.setFollower(user);
					ar.setMaster(this.load(gId));
					ar.setType(ActorRelation.TYPE_BELONG);
					newArs.add(ar);
				}else{
					//已存在的排除不处理
					curArs.remove(ar);
				}
			}
			//保存新增加的关系
			this.actorRelationDao.save(newArs);
			
			//删除不再关联的关系
			this.actorRelationDao.delete(curArs);
		} else {
			// 删除现有的岗位关联
			this.actorRelationDao.delete(curArs);
		}

		// 返回
		return user;
	}

	public AuthData loadAuthData(Long userId) {
		return this.authDataDao.load(userId);
	}

	@Override
	public void delete(Serializable id) {
		super.delete(id);
		// 同时删除认证信息
		this.authDataDao.delete((Long) id);
	}

	@Override
	public void delete(Serializable[] ids) {
		super.delete(ids);
		// 同时删除认证信息
		this.authDataDao.delete((Long[]) ids);
	}
}
