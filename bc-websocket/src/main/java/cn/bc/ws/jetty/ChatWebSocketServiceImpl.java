package cn.bc.ws.jetty;

import cn.bc.Context;
import cn.bc.chat.service.OnlineUserService;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.json.Json;
import cn.bc.web.util.WebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务接口的实现
 *
 * @author dragon
 */
public class ChatWebSocketServiceImpl implements ChatWebSocketService,
  ApplicationContextAware {
  private final Log logger = LogFactory
    .getLog(ChatWebSocketServiceImpl.class);
  private OnlineUserService onlineUserService;
  private ApplicationContext applicationContext;
  private final Set<ChatWebSocket> members = new CopyOnWriteArraySet<ChatWebSocket>();// 在线连接
  private final Set<ChatWebSocket> pools = new CopyOnWriteArraySet<ChatWebSocket>();// 离线连接

  @Autowired
  public void setOnlineUserService(OnlineUserService onlineUserService) {
    this.onlineUserService = onlineUserService;
  }

  public void setApplicationContext(ApplicationContext applicationContext)
    throws BeansException {
    this.applicationContext = applicationContext;
  }

  /**
   * 向连接池增加一个连接缓存
   *
   * @param webSocket
   */
  private void addPoolsMember(ChatWebSocket webSocket) {
    this.pools.add(webSocket);
  }

  /**
   * 从连接池取一个新的连接，如果连接池为空，则自动初始化一个新的连接
   *
   * @param webSocket
   */
  private ChatWebSocket getPoolsMember() {
    ChatWebSocket webSocket;
    if (this.pools.isEmpty()) {
      webSocket = this.applicationContext.getBean(ChatWebSocket.class);

      if (logger.isDebugEnabled())
        logger.debug("getPoolsMember:create webSocket");
    } else {
      // 取一个连接
      webSocket = this.pools.iterator().next();

      // 从连接池中剔除取出的连接
      this.pools.remove(webSocket);

      // 清空参数
      webSocket.setSid(null);
      webSocket.setClientIp(null);
      webSocket.setClientName(null);
      webSocket.setClientId(null);

      if (logger.isDebugEnabled())
        logger.debug("getPoolsMember:get webSocket from pools");
    }
    return webSocket;
  }

  public ChatWebSocket createWebSocket(HttpServletRequest request) {
    // session id
    String sid = request.getParameter("sid");
    String c = request.getParameter("c");
    if (c == null || c.length() == 0)
      throw new CoreException(
        "createWebSocket:need parameter c to create WebSocket");

    // 解析客户端的连接信息
    String[] cc = c.split(",");
    String clientId = cc[0];// 客户端的id，通常为用户的id
    String clientName = cc.length > 1 ? cc[1] : "noname";// 客户端的名称，通常为用户的姓名

    // debug
    if (logger.isDebugEnabled()) {
      // 系统上下文：jetty8.0.4实际测试表明：context == null
      SystemContext context = (SystemContext) request.getSession()
        .getAttribute(Context.KEY);
      logger.debug("createWebSocket:sid=" + sid + ",session id="
        + request.getSession().getId() + ",c=" + c + ",clientId="
        + clientId + ",clientName=" + clientName + ",context="
        + context);
    }

    // 创建一个连接
    ChatWebSocket webSocket = this.getPoolsMember();
    webSocket.setSid(sid);
    webSocket.setClientName(clientName);
    webSocket.setClientId(clientId);
    webSocket.setClientIp(WebUtils.getClientIP(request));// 客户端的IP地址信息
    webSocket.setClient(this.onlineUserService.get(sid));// 记录在线用户的详细信息

    return webSocket;
  }

  public void addMember(ChatWebSocket webSocket) {
    this.members.add(webSocket);
  }

  public void removeMember(ChatWebSocket webSocket) {
    this.members.remove(webSocket);

    // 将离线连接缓存给后续的新连接使用
    this.addPoolsMember(webSocket);
  }

  public boolean isExisting(String sid) {
    for (ChatWebSocket member : members) {
      if (member.getSid().equals(sid))
        return true;
    }
    return false;
  }

  public ChatWebSocket get(String sid) {
    for (ChatWebSocket member : members) {
      if (member.getSid().equals(sid))
        return member;
    }
    return null;
  }

  public void sendMessage(String fromSid, String toSid, int type, String msg) {
    if (toSid != null && toSid.length() > 0) {// 发给指定连接
      ChatWebSocket fromWebSocket = this.get(fromSid);
      Assert.notNull(fromWebSocket);
      ChatWebSocket toWebSocket = this.get(toSid);
      Assert.notNull(toWebSocket);
      String json = buildJson(fromWebSocket, type, msg, null);
      if (logger.isDebugEnabled())
        logger.debug("sendMessage:fromSid=" + fromSid + ",toSid="
          + toSid + ",msg=" + json);

      try {
        toWebSocket.getConnection().sendMessage(json);
      } catch (IOException e) {
        logger.error(e);
      }
    } else {// 发给所有连接
      sendMessageToAllClient(fromSid, type, msg);
    }
  }

  public void sendMessageToOtherClient(String fromSid, int type, String msg) {
    if (logger.isDebugEnabled())
      logger.debug("sendMessageToOtherUser:fromSid=" + fromSid + ",msg="
        + msg);
    ChatWebSocket fromWebSocket = this.get(fromSid);
    Assert.notNull(fromWebSocket);
    String json = buildJson(fromWebSocket, type, msg, null);
    for (ChatWebSocket member : members) {
      if (!member.getSid().equals(fromSid)) {
        try {
          member.getConnection().sendMessage(json);
        } catch (IOException e) {
          logger.error(
            "toSid=" + member.getSid() + ":" + e.getMessage(),
            e);
        }
      }
    }
  }

  public void sendMessageToAllClient(String fromSid, int type, String msg) {
    ChatWebSocket fromWebSocket = this.get(fromSid);
    Assert.notNull(fromWebSocket);
    String json = buildJson(fromWebSocket, type, msg, null);
    if (logger.isDebugEnabled())
      logger.debug("sendMessageToAllClient:fromSid=" + fromSid + ",msg="
        + json);
    for (ChatWebSocket member : this.members) {
      try {
        member.getConnection().sendMessage(json);
      } catch (IOException e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  private SimpleDateFormat formatter = new SimpleDateFormat("H:mm:ss");

  private String buildJson(ChatWebSocket member, int type, String msg,
                           String originSid) {
    Json json = new Json();
    json.put("time", formatter.format(new Date()));// 发送的服务器时间
    json.put("type", type);
    json.put("sid", member.getSid());
    json.put("name", member.getClientName());
    json.put("uid", member.getClientId());
    json.put("ip", member.getClientIp());
    json.put("msg", msg);

    if (originSid != null)
      json.put("origin", originSid);
    return json.toString();
  }

  public void sendMessageBack(String fromSid, String toSid, String msg) {
    ChatWebSocket fromWebSocket = this.get(fromSid);
    Assert.notNull(fromWebSocket);
    String json = buildJson(fromWebSocket, ChatWebSocket.TYPE_SYSTIP, msg,
      toSid);
    if (logger.isDebugEnabled())
      logger.debug("sendMessageBack:fromSid=" + fromSid + ",toSid="
        + toSid + ",msg=" + json);
    try {
      fromWebSocket.getConnection().sendMessage(json);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void offline(String sid) {
    if (logger.isDebugEnabled())
      logger.debug("offline:sid=" + sid);

    ChatWebSocket webSocket = this.get(sid);

    // Assert.notNull(webSocket);
    if (webSocket == null) {
      logger.info("offline: not exists webSocket,ignore! sid=" + sid);
      return;
    }

    String msg = webSocket.getClientName() + "下线了(登录超时)！";
    String json = buildJson(webSocket, ChatWebSocket.TYPE_OFFLINE, msg,
      null);

    try {
      // 向登录超时的连接发送消息，避免重复连接
      webSocket.getConnection().sendMessage(json);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }

    // 向其它连接发送消息
    // this.sendMessageToOtherClient(sid, ChatWebSocket.TYPE_OFFLINE, msg);

    // 断开连接
    // this.removeMember(webSocket);
  }

  public void memberLogin(String sid, boolean relogin) {
    logger.debug("memberLogin:relogin=" + relogin + ",sid=" + sid);
  }

  public Set<ChatWebSocket> getAll() {
    return this.members;
  }
}