bc.emailViewBase = {
  i: 0,
  /** 写邮件 */
  writeEmail: function () {
    var $view = $(this);
    var $writeButton = $view.find(".bc-toolbar>button:eq(0)");
    bc.page.newWin({
      url: bc.root + "/bc/email/create",
      mid: "email::create::" + bc.emailViewBase.i,
      name: "新邮件",
      title: "新邮件",
      afterClose: function () {
        //通过发件箱的按钮写邮件
        if ($writeButton.hasClass('bc-email-vriteEmail-send')) bc.grid.reloadData($view);
      }
    });
    bc.emailViewBase.i = bc.emailViewBase.i + 1;
  },
  /** 查看邮件 **/
  open: function () {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var $trs = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
    if ($tds.length == 1) {
      var $hidden = $trs.data("hidden");
      var mid = null;
      var name = null;
      var title = null;
      var data = null;
      var openType = $hidden.openType;

      if (openType == 1) {
        data = {id: $tds.attr("data-id"), openType: openType};
        mid = "email::send::" + $tds.attr("data-id");
        name = "查看已发邮件";
        title = "查看已发邮件";
      } else if (openType == 2) {
        data = {id: $tds.attr("data-id"), openType: openType};
        mid = "email::to::" + $tds.attr("data-id");
        name = "查看已收邮件";
        title = "查看已收邮件";
      } else if (openType == 3) {
        data = {
          id: $hidden.emailId,
          openType: openType,
          trashSource: $hidden.source,
          trashHandleDate: $hidden.handleDate
        };
        mid = "email::trash::" + $hidden.source + "::" + $hidden.emailId;
        name = "查看垃圾邮件";
        title = "查看垃圾邮件";
      } else {
        return;
      }

      bc.page.newWin({
        url: bc.root + "/bc/email/open",
        data: data,
        mid: mid,
        name: name,
        title: title,
        afterClose: function () {
          //查看收件箱中未读邮件，查看后刷新视图
          if (openType == 2 && $hidden.read === false) bc.grid.reloadData($view);
        }
      });
    } else if ($tds.length > 1) {
      bc.msg.slide("一次只能查看一封邮件！");
    } else {
      bc.msg.slide("请先选择要查看的信息！");
    }
  },
  /** 回复 */
  reply: function () {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    if ($tds.length == 1) {
      bc.page.newWin({
        url: bc.root + "/bc/email/reply",
        data: {id: $tds.attr("data-id")},
        mid: "email::reply::" + $tds.attr("data-id"),
        name: "回复邮件",
        title: "回复邮件"
      });
    } else if ($tds.length > 0) {
      bc.msg.slide("一次只能回复一封邮件！");
    } else {
      bc.msg.slide("请先选择要回复的邮件！");
    }
  },
  /** 转发 */
  forwoard: function () {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    if ($tds.length == 1) {
      bc.page.newWin({
        url: bc.root + "/bc/email/forward",
        data: {id: $tds.attr("data-id")},
        mid: "email::forward::" + $tds.attr("data-id"),
        name: "转发邮件",
        title: "转发邮件"
      });
    } else if ($tds.length > 0) {
      bc.msg.slide("一次只能转发一封邮件！");
    } else {
      bc.msg.slide("请先选择要转发的邮件！");
    }
  },
  /** 发件箱 */
  sendBox: function () {
    var $view = $(this);

  },
  /** 收件箱 */
  toBox: function () {
    var $view = $(this);

  },
  /** 垃圾箱 */
  trashBox: function () {
    bc.page.newWin({
      url: bc.root + "/bc/emailTrashs/paging",
      mid: "email::trashs",
      name: "垃圾箱",
      title: "垃圾箱"
    });
  },
  /** 移至垃圾箱  **/
  moveTrash: function () {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var $trs = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
    var data = null;
    var source = null;
    if ($tds.length == 1) {
      data = "emailId=" + $tds.attr("data-id");
      source = $trs.data("hidden").source;
    } else if ($tds.length > 1) {
      data = "emailIds=";
      $tds.each(function (i) {
        data += $(this).attr("data-id") + (i == $tds.length - 1 ? "" : ",");
      });
      source = $($trs.get(0)).data("hidden").source;
    }

    if (data == null) {
      bc.msg.slide("请先选择要移至垃圾箱的邮件！");
      return;
    }

    bc.ajax({
      url: bc.root + "/bc/emailTrash/deleteEmail",
      data: data + "&status=0&source=" + source,
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
  /** 彻底删除 **/
  shiftDelete: function () {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var $trs = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
    var data = null;
    var source = null;
    if ($tds.length == 1) {
      data = "emailId=" + $tds.attr("data-id");
      source = $trs.data("hidden").source;
    } else if ($tds.length > 1) {
      data = "emailIds=";
      $tds.each(function (i) {
        data += $(this).attr("data-id") + (i == $tds.length - 1 ? "" : ",");
      });
      source = $($trs.get(0)).data("hidden").source;
    }

    if (data == null) {
      bc.msg.slide("请先选择要彻底删除的邮件！");
      return;
    }

    bc.msg.confirm("邮件删除后将不可恢复，确定要彻底删除选定的 <b>" + $tds.length + "</b>封邮件吗？", function () {
      bc.ajax({
        url: bc.root + "/bc/emailTrash/deleteEmail",
        data: data + "&status=1&source=" + source,
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
  // 编辑
  edit: function () {
    var $view = $(this);
    var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var $trs = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
    if ($tds.length == 1) {
      bc.page.newWin({
        url: bc.root + "/bc/email/edit",
        data: {id: $tds.attr("data-id")},
        mid: "email::edit::" + $tds.attr("data-id"),
        name: "草稿邮件",
        title: "草稿邮件",
        afterClose: function () {
          bc.grid.reloadData($view);
        }
      });
    } else if ($tds.length > 1) {
      bc.msg.slide("一次只能编辑一封邮件！");
    } else {
      bc.msg.slide("请先选择要编辑的邮件！");
    }
  },
  // 直接删除
  _delete: function () {

  }
};