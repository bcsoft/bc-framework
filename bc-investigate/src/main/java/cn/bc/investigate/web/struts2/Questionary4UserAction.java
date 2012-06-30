/**
 * 
 */
package cn.bc.investigate.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

	@Override
	public boolean isReadonly() {
		// 问卷管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.driver"),
				getText("key.role.bc.admin"));
	}

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
		PageOption pageOption = new PageOption().setWidth(640).setMinWidth(320)
				.setHeight(500);
		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.put("readonly", readonly);

		} else {
			pageOption.put("readonly", false);
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
		Respond respondResource;
		respondResource = new Respond();
		respondResource.setAuthor(context.getUserHistory());
		respondResource.setFileDate(Calendar.getInstance());
		respondResource.setQuestionary(this.getE());

		try {
			if (topics != null && topics.length() > 0) {
				Answer answerResource;
				JSONArray jsons = new JSONArray(this.topics);
				JSONObject json;
				json = new JSONObject();
				JSONObject answerItem;
				// optionItemsValue = jsons.getString("optionItemsValue");
				// if (optionItemsValue != null && optionItemsValue.length() >
				// 0) {
				for (int n = 0; n < jsons.length(); n++) {
					json = jsons.getJSONObject(n);
					JSONArray answerItems = new JSONArray(
							json.getString("optionItemsValue"));
					for (int i = 0; i < answerItems.length(); i++) {
						answerItem = answerItems.getJSONObject(i);
						QuestionItem questionItem = new QuestionItem();
						questionItem.setId(answerItem.getLong("itemId"));
						answerResource = new Answer();
						// 选择题
						if (json.getInt("type") == Question.TYPE_RADIO
								|| json.getInt("type") == Question.TYPE_CHECKBOX) {
							QuestionItem q = new QuestionItem();
							q = this.questionItemService.load(answerItem
									.getLong("itemId"));
							answerResource.setRespond(respondResource);
							answerResource.setItem(questionItem);
							answerResource.setScore(q.getScore());
						}
						// 简答题
						if (json.getInt("type") == Question.TYPE_QA) {
							answerResource.setRespond(respondResource);
							answerResource.setItem(questionItem);
							answerResource.setScore(0);
							answerResource.setContent(answerItem
									.getString("subject"));
						}
						// 填空题
						if (json.getInt("type") == Question.TYPE_FILL_IN) {
							answerResource.setRespond(respondResource);
							answerResource.setItem(questionItem);
							answerResource.setScore(0);
							// answerResource.setContent(answerItem.getString("subject"));

						}

						answer.add(answerResource);
					}

				}
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

	@Override
	public String open() throws Exception {
		Questionary e = this.questionaryService.load(this.getId());
		this.setE(e);
		// 强制表单只读
		this.formPageOption = buildFormPageOption(false);
		// 初始化表单的其他配置
		this.initForm(false);
		this.afterOpen(e);
		boolean isExist = IsExisUser(e);
		if (isExist) {
			return "statistics";
		} else {
			return "formr";
		}
	}

	/**
	 * 用户是否答卷
	 * 
	 * @param e
	 * @return
	 */
	private boolean IsExisUser(Questionary e) {
		SystemContext context = this.getSystyemContext();
		Long userId = context.getUserHistory().getId();
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

	// 预览
	// -----------------------------------------------------------------
	public String preview() {
		Questionary e = questionaryService.load(this.getId());
		this.formPageOption = buildPreviewFormPageOption(true);
		this.setE(e);
		statusesValue = this.getBSStatuses();
		return "form";
	}

	protected PageOption buildPreviewFormPageOption(boolean editable) {
		PageOption pageOption = new PageOption().setWidth(640).setMinWidth(320)
				.setHeight(550);
		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.put("readonly", false);

		} else {
			pageOption.put("readonly", false);
		}
		// 添加按钮
		buildPreviewFormPageButtons(pageOption, editable);

		return pageOption;
	}

	protected void buildPreviewFormPageButtons(PageOption pageOption,
			boolean editable) {
		pageOption.addButton(new ButtonOption(getText("questionary.issue"),
				null, "bc.questionaryForm.issue"));

	}

	// -----------------------------------------
	// 发布
	public void issue() {
		SystemContext context = this.getSystyemContext();
		Questionary e = questionaryService.load(this.getId());
		// 设置发布人的信息
		e.setIssuer(context.getUserHistory());
		e.setIssueDate(Calendar.getInstance());
		e.setStatus(Questionary.STATUS_ISSUE);
		this.questionaryService.save(e);
	}

	// 归档
	public void archiving() {
		SystemContext context = this.getSystyemContext();
		Questionary e = questionaryService.load(this.getId());
		// 设置发布人的信息
		e.setPigeonholer(context.getUserHistory());
		e.setPigeonholeDate(Calendar.getInstance());
		e.setStatus(Questionary.STATUS_END);
		this.questionaryService.save(e);
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
	public String formatCompletionValue(String source, String configs) {
		Map<String, Object> args = new HashMap<String, Object>();
		List<String> markers = TemplateUtils.findMarkers(source);
		JSONArray configValues;
		try {
			configValues = new JSONArray(configs);
			JSONObject config;
			for (String marker : markers) {
				config = findConfig(configValues, marker);
				if (config != null) {
					args.put(
							marker,
							"<input type='text' name='"
									+ null
									+ "' value='"
									+ config.getString("value")
									+ "' style='width:45px;border-width: 0 0 1px 0;text-align: center;' class='ui-widget-content'/>");
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return TemplateUtils.format(source, args);
	}

	private JSONObject findConfig(JSONArray configValues, String marker) {
		JSONObject config;
		for (int i = 0; i < configValues.length(); i++) {
			try {
				config = configValues.getJSONObject(i);
				if (marker.equals(config.getString("key")))
					return config;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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