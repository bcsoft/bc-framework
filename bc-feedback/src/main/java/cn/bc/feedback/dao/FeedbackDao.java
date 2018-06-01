package cn.bc.feedback.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.feedback.domain.Feedback;
import cn.bc.feedback.domain.Reply;

/**
 * 反馈Dao接口
 *
 * @author dragon
 */
public interface FeedbackDao extends CrudDao<Feedback> {
  /**
   * 保存回复信息
   *
   * @param reply
   * @return
   */
  Reply addReply(Reply reply);

  /**
   * 删除指定的回复：标记删除
   *
   * @param replyId
   */
  void deleteReply(Long replyId);
}
