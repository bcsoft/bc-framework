/**
 *
 */
package cn.bc.web.struts2;

import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Div;
import cn.bc.web.ui.html.tree.Tree;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 平台各模块带树视图Action的基类封装
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class TreeViewAction<T extends Object> extends ViewAction<T> {
  private static final long serialVersionUID = 1L;

  @Override
  protected Component getHtmlPageCenter() {
    // 构建容器
    Div treeGrid = new Div();
    treeGrid.addClazz("treeGrid");

    // 添加树
    Tree tree = this.getHtmlPageTree();
    Integer treeWidth = getTreeWith();
    treeGrid.addChild(tree);

    // 添加Grid
    Div gridWrap = new Div();
    gridWrap.addClazz("bc-grid-wrap");
    gridWrap.addChild(this.getHtmlPageGrid());
    treeGrid.addChild(gridWrap);

    // 控制树的宽的
    if (treeWidth != null) {
      tree.addStyle("width", treeWidth + "px");
      gridWrap.addStyle("left", treeWidth + "px");
    }

    return treeGrid;
  }

  /**
   * 设置树的宽度：默认由平台的样式控制(tree.css、grid.css)为160
   *
   * @return
   */
  protected Integer getTreeWith() {
    return null;
  }

  /**
   * 构建树
   */
  protected Tree getHtmlPageTree() {
    Tree tree = new Tree();
    return tree;
  }
}
