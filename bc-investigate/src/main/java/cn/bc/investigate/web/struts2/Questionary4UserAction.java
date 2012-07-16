/**
 * 
 */
package cn.bc.investigate.web.struts2;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.util.TemplateUtils;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.investigate.domain.Answer;
import cn.bc.investigate.domain.Question;
import cn.bc.investigate.domain.QuestionItem;
import cn.bc.investigate.domain.Questionary;
import cn.bc.investigate.domain.Respond;
import cn.bc.investigate.service.QuestionItemService;
import cn.bc.investigate.service.QuestionaryService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 调查问卷的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Questionary4UserAction extends FileEntityAction<Long, Questionary> {

	private static final long serialVersionUID = 1L;
	private QuestionaryService questionaryService;
	private QuestionItemService questionItemService;
	public Set<Actor> ownedUsers;// 已分配的用户
	public String topics;// 问卷中题目的json字符串
	public String optionItemsValue;// 问题选项
	public Map<String, String> statusesValue;
	public Long userId;// 用户ID
	public int score4User;// 用户得分
	public Long pid;// 作答表关联试卷的Id

	// @Override
	// public boolean isReadonly() {
	// // 问卷管理员或系统管理员
	// SystemContext context = (SystemContext) this.getContext();
	// return !context.hasAnyRole(getText("key.role.bs.driver"),
	// getText("key.role.bc.admin"));
	// }

	@Autowired
	public void setQuestionaryService(QuestionaryService questionaryService) {
		this.setCrudService(questionaryService);
		this.questionaryService = questionaryService;
	}

	@Autowired
	public void setQuestionItemService(QuestionItemService questionItemService) {
		this.questionItemService = questionItemService;
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		statusesValue = this.getBSStatuses();
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		PageOption pageOption = new PageOption().setWidth(645).setMinWidth(325)
				.setHeight(500);
		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		// boolean readonly = this.isReadonly();
		if (editable) {
			pageOption.put("readonly", false);

		} else {
			pageOption.put("readonly", true);
		}
		// 添加按钮
		buildFormPageButtons(pageOption, editable);

		return pageOption;
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (this.getE().getStatus() == Questionary.STATUS_DRAFT) {
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.questionaryForm.save"));
			pageOption.addButton(new ButtonOption(
					getText("questionary.preview"), null,
					"bc.questionaryForm.preview"));
			pageOption.addButton(new ButtonOption(getText("questionary.issue"),
					null, "bc.questionaryForm.issue"));
		}
		// 提交答卷
		if (this.getE().getStatus() == Questionary.STATUS_ISSUE
				&& this.IsExisUser(this.getE()) == false) {
			pageOption.addButton(new ButtonOption(
					getText("questionary4User.passed"), null,
					"bc.questionary4UserForm.save"));
		}

	}

	@Override
	protected void afterCreate(Questionary entity) {
		super.afterCreate(entity);
		this.getE().setStatus(Questionary.STATUS_DRAFT);
		this.getE().setType(Questionary.TYPE_PAPER);
		this.getE().setIssuer(null);
	}

	@Override
	protected void beforeSave(Questionary entity) {
		super.beforeSave(entity);
		Questionary questionary = this.questionaryService.load(this.getE()
				.getId());
		this.setE(questionary);
		SystemContext context = (SystemContext) this.getContext();
		Set<Respond> respond = new LinkedHashSet<Respond>();
		Set<Answer> answer = new LinkedHashSet<Answer>();
		Set<JSONObject> answerItems4OneQuestion;
		Respond respondResource;
		respondResource = new Respond();
		int score = 0;// 分数
		boolean allRight;// 是否全选
		int allRightPoint;// 全选题目的分数
		respondResource.setAuthor(context.getUserHistory());
		respondResource.setFileDate(Calendar.getInstance());
		respondResource.setQuestionary(this.getE());

		try {
			if (topics != null && topics.length() > 0) {
				Answer answerResource;
				JSONArray questionJsons = new JSONArray(this.topics);
				JSONObject questionJson;
				questionJson = new JSONObject();
				JSONObject answerItem;
				for (int n = 0; n < questionJsons.length(); n++) {
					questionJson = questionJsons.getJSONObject(n);
					JSONArray answerItems = new JSONArray(
							questionJson.getString("optionItemsValue"));
					answerItems4OneQuestion = new LinkedHashSet<JSONObject>();
					// 判断此问题是否全答对才有分
					allRight = false;
					allRightPoint = 0;
					if (questionJson.getInt("type") == Question.TYPE_CHECKBOX) {
						// 记录需要处理的多选题
						Set<Question> question = questionary.getQuestions();
						Iterator<Question> qu = question.iterator();
						long qid = questionJson.getLong("questionId");
						while (qu.hasNext()) {
							Question oneQuestion = qu.next();
							if (qid == oneQuestion.getId()) {
								if (oneQuestion.isSeperateScore()) {
									allRight = true;
									allRightPoint = oneQuestion.getScore();
								}
							}
						}
					}

					for (int i = 0; i < answerItems.length(); i++) {
						answerItem = answerItems.getJSONObject(i);
						QuestionItem questionItem = new QuestionItem();
						questionItem.setId(answerItem.getLong("itemId"));
						answerResource = new Answer();
						// 选择题
						if (questionJson.getInt("type") == Question.TYPE_RADIO
								|| questionJson.getInt("type") == Question.TYPE_CHECKBOX) {
							QuestionItem q = this.questionItemService
									.load(answerItem.getLong("itemId"));
							answerResource.setRespond(respondResource);
							answerResource.setItem(questionItem);
							answerResource.setScore(q.getScore());
							if (allRight) {
								answerItems4OneQuestion.add(answerItem);
							} else {
								score += q.getScore();
							}
						}
						// 简答题
						if (questionJson.getInt("type") == Question.TYPE_QA) {
							answerResource.setRespond(respondResource);
							answerResource.setItem(questionItem);
							answerResource.setContent(answerItem
									.getString("subject"));
							// 如果需要评分的不作计分处理
							boolean grade = answerItem.getBoolean("grade");
							if (!grade) {
								Question oneQuestion = getQuestionObject(questionJson
										.getLong("questionId"));
								int Jquizscore = oneQuestion.getScore();
								answerResource.setScore(Jquizscore);
								score += Jquizscore;
							} else {
								answerResource.setGrade(grade);
								respondResource.setGrade(grade);
							}
						}
						// 填空题
						if (questionJson.getInt("type") == Question.TYPE_FILL_IN) {
							answerResource.setRespond(respondResource);
							answerResource.setItem(questionItem);
							// 获取填空题的分数
							JSONArray answer4Completion = new JSONArray(
									answerItem.getString("completions"));
							JSONArray config = getCompletionConfig(questionJson
									.getLong("questionId"));
							answerResource.setContent(answer4Completion
									.toString());
							Map<String, Object> anwerMsp = getAnswerMap(answer4Completion);
							// answerResource.setScore(getCompletionScore(config,
							// anwerMsp));
							score += getCompletionScore(config, anwerMsp);
							// answerResource.setContent(answerItem.getString("subject"));
						}
						answer.add(answerResource);
					}

					// 对多选题特殊处理
					if (allRight && answerItems4OneQuestion != null
							&& !answerItems4OneQuestion.isEmpty()) {
						// 获取用户的选项数组
						List<Long> answerList = new LinkedList<Long>();
						Long[] answertArray = null;
						Iterator<JSONObject> aiq = answerItems4OneQuestion
								.iterator();
						while (aiq.hasNext()) {
							JSONObject oneAnswerItems = aiq.next();
							answerList.add(oneAnswerItems.getLong("itemId"));
						}
						answertArray = answerList.toArray(new Long[answerList
								.size()]);
						Arrays.sort(answertArray);
						// 对比获取用户的选项数组与获取标准答案的选择项数组
						// 判断用户是否全对
						Long[] questionArray = getResultArray(questionary,
								questionJson);
						if (Arrays.equals(questionArray, answertArray)) {
							score += allRightPoint;
						}

					}
				}
				respondResource.setScore(score);
			}
			// }
			if (respondResource.getAnswers() != null) {
				// respondResource.getAnswers().clear();
				respondResource.getAnswers().addAll(answer);
			} else {
				respondResource.setAnswers(answer);
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

		respond.add(respondResource);
		if (questionary.getResponds() != null) {
			// questionary.getResponds().clear();
			questionary.getResponds().addAll(respond);

		} else {
			questionary.setResponds(respond);
		}
	}

	/**
	 * 获取填空题分数
	 * 
	 * @param completionConfig
	 *            填空题的答案配置
	 * @param answerMap
	 *            用户答题的选项map
	 */
	private int getCompletionScore(JSONArray completionConfig,
			Map<String, Object> answerMap) {
		int score = 0;// 分数
		int score4Config;
		try {
			for (int i = 0; i < completionConfig.length(); i++) {
				JSONObject oneConfig = completionConfig.getJSONObject(i);
				String key = oneConfig.getString("key");
				String value = oneConfig.getString("value");
				score4Config = oneConfig.getInt("score");
				if (value.equals(answerMap.get(key))) {
					score += score4Config;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return score;
	}

	/**
	 * 获取用户答题的map
	 * 
	 * @param answer4Completion
	 */
	private Map<String, Object> getAnswerMap(JSONArray answer4Completion) {
		Map<String, Object> answerMap = null;// 用户答题的map
		JSONObject answerItem;
		try {
			answerMap = new HashMap<String, Object>();
			for (int i = 0; i < answer4Completion.length(); i++) {
				answerItem = answer4Completion.getJSONObject(i);
				answerMap.put(answerItem.getString("key"),
						answerItem.get("value"));

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return answerMap;
	}

	/**
	 * 获取填空题的特殊配置
	 * 
	 * @param long1
	 *            问题项ID
	 */
	private JSONArray getCompletionConfig(long qid) {
		JSONArray config = null;
		Set<Question> question3 = this.getE().getQuestions();
		Iterator<Question> qu3 = question3.iterator();
		while (qu3.hasNext()) {
			Question oneQuestion = qu3.next();
			if (qid == oneQuestion.getId()) {
				Set<QuestionItem> questionItem3 = oneQuestion.getItems();
				Iterator<QuestionItem> qi3 = questionItem3.iterator();
				while (qi3.hasNext()) {
					QuestionItem questionItem = qi3.next();
					config = questionItem.getConfigJsonArray();
				}
			}
		}

		return config;
	}

	// 获取标准答案的选择项数组
	private Long[] getResultArray(Questionary questionary,
			JSONObject questionJson) throws JSONException {
		Set<Question> question2 = questionary.getQuestions();
		Iterator<Question> qu2 = question2.iterator();
		long qid2 = questionJson.getLong("questionId");
		List<Long> result = new LinkedList<Long>();
		Long[] resultArray = null;
		while (qu2.hasNext()) {
			Question oneQuestion = qu2.next();
			if (qid2 == oneQuestion.getId()) {
				Set<QuestionItem> questionItem2 = oneQuestion.getItems();
				Iterator<QuestionItem> qi2 = questionItem2.iterator();
				while (qi2.hasNext()) {
					QuestionItem qti = qi2.next();
					Long resultId = qti.getId();
					if (qti.isStandard()) {
						result.add(resultId);
					}
					resultArray = result.toArray(new Long[result.size()]);
					Arrays.sort(resultArray);
				}
			}
		}
		return resultArray;
	}

	// 获取问卷是否已评分
	public boolean getIsNeedGrade() {

		Respond respond = this.getUserRespond();
		if (respond.isGrade()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String open() throws Exception {
		SystemContext context = this.getSystyemContext();
		Questionary e;
		if (userId == null) {
			userId = context.getUserHistory().getId();
		}
		if (pid == null) {
			e = this.questionaryService.load(this.getId());
		} else {
			e = this.questionaryService.load(pid);
		}

		this.setE(e);
		this.formPageOption = buildFormPageOption(true);
		// 初始化表单的其他配置
		this.initForm(false);
		this.afterOpen(e);
		boolean isExist = IsExisUser(e);
		if (isExist || e.getStatus() == Questionary.STATUS_END) {
			// 强制表单只读
			this.formPageOption = buildFormPageOption(false);
			score4User = this.getUserScore(userId, e);
			return "statistics";
		} else {
			return "formr";
		}
	}

	/**
	 * 获取用户分数
	 * 
	 * @param userId2用户ID
	 * @return
	 */
	private int getUserScore(Long userId, Questionary e) {
		int i = 0;
		Set<Respond> respond = e.getResponds();
		Iterator<Respond> r = respond.iterator();
		while (r.hasNext()) {
			Respond oneRespond = r.next();
			if (new Long(userId)
					.equals(new Long(oneRespond.getAuthor().getId()))) {
				i = oneRespond.getScore();
			}
		}

		return i;
	}

	// 获取试卷的总分
	public int totalScore() {
		int i = 0;
		Set<Question> questions = this.getE().getQuestions();
		Iterator<Question> q = questions.iterator();
		while (q.hasNext()) {
			Question question = q.next();
			i += question.getScore();
		}
		return i;
	}

	/**
	 * 用户是否答卷
	 * 
	 * @param e
	 * @return
	 */
	private boolean IsExisUser(Questionary e) {
		SystemContext context = this.getSystyemContext();
		if (userId == null) {
			userId = context.getUserHistory().getId();
		}
		Set<Respond> respond = e.getResponds();
		Iterator<Respond> r = respond.iterator();
		boolean isExist = false;
		while (r.hasNext()) {
			Respond oneRespond = r.next();
			if (new Long(userId)
					.equals(new Long(oneRespond.getAuthor().getId()))) {
				isExist = true;
			}

		}
		return isExist;
	}

	// 获取问题项的作答人数
	public int getQuestItemRespondCount(Long questItemId) {
		Set<Respond> respond = this.getE().getResponds();
		QuestionItem item;
		int i = 0;
		Iterator<Respond> it = respond.iterator();
		while (it.hasNext()) {
			Respond re = (Respond) it.next();
			Set<Answer> answer = re.getAnswers();
			Iterator<Answer> an = answer.iterator();
			while (an.hasNext()) {
				Answer noeAnswer = an.next();
				item = noeAnswer.getItem();
				if (questItemId == item.getId()) {
					i++;
				}
			}

		}

		return i;
	}

	// 获取参与人数
	public int getJoinCount() {
		Set<Respond> actor = this.getE().getResponds();
		return actor.size();
	}

	@Override
	protected void afterEdit(Questionary entity) {
		super.afterEdit(entity);

		// 加载已参与的人
		this.ownedUsers = entity.getActors();
	}

	@Override
	protected void afterOpen(Questionary entity) {
		super.afterOpen(entity);

		// 加载已参与的人
		this.ownedUsers = entity.getActors();
	}

	// 将填空题中的占位符替换为想要的结果
	public String formatCompletion(String source) {
		Map<String, Object> args = new HashMap<String, Object>();
		List<String> markers = TemplateUtils.findMarkers(source);
		for (String marker : markers) {
			args.put(
					marker,
					"<input type='text' name='"
							+ marker
							+ "' style='width:45px;border-width: 0 0 1px 0;text-align: center;' class='ui-widget-content'/>");
		}
		return TemplateUtils.format(source, args);
	}

	// 将填空题中的占位符替换为想要的结果
	public String formatCompletionValue(String source, String configs, Long qid) {
		// 获取用户该题的作答
		JSONArray answerValues = getUserAnswerValues(qid);
		Map<String, Object> args = new HashMap<String, Object>();
		List<String> markers = TemplateUtils.findMarkers(source);
		JSONArray configValues;
		try {
			configValues = new JSONArray(configs);
			JSONObject config;
			JSONObject answerValue;
			for (String marker : markers) {
				config = findConfig(configValues, marker);
				answerValue = findAnswerValue(answerValues, marker);
				if (config != null) {
					String userAnswer = answerValue.getString("value");
					String standardValue = config.getString("value");
					args.put(
							marker,
							"<input type='text' name='"
									+ null
									+ "' value='"
									+ userAnswer
									+ "' style='width:45px;border-width: 0 0 1px 0;text-align: center;'"
									+ (userAnswer.equals(standardValue) ? " class='ui-widget-content'/>"
											: " class='ui-widget-content ui-state-error' title='"
													+ standardValue + "' />"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return TemplateUtils.format(source, args);
	}

	/**
	 * 获取用户填空题中每个空的相关的信息
	 * 
	 * @param answerValue
	 * @param marker
	 * @return
	 */
	private JSONObject findAnswerValue(JSONArray answerValues, String marker) {
		JSONObject answer;
		for (int i = 0; i < answerValues.length(); i++) {
			try {
				answer = answerValues.getJSONObject(i);
				if (marker.equals(answer.getString("key")))
					return answer;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获取用户的填空题的作答结果
	 * 
	 * @param qid
	 * @return
	 */
	private JSONArray getUserAnswerValues(Long qid) {
		Respond respond = getUserRespond();
		Set<Answer> answer = respond.getAnswers();
		Iterator<Answer> a = answer.iterator();
		try {
			while (a.hasNext()) {
				Answer oneAnswer = a.next();
				if (qid == oneAnswer.getItem().getId()) {
					return new JSONArray(oneAnswer.getContent());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取用户的作答对象
	 * 
	 * @param qid
	 * @return
	 */
	private Respond getUserRespond() {
		Respond oneRespond;
		SystemContext context = this.getSystyemContext();
		long userId = context.getUserHistory().getId();
		Set<Respond> respond = this.getE().getResponds();
		Iterator<Respond> r = respond.iterator();
		while (r.hasNext()) {
			oneRespond = r.next();
			if (userId == oneRespond.getAuthor().getId()) {
				return oneRespond;

			}
		}
		return null;
	}

	private JSONObject findConfig(JSONArray configValues, String marker) {
		JSONObject config;
		for (int i = 0; i < configValues.length(); i++) {
			try {
				config = configValues.getJSONObject(i);
				if (marker.equals(config.getString("key")))
					return config;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 根据问题Id获取问题对象
	 * 
	 * @param qid
	 * @return
	 */
	private Question getQuestionObject(long qid) {

		Set<Question> question = this.getE().getQuestions();
		Iterator<Question> qu = question.iterator();
		while (qu.hasNext()) {
			Question oneQuestion = qu.next();
			if (qid == oneQuestion.getId()) {
				return oneQuestion;
			}
		}
		return null;
	}

	/**
	 * 获取用户简答题的作答结果
	 * 
	 * @param qid
	 * @return
	 */
	public String formatJQuizValue(Long qid) {
		Respond respond = getUserRespond();
		Set<Answer> answer = respond.getAnswers();
		Iterator<Answer> a = answer.iterator();
		while (a.hasNext()) {
			Answer oneAnswer = a.next();
			if (oneAnswer.getItem().getId() == qid) {
				return oneAnswer.getContent();
			}
		}
		return null;
	}

	/**
	 * 状态值转换列表：待发布|已发布|已归档|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Questionary.STATUS_DRAFT),
				getText("questionary.release.wait"));
		statuses.put(String.valueOf(Questionary.STATUS_ISSUE),
				getText("questionary.release.already"));
		statuses.put(String.valueOf(Questionary.STATUS_END),
				getText("questionary.release.end"));
		statuses.put("", getText("questionary.release.all"));
		return statuses;
	}

}