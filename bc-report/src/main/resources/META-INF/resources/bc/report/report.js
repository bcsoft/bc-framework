bc.namespace("bc");
bc.report = {
  /** 将报表运行结果存为历史 */
  save2history: function () {
    var $page = $(this);
    logger.info("save2history");

    bc.msg.confirm("确定要将当前报表结果存为历史报表吗？", function () {
      // 显示提示信息
      var win = bc.msg.info("<div id='msg' style='font-size:20px;'>正在存为历史报表...</div><div id='info'>报表执行可能比较耗时，请耐心等候!</div>", null);

      // 获取条件信息
      var data = {};
      data.exporting = true;
      data.exportFormat = "xls";
      data = $.extend(data, $page.data("extras"));

      // 执行请求处理
      bc.ajax({
        url: bc.root + "/bc/report/save2history",
        data: data,
        dataType: "json",
        success: function (json) {
          logger.info("success=" + json.success);
          if (json.success) {
            if (win) {
              try {
                win.dialog("destroy").remove();
              } catch (e) {
              }
            }
            bc.msg.slide("存为历史报表成功！");
          } else {
            if (win) {
              win.find("#msg").html("存为历史报表失败了！");
              logger.info(json.msg);
              logger.info(win.find("#info").size());
              win.find("#info").html(json.msg);
            } else {
              bc.msg.alert("存为历史报表失败了：" + json.msg);
            }
          }
        }
      });
    });
  }
};