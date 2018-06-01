bc.emailToView = {
  /** 标记为按钮的事件处理 */
  selectMenuButtonItem: function (option) {
    var $view = $(this);
    switch (option.value) {
      case "emailTrash":
        bc.emailViewBase.trashBox.call($view);
        break;
      case "markRead":
        bc.emailToView.markRead.call($view);
        break;
      case "markUnread":
        bc.emailToView.markUnread.call($view);
        break;
      case "markReadAll":
        bc.emailToView.markReadAll.call($view);
        break;
      case "reply":
        bc.emailViewBase.reply.call($view);
        break;
      case "forwoard":
        bc.emailViewBase.forwoard.call($view);
        break;
      case "moveTrash":
        bc.emailViewBase.moveTrash.call($view);
        break;
      case "shiftDelete":
        bc.emailViewBase.shiftDelete.call($view);
        break;
      default:
        alert("other");
    }
  },
  /** 标记为已读 */
  markRead: function () {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var data = null;
    if ($tds.length > 0) {
      data = "ids=";
      $tds.each(function (i) {
        data += $(this).attr("data-id") + (i == $tds.length - 1 ? "" : ",");
      });
    }

    if (data == null) {
      bc.msg.slide("请选择将要标记为已读的邮件！");
      return;
    }

    bc.ajax({
      url: bc.root + "/bc/email/mark",
      data: data + "&read=true",
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
  /** 标记为未读 */
  markUnread: function () {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var data = null;
    if ($tds.length > 0) {
      data = "ids=";
      $tds.each(function (i) {
        data += $(this).attr("data-id") + (i == $tds.length - 1 ? "" : ",");
      });
    }

    if (data == null) {
      bc.msg.slide("请选择将要标记为未读的邮件！");
      return;
    }

    bc.ajax({
      url: bc.root + "/bc/email/mark",
      data: data + "&read=false",
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
  /** 全部标记为已读 */
  markReadAll: function () {
    var $view = $(this);
    bc.msg.confirm("确定要将未读的邮件都标记<b>已读</b>吗？", function () {
      bc.ajax({
        url: bc.root + "/bc/email/mark4read",
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
  }

};