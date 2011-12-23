package cn.bc.docs.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.core.RichEntity;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.docs.domain.Attach;

/**
 * 附件service接口的实现
 * 
 * @author dragon
 * 
 */
public class AttachServiceImpl extends DefaultCrudService<Attach> implements
		AttachService {
	private static Log logger = LogFactory.getLog(AttachServiceImpl.class);

	public List<Attach> findByPtype(String ptype, String puid) {
		return this
				.createQuery()
				.condition(
						new AndCondition()
								.add(new EqualsCondition("ptype", ptype))
								.add(new EqualsCondition("puid", puid))
								.add(new EqualsCondition("status",
										RichEntity.STATUS_ENABLED))
								.add(new OrderCondition("fileDate",
										Direction.Desc))).list();
	}

	public Attach loadByPtype(String ptype, String puid) {
		List<Attach> list = this
				.createQuery()
				.condition(
						new AndCondition()
								.add(new EqualsCondition("ptype", ptype))
								.add(new EqualsCondition("puid", puid))
								.add(new EqualsCondition("status",
										RichEntity.STATUS_ENABLED))
								.add(new OrderCondition("fileDate",
										Direction.Desc))).list(1, 1);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<Attach> doCopy(String fromPtype, String fromPuid,
			String toPtype, String toPuid) {
		// TODO
		logger.fatal("复制附件的方法还没实现！");
		return null;
	}
}
