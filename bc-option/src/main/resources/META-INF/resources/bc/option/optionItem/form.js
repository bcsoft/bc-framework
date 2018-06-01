bc.optionItemForm = {
  init: function () {
    var $form = $(this);
    //绑定选择所属分组的事件
    $form.find("#selectOptionGroup").click(function () {
      var data = {};
      var selected = $form.find(":input[name='e.optionGroup.id']").val();
      if (selected && selected.length > 0)
        data.selected = selected;

      var option = jQuery.extend({
        url: bc.root + "/bc/selectOptionGroup",
        name: "选择所属分组信息",
        mid: "selectOptionGroup",
        afterClose: function (status) {
          if (status) {
            $form.find(":input[name='e.optionGroup.id']").val(status.id);
            $form.find(":input[name='e.optionGroup.value']").val(status.name);
          }
        }
      }, option);

      bc.page.newWin(option);
    });

    //绑定选择图标样式的按钮事件处理
    $form.find("#selectIconClass").click(function () {
      bc.page.newWin({
        url: bc.root + "/bc/shortcut/selectIconClass",
        name: "选择图标样式",
        mid: "selectShortcutIconClass4Module",
        afterClose: function (iconClass) {
          logger.info("iconClass=" + iconClass);
          if (iconClass) {
            $form.find(":input[name='e.icon']").val(iconClass);
          }
        }
      });
    });
  }
};