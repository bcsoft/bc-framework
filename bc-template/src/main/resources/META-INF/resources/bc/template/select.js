bc.templateSelectDialog = {
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
      data.typeName = $tr.find("td:eq(5)").text();
      data.subject = $tr.find("td:eq(1)").text();
      data.code = $tr.find("td:eq(4)").text();
      data.version = $tr.find("td:eq(3)").text();
      data.formatted = $tr.find("td:eq(7)").attr("data-value");
      data.size = parseInt($tr.find("td:eq(6)").attr("data-value"));
      data.desc = $tr.find("td:eq(2)").text();
      data.category = $tr.find("td:eq(0)").text();
      $.extend(data, $tr.data("hidden"));
    } else {
      data = [];
      var $right = $($tds[0]).closest(".left").siblings();
      $tds.each(function (i) {
        var $this = $(this);
        var index = $this.parent().index();
        var $row = $right.find("tr.row:eq(" + index + ")");
        var id = $this.attr("data-id");
        var typeName = $row.find("td:eq(5)").text();
        var subject = $row.find("td:eq(1)").text();
        var code = $row.find("td:eq(4)").text();
        var version = $row.find("td:eq(3)").text();
        var formatted = $row.find("td:eq(7)").attr("data-value");
        var size = parseInt($row.find("td:eq(6)").attr("data-value"));
        var category = $row.find("td:eq(0)").text();
        var desc = $row.find("td:eq(2)").text();
        data.push($.extend({
          id: id,
          typeName: typeName,
          subject: subject,
          code: code,
          version: version,
          formatted: formatted,
          size: size,
          category: category,
          desc: desc
        }, $row.data("hidden")));
      });
    }

    logger.info($.toJSON(data));
    // 返回
    $page.data("data-status", data);
    $page.dialog("close");
  }
};

