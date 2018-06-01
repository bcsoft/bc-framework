/**
 *
 */
package cn.bc.identity.web.struts2;

import cn.bc.BCConstants;
import cn.bc.identity.domain.Actor;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 单位Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class UnitAction extends AbstractActorAction {
  private static final long serialVersionUID = 1L;

  protected String getEntityConfigName() {
    return "Unit";
  }

  @Override
  protected void afterCreate(Actor entity) {
    super.afterCreate(entity);
    this.getE().setType(Actor.TYPE_UNIT);
    this.getE().setUid(this.getIdGeneratorService().next("unit"));
  }

  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    return super.buildFormPageOption(editable).setWidth(618);
  }

  @Override
  protected ButtonOption getDefaultSaveButtonOption() {
    return super.getDefaultSaveButtonOption().setAction(null)
      .setClick("bc.unitForm.save").setId("bcSaveDlgButton");
  }

  protected Integer[] getBelongTypes() {
    return new Integer[]{Actor.TYPE_UNIT};
  }

  /**
   * 查找分公司
   *
   * @return [{"id":xx, "name":xx, "code":xx}, ...]
   */
  public String findBranchOffice() throws JSONException {
    JSONArray branchOffice = new JSONArray();
    List<Actor> unitList = this.getActorService().findAllUnit(BCConstants.STATUS_ENABLED);

    // 迭代获取分公司列表
    for (Actor actor : unitList) {
      if (actor.getName().indexOf("分公司") != -1) {
        JSONObject j = new JSONObject();
        j.put("id", actor.getId());
        j.put("name", actor.getName());
        j.put("code", actor.getCode());
        branchOffice.put(j);
      }
    }

    JSONObject json = new JSONObject();
    json.put("success", branchOffice.length() > 0);
    json.put("branchOffice", branchOffice);
    this.json = json.toString();
    return "json";
  }
}
