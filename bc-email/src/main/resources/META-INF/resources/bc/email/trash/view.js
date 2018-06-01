bc.emailTrashView = {
  /** 还原 */
  restore: function (option) {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var data = null;
    if ($tds.length == 1) {
      data = "id=" + $tds.attr("data-id");
    } else if ($tds.length > 1) {
      data = "ids=";
      $tds.each(function (i) {
        data += $(this).attr("data-id") + (i == $tds.length - 1 ? "" : ",");
      });
    }
    if (data == null) {
      bc.msg.slide("请先选择要还原的邮件！");
      return;
    }

    bc.ajax({
      url: bc.root + "/bc/emailTrash/delete",
      data: data + "&status=0",
      dataType: "json",
      success: function (json) {
        if (json.success === false) {
          bc.msg.alert(json.msg);// 仅显示失败信息
        } else {

          bc.msg.slide(json.msg);
          //重新加载列表
          bc.grid.reloadData($view);
        }
      }
    });
  },
  /** 删除 */
  _delete: function (option) {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var data = null;
    if ($tds.length == 1) {
      data = "id=" + $tds.attr("data-id");
    } else if ($tds.length > 1) {
      data = "ids=";
      $tds.each(function (i) {
        data += $(this).attr("data-id") + (i == $tds.length - 1 ? "" : ",");
      });
    }
    if (data == null) {
      bc.msg.slide("请先选择要删除的邮件！");
      return;
    }

    bc.msg.confirm("邮件删除后将不可恢复，确定要删除选定的 <b>" + $tds.length + "</b>封邮件吗？", function () {
      bc.ajax({
        url: bc.root + "/bc/emailTrash/delete",
        data: data + "&status=1",
        dataType: "json",
        success: function (json) {
          if (json.success === false) {
            bc.msg.alert(json.msg);// 仅显示失败信息
          } else {
            bc.msg.slide(json.msg);
            //重新加载列表
            bc.grid.reloadData($view);
          }
        }
      });
    });

  },
  /** 清空 */
  clear: function (option) {
    var $view = $(this);
    bc.msg.confirm("清空所有垃圾箱的邮件后将不可恢复，确定要清空垃圾箱邮件吗？", function () {
      bc.ajax({
        url: bc.root + "/bc/emailTrash/clear",
        dataType: "json",
        success: function (json) {
          if (json.success === false) {
            bc.msg.alert(json.msg);// 仅显示失败信息
          } else {
            bc.msg.slide("已清空" + json.size_ + "封邮件！");
            //重新加载列表
            bc.grid.reloadData($view);
          }
        }
      });
    });
  }

};