bc.reportTaskList = {
  /**启用或重置任务**/
  start: function () {
    var $page = $(this);
    var ids = bc.grid.getSelected($page.find(".bc-grid"));
    if (ids.length == 0) {
      bc.msg.slide("请先选择要处理的任务！");
      return;
    }
    bc.msg.confirm("确定启用或重置任务？", function () {
      bc.ajax({
        url: bc.root + "/bc/reportTask/start",
        data: {ids: ids.join(",")},
        dataType: "json",
        success: function (json) {
          //重新加载列表
          bc.grid.reloadData($page);
          //显示提示信息
          bc.msg.slide(json.msg);
        }
      });
    });

  },
  /** 停止任务 **/
  stop: function () {
    var $page = $(this);
    var ids = bc.grid.getSelected($page.find(".bc-grid"));
    if (ids.length == 0) {
      bc.msg.slide("请先选择要处理的任务！");
      return;
    }

    bc.msg.confirm("确定停止任务？", function () {
      bc.ajax({
        url: bc.root + "/bc/reportTask/stop",
        data: {ids: ids.join(",")},
        dataType: "json",
        success: function (json) {
          //重新加载列表
          bc.grid.reloadData($page);
          //显示提示信息
          bc.msg.slide(json.msg);
        }
      });
    });
  }
};