bc.selectIconClass = {
  init: function () {
    var $page = $(this);
    var $icons = $page.find("a.shortcut");
    $icons.click(function () {
      $icons.toggleClass("selectedIcon", false);
      $(this).toggleClass("selectedIcon", true);
    }).dblclick(function () {
      bc.selectIconClass.clickOk.apply($page[0], arguments);
      return false;
    });
  },
  clickOk: function () {
    var $page = $(this);
    var select = $page.find("a.selectedIcon").attr("title");
    if (!select || select.length == 0) {
      alert("必须先选择一个图标！");
      return false;
    }
    $page.data("data-status", select);
    $page.dialog("close");
  }
};