package cn.bc.email.dao.jpa;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.email.dao.EmailHistoryDao;
import cn.bc.email.domain.EmailHistory;
import cn.bc.orm.jpa.JpaCrudDao;

/**
 * 邮件查看历史dao接口的实现
 *
 * @author lbj
 */
public class EmailHistoryDaoImpl extends JpaCrudDao<EmailHistory> implements EmailHistoryDao {
	public int getEmailReceiverReadCount(Long emailId, String receiverCode) {
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("email.id", emailId));
		condition.add(new EqualsCondition("reader.code", receiverCode));
		return this.createQuery().condition(condition).list().size();
	}
}