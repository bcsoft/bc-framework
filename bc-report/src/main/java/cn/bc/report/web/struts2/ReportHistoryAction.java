package cn.bc.report.web.struts2;

import cn.bc.identity.web.SystemContext;
import cn.bc.report.domain.ReportHistory;
import cn.bc.report.service.ReportHistoryService;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.PageOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 历史报表表单Action
 *
 * @author lbj
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ReportHistoryAction extends EntityAction<Long, ReportHistory> {
  private static final long serialVersionUID = 1L;
  public ReportHistoryService reportHistoryService;

  @Autowired
  public void setReportHistoryService(ReportHistoryService reportHistoryService) {
    this.reportHistoryService = reportHistoryService;
    this.setCrudService(reportHistoryService);
  }

  @Override
  public boolean isReadonly() {
    SystemContext context = (SystemContext) this.getContext();
    // 配置权限：报表管理员，历史报表管理员、超级管理员
    return !context.hasAnyRole(getText("key.role.bc.report"),
      getText("key.role.bc.report.History"),
      getText("key.role.bc.admin"));
  }

  @Override
  protected PageOption buildPageOption(boolean editable) {
    return super.buildPageOption(editable).setWidth(500)
      .setMinHeight(200).setMinWidth(300).setHeight(201);
  }
}