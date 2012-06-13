package cn.bc.docs.dao.hibernate.jpa;

import cn.bc.docs.dao.AttachHistoryDao;
import cn.bc.docs.domain.AttachHistory;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 附件操作日志Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class AttachHistoryDaoImpl extends HibernateCrudJpaDao<AttachHistory>
		implements AttachHistoryDao {
}
