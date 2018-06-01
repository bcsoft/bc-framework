bc.subscribeView = {
  /** 发布 */
  release: function () {
    var $view = $(this);
    // 获取用户选中的条目
    var ids = bc.grid.getSelected($view.find(".bc-grid"));
    if (ids.length == 0) {
      bc.msg.slide("请先选择需要发布的订阅！");
      return;
    }

    if (ids.length > 1) {
      bc.msg.slide("只能对一个订阅进行操作！");
      return;
    }
    var $tr = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");

    if ($tr.find("td:eq(0)").attr("data-value") == "0") {
      bc.msg.alert("此订阅已经使用中！");
      return;
    }

    bc.ajax({
      url: bc.root + "/bc/subscribe/update",
      data: {id: ids[0], status: 0},
      dataType: "json",
      success: function (json) {
        if (json.success) {
          bc.msg.slide("发布成功！");
          bc.grid.reloadData($view);
        }
      }
    })

  },
  /** 停用**/
  stop: function () {
    var $view = $(this);
    // 获取用户选中的条目
    var ids = bc.grid.getSelected($view.find(".bc-grid"));
    if (ids.length == 0) {
      bc.msg.slide("请先选择需要停用的订阅！");
      return;
    }

    if (ids.length > 1) {
      bc.msg.slide("只能对一个订阅进行操作！");
      return;
    }
    var $tr = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
    if ($tr.find("td:eq(0)").attr("data-value") == "1") {
      bc.msg.alert("此订阅已经禁用！");
      return;
    }

    bc.ajax({
      url: bc.root + "/bc/subscribe/update",
      data: {id: ids[0], status: 1},
      dataType: "json",
      success: function (json) {
        if (json.success) {
          bc.msg.slide("禁用成功！");
          bc.grid.reloadData($view);
        }
      }
    })
  }
};