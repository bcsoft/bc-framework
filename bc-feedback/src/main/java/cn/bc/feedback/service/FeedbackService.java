package cn.bc.feedback.service;

import cn.bc.core.service.CrudService;
import cn.bc.feedback.domain.Feedback;
import cn.bc.feedback.domain.Reply;

/**
 * 反馈Service接口
 * 
 * @author dragon
 * 
 */
public interface FeedbackService extends CrudService<Feedback> {
	/**
	 * 保存回复信息
	 * 
	 * @param pid
	 *            所属反馈的id
	 * @param content
	 *            回复的内容
	 * @return
	 */
	Reply addReply(Long pid, String content);

	/**
	 * 删除指定的回复：标记删除
	 * 
	 * @param replyId
	 */
	void deleteReply(Long replyId);
}
