if (!window['bc']) window['bc'] = {};
bc.questionary4UserView = {
  /** 双击视图处理 */
  dblclick: function () {
    var $page = $(this);
    var $grid = $page.find(".bc-grid");
    var $trs = $grid.find(">.data>.right tr.ui-state-highlight");
    var status = $trs.find("td:eq(0)").attr("data-value");
    //如果是草稿状态的调用编辑方法其他的调用打开方法
    if (status == -1) {
      bc.page.edit.call($page);
    } else {
      bc.page.open.call($page);
    }
  }

};