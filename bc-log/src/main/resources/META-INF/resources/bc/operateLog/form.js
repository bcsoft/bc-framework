bc.operateLongForm = {
  /** 维护处理 */
  doMaintenance: function () {
    var $page = $(this);
    bc.msg.confirm("是否对标题为" + $page.find(":input[name='e.subject']").val() + "的操作日志进行维护？", function () {
      // 关闭当前窗口
      $page.dialog("close");
      // 重新打开可编辑表单
      bc.page.newWin({
        name: "维护标题为" + $page.find(":input[name='e.subject']").val() + "的操作日志",
        mid: "operateLog" + $page.find(":input[name='e.id']").val(),
        url: bc.root + "/bc/operateLog/edit",
        data: {id: $page.find(":input[name='e.id']").val()},
        afterClose: function (status) {
          if (status) bc.grid.reloadData($page);
        }
      });
    });

  }

};





