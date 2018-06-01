define(["jquery", "bc", "bc.identity"], function ($, bc) {
  "use strict";

  /**
   * 页面模块
   * @param $page 页面的jq对象
   * @constructor
   */
  function Form($page) {
    this.$page = $page;
  }

  Form.prototype = {
    init: function () {
      var $page = this.$page;
      //绑定选择所属分类的按钮事件处理
      $page.find("#selectBelong").click(function () {
        var excludes = $page.find(":input[name='e.id']").val();
        bc.identity.selectResource({
          types: '1',
          excludes: excludes,
          onOk: function (resource) {
            if (excludes != resource.id) {
              $page.find(":input[name='e.belong.name']").val(resource.name);
              $page.find(":input[name='e.belong.id']").val(resource.id);
            } else {
              alert("不能选择自己作为自己的所属分类！");
            }
          }
        });
      });

      var urlText = $page.find("#urlText").attr("data-text");
      //绑定类型选择变动事件
      $page.find(":radio").change(function () {
        var $this = $(this);
        var type = $this.val();
        logger.info("select:" + this.id + "," + type);
        if (type == "5") {//操作--无需相关配置
          $page.find("td[data-name='iconClass'],td[data-name='option'],td[data-name='url']").hide();
          $page.find(":input[name='e.url']").removeAttr("data-validate");
          $page.find("#urlText").text(urlText + "：");
        } else {//链接
          $page.find("td[data-name='iconClass'],td[data-name='option'],td[data-name='url']").show();

          //如果是链接了类型，强制链接为必填域
          if (type == "2" || type == "3") {
            $page.find(":input[name='e.url']").attr("data-validate", "required");
            $page.find("#urlText").text("*" + urlText + "：");
          } else {
            $page.find(":input[name='e.url']").removeAttr("data-validate");
            $page.find("#urlText").text(urlText + "：");
          }
        }
      });
      //以当前选中的选项触发一下change事件对界面做一下处理
      $page.find(":radio:checked").trigger("change");
      $page.find(":input[name='e.name']").focus();

      //绑定选择图标样式的按钮事件处理
      $page.find("#selectIconClass").click(function () {
        bc.page.newWin({
          url: bc.root + "/bc/shortcut/selectIconClass",
          name: "选择图标样式",
          mid: "selectShortcutIconClass4Module",
          afterClose: function (iconClass) {
            logger.info("iconClass=" + iconClass);
            if (iconClass) {
              $page.find(":input[name='e.iconClass']").val(iconClass);
            }
          }
        });
      });
    }
  };

  return Form;
});