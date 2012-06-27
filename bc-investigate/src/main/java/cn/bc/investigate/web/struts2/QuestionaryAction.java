/**
 * 
 */
package cn.bc.investigate.web.struts2;

import java.util.HashMap;
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
import cn.bc.investigate.domain.Question;
import cn.bc.investigate.domain.QuestionItem;
import cn.bc.investigate.domain.Questionary;
import cn.bc.investigate.domain.Respond;
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
public class QuestionaryAction extends FileEntityAction<Long, Questionary> {

	private static final long serialVersionUID = 1L;
	private QuestionaryService questionaryService;
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
			pageOption.put("readonly", false);

		} else {
			pageOption.put("readonly", false);
		}
		// 添加按钮
		buildFormPageButtons(pageOption, editable);

		return pageOption;
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		pageOption.addButton(new ButtonOption(getText("label.save"), null,
				"bc.questionaryForm.save"));
		pageOption.addButton(new ButtonOption(getText("questionary.preview"),
				null, "bc.questionaryForm.preview"));
		pageOption.addButton(new ButtonOption(getText("questionary.issue"),
				null, null));

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
		// 插入题目
		try {
			Set<Question> question = null;
			Set<QuestionItem> questionItem = null;
			if (this.topics != null && this.topics.length() > 0) {
				question = new LinkedHashSet<Question>();
				Question questionResource;
				JSONArray jsons = new JSONArray(this.topics);
				JSONObject json;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					questionResource = new Question();
					if (json.has("id"))
						questionResource.setId(json.getLong("id"));
					questionResource.setOrderNo(Integer.parseInt(json
							.getString("orderNo")));
					questionResource.setRequired(Boolean.valueOf(json
							.getString("required")));
					// 题型
					int type = Integer.parseInt(json.getString("type"));
					questionResource.setType(type);
					// 填空题才有全选方得分的概念
					if (type == 1) {
						questionResource.setSeperateScore(Boolean.valueOf(json
								.getString("seperateScore")));
					}
					// questionResource.setSeperateScore(true);
					// 布局
					// 选择题才有布局
					if (type == 0 || type == 1) {
						questionResource.setConfig("{layout_orientation:"
								+ json.getString("config") + "}");
					}
					// questionResource.setConfig(null);
					questionResource.setSubject(json.getString("subject"));
					questionResource.setScore(json.getInt("score"));
					questionResource.setQuestionary(this.getE());
					// 问题选项
					optionItemsValue = json.getString("optionItemsValue");
					try {
						if (optionItemsValue != null
								&& optionItemsValue.length() > 0) {
							questionItem = new LinkedHashSet<QuestionItem>();
							QuestionItem questionItemResource;
							JSONArray items = new JSONArray(
									json.getString("optionItemsValue"));
							JSONObject item;
							for (int n = 0; n < items.length(); n++) {
								item = items.getJSONObject(n);
								questionItemResource = new QuestionItem();
								if (item.has("id"))
									questionResource.setId(item.getLong("id"));
								// 填空题的标准答案配置
								if (type == 2) {
									questionItemResource.setConfig(item
											.getString("config"));
								}
								// questionItemResource.setConfig(null);
								questionItemResource.setOrderNo(n);
								questionItemResource.setScore(0);
								// 选择题
								if (type == 1 || type == 0) {
									questionItemResource
											.setStandard(Boolean.valueOf(item
													.getString("standard")));
									questionItemResource.setScore(item
											.getInt("score"));
								}

								questionItemResource.setSubject(item
										.getString("subject"));
								questionItemResource
										.setQuestion(questionResource);
								questionItem.add(questionItemResource);
							}

						}
						if (questionResource.getItems() != null) {
							questionResource.getItems().clear();
							questionResource.getItems().addAll(questionItem);
						} else {
							questionResource.setItems(questionItem);
						}
					} catch (JSONException e) {
						logger.error(e.getMessage(), e);
						try {
							throw e;
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}

					question.add(questionResource);

				}
			}
			if (this.getE().getQuestions() != null) {
				this.getE().getQuestions().clear();
				this.getE().getQuestions().addAll(question);

			} else {
				this.getE().setQuestions(question);
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		this.getE().setResponds(new LinkedHashSet<Respond>());
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
				null, null));

	}

	// -----------------------------------------
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