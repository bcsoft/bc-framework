package cn.bc.feedback.dao.hibernate.jpa;

import cn.bc.BCConstants;
import cn.bc.feedback.dao.FeedbackDao;
import cn.bc.feedback.domain.Feedback;
import cn.bc.feedback.domain.Reply;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.orm.jpa.JpaCrudDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * 反馈Dao接口的实现
 *
 * @author dragon
 */
public class FeedbackDaoImpl extends JpaCrudDao<Feedback> implements FeedbackDao {
	private static Logger logger = LoggerFactory.getLogger(FeedbackDaoImpl.class);

	public Reply addReply(Reply reply) {
		this.getEntityManager().persist(reply);

		// 更新反馈的最后回复信息
		StringBuffer jpql = new StringBuffer();
		jpql.append("update Feedback set lastReplyDate=?");
		jpql.append(",lastReplier=?");
		jpql.append(",replyCount=replyCount+1");
		jpql.append(" where id=?");
		this.executeUpdate(jpql.toString(), new Object[]{reply.getFileDate(), reply.getAuthor(), reply.getFeedback().getId()});

		return reply;
	}

	public void deleteReply(Long replyId) {
		Reply reply = this.getEntityManager().find(Reply.class, replyId);
		if (reply == null) {
			logger.warn("要删除的回复信息已不存在！id={}", replyId);
			return;
		}

		reply.setModifiedDate(Calendar.getInstance());
		reply.setModifier(SystemContextHolder.get().getUserHistory());
		reply.setStatus(BCConstants.STATUS_DELETED);
		this.getEntityManager().merge(reply);// 更新
	}
}