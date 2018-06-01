package cn.bc.cache.web.struts2;

import cn.bc.cache.service.CacheService;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.Icon;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 缓存管理视图Action
 * <p>
 * Created by Action on 2015/1/23.
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CacheViewAction extends ViewAction {
  private final static Log logger = LogFactory.getLog(CacheViewAction.class);
  private CacheService cacheService;

  /**
   * 缓存容器名称
   */
  public String containerName;
  /**
   * 缓存key
   */
  public String cacheKey;

  @Autowired
  public void setCacheService(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  @Override
  protected List findList() {
    return this.cacheService.findSimilarCaches(this.search, this.search);
  }

  @Override
  protected SqlObject getSqlObject() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected String getGridRowLabelExpression() {
    return null;
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"containerName", "cacheKey"};
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar toolbar = new Toolbar();
    // 删除所有缓存
    toolbar.addButton(new ToolbarButton()
      .setText("清空全部")
      .setClick("bc.cache.view.deleteAll"));
    // 搜索按钮
    toolbar.addButton(this.getDefaultSearchToolbarButton());
    return toolbar;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<>();
    columns.add(new IdColumn4MapKey("id", "id"));
    columns.add(new TextColumn4MapKey("containerName", "containerName", getText("cache.containerName"), 150)
      .setSortable(true)
      .setUseTitleFromLabel(true)
      .setValueFormater(new AbstractFormater<String>() {
        @Override
        public String format(Object context, Object value) {
          Map<String, Object> map = (Map<String, Object>) context;
          String containerName = (String) map.get("containerName");
          return containerName != null
            ? delIcon("清空该容器的所有缓存", "bc.cache.view.delContainer").wrap() + containerName
            : null;
        }

        @Override
        public String getExportText(Object context, Object value) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) context;
          return (String) map.get("containerName");
        }
      }));
    columns.add(new TextColumn4MapKey("cacheKey", "cacheKey", getText("cache.cacheKey"))
      .setSortable(true)
      .setUseTitleFromLabel(true)
      .setValueFormater(new AbstractFormater<String>() {
        @Override
        public String format(Object context, Object value) {
          Map<String, Object> map = (Map<String, Object>) context;
          String cacheKey = (String) map.get("cacheKey");
          return cacheKey != null
            ? delIcon("删除该缓存", "bc.cache.view.delCacheKey").wrap() + cacheKey
            : null;
        }

        @Override
        public String getExportText(Object context, Object value) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) context;
          return (String) map.get("cacheKey");
        }
      }));
    return columns;
  }

  private Icon delIcon(String title, String event) {
    Icon icon = new Icon();
    icon.setClazz("ui-icon ui-icon-trash");
    icon.setTitle(title);// 鼠标提示信息
    icon.setClick(event);// 点击函数
    return icon;
  }

  // 视图双击的方法
  @Override
  protected String getGridDblRowMethod() {
    return null;
  }

  @Override
  protected String getHtmlPageTitle() {
    return this.getText("cache.view.title");
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(700).setHeight(441);
  }

  @Override
  protected String getHtmlPageNamespace() {
    return this.getContextPath() + "/bc/cache";
  }

  @Override
  protected String getHtmlPageJs() {
    return this.getHtmlPageNamespace() + "/view.js";
  }

  @Override
  protected String getFormActionName() {
    return CacheViewAction.class.getName();
  }

  // =====| 删除操作 |=====

  /**
   * 删除所有缓存
   *
   * @return
   */
  public String deleteAll() throws JSONException {
    JSONObject json = new JSONObject();
    try {
      this.cacheService.deleteExistCaches();
      json.put("success", true);
      json.put("msg", getText("cache.msg.delete.success"));
    } catch (Exception e) {
      json.put("msg", e.getMessage());
      logger.error(e.getMessage());
    }
    this.json = json.toString();
    return "json";
  }

  /**
   * 清空容器的缓存
   *
   * @return
   */
  public String delContainer() throws JSONException {
    JSONObject json = new JSONObject();
    try {
      this.cacheService.deleteCachesInContainer(this.containerName);
      json.put("success", true);
      json.put("msg", getText("cache.msg.delete.success"));
    } catch (Exception e) {
      json.put("msg", e.getMessage());
      logger.error(e.getMessage());
    }
    this.json = json.toString();
    return "json";
  }

  /**
   * 删除一个缓存
   *
   * @return
   */
  public String delCacheKey() throws JSONException {
    JSONObject json = new JSONObject();
    try {
      this.cacheService.deleteCache(this.containerName, this.cacheKey);
      json.put("success", true);
      json.put("msg", getText("cache.msg.delete.success"));
    } catch (Exception e) {
      json.put("msg", e.getMessage());
      logger.error(e.getMessage());
    }
    this.json = json.toString();
    return "json";
  }
}
