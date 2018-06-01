package cn.bc.web.ui.html.page;

import cn.bc.web.ui.json.Json;

/**
 * 对话框按钮
 *
 * @author dragon
 */
public class ButtonOption extends Json {
  public ButtonOption() {
  }

  /**
   * @param label  显示的名称
   * @param action 内部action的名称
   */
  public ButtonOption(String label, String action) {
    this();
    this.put("text", label);
    this.put("action", action);
  }

  /**
   * @param label  显示的名称
   * @param action 内部action的名称
   * @param click  自定义的回调函数
   */
  public ButtonOption(String label, String action, String click) {
    this();
    this.put("text", label);
    this.put("action", action);
    this.put("click", click);
  }

  public String getId() {
    return this.getString("id");
  }

  public ButtonOption setId(String id) {
    this.put("id", id);
    return this;
  }

  public String getAction() {
    return this.getString("action");
  }

  public ButtonOption setAction(String action) {
    this.put("action", action);
    return this;
  }

  public String getClick() {
    return this.getString("click");
  }

  public ButtonOption setClick(String click) {
    this.put("click", click);
    return this;
  }
}
