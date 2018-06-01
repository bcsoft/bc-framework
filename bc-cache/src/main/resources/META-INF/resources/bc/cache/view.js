bc.namespace("bc.cache.view");
bc.cache.view = {
  /**
   * 删除全部缓存
   */
  deleteAll: function () {
    var $page = $(this)

    var option = {
      msg: "确定要清空全部的缓存吗？",
      url: bc.root + "/bc/cache/deleteAll"
    };

    bc.cache.view.delete_.call($page, option);
  },
  /**
   * 清空容器的缓存
   */
  delContainer: function () {
    var $page = $(this).closest(".bc-page");

    var option = {
      msg: "确定要清空容器的缓存吗？",
      url: bc.root + "/bc/cache/delContainer",
      data: {
        containerName: $(this).closest("td").text()
      }
    };

    bc.cache.view.delete_.call($page, option);
  },
  /**
   * 删除缓存
   */
  delCacheKey: function () {
    var $page = $(this).closest(".bc-page");

    var option = {
      msg: "确定要删除缓存吗？",
      url: bc.root + "/bc/cache/delCacheKey",
      data: {
        containerName: $(this).closest("td").siblings("td[data-column='containerName']").text(),
        cacheKey: $(this).closest("td").text()
      }
    };

    bc.cache.view.delete_.call($page, option);
  },
  /**
   * @param option
   * @option msg [String] 提示信息 not null
   * @option url [String] 删除的url not null
   * @option data [Object] 数据
   */
  delete_: function (option) {
    var $page = $(this);

    bc.msg.confirm(option.msg, function () {
      bc.ajax({
        url: option.url,
        data: option.data ? option.data : "",
        dataType: "json",
        success: function (json) {
          if (logger.debugEnabled) logger.debug("delete success.json=" + $.toJSON(json));
          if (json.success === false) {
            bc.msg.alert(json.msg);// 仅显示失败信息
          } else {
            //调用回调函数
            var showMsg = true;
            if (typeof option.callback == "function") {
              //返回false将禁止保存提示信息的显示
              if (option.callback.call($page[0], json) === false)
                showMsg = false;
            }
            if (showMsg)
              bc.msg.slide(json.msg);

            //重新加载列表
            bc.grid.reloadData($page);
          }
        }
      });
    });
  }
};