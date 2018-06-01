define(function () {
  /**
   * 页面模块
   * @param $page 页面的jq对象
   * @param option 初始化参数
   * @param readonly 是否为只读页面
   * @constructor
   */
  function Form($page, option, readonly) {
    //绑定“帮助”链接的点击事件
    $page.find("#helpref").click(function () {
      var url = "http://rongjih.blog.163.com/blog/static/33574461201032011858793/";
      window.open(url, "_blank");
      return false;
      ;
    });
  }

  return Form;
});