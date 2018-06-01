bc.templateParamSelectDialog = {
  /** 点击确认按钮后的处理函数 */
  clickOk: function () {
    var $page = $(this);

    // 获取选中的行的id单元格
    var $tds = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    if ($tds.length == 0) {
      bc.msg.alert("请先选择！");
      return false;
    }

    // 获取选中的数据
    var data;
    var $grid = $page.find(".bc-grid");
    if ($grid.hasClass("singleSelect")) {// 单选
      data = {};
      data.id = $tds.attr("data-id");
      var $tr = $grid.find(">.data>.right tr.ui-state-highlight");
      data.name = $tr.find("td:eq(0)").text();
      data.desc = $tr.find("td:eq(1)").text();
    } else {
      data = [];
      var $right = $($tds[0]).closest(".left").siblings();
      $tds.each(function (i) {
        var $this = $(this);
        var index = $this.parent().index();
        var $row = $right.find("tr.row:eq(" + index + ")");
        var id = $this.attr("data-id");
        var name = $row.find("td:eq(0)").text();
        var desc = $row.find("td:eq(1)").text();
        data.push({
          id: id,
          name: name,
          desc: desc
        });
      });
    }


    logger.info($.toJSON(data));
    // 返回
    $page.data("data-status", data);
    $page.dialog("close");
  }
};

