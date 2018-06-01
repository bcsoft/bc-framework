bc.browser = {
  init: function () {
    var $page = $(this);
    $page.on({
      mouseover: function () {
        $(this).addClass("ui-state-hover");
      },
      mouseout: function () {
        $(this).removeClass("ui-state-hover");
      },
      click: function () {
        bc.browser.download($(this).attr("data-id"));
      }
    }, "li.browser");
  },
  /**下载指定的浏览器安装文件
   * @param id browser表的id
   */
  download: function (id) {
    window.open(bc.root + "/bc/browser/download?id=" + id, "blank");
  }
};