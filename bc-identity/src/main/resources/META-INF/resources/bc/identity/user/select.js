if (!window['bc']) window['bc'] = {};
bc.userSelectDialog = {
  /** 点击确认按钮后的处理函数 */
  clickOk: function () {
    var $page = $(this);

    // 获取选中的行的id单元格
    var $tds = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    if ($tds.length == 0) {
      alert("请先选择！");
      return false;
    }
    // 获取选中的数据
    var data;
    var $grid = $page.find(".bc-grid");
    if ($grid.hasClass("singleSelect")) {//单选
      data = {};
      data.id = $tds.attr("data-id");
      var $trs = $grid.find(">.data>.right tr.ui-state-highlight");
      data.name = $trs.find("td:eq(1)").attr("data-value");
      //data.account = $trs.find("td:eq(2)").attr("data-value");
      data.account = $trs.data("hidden").code;
    } else {//多选
      data = [];
      var $trs = $grid.find(">.data>.right tr.ui-state-highlight");
      $tds.each(function (i) {
        data.push({
          id: $(this).attr("data-id"),
          name: $($trs.get(i)).find("td:eq(1)").attr("data-value"),
          account: $($trs.get(i)).data("hidden").code
        });
      });
    }
    logger.info($.toJSON(data));

    // 返回
    $page.data("data-status", data);
    $page.dialog("close");
  }
};