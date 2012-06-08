/**
 * 
 */
package cn.bc.business.investigate.dao.hibernate.jpa;

import cn.bc.business.investigate.dao.QuestionaryDao;
import cn.bc.investigate.domain.Questionary;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 调查问卷Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class QuestionaryDaoImpl extends HibernateCrudJpaDao<Questionary>
		implements QuestionaryDao {
	//private static Log logger = LogFactory.getLog(QuestionaryDaoImpl.class);
}