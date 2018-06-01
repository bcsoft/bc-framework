package cn.bc.web.event;

import cn.bc.Context;
import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpSession;

/**
 * session销毁事件
 *
 * @author dragon
 */
public class SessionDestroyedEvent extends ApplicationEvent {
  private static final long serialVersionUID = 6578419945758700738L;
  private final Context context;
  private final String sid;

  /**
   * @param source  事件源
   * @param context 上下文
   */
  public SessionDestroyedEvent(Object source, Context context, String sid) {
    super(source);
    this.context = context;
    this.sid = sid;
  }

  public String getSid() {
    return sid;
  }

  /**
   * 获取上下文
   *
   * @return
   */
  public Context getContext() {
    return this.context;
  }

  /**
   * 获取Session
   *
   * @return
   */
  public HttpSession getSession() {
    return (HttpSession) this.getSource();
  }
}
