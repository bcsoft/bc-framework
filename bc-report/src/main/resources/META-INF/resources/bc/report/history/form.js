bc.reportHistoryForm = {
  init: function (option, readonly) {
    var $form = $(this);

    // 让详细配自动高度
    var $cfg = $form.find(":input[name='e.msg']");
    var cfgEl = $cfg.get(0);
    if (cfgEl.scrollHeight > 170) {
      cfgEl.style.height = cfgEl.scrollHeight + "px";
    }


    //隐藏信息
    $form.find("#idReportMsgError").hide();

    //绑定显示异常信息按钮事件
    var msg = $form.find(":input[name='e.msg']").val();
    var success = $form.find(":input[name='e.success']").val();
    if (msg != '' && success == 'false') {
      $form.find("#idReportMsgError").show();
      $form.find(":input[name='e.msg']").tah({
        moreSpace: 1,   // 输入框底部预留的空白, 默认15, 单位像素
        maxHeight: 400,  // 指定Textarea的最大高度, 默认600, 单位像素
        animateDur: 10  // 调整高度时的动画过渡时间, 默认200, 单位毫秒
      }).keydown();
    }


    //绑定下载按钮事件
    $form.find("#reportHistoryDownLoad").click(function () {
      var n = $form.find(":input[name='e.subject']").val();// 获取文件名
      var sub_path = "report/history/";
      var path = $form.find(":input[name='e.path']").val()
      var f = sub_path + path;// 获取附件相对路径

      if (n == '' || path == '') {
        bc.msg.slied("附件路径错误，不能下载");
        return;
      }
      // 下载文件
      bc.file.download({f: f, n: n});
    });

    //绑定在线查看按钮事件
    $form.find("#reportHistoryInline").click(function () {
      var n = $form.find(":input[name='e.subject']").val();// 获取文件名
      var sub_path = "report/history/";
      var path = $form.find(":input[name='e.path']").val()
      var f = sub_path + path;// 获取附件相对路径

      if (n == '' || path == '') {
        bc.msg.slied("附件路径错误，不能查看！");
        return;
      }
      // 预览文件
      var option = {f: f, n: n};
      var ext = f.substr(f.lastIndexOf("."));
      if (ext == ".xml") {// Microsoft Word 2003 XML格式特殊处理
        option.from = "docx";
      }
      bc.file.inline(option);
    });


  }
};