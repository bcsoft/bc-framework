/**
 *
 */
package cn.bc.identity.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.domain.*;
import cn.bc.identity.service.DutyService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.service.UserService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class UserAction extends AbstractActorAction {
  private static final long serialVersionUID = 1L;
  // private static final Log logger = LogFactory.getLog(UserAction.class);
  private UserService userService;
  private DutyService dutyService;
  private IdGeneratorService idGeneratorService;

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
    this.setActorService(userService);
  }

  @Autowired
  public void setDutyService(DutyService dutyService) {
    this.dutyService = dutyService;
  }

  @Autowired
  public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
    this.idGeneratorService = idGeneratorService;
  }

  protected String getEntityConfigName() {
    return "User";
  }

  @Override
  protected void afterCreate(Actor entity) {
    super.afterCreate(entity);
    this.getE().setType(Actor.TYPE_USER);
    this.getE().setStatus(BCConstants.STATUS_ENABLED);
    this.getE().setUid(this.idGeneratorService.next("user"));

    // 初始化用户的扩展信息
    ActorDetail detail = new ActorDetail();
    detail.setCreateDate(Calendar.getInstance());
    detail.setSex(ActorDetail.SEX_NONE);
    this.getE().setDetail(detail);

    this.ownedGroups = new ArrayList<Actor>();
  }

  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    return super.buildFormPageOption(editable).setWidth(665).setHeight(600);
  }


  @Override
  protected void buildPageButtons(PageOption pageOption, boolean editable) {
    super.buildPageButtons(pageOption, editable);

    SystemContext context = (SystemContext) this.getContext();
    if (context.hasAnyRole(getText("key.role.bc.user.copy.role")))
      pageOption.addButton(new ButtonOption(getText("user.copyUserRole"), null, "bc.userForm.copyUserRole"));
  }

  @Override
  protected ButtonOption getDefaultSaveButtonOption() {
    return super.getDefaultSaveButtonOption().setAction(null)
      .setClick("bc.userForm.save");
  }

  @Override
  protected String getJs() {
    String cp = ServletActionContext.getRequest().getContextPath();
    return cp + "/bc/identity/user/list.js";
  }

  protected Integer[] getBelongTypes() {
    return new Integer[]{Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT};
  }

  @Override
  public String save() throws Exception {
    JSONObject json = new JSONObject();

    try {
      // 编码的唯一性检测
      if (!this.getActorService().isUnique(this.getE().getId(),
        this.getE().getCode(), this.getE().getType())) {
        json.put("success", false);
        json.put("id", this.getE().getId());
        json.put("msg", "登录名已被其他用户占用，请重新修改！");
      } else {
        // 处理分配的角色
        dealRoles4Save();

        // 处理分派的岗位
        Long[] groupIds = null;
        if (this.assignGroupIds != null
          && this.assignGroupIds.length() > 0) {
          String[] gids = this.assignGroupIds.split(",");
          groupIds = new Long[gids.length];
          for (int i = 0; i < gids.length; i++) {
            groupIds[i] = new Long(gids[i]);
          }
        }

        // 保存
        this.userService.save(this.getE(), this.buildBelongIds(),
          groupIds);

        json.put("success", true);
        json.put("id", this.getE().getId());
        json.put("msg", this.getText("form.save.success"));
      }
    } catch (Exception e) {
      logger.warn(e.getMessage(), e);
      json.put("success", false);
      json.put("msg", e.getMessage());
    }

    this.json = json.toString();
    return "json";
  }

  public List<Duty> duties;// 可选的职务列表
  public List<Actor> ownedGroups;// 已拥有的岗位
  public Set<Role> inheritRolesFromGroup;// 从岗位间接获取的角色信息
  public String assignGroupIds;// 分派的岗位id，多个id用逗号连接

  @Override
  protected void initForm(boolean editable) throws Exception {
    super.initForm(editable);

    // 表单可选项的加载
    initSelects();

    // 新建时无需执行下面的初始化
    if (this.getE().isNew())
      return;

    // 加载已拥有的岗位信息
    this.ownedGroups = this.userService.findMaster(this.getId(),
      new Integer[]{ActorRelation.TYPE_BELONG},
      new Integer[]{Actor.TYPE_GROUP});

    // 加载从岗位间接获取的角色信息
    this.inheritRolesFromGroup = new HashSet<Role>();
    for (Actor g : ownedGroups) {
      inheritRolesFromGroup.addAll(g.getRoles());
    }
  }

  // 表单可选项的加载
  public void initSelects() {
    // 加载可选的职务列表
    this.duties = this.dutyService.createQuery()
      .condition(new OrderCondition("code", Direction.Asc)).list();
  }

  public Long actorId;
  public String findGroupsAndRoles() {
    JSONObject json = new JSONObject();
    try {
      Actor actor = userService.load(actorId);
      List<Map<String, Object>> roles = actor.getRoles().stream().map(r -> new HashMap<String, Object>() {{
        put("id", r.getId());
        put("name", r.getName());
      }}).collect(Collectors.toList());
      // 加载已拥有的岗位信息
      List<Actor> groupsData = this.userService.findMaster(actorId,
        new Integer[]{ActorRelation.TYPE_BELONG},
        new Integer[]{Actor.TYPE_GROUP});
      List<Map<String, Object>> groups = groupsData.stream().map(r -> new HashMap<String, Object>() {{
        put("id", r.getId());
        put("name", r.getName());
      }}).collect(Collectors.toList());

      // 加载从岗位间接获取的角色信息
      List<Map<String, Object>> inheritRolesFromGroups = groupsData.stream()
        .flatMap(ir -> ir.getRoles().stream().map(r -> new HashMap<String, Object>() {{
          put("id", r.getId());
          put("name", r.getName());
        }})).collect(Collectors.toList());

      json.put("roles", roles);
      json.put("groups", groups);
      json.put("inheritRolesFromGroups", inheritRolesFromGroups);
    } catch (Exception e) {
      logger.warn(e.getMessage(), e);
      json.put("success", false);
      json.put("msg", e.getMessage());
    }
    this.json = json.toString();
    return "json";
  }
}
