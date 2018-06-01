bc.formMangeView = {
  /** 表单预览 */
  edit: function () {
    var $page = $(this);
    var $tds = $page
      .find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");

    var id = null;
    var option = {};
    if ($tds.length == 1) {
      id = $tds.attr("data-id");
      var $tds = $page
        .find(".bc-grid>.data>.right tr.ui-state-highlight>td");
      $tds.each(function () {
        var $td = $(this);
        if ($td.attr("data-column") == "f.type_") {
          option.type = $.trim($td.html());
        } else if ($td.attr("data-column") == "f.pid") {
          option.pid = $.trim($td.html());
        } else if ($td.attr("data-column") == "f.code") {
          option.code = $.trim($td.html());
        } else if ($td.attr("data-column") == "f.subject") {
          option.subject = $.trim($td.html());
        } else if ($td.attr("data-column") == "f.tpl_") {
          option.tpl = $.trim($td.html());
        }
      });
    } else if ($tds.length > 1) {
      bc.msg.slide("一次只能打开一条条目！");
      return;
    }

    if (id == null) {
      bc.msg.slide("请先选择要查看的条目！");
      return;
    }

    bc.customForm.render(option);
  },

  /** 删除表单 */
  delete_: function () {
    var $page = $(this);
    var $tds = $page
      .find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");

    var id = null;
    var option = {};
    if ($tds.length == 1) {
      id = $tds.attr("data-id");
      var $tds = $page
        .find(".bc-grid>.data>.right tr.ui-state-highlight>td");
      $tds.each(function () {
        var $td = $(this);
        if ($td.attr("data-column") == "f.type_") {
          option.type = $.trim($td.html());
        } else if ($td.attr("data-column") == "f.pid") {
          option.pid = $.trim($td.html());
        } else if ($td.attr("data-column") == "f.code") {
          option.code = $.trim($td.html());
        } else if ($td.attr("data-column") == "f.subject") {
          option.subject = $.trim($td.html());
        } else if ($td.attr("data-column") == "f.tpl_") {
          option.tpl = $.trim($td.html());
        }
        option.page = $page;
      });
    } else if ($tds.length > 1) {
      bc.msg.slide("一次只能打开一条条目！");
      return;
    }

    if (id == null) {
      bc.msg.slide("请先选择要查看的条目！");
      return;
    }

    bc.customForm.delete_(option);
  },

  /** 打开表单字段管理端视图 */
  manageField: function () {
    var $page = $(this);
    var $tds = $page
      .find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");

    var id = null;

    if ($tds.length == 1) {
      id = $tds.attr("data-id");
      var data = {};
      data.formId = id;
    } else if ($tds.length > 1) {
      bc.msg.slide("一次只能打开一条条目！");
      return;
    }

    /*if (id == null) {
      bc.msg.slide("请先选择要查看的条目！");
      return;
    }*/

    bc.page.newWin({
      name: "表单字段管理",
      mid: "field.manage",
      url: bc.root + "/bc/fieldManages/paging",
      data: data
    });
  },

  /** 打开表单审计日志管理端视图 */
  manageFieldLog: function () {
    var $page = $(this);
    var $tds = $page
      .find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");

    var id = null;

    if ($tds.length == 1) {
      id = $tds.attr("data-id");
      var data = {};
      data.formId = id;
    } else if ($tds.length > 1) {
      bc.msg.slide("一次只能打开一条条目！");
      return;
    }

    /*if (id == null) {
      bc.msg.slide("请先选择要查看的条目！");
      return;
    }*/

    bc.page.newWin({
      name: "表单审计日志管理",
      mid: "fieldLog.manage",
      url: bc.root + "/bc/fieldLogManages/paging",
      data: data
    });
  }
};