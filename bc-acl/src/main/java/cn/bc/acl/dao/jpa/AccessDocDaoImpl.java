package cn.bc.acl.dao.jpa;

import cn.bc.acl.dao.AccessDocDao;
import cn.bc.acl.domain.AccessDoc;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import org.springframework.util.Assert;

/**
 * 访问对象DAO接口的实现
 *
 * @author lbj
 */
public class AccessDocDaoImpl extends JpaCrudDao<AccessDoc> implements AccessDocDao {
	public AccessDoc load(String docId, String docType) {
		Assert.notNull(docId);
		Assert.notNull(docType);
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("docId", docId));
		condition.add(new EqualsCondition("docType", docType));
		return this.createQuery().condition(condition).singleResult();
	}
}