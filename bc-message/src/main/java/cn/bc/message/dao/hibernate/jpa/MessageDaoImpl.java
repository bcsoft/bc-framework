package cn.bc.message.dao.hibernate.jpa;

import cn.bc.message.dao.MessageDao;
import cn.bc.message.domain.Message;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 消息Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class MessageDaoImpl extends HibernateCrudJpaDao<Message>
		implements MessageDao {
}
