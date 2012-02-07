/**
 * 
 */
package cn.bc.log.web.struts2;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.log.domain.Syslog;
import cn.bc.log.service.SyslogService;
import cn.bc.web.struts2.EntityAction;

/**
 * 系统日志表单Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SyslogAction extends EntityAction<Long, Syslog> implements
		SessionAware {
	//private static Log logger = LogFactory.getLog(SyslogAction.class);
	private static final long serialVersionUID = 1L;
	public boolean my = false;

	@Autowired
	public void setSyslogService(SyslogService syslogService) {
		this.setCrudService(syslogService);
	}

//	// 记录登陆日志
//	public static Syslog buildSyslog(Calendar now, Integer type,
//			ActorHistory user, String subject, boolean traceClientMachine,
//			HttpServletRequest request) {
//		Syslog log = new Syslog();
//		log.setType(type);
//		log.setAuthor(user);
//		log.setFileDate(now);
//		log.setSubject(subject);
//
//		// 服务器信息
//		InetAddress localhost;
//		try {
//			localhost = InetAddress.getLocalHost();
//			log.setServerIp(localhost.getHostAddress());
//			log.setServerName(localhost.getHostName());
//		} catch (UnknownHostException e) {
//			log.setServerIp("UnknownHost");
//			log.setServerName("UnknownHost");
//		}
//
//		if (request != null) {
//			log.setServerInfo(request.getRequestURL().toString());
//			log.setSid(request.getSession().getId());
//
//			// 客户端信息
//			log.setClientInfo(request.getHeader("User-Agent"));
//			String key = "X-Forwarded-For";// 如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串Ip值,其中第一个非unknown的有效IP字符串是真正的用户端的真实IP
//			String clientIp = request.getHeader(key);
//			if (clientIp == null || clientIp.length() == 0
//					|| "unknown".equalsIgnoreCase(clientIp)) {
//				key = "Proxy-Client-IP";
//				clientIp = request.getHeader(key);
//			}
//			if (clientIp == null || clientIp.length() == 0
//					|| "unknown".equalsIgnoreCase(clientIp)) {
//				key = "WL-Proxy-Client-IP"; // Weblogic集群获取客户端IP
//				clientIp = request.getHeader(key);
//			}
//			if (clientIp == null || clientIp.length() == 0
//					|| "unknown".equalsIgnoreCase(clientIp)) {
//				key = "RemoteAddr";
//				clientIp = request.getRemoteAddr();// 获得客户端电脑的名字，若失败则返回客户端电脑的ip地址
//			}
//			log.setClientIp(clientIp);
//			if (traceClientMachine) {// name+ip获取方式+mac
//				logger.info("start traceClientMachine...:clientIp=" + clientIp);
//				if ("127.0.0.1".equals(clientIp)
//						|| "localhost".equals(clientIp)) {
//					// 排除本机的解析(会导致死掉)
//					log.setClientName(clientIp + "|" + key);
//					logger.info("skip traceClientMachine because local machine");
//				} else {
//					log.setClientName(request.getRemoteHost());// 客户端与服务器不同ip段时，获取计算机名可能会太耗时(视网络配置)
//					try {
//						// 获取mac地址
//						String mac = WebUtils.getMac(clientIp);
//						log.setClientName(log.getClientName() + "|" + key + "|"
//								+ mac);
//					} catch (Exception e) {
//						logger.info(e.getMessage(), e);
//					}
//				}
//				logger.info("finished traceClientMachine");
//			} else {
//				log.setClientName(clientIp + "|" + key);
//			}
//		}
//		return log;
//	}
}