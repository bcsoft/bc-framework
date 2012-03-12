package cn.bc.feedback.dao.hibernate.jpa;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.BCConstants;
import cn.bc.feedback.dao.FeedbackDao;
import cn.bc.feedback.domain.Feedback;
import cn.bc.feedback.domain.Reply;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 反馈Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class FeedbackDaoImpl extends HibernateCrudJpaDao<Feedback> implements
		FeedbackDao {
	private static Log logger = LogFactory.getLog(FeedbackDaoImpl.class);

	public Reply addReply(Reply reply) {
		this.getJpaTemplate().persist(reply);
		return reply;
	}

	public void deleteReply(Long replyId) {
		Reply reply = this.getJpaTemplate().find(Reply.class, replyId);
		if (reply == null) {
			logger.warn("要删除的回复信息已不存在！id=" + replyId);
			return;
		}

		reply.setModifiedDate(Calendar.getInstance());
		reply.setModifier(SystemContextHolder.get().getUserHistory());
		reply.setStatus(BCConstants.STATUS_DELETED);
		this.getJpaTemplate().merge(reply);// 更新
	}
}
