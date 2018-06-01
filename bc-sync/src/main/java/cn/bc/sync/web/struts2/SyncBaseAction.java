/**
 *
 */
package cn.bc.sync.web.struts2;

import cn.bc.core.util.StringUtils;
import cn.bc.sync.service.SyncBaseService;
import cn.bc.web.ui.json.Json;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 同步的公共处理方法Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SyncBaseAction extends ActionSupport {
  private static final long serialVersionUID = 1L;
  private SyncBaseService syncBaseService;
  public String syncTo;// 生成处理单的表名
  public int toStatus;// 标记为的状态值
  public Long id;
  public String ids;
  public Json json;

  @Autowired
  public void setSyncBaseService(SyncBaseService syncBaseService) {
    this.syncBaseService = syncBaseService;
  }

  /**
   * 判断是否已经生成过交通违法单
   *
   * @return
   * @throws Exception
   */
  public String hadGenerate() throws Exception {
    boolean hadGenerate = this.syncBaseService.hadGenerate(this.syncTo,
      this.id);
    json = new Json();
    json.put("success", !hadGenerate);
    if (hadGenerate) {
      json.put("msg", getText("sync.hadGenerate"));
    }
    return "json";
  }

  /**
   * 修改同步记录的处理状态
   *
   * @return
   * @throws Exception
   */
  public String changeStatus() throws Exception {
    Long[] _ids = StringUtils.stringArray2LongArray(this.ids.split(","));
    int count = this.syncBaseService.updateStatus(_ids, this.toStatus);
    json = new Json();
    json.put("success", true);
    json.put(
      "msg",
      getText("sync.changeStatus",
        new String[]{String.valueOf(count)}));
    return "json";
  }
}
