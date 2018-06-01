package cn.bc.identity.web;

import cn.bc.ContextHolder;

/**
 * Web系统上下文的线程变量保持器
 *
 * @author dragon
 */
public final class SystemContextHolder {
  /**
   * 获取当前线程的系统上下文
   *
   * @return
   */
  public static SystemContext get() {
    return (SystemContext) ContextHolder.get();
  }

  /**
   * 设置当前线程的系统上下文
   *
   * @param context
   */
  public static void set(SystemContext context) {
    ContextHolder.set(context);
  }

  /**
   * 删除当前线程的系统上下文
   */
  public static void remove() {
    ContextHolder.remove();
  }
}
