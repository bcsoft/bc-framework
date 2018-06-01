/**
 *
 */
package cn.bc.identity.web.struts2;

import cn.bc.BCConstants;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.service.GroupService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 岗位Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class GroupAction extends AbstractActorAction {
  private static final long serialVersionUID = 1L;
  private GroupService groupService;

  @Autowired
  public void setGroupService(GroupService groupService) {
    this.groupService = groupService;
  }

  protected String getEntityConfigName() {
    return "Group";
  }

  @Override
  protected void afterCreate(Actor entity) {
    super.afterCreate(entity);
    this.getE().setStatus(BCConstants.STATUS_ENABLED);
    this.getE().setType(Actor.TYPE_GROUP);
    this.getE().setUid(this.getIdGeneratorService().next("group"));
  }

  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    return super.buildFormPageOption(editable).setWidth(618);
  }

  @Override
  protected ButtonOption getDefaultSaveButtonOption() {
    return super.getDefaultSaveButtonOption().setAction(null)
      .setClick("bc.groupForm.save");
  }

  protected Integer[] getBelongTypes() {
    return new Integer[]{Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT};
  }

  public String assignUserIds;// 分配的用户id，多个id用逗号连接
  public List<Actor> ownedUsers;// 已分配的用户

  @Override
  public String save() throws Exception {
    JSONObject json = new JSONObject();

    try {
      // 编码唯一性检测
      if (!this.getActorService().isUnique(this.getE().getId(),
        this.getE().getCode(), this.getE().getType())) {
        json.put("success", false);
        json.put("id", this.getE().getId());
        json.put("msg", "编码已被其它岗位占用，请重新修改！");
      } else {
        // 处理分配的角色
        dealRoles4Save();

        // 处理分配的用户
        Long[] userIds = null;
        if (this.assignUserIds != null
          && this.assignUserIds.length() > 0) {
          String[] uIds = this.assignUserIds.split(",");
          userIds = new Long[uIds.length];
          for (int i = 0; i < uIds.length; i++) {
            userIds[i] = new Long(uIds[i]);
          }
        }

        // 保存
        this.groupService.save(this.getE(), this.buildBelongIds(),
          userIds);

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

  @Override
  protected void afterEdit(Actor entity) {
    super.afterEdit(entity);

    // 加载已分配的用户
    this.ownedUsers = this.groupService.findFollower(this.getId(),
      new Integer[]{ActorRelation.TYPE_BELONG},
      new Integer[]{Actor.TYPE_USER});
  }
}
