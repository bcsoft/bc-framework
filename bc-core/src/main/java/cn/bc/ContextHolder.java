package cn.bc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 系统上下文的线程变量保持器
 *
 * @author dragon
 */
public class ContextHolder {
  private static Log logger = LogFactory.getLog(ContextHolder.class);

  // 定义私有的线程变量保持器
  private static ThreadLocal<Context> contextHolder = new ThreadLocal<Context>() {
    @Override
    public void set(Context value) {
      if (logger.isDebugEnabled())
        logger.debug("	threadLocal:class=" + this.toString()
          + ",hashCode=" + this.hashCode());
      super.set(value);
    }
  };

  /**
   * 获取当前线程的系统上下文
   *
   * @return
   */
  public static Context get() {
    return (Context) contextHolder.get();
  }

  /**
   * 设置当前线程的系统上下文
   *
   * @param context
   */
  public static void set(Context context) {
    if (logger.isDebugEnabled())
      logger.debug("set context="
        + (context == null ? "null" : context.toString()));
    contextHolder.set(context);
  }

  /**
   * 删除当前线程的系统上下文
   */
  public static void remove() {
    if (logger.isDebugEnabled())
      logger.debug("remove context");
    contextHolder.remove();
  }
}
