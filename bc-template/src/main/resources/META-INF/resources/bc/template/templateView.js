bc.namespace("bc.template.view");
bc.template.view = {
  /** 点击树节点的处理 */
  clickTreeNode: function (node) {
    var $tree = $(this);
    var $page = $tree.closest(".bc-page");

    // 判断是否在loading
    if ($page.data("loading"))
      return;

    var startTime = new Date().getTime();
    $page.data("loading", true);
    $page.data("extras").pid = node.id;
    bc.grid.reloadData($page, {
      callback: function () {
        var now = new Date().getTime() - startTime;
        if (now > 500) {
          $page.removeData("loading");
        } else {
          setTimeout(function () {
            $page.removeData("loading");
          }, 500 - now);
        }
      }
    });
  }
}