/**
 *
 */
package cn.bc.investigate.web.struts2;

import cn.bc.core.exception.PermissionDeniedException;
import cn.bc.core.util.TemplateUtils;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.investigate.domain.*;
import cn.bc.investigate.service.QuestionaryService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 调查问卷的Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class QuestionaryAction extends FileEntityAction<Long, Questionary> {

  private static final long serialVersionUID = 1L;
  private QuestionaryService questionaryService;
  public String assignUserIds;// 分配的用户id，多个id用逗号连接
  public Set<Actor> ownedUsers;// 已分配的用户
  public String topics;// 问卷中题目的json字符串
  public String optionItemsValue;// 问题选项
  public Map<String, String> statusesValue;
  private ActorService actorService;

  @Override
  public boolean isReadonly() {
    // 网上考试管理员或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole(getText("key.role.bc.question.exam"),
      getText("key.role.bc.admin"));
  }

  @Autowired
  public void setActorService(ActorService actorService) {
    this.actorService = actorService;
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
    PageOption pageOption = new PageOption().setWidth(645).setMinWidth(320)
      .setHeight(500);
    // 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
    boolean readonly = this.isReadonly();
    if (editable && !readonly) {
      pageOption.put("readonly", readonly);

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
    // 归档
    if (this.getE().getStatus() == Questionary.STATUS_ISSUE) {
      pageOption.addButton(new ButtonOption(
        getText("questionary.archiving"), null,
        "bc.questionaryForm.checkIsGrade"));
    }

  }

  @Override
  protected Questionary createEntity() {
    Questionary questionary = super.createEntity();
    questionary.setStatus(Questionary.STATUS_DRAFT);
    return questionary;
  }

  @Override
  protected void afterCreate(Questionary entity) {
    super.afterCreate(entity);
    this.getE().setType(Questionary.TYPE_PAPER);
    this.getE().setPermitted(false);
    this.getE().setIssuer(null);
  }

  @Override
  protected void beforeSave(Questionary entity) {
    super.beforeSave(entity);
    // 插入题目
    Set<Question> question = null;
    Set<QuestionItem> questionItem = null;
    if (this.topics != null && this.topics.length() > 0) {
      question = new LinkedHashSet<>();
      Question questionResource;
      JSONArray jsons = new JSONArray(this.topics);
      JSONObject json;
      for (int i = 0; i < jsons.length(); i++) {
        json = jsons.getJSONObject(i);
        questionResource = new Question();
        if (json.has("id")) questionResource.setId(json.getLong("id"));
        questionResource.setOrderNo(Integer.parseInt(json.getString("orderNo")));
        questionResource.setRequired(json.getBoolean("required"));
        // 题型
        int type = Integer.parseInt(json.getString("type"));
        questionResource.setType(type);
        // 多选题才有全选方得分的概念
        if (type == Question.TYPE_CHECKBOX) {
          questionResource.setSeperateScore(json.getBoolean("seperateScore"));
        }
        // 问答题是否需要评分
        if (type == Question.TYPE_QA) {
          questionResource.setGrade(json.getBoolean("grade"));
        }
        // questionResource.setSeperateScore(true);
        // 布局
        // 选择题才有布局
        // if (type == 0 || type == 1) {
        // questionResource.setConfig("{layout_orientation:"
        // + json.getString("config") + "}");
        // }
        // questionResource.setConfig(null);
        questionResource.setSubject(json.getString("subject"));
        questionResource.setScore(json.getInt("score"));
        questionResource.setQuestionary(this.getE());
        // 问题选项
        optionItemsValue = json.getString("optionItemsValue");
        try {
          if (optionItemsValue != null && optionItemsValue.length() > 0) {
            questionItem = new LinkedHashSet<>();
            QuestionItem questionItemResource;
            JSONArray items = new JSONArray(json.getString("optionItemsValue"));
            JSONObject item;
            for (int n = 0; n < items.length(); n++) {
              item = items.getJSONObject(n);
              questionItemResource = new QuestionItem();
              if (item.has("id")) questionResource.setId(item.getLong("id"));
              // 填空题的标准答案配置
              if (type == 2) {
                questionItemResource.setConfig(item.getString("config"));
              }
              // questionItemResource.setConfig(null);
              questionItemResource.setOrderNo(n);
              questionItemResource.setScore(0);
              // 选择题
              if (type == 1 || type == 0) {
                questionItemResource.setStandard(item.getBoolean("standard"));
                questionItemResource.setScore(item.getInt("score"));
              }

              questionItemResource.setSubject(item.getString("subject"));
              questionItemResource.setQuestion(questionResource);
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
    this.getE().setResponds(new LinkedHashSet<Respond>());
    // 保存用户

    // 处理分配的用户
    Long[] userIds = null;
    if (this.assignUserIds != null && this.assignUserIds.length() > 0) {
      String[] uIds = this.assignUserIds.split(",");
      userIds = new Long[uIds.length];
      for (int i = 0; i < uIds.length; i++) {
        userIds[i] = new Long(uIds[i]);
      }
    }

    if (userIds != null && userIds.length > 0) {
      Set<Actor> users = null;
      Actor user = null;
      for (int i = 0; i < userIds.length; i++) {
        if (i == 0) {
          users = new HashSet<Actor>();
        }
        user = this.actorService.load(userIds[i]);
        users.add(user);
      }

      if (this.getE().getActors() != null) {
        this.getE().getActors().clear();
        this.getE().getActors().addAll(users);
      } else {
        this.getE().setActors(users);
      }
    }
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
    this.pageOption = buildPreviewFormPageOption(true);
    this.setE(e);
    statusesValue = this.getBSStatuses();
    return "form";
  }

  protected PageOption buildPreviewFormPageOption(boolean editable) {
    PageOption pageOption = new PageOption().setWidth(645).setMinWidth(320)
      .setHeight(550);
    // 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
    boolean readonly = this.isReadonly();
    if (editable && !readonly) {
      pageOption.put("readonly", false);

    } else {
      pageOption.put("readonly", true);
    }
    // 添加按钮
    // buildPreviewFormPageButtons(pageOption, editable);

    return pageOption;
  }

  // 发布按钮
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
        e.printStackTrace();
      }
    }
    return null;
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

  // 归档前检查该试卷用户的考卷是否全部评分
  public String checkIsGrade() {
    Json json = new Json();
    Questionary q = this.questionaryService.load(this.getId());
    Set<Respond> respond = q.getResponds();
    Iterator<Respond> r = respond.iterator();
    while (r.hasNext()) {
      Respond oneRespond = r.next();
      if (oneRespond.isGrade()) {
        json.put("success", false);
        json.put("msg", "该试卷存在需要评分的答卷,如果归档将无法继续对答卷进行评分,是否归档？");
        this.json = json.toString();
        return "json";
      }
    }
    json.put("success", true);
    this.json = json.toString();
    return "json";
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

  // 提示只能删除未作答过的试卷
  @Override
  protected String getDeleteExceptionMsg(Exception e) {
    //
    if (e instanceof PermissionDeniedException) {
      return "只能删除删除未作答过的试卷！";
    }
    return super.getDeleteExceptionMsg(e);
  }

}