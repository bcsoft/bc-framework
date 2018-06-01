bc.emailFormr = {
  init: function (option, readonly) {
    var $form = $(this);

    $form.find(".email-open").show();
    $form.find(".email-subject").focus();

    var sendDate = $form.find(":input[name='e.sendDate']").val();
    var fromNow = moment(sendDate, "YYYY-MM-DD HH:mm:ss").fromNow()
    $form.find(".emailFormr-fromNow").text(fromNow);


    //绑定回复事件
    $form.find(".emailFormr-reply").click(function () {
      var id = $form.find(":input[name='e.id']").val();
      bc.page.newWin({
        url: bc.root + "/bc/email/reply",
        data: {id: id},
        mid: "email::reply::" + id,
        name: "回复邮件",
        title: "回复邮件"
      });
    });

    //绑定转发事件
    $form.find(".emailFormr-forward").click(function () {
      var id = $form.find(":input[name='e.id']").val();
      bc.page.newWin({
        url: bc.root + "/bc/email/forward",
        data: {id: id},
        mid: "email::forward::" + id,
        name: "转发邮件",
        title: "转发邮件"
      });
    });

    $form.find(".emailFormr-show").click(function () {
      $form.find(".email-history:gt(0)").toggle("fast");
      $form.find(".emailFormr-show>.ui-icon-triangle-1-s").toggle("fast");
      $form.find(".emailFormr-show>.ui-icon-triangle-1-n").toggle("fast");
    });
  }
};