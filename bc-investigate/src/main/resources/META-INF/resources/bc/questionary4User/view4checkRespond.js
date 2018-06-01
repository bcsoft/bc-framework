if (!window['bc']) window['bc'] = {};
bc.checkRespondView = {
  /** 双击视图处理 */
  dblclick: function () {
    var $page = $(this);
    var $grid = $page.find(".bc-grid");
    var $trs4Pid = $grid.find(">.data>.left tr.ui-state-highlight");
    var $trs4UserId = $grid.find(">.data>.right tr.ui-state-highlight");
    var $trs4Name = $grid.find(">.data>.right tr.ui-state-highlight");
    var name = $trs4Name.find("td:eq(0)").text();
    var pid = $trs4Pid.find("td:eq(0)").attr("data-id");
    var userId = $.evalJSON($trs4UserId.attr("data-hidden")).userId;
    bc.page.newWin({
      name: name + "的答卷",
      mid: "questionary4User" + userId,
      url: bc.root + "/bc/questionary4User/open",
      data: {pid: pid, userId: userId},
      afterClose: function (status) {
        if (status) bc.grid.reloadData($form);
      }
    });
  }

};