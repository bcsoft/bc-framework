bc.bulletinForm = {
  init: function () {
    var $form = $(this);
  },

  /** 发布公告 */
  issue: function () {
    var $page = $(this);
    var url = bc.root + "/bc/bulletin/issue";
    var $form = $("form", $page);

    //表单验证
    if (!bc.validator.validate($form))
      return;

    //使用ajax发布公告
    var data = $form.serialize();
    bc.msg.confirm("确定要发布当前公告吗？", function () {
      bc.ajax({
        url: url, data: data, dataType: "json",
        success: function (json) {
          if (logger.debugEnabled) logger.debug("issue success.json=" + jQuery.toJSON(json));
          if (json.id) {
            $form.find("input[name='e.id']").val(json.id);
          }
          //记录已保存状态
          $page.attr("data-status", "saved").data("data-status", "saved");

          //显示提示信息
          bc.msg.slide(json.msg);

          //关闭窗口
          $page.dialog("close");
        }
      });
    });
  }
};