var _themeId = 0;
bc.personal = {
  init: function () {
    var $form = this;
    var $slider = this.find("#fontSlider");
    var curValue = parseInt($slider.attr("data-value"));
    $form.find("#fontSize").html(bc.personal.getFontSizeDesc(curValue));
    logger.info("curValue=" + curValue);
    $slider.slider({
      value: curValue, min: 12, max: 20, step: 2,
      slide: function (event, ui) {
        $form.find("[name='e.font']").val(ui.value);
        $form.find("#fontSize").html(bc.personal.getFontSizeDesc(ui.value));
        $("body").css("fontSize", ui.value + 'px');
        logger.info(ui.value);
      }
    });

    //主题选择
    var curTheme = $form.find("input[name='e.theme']").val();
    logger.info("curTheme=" + curTheme);
    var $themes = $form.find("td.theme");
    $themes.filter("[data-theme='" + curTheme + "']").toggleClass("active", true);
    $themes.find("div").each(function () {
      $(this).attr("title", $(this).text());
    });
    $themes.hover(
      function () {
        $(this).toggleClass("over", true);
      },
      function () {
        $(this).toggleClass("over", false);
      }
    ).click(function () {
      $themes.toggleClass("active", false);
      var $this = $(this);
      $this.toggleClass("active", true);

      //记录选定的主题
      var themeName = $this.attr("data-theme");
      $form.find("input[name='e.theme']").val(themeName);

      //动态切换主题
      var css = $this.attr("data-css");
      if (css && css.length > 0) {
        css = css.split(",");
        var head = document.getElementsByTagName("head")[0];
        var preThemeId = _themeId++;
        for (var i = 0; i < css.length; i++) {
          var link = document.createElement("link");
          link.setAttribute("type", "text/css");
          link.setAttribute("rel", "stylesheet");
          link.setAttribute("href", bc.addParamToUrl(css[i], "ts=" + bc.ts));
          link.setAttribute("class", "ui-theme-" + _themeId);
          head.appendChild(link);
        }
        //移除之前添加的样式
        $(head).find("link.ui-theme-" + preThemeId).remove();
      } else {
        alert("error theme config: " + themeName);
      }
    });
  },
  getFontSizeDesc: function (fontSize) {
    switch (fontSize + "") {
      case "12":
        return "标准 (12px)";
      case "14":
        return "中 (14px)";
      case "16":
        return "大 (16px)";
      case "18":
        return "很大 (18px)";
      case "20":
        return "超大 (20px)";
      default:
        return "未知";
    }
  },
  save: function () {
    bc.page.save.call(this, function (json) {
      logger.info("json.id=" + json.id);
      //$(this).find("input[name='e.actorId']").val(json.id);
    });
  },
  /**弹出修改密码对话框*/
  changePassword: function () {
    var option = jQuery.extend({
      url: bc.root + "/bc/auth/setPassword",
      name: "修改我的登录密码",
      mid: "setPassword"
    }, option);

    bc.page.newWin(option);
  }
};