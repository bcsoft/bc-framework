package cn.bc.photo.dao.jpa;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.IsNullCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.photo.dao.IpCameraDao;
import cn.bc.photo.domain.IpCamera;

import java.util.List;

/**
 * IP摄像头DAO接口的实现
 *
 * @author dragon
 */
public class IpCameraDaoImpl extends JpaCrudDao<IpCamera> implements IpCameraDao {
	public List<IpCamera> findByOwner(Long ownerId) {
		Condition c = new OrCondition()
				.add(new IsNullCondition("owner.id"))
				.add(new EqualsCondition("owner.id", ownerId))
				.add(new OrderCondition("owner.orderNo").add("name", Direction.Asc));
		return this.createQuery().condition(c).list();
	}
}