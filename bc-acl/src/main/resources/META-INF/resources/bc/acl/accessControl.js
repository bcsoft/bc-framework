/**
 * 添加访问监控配置
 * @param {Object} option 配置参数
 * @option {String} docId [必填] 文档标识
 * @option {String} docType [必填] 文档类型
 * @option {String} docName [必填] 文档名称
 * @option {String} title [可选] 监控配置窗口标题
 * @option {String} name [可选] 监控配置窗口名称
 * @option {String} mid [可选] 监控配置窗口标识
 */
bc.addAccessControl = function (option) {
  if (option.docId == null) {
    bc.msg.alert("未配置文档标识");
    return;
  }

  if (option.docType == null) {
    bc.msg.alert("未配置文档类型");
    return;
  }

  if (option.docName == null) {
    bc.msg.alert("未配置文档名称");
    return;
  }

  //弹出选择对话框
  bc.page.newWin({
    url: bc.root + "/bc/accessControl/configureFromDoc",
    data: {
      docId: option.docId,
      docType: option.docType,
      docName: option.docName,
      isFromDoc: true,
      showRole: option.showRole ? option.showRole : null
    },
    mid: option.mid ? option.mid : "configureFromDoc." + option.docId + "." + option.docType,
    name: option.name ? option.name : "[" + option.docName + "]监控配置",
    title: option.title ? option.title : "[" + option.docName + "]监控配置",
  });
};

/**
 * 访问监控【可以在模块中直接调用】
 * 默认已选择查阅，
 * 视图必须要配置一下两个隐藏列
 *accessControlDocType
 *accessControlDocName
 *
 */
bc.accessControl = function () {
  var $page = $(this);
  // 获取用户选中的条目
  var ids = bc.grid.getSelected($page.find(".bc-grid"));
  if (ids.length == 0) {
    bc.msg.slide("请先选择需要访问配置的信息！");
    return;
  }

  if (ids.length > 1) {
    bc.msg.slide("只能对一个信息操作！");
    return;
  }

  var $tr = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
  var $hidden = $tr.data("hidden");

  if (!($hidden && $hidden.accessControlDocType)) {
    alert("视图必须要添加：accessControlDocType隐藏列！");
    return;
  }

  if (!($hidden && $hidden.accessControlDocName)) {
    alert("视图必须要添加：accessControlDocName隐藏列！");
    return;
  }

  bc.addAccessControl({
    docId: ids[0],
    docType: $hidden.accessControlDocType,
    docName: $hidden.accessControlDocName,
    showRole: "01"
  });
};
