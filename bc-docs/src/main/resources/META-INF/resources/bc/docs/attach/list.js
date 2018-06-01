bc.attachList = {
  /** 打开附件访问日志视图*/
  visitHistory: function () {
    bc.page.newWin({
      url: bc.root + "/bc/attachHistorys/paging",
      mid: "attachHistory.paging",
      name: "附件操作日志"
    });
  },
  /** 在线预览附件 */
  inline: function () {
    var $tds = $(this).find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    if ($tds.length == 1) {
      //在新窗口中打开文件
      window.open(bc.root + "/bc/attach/inline?to=pdf&id=" + $tds.attr("data-id"), "_blank");
    } else if ($tds.length > 1) {
      bc.msg.slide("一次只可以预览一个附件，请确认您只选择了一个附件！");
      return;
    } else {
      bc.msg.slide("请先选择要预览的附件！");
      return;
    }

  },
  /** 下载选择的附件*/
  download: function () {
    var $tds = $(this).find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    if ($tds.length == 1) {
      window.open(bc.root + "/bc/attach/download?id=" + $tds.attr("data-id"), "blank");
    } else if ($tds.length > 1) {
      bc.msg.slide("一次只可以下载一个附件，请确认您只选择了一个附件！");
      return;
    } else {
      bc.msg.slide("请先选择要下载的附件！");
      return;
    }
  },
  /** 将选择的附件打包下载*/
  downloadZip: function () {
    var $this = $(this);
    var url = $this.attr("data-deleteUrl");
    var $tds = $this.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    if ($tds.length == 0) {
      bc.msg.slide("请先选择要下载的附件！");
      return;
    }

    var ids = "";
    $tds.each(function (i) {
      ids += $(this).attr("data-id") + (i == $tds.length - 1 ? "" : ",");
    });
    window.open(bc.root + "/bc/attach/downloadAll?ids=" + ids, "blank");
  }
};