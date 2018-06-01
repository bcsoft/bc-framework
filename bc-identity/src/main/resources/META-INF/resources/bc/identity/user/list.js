bc.userList = {
  /** 密码设置对话框*/
  setPassword: function () {
    var $page = $(this);

    //获取选中的用户id
    var $tds = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var data = null;
    if ($tds.length == 1) {
      data = "ids=" + $tds.attr("data-id");
    } else if ($tds.length > 1) {
      data = "ids=";
      $tds.each(function (i) {
        data += $(this).attr("data-id") + (i == $tds.length - 1 ? "" : ",");
      });
    }
    if (data == null) {
      bc.msg.slide("请先选择用户！");
      return;
    }
    if (logger.infoEnabled) logger.info("setPassword: data=" + data);


    var option = jQuery.extend({
      url: bc.root + "/bc/auth/setPassword",
      name: "密码设置",
      mid: "setPassword",
      data: data
    }, option);

    bc.page.newWin(option);
  }
};