/**
 * 
 */
package cn.bc.investigate.service;

import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.investigate.domain.Grade;

/**
 * 评分Service
 * 
 * 
 * @author zxr
 * 
 */
public interface GradeService extends CrudService<Grade> {

	/**
	 * 问答题评分
	 * 
	 * @param context
	 *            上下文
	 * @param amount
	 *            计分
	 * @param gid
	 *            评分表Id
	 * 
	 * @param qid试卷id
	 * @param aid答案id
	 * @param rid作答id
	 * @return
	 */
	String dograde(Long qid, Long aid, Long rid, int amount,
			SystemContext context, Long gid);

}