/**
 * 
 */
package cn.bc.log.web.struts2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContext;
import cn.bc.log.domain.Syslog;
import cn.bc.log.service.SyslogService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.CrudAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.html.toolbar.ToolbarSearchButton;
import cn.bc.web.util.WebUtils;

/**
 * 系统日志Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SyslogAction extends CrudAction<Long, Syslog> implements
		SessionAware {
	private static Log logger = LogFactory.getLog(SyslogAction.class);
	private static final long serialVersionUID = 1L;
	public boolean my = false;

	// private SyslogService syslogService;

	@Autowired
	public void setSyslogService(SyslogService syslogService) {
		// this.syslogService = syslogService;
		this.setCrudService(syslogService);
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("subject");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}

	@Override
	protected Condition getSpecalCondition() {
		if (!my) {// 查看所有用户的日志信息
			return null;
		} else {// 仅查看自己的日志信息
			ActorHistory curUser = (ActorHistory) ((SystemContext) this.getContext())
					.getUserHistory();
			return new EqualsCondition("author.id", curUser.getId());
		}
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(600).setMinHeight(300);
	}

	@Override
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();

		// 查看按钮
		tb.addButton(new ToolbarButton().setIcon("ui-icon-check")
				.setText(getText("label.check"))
				.setClick("bc.syslogList.checkWork"));

		// 搜索按钮
		ToolbarSearchButton sb = new ToolbarSearchButton();
		sb.setAction("search").setTitle(getText("title.click2search"));
		tb.addButton(sb);

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "subject", "author.name", "author.upperName", "author.unitName",
				"clientIp", "clientInfo", "serverIp" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("fileDate", getText("syslog.createDate"),
				150).setSortable(true).setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
		columns.add(new TextColumn("type", getText("syslog.type"), 80)
				.setSortable(true).setValueFormater(
						new SyslogTypeFormater(getEntityStatuses())));
		columns.add(new TextColumn("clientIp", getText("syslog.clientIp"), 120)
				.setSortable(true));
		columns.add(new TextColumn("author.name", getText("syslog.userName"), 80)
				.setSortable(true));
		columns.add(new TextColumn("author.unitName", getText("syslog.unitName"), 100)
				.setSortable(true));
		columns.add(new TextColumn("author.upperName", getText("syslog.departName"),
				100).setSortable(true));
		if (!my)
			columns.add(new TextColumn("serverIp", getText("syslog.serverIp"),
					120).setSortable(true));
		columns.add(new TextColumn("clientInfo", getText("syslog.clientInfo"))
				.setSortable(true).setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String getJs() {
		return contextPath + "/bc/log/syslog/list.js";
	}

	/**
	 * 获取系统日志分类值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityStatuses() {
		Map<String, String> types = new HashMap<String, String>();
		types = new HashMap<String, String>();
		types.put(String.valueOf(Syslog.TYPE_LOGIN),
				getText("syslog.type.login"));
		types.put(String.valueOf(Syslog.TYPE_LOGOUT),
				getText("syslog.type.logout"));
		types.put(String.valueOf(Syslog.TYPE_LOGOUT2),
				getText("syslog.type.logout2"));
		return types;
	}

	@Override
	protected String getPageTitle() {
		if (my)
			return this.getText(StringUtils.uncapitalize(getEntityConfigName())
					+ ".title.my");
		else
			return this.getText(StringUtils.uncapitalize(getEntityConfigName())
					+ ".title");
	}

	// 记录登陆日志
	public static Syslog buildSyslog(Calendar now, Integer type,
			ActorHistory user, String subject, boolean traceClientMachine,
			HttpServletRequest request) {
		Syslog log = new Syslog();
		log.setType(type);
		log.setAuthor(user);
		log.setFileDate(now);
		log.setSubject(subject);

		// 服务器信息
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			log.setServerIp(localhost.getHostAddress());
			log.setServerName(localhost.getHostName());
		} catch (UnknownHostException e) {
			log.setServerIp("UnknownHost");
			log.setServerName("UnknownHost");
		}

		if (request != null) {
			log.setServerInfo(request.getRequestURL().toString());

			// 客户端信息
			log.setClientInfo(request.getHeader("User-Agent"));
			String key = "X-Forwarded-For";// 如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串Ip值,其中第一个非unknown的有效IP字符串是真正的用户端的真实IP
			String clientIp = request.getHeader(key);
			if (clientIp == null || clientIp.length() == 0
					|| "unknown".equalsIgnoreCase(clientIp)) {
				key = "Proxy-Client-IP";
				clientIp = request.getHeader(key);
			}
			if (clientIp == null || clientIp.length() == 0
					|| "unknown".equalsIgnoreCase(clientIp)) {
				key = "WL-Proxy-Client-IP"; // Weblogic集群获取客户端IP
				clientIp = request.getHeader(key);
			}
			if (clientIp == null || clientIp.length() == 0
					|| "unknown".equalsIgnoreCase(clientIp)) {
				key = "RemoteAddr";
				clientIp = request.getRemoteAddr();// 获得客户端电脑的名字，若失败则返回客户端电脑的ip地址
			}
			log.setClientIp(clientIp);
			if (traceClientMachine) {// name+ip获取方式+mac
				logger.info("start traceClientMachine...:clientIp=" + clientIp);
				if ("127.0.0.1".equals(clientIp)
						|| "localhost".equals(clientIp)) {
					// 排除本机的解析(会导致死掉)
					log.setClientName(clientIp + "|" + key);
					logger.info("skip traceClientMachine because local machine");
				} else {
					log.setClientName(request.getRemoteHost());// 客户端与服务器不同ip段时，获取计算机名可能会太耗时(视网络配置)
					try {
						// 获取mac地址
						String mac = WebUtils.getMac(clientIp);
						log.setClientName(log.getClientName() + "|" + key + "|"
								+ mac);
					} catch (Exception e) {
						logger.info(e.getMessage(), e);
					}
				}
				logger.info("finished traceClientMachine");
			} else {
				log.setClientName(clientIp + "|" + key);
			}
		}
		return log;
	}
}
