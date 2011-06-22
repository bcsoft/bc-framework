package cn.bc.docs.service;

import java.util.List;

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

	public List<Attach> findByPtype(String ptype) {
		return this
				.createQuery()
				.condition(
						new AndCondition().add(
								new EqualsCondition("ptype", ptype)).add(
								new OrderCondition("fileDate", Direction.Desc)))
				.list();
	}
}
