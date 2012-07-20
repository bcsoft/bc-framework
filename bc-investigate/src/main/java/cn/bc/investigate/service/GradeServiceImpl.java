/**
 * 
 */
package cn.bc.investigate.service;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.investigate.dao.AnswerDao;
import cn.bc.investigate.dao.GradeDao;
import cn.bc.investigate.dao.QuestionaryDao;
import cn.bc.investigate.dao.RespondDao;
import cn.bc.investigate.domain.Answer;
import cn.bc.investigate.domain.Grade;
import cn.bc.investigate.domain.Questionary;
import cn.bc.investigate.domain.Respond;
import cn.bc.web.ui.json.Json;

/**
 * 评分的实现
 * 
 * @author zxr
 */
public class GradeServiceImpl extends DefaultCrudService<Grade> implements
		GradeService {
	private GradeDao gradeDao;
	private QuestionaryDao questionaryDao;
	private AnswerDao answerDao;
	private RespondDao respondDao;

	@Autowired
	public void setAnswerDao(AnswerDao answerDao) {
		this.answerDao = answerDao;
	}

	@Autowired
	public void setRespondDao(RespondDao respondDao) {
		this.respondDao = respondDao;
	}

	@Autowired
	public void setQuestionaryDao(QuestionaryDao questionaryDao) {
		this.questionaryDao = questionaryDao;
	}

	public GradeDao getGradeDao() {
		return gradeDao;
	}

	@Autowired
	public void setGradeDao(GradeDao gradeDao) {
		this.gradeDao = gradeDao;
		this.setCrudDao(gradeDao);

	}

	public String json;

	public String dograde(Long qid, Long aid, Long rid, int amount,
			SystemContext context, Long gid) {
		Json json = new Json();
		Respond oneRespond = null;
		Answer oneAnswer = null;
		Questionary q = this.questionaryDao.load(qid);
		// 找出作答表中的记录
		Set<Respond> respond = q.getResponds();
		Iterator<Respond> r = respond.iterator();
		while (r.hasNext()) {
			Respond re = r.next();
			if (rid.equals(re.getId())) {
				oneRespond = re;
			}
		}
		// 找出答案表中的记录
		Set<Answer> answer = oneRespond.getAnswers();
		Iterator<Answer> a = answer.iterator();
		while (a.hasNext()) {
			Answer an = a.next();
			if (aid.equals(an.getId())) {
				oneAnswer = an;
			}
		}
		// 保存评分记录
		Grade grade;
		int oldScore = 0;// 原来的评分
		if (gid != null) {
			grade = this.gradeDao.load(gid);
			// 获取上一次所评的分数
			oldScore = grade.getScore();
		} else {
			grade = new Grade();
		}
		grade.setScore(amount);
		grade.setAuthor(context.getUserHistory());
		grade.setFileDate(Calendar.getInstance());
		// 将答案表的是否需要评分字段设置为false
		oneAnswer.setGrade(false);
		grade.setAnswer(oneAnswer);

		// 将分数加到作答表的总分字段时
		int score = oneRespond.getScore();
		if (gid != null) {
			oneRespond.setScore(score - oldScore + amount);
		} else {
			oneRespond.setScore(score + amount);

		}
		// 查看是否所有问题都已经评分
		Iterator<Answer> a2 = answer.iterator();
		while (a2.hasNext()) {
			Answer aswer2 = a2.next();
			if (aswer2.isGrade()) {
				oneRespond.setGrade(true);
				break;
			} else {
				oneRespond.setGrade(false);
			}
		}
		// 保存用户作答内容表数据
		this.answerDao.save(oneAnswer);
		// 保存用户作答表表数据
		this.respondDao.save(oneRespond);
		// 保存评分表数据
		this.gradeDao.save(grade);
		json.put("success", true);

		this.json = json.toString();
		return this.json;

	}

}