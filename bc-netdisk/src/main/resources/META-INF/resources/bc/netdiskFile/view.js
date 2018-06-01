if (!window['bc']) window['bc'] = {};
bc.netdiskFileView = {
  init: function () {
    var $form = $(this);
    //如果是火狐或苹果浏览器不支持上传文件夹
    if (!$.browser.webkit) {
      $form.find(":input[name='uploadFolder']").remove();
    }

  },
  //操作按钮组
  selectMenuButtonItem: function (option) {
    var $page = $(this);
    //获取选中节点的id
    var selectNodeId = bc.tree.getSelected($page.find(".bc-tree"));
    //上传文件
    if (option.value == "shangchuanwenjian") {

      //上传文件夹
    } else if (option.value == "shangchuanwenjianjia") {
      //如果是火狐或苹果浏览器不支持上传文件夹
      if (!$.browser.webkit) {
        bc.msg.alert("您的浏览器不支持上传整个文件夹。我们建议您使用 Google Chrome 网络浏览器，该浏览器支持文件夹上传。")
      }

      //新建个人文件夹
    } else if (option.value == "xinjiangerenwenjianjia") {

      bc.page.newWin({
        mid: "xinjiangerenwenjianjia",
        name: "新建个人文件夹",
        data: {dialogType: "xinjiangerenwenjianjia"},
        url: bc.root + "/bc/netdiskFile/create",
        afterClose: function (status) {
          if (status) bc.grid.reloadData($page);
          //刷新节点
          var tree = $page.find(".bc-tree");
          bc.tree.reload(tree, -1);
        }
      });
      //新建公共文件夹
    } else if (option.value == "xinjiangonggongwenjianjia") {
      bc.page.newWin({
        mid: "xinjiangonggongwenjianjia",
        name: "新建公共文件夹",
        data: {dialogType: "xinjiangonggongwenjianjia"},
        url: bc.root + "/bc/netdiskFile/create",
        afterClose: function (status) {
          if (status) bc.grid.reloadData($page);
          //刷新节点
          var tree = $page.find(".bc-tree");
          bc.tree.reload(tree, -2);
        }
      });
      //下载
    } else if (option.value == "xiazai") {

      var $page = $(this);
      // 确定选中的行
      var $trs = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
      if ($trs.length == 0) {
        bc.msg.slide("请先选择需要下载的文件！");
        return;
      } else if ($trs.length == 1) {
        var $tr = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
        var $hidden = $tr.data("hidden");
        //文件才支持下载，文件夹不支持
        if ($hidden.type == 1) {
          var n = $tr.find(">td:eq(1)").attr("data-value");// 获取文件名
          var f = "netdisk/" + $hidden.path;// 获取附件相对路径
          // 预览文件
          var option = {f: f, n: n, ptype: "Netdisk"};
          var ext = f.substr(f.lastIndexOf("."));
          if (ext == '.xls' || ext == ".xml") {// Microsoft Word 2003 XML格式特殊处理
            option.from = "docx";
          }
          bc.file.download(option);
        } else {
          bc.msg.alert("文件夹不支持直接下载！");
        }

      } else {
        bc.msg.alert("每次只能下载一个文件！");
      }

    }
  },
  //整理
  clearUp: function () {
    var $page = $(this);
    //获取选中节点的id
    var selectNodeId = bc.tree.getSelected($page.find(".bc-tree"));
    // 确定选中的行
    var $trs = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");

    //选择左边导航的文件进行整理
    if (selectNodeId && $trs.length == 0) {
      //不能对“我的硬盘”“公共硬盘”“共享给我的”的节点进行整理
      if (selectNodeId == -1 || selectNodeId == -2 || selectNodeId == -3) {
        bc.msg.alert("不能对根目录进行整理！");
      } else {
        bc.netdiskFileView.ajax4ClearUp($page, selectNodeId, 1);
      }
    } else if ((selectNodeId && $trs.length != 0) || (!selectNodeId && $trs.length != 0)) {
      //选择右边的文件进行整理
      if ($trs.length == 1) {
        var $leftTr = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
        var $rightTr = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
        var $hidden = $rightTr.data("hidden");
        bc.netdiskFileView.ajax4ClearUp($page, $leftTr.attr("data-id"), $hidden.type);
      } else {
        bc.msg.alert("每次只能整理一个文件！");
      }

    } else {
      bc.msg.slide("请先选择需要整理的文件！");
      return;
    }
  },
  /**整理文件的ajax方法
   * @param clearUpId 共享文件的id
   * @param view 视图上下文
   * @param type 文件类型
   */
  ajax4ClearUp: function (view, clearUpId, type) {
    var data = {};
    data.id = clearUpId;
    data.dialogType = "zhengliwenjian";
    bc.page.newWin({
      mid: "zhengliwenjian" + clearUpId,
      name: (type == 0 ? "整理文件夹" : "整理文件"),
      data: data,
      url: bc.root + "/bc/netdiskFile/createDialog",
      afterClose: function (status) {
        if (status) bc.grid.reloadData($page);
      }
    });
  },
  //共享
  share: function () {
    var $page = $(this);
    //获取选中节点的id
    var selectNodeId = bc.tree.getSelected($page.find(".bc-tree"));
    // 确定选中的行
    var $trs = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
    var $leftTr = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");

    //选择左边导航的文件进行共享设置
    if (selectNodeId && $trs.length == 0) {
      //不能对“我的硬盘”“公共硬盘”“共享给我的”的节点进行共享设置
      if (selectNodeId == -1 || selectNodeId == -2 || selectNodeId == -3) {
        bc.msg.alert("不能对根目录进行共享设置！");
      } else {
        bc.netdiskFileView.ajax4Share(selectNodeId, $page);
      }
    } else if ((selectNodeId && $trs.length != 0) || (!selectNodeId && $trs.length != 0)) {
      //选择右边的文件进行共享
      if ($trs.length == 1) {
        bc.netdiskFileView.ajax4Share($leftTr.attr("data-id"), $page);
      } else {
        bc.msg.alert("每次只能共享一个文件！");
      }

    } else {
      bc.msg.slide("请先选择需要共享的文件！");
      return;
    }
  },
  /**删除文件的ajax方法
   * @param shareId 共享文件的id
   * @param view 视图
   */
  ajax4Share: function (shareId, view) {
    var data = {};
    data.id = shareId;
    data.dialogType = "gongxiang";
    bc.page.newWin({
      mid: "gongxiang",
      name: "共享",
      data: data,
      url: bc.root + "/bc/netdiskFile/createDialog",
      afterClose: function (status) {
        if (status) bc.grid.reloadData(view);
      }
    });
  },

  //预览
  preview: function () {
    var $page = $(this);
    // 确定选中的行
    var $trs = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
    if ($trs.length == 0) {
      bc.msg.slide("请先选择需要预览的文件！");
      return;
    } else if ($trs.length == 1) {
      var $tr = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
      var $hidden = $tr.data("hidden");
      //文件才支持在线预览，文件夹不支持
      if ($hidden.type == 1) {
        var n = $tr.find(">td:eq(2)").attr("data-value");// 获取文件名
        var f = "netdisk/" + $hidden.path;// 获取附件相对路径
        // 预览文件
        var option = {f: f, n: n, ptype: "Netdisk"};
        var ext = f.substr(f.lastIndexOf("."));
        if (ext == '.xls' || ext == ".xml") {// Microsoft Word 2003 XML格式特殊处理
          option.from = "docx";
        }
        bc.file.inline(option);
      } else {
        bc.msg.alert("文件夹不支持在线查看！");
      }

    } else {
      bc.msg.alert("每次只能预览一个文件！");
    }
  },
  //双击预览
  dblclick: function () {
    var $page = $(this);
    var $grid = $page.find(".bc-grid");
    var $tr = $grid.find(">.data>.right tr.ui-state-highlight");
    var $hidden = $tr.data("hidden");
    //文件才支持在线预览，文件夹不支持
    if ($hidden.type == 1) {
      var n = $tr.find(">td:eq(1)").attr("data-value");// 获取文件名
      var f = "netdisk/" + $hidden.path;// 获取附件相对路径
      // 预览文件
      var option = {f: f, n: n, ptype: "Netdisk"};
      var ext = f.substr(f.lastIndexOf("."));
      if (ext == '.xls' || ext == ".xml") {// Microsoft Word 2003 XML格式特殊处理
        option.from = "docx";
      }
      bc.file.inline(option);
    } else {
      bc.msg.alert("该文件的类型是文件夹不支持在线预览！");
    }
  },
  //删除
  remove: function () {
    var $page = $(this);
    var $leftTr = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var id = $leftTr.attr("data-id");
    //获取选中节点的id
    var selectNodeId = bc.tree.getSelected($page.find(".bc-tree"));
    // 确定选中的行
    var $trs = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");

    //选择左边导航的文件进行删除
    if (selectNodeId != null && $trs.length == 0) {
      if (selectNodeId == -1 || selectNodeId == -2 || selectNodeId == -3) {
        bc.msg.alert("不能删除根目录！")
      } else {
        //删除一份文件夹
        var div4Delete = bc.msg.confirm("是否确定要删除该文件夹吗?<br><input type=\"checkbox\" name=\"isRelevanceDelete\" style=\"width:1em;\">删除该文件夹下的子文件", function () {
          var $div = $(div4Delete);
          var isRelevanceDelete = $div.find(":input[name='isRelevanceDelete']")[0].checked;
          //ajax删除方法
          bc.netdiskFileView.ajax4Delete({id: selectNodeId, isRelevanceDelete: isRelevanceDelete}, true, $page, true);
        });
      }
    } else if ((selectNodeId != null && $trs.length != 0) || (selectNodeId == null && $trs.length != 0)) {
      //选择右边的文件进行删除

      //删除一份文件时
      if ($trs.length == 1) {

        var $tr = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
        var $hidden = $tr.data("hidden");
        //删除一份文件
        if ($hidden.type == 1) {
          bc.msg.confirm("是否确定要删除该文件吗?", function () {
            //ajax删除方法
            bc.netdiskFileView.ajax4Delete({id: id}, false, $page);
          });
        } else {
          //删除一份文件夹
          var div4Delete = bc.msg.confirm("是否确定要删除该文件夹吗?<br><input type=\"checkbox\" name=\"isRelevanceDelete\" style=\"width:1em;\">删除该文件夹下的子文件", function () {
            var $div = $(div4Delete);
            var isRelevanceDelete = $div.find(":input[name='isRelevanceDelete']")[0].checked;
            //ajax删除方法
            bc.netdiskFileView.ajax4Delete({id: id, isRelevanceDelete: isRelevanceDelete}, true, $page, false);
          });
        }
        //删除多份文件时				
      } else {
        //多选
        var ids = "";
        //是否存在文件夹
        var isFolder = false;
        var $trs = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
        var $tds = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
        $tds.each(function (i) {
          if ($($trs.get(i)).data("hidden").type == 0) {
            isFolder = true;
          }
          //组装id
          ids += $(this).attr("data-id") + (i == $tds.length - 1 ? "" : ",");

        });
        //删除多份文件中含有文件夹的提示
        if (isFolder) {
          var div4Delete = bc.msg.confirm("删除的多份文件中包含文件夹！是否删除文件夹下的子文件？<br><input type=\"checkbox\" name=\"isRelevanceDelete\" style=\"width:1em;\">删除文件夹下的子文件", function () {
            var $div = $(div4Delete);
            var isRelevanceDelete = $div.find(":input[name='isRelevanceDelete']")[0].checked;
            //ajax删除方法
            bc.netdiskFileView.ajax4Delete({ids: ids, isRelevanceDelete: isRelevanceDelete}, true, $page, false);
          });
        } else {
          bc.msg.confirm("是否确定要删除多份文件吗?", function () {
            //ajax删除方法
            bc.netdiskFileView.ajax4Delete({ids: ids}, false, $page);
          });
        }

      }

    } else {
      bc.msg.slide("请先选择需要删除的文件！");
      return;
    }
  },

  /** 点击树节点的处理 */
  clickTreeNode: function (node) {
    var $tree = $(this);
    var $page = $tree.closest(".bc-page");
    logger.info("clickTreeNode: id=" + node.id + ",name=" + node.name);
    $page.data("extras").pid = node.id;
    bc.grid.reloadData($page);
  },
  /**删除文件的ajax方法
   * @param data 参数
   * @param isRefurbish 是否更新的节点
   * @param view 视图
   * @param isRefurbishParentNode 是否刷新父节点，否则刷新本本身
   */
  ajax4Delete: function (data, isRefurbish, view, isRefurbishParentNode) {
    bc.ajax({
      url: bc.root + "/bc/netdiskFile/delete",
      dataType: "json",
      data: data,
      success: function (json) {
        if (json.success) {
          bc.msg.slide(json.msg);
          view.data("data-status", "saved");
          bc.grid.reloadData(view);
          var tree = view.find(".bc-tree");
          //刷新节点
          if (isRefurbish) {
            //获取选中节点的id
            var selectNodeId = bc.tree.getSelected(tree);

            //获取选中节点的父节点的id
            var selectNodeParentId = bc.tree.getParentNodeId(bc.tree.getNode(tree, selectNodeId));

            if (isRefurbishParentNode) {//刷新父节点
              if (selectNodeParentId) {
                bc.tree.reload(tree, selectNodeParentId);
              }
            } else {//刷新本身
              if (selectNodeId) {
                bc.tree.reload(tree, selectNodeId);
              }
            }

          }

        } else {
          bc.msg.alert(json.msg);
        }
      }
    });

  }
};