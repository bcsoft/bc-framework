package cn.bc.web.ui.html.toolbar;

import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Text;

/**
 * 工具条占位符按钮
 *
 * @author dragon
 */
public class ToolbarEmptyButton extends Button {
  public String getTag() {
    return "span";
  }

  protected void init() {
    this.addClazz("ui-button ui-widget ui-state-default");
    this.setAttr("type", "button");// 添加这个是为了解决ie8在文本框回车的问题
    this.addStyle("cursor", "default");
    this.addStyle("width", "0px");
    this.addStyle("_width", "2px");
  }

  public ToolbarEmptyButton() {
    super();
  }

  public void renderButton(StringBuffer main) {
    this.addClazz("ui-button-text-only");
    this.addChild(createTextChild());
  }

  // 文本dom
  protected Component createTextChild() {
    //添加的空格用于控制按钮的自动高度
    return new Span().addClazz("ui-button-text")
      .addChild(new Text("&nbsp;")).addStyle("padding-left", "0")
      .addStyle("padding-right", "0");
  }
}
