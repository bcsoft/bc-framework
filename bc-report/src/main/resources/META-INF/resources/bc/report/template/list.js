bc.reportTemplateList = {
  /**执行的处理**/
  execute: function () {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    if ($tds.length == 1) {
      //获取选中的行
      var $tr = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
      var status = $tr.find(">td:eq(0)").attr("data-value");
      if (status == 1) {
        bc.msg.slide("禁用状态不能够执行！");
        return;
      }

      var code = $tr.find(">td:eq(4)").attr("data-value");
      var name = $tr.find(">td:eq(3)").attr("data-value");

      bc.msg.confirm("确定要执行报表 <b>" + name + "</b> 吗？", function () {
        //弹出选择对话框
        bc.page.newWin({
          url: bc.root + "/bc/report/run?code=" + code,
          name: name,
          title: name,
          mid: code
        });
      });


    } else if ($tds.length > 1) {
      bc.msg.slide("一次只可以执行一个报表，请确认您只选择了一个报表！");
      return;
    } else {
      bc.msg.slide("请先选择要执行的报表模板！");
      return;
    }
  },
  /** 查看历史报表 **/
  viewReportHistory: function () {
    var $view = $(this);
    var mid = $view.attr("data-mid");
    bc.page.newWin({
      url: bc.root + "/bc/myReportHistorys/paging",
      name: "我的历史报表",
      mid: "myReportHistory" + mid
    });


  }
};