if (!window['bc']) window['bc'] = {};
bc.netdiskFileForm = {
  init: function () {
    var $form = $(this);
    //选择文件夹
    $form.find("#selectFolder").click(function () {
      bc.selectFolder({
        folderId: $form.find(":input[name='e.id']").val(),
        onOk: function (folder) {
          $form.find(":input[name='folder']").val(folder.name);
          $form.find(":input[name='e.pid']").val(folder.id);
        }
      });
    });
    //清除选择的文件夹
    $form.find("#clearFolder").click(function () {
      $form.find(":input[name='folder']").val("");
      $form.find(":input[name='e.pid']").val("");
    });


    //绑定添加访问者的按钮事件处理
    //访问者列表
    var tableEl = $form.find("#visitorTables")[0];
    $form.find("#addVisitor").click(function () {
      bc.identity.selectUser({
        multiple: true,//可多选
        history: false,
        status: '0',
        onOk: function (users) {
          $.each(users, function (i, user) {

            //插入行
            var newRow = tableEl.insertRow(tableEl.rows.length);
            newRow.setAttribute("class", "ui-widget-content row");
            //插入列
            var cell = newRow.insertCell(0);
            cell.style.padding = "0";
            cell.style.textAlign = "left";
            cell.setAttribute("class", "id first");
            cell.innerHTML = '<span class="ui-icon"></span>';//空白头列

            cell = newRow.insertCell(1);
            cell.style.padding = "0";
            cell.style.textAlign = "left";
            cell.setAttribute("class", "middle");
            cell.innerHTML = '<input type="text" name="visitor" style="width:99%;height:100%;border:none;margin:0;padding:0 0 0 2px;'
              + 'background:none;" class="bc-select ui-widget-content" data-maxheight="150px" value="' + user.name + '"'
              + 'data-id=' + user.id + '>';
            //插入访问者名字

            cell = newRow.insertCell(2);//访问权限
            cell.style.padding = "0";
            cell.style.textAlign = "left";
            cell.setAttribute("class", "middle");
            cell.innerHTML = '<input type=\"checkbox\" name=\"edit\" style=\"width:1em;margin-right:7px;\">编辑';
            //暂时开放编辑权限
            // +'<input type=\"checkbox\" name=\"check\" checked=\"checked\" style=\"width:1em;margin-left:8px;margin-right:8px;\">查看'
            //  +'<input type=\"checkbox\" name=\"discuss\" style=\"width:1em;margin-left:8px;margin-right:8px;\">评论'//插入证件号
            cell = newRow.insertCell(3);
            cell.style.padding = "0";
            cell.style.textAlign = "left";
            cell.setAttribute("class", "middle");
            cell.innerHTML = '<input type="text" name="authorId" style="width:99%;height:100%;border:none;margin:0;padding:0 0 0 2px;'
              + 'background:none;" class="bc-select ui-widget-content" data-maxheight="150px" data-id="">';
            //插入添加者名字

            cell = newRow.insertCell(4);
            cell.style.padding = "0";
            cell.style.textAlign = "left";
            cell.setAttribute("class", "last");
            cell.innerHTML = '<input type="text" name="fileDate" style="width:99%;height:100%;border:none;margin:0;padding:0 0 0 2px;'
              + 'background:none;" class="bc-select ui-widget-content" data-maxheight="150px">';
            //插入添加时间
          });

        }
      });
    });
    //点击选中行
    $form.find("#visitorTables").delegate("tr.ui-widget-content.row>td.id", "click", function () {
      $(this).parent().toggleClass("ui-state-highlight").find("td:eq(0)>span.ui-icon").toggleClass("ui-icon-check");
    });
    $form.find("#visitorTables").delegate("tr.ui-widget-content.row input", "focus", function () {
      $(this).closest("tr.row").removeClass("ui-state-highlight").find("td:eq(0)>span.ui-icon").removeClass("ui-icon-check");
    });

    //全选
    $form.find("#visitorTables").delegate("tr.ui-state-default.row>td.first", "click", function () {
      var spanClass = $form.find("#visitorTables > tbody > tr.ui-state-default.row>td.first >span").attr("class");
      if (spanClass == "ui-icon ui-icon-notice") {
        $form.find("#visitorTables > tbody > tr:gt(0)").addClass("ui-state-highlight").find("td:eq(0)>span.ui-icon").addClass("ui-icon-check");
        $form.find("#visitorTables > tbody > tr.ui-state-default.row>td.first >span").addClass("ui-icon-check").removeClass("ui-icon-notice");
      } else {
        $form.find("#visitorTables > tbody > tr:gt(0)").removeClass("ui-state-highlight").find("td:eq(0)>span.ui-icon").removeClass("ui-icon-check");
        $form.find("#visitorTables > tbody > tr.ui-state-default.row>td.first >span").addClass("ui-icon-notice").removeClass("ui-icon-check");
      }
    });
    //删除表中选中的访问者
    $form.find("#deleteVisitor").click(function () {
      var $trs = $form.find("#visitorTables tr.ui-state-highlight");
      if ($trs.length == 0) {
        bc.msg.slide("请先选择要删除的访问者！");
        return;
      }
      bc.msg.confirm("确定要删除选定的 <b>" + $trs.length + "</b>个访问者吗？", function () {
        for (var i = 0; i < $trs.length; i++) {
          $($trs[i]).remove();
        }
      });

    });


  },
  /** 文件上传之前 */
  beforeUploadfile: function (continueUpload) {
    var $form = $(this).closest(".bc-page");
    //获取选中节点的id
    var selectNodeId = bc.tree.getSelected($form.find(".bc-tree"));
    //文件信息
    if (selectNodeId) {
      //选择节点上传
      //如果没有选中节点默认上传到“我的硬盘”	
      if (selectNodeId == -1) {
        //如果选择上传到“我的硬盘”
        if (continueUpload) {
          continueUpload.call();
        }
      } else if (selectNodeId == -2) {
        //如果选择上传到“公共硬盘”
        //判断是否有“公共硬盘”管理权限
        bc.ajax({
          url: bc.root + "/bc/netdiskFile/checkPublicHardDiskPower",
          dataType: "json",
          success: function (json) {
            //可以上传
            if (json.success) {
              if (continueUpload) {
                continueUpload.call();
              }
            } else {
              bc.msg.alert(json.msg);
            }

          }
        });
      } else if (selectNodeId == -3) {
        //如果选择上传到“共享到我的”	
//				if(continueUpload){
//					continueUpload.call();
//				}
        bc.msg.alert("不能上传文件到“共享给我”的根目录下！");
      } else {
        //判断选中的节点是否拥有管理权限
        bc.ajax({
          url: bc.root + "/bc/netdiskFile/checkSelectNodePower",
          dataType: "json",
          data: {selectNodeId: selectNodeId},
          success: function (json) {
            //可以上传
            if (json.success) {
              if (continueUpload) {
                continueUpload.call();
              }
            } else {
              bc.msg.alert(json.msg);
            }
          }
        });
      }
    } else {//无选中提示需要选中文件夹
      bc.msg.alert("请先选择要上传到的目录！(在左侧的文件夹导航区点选指定的文件夹后，再点击上传按钮上传)");
//			if(continueUpload){
//				continueUpload.call();
//			}
    }
  },
  /** 文件上传完毕后 */
  afterUploadfile: function (json4file) {
    var $form = $(this).closest(".bc-page");
    //获取选中节点的id
    var selectNodeId = bc.tree.getSelected($form.find(".bc-tree"));
    logger.info("--" + $.toJSON(json4file));
    //文件信息
    if (json4file != null) {
      if (selectNodeId != null) {
        //选择节点上传
        //不能上传文件到“共享给我”的根目录下
        if (selectNodeId != -3) {
          bc.netdiskFileForm.ajax4Uploadfile(json4file, selectNodeId, $form);
        }
      }
    }
  },
  /** 文件上传的ajax方法 */
  ajax4Uploadfile: function (json, selectNodeId, form) {
    var fileInfo = {};
    var fileInfos = [];
    if (json.success) {
      fileInfo = {
        name: json.source,
        size: json.size,
        path: json.to,
        selectNodeId: selectNodeId
      };
      if (fileInfo)
        fileInfos.push(fileInfo);
      //上传文件
      bc.ajax({
        url: bc.root + "/bc/netdiskFile/uploadfile",
        dataType: "json",
        data: {fileInfo: $.toJSON(fileInfos)},
        success: function (json) {
          logger.info("doLogout result=" + $.toJSON(json));
          //完成后提示用户
          bc.msg.slide(json.msg);
          if (json.success) bc.grid.reloadData(form);
          return false;
        }
      });
    } else
      bc.msg.alert(json.msg);
  },

  /** 文件夹上传完毕后 */
  afterUploadfolder: function (json4folder) {
    var $form = $(this).closest(".bc-page");
    //获取选中节点的id
    var selectNodeId = bc.tree.getSelected($form.find(".bc-tree"));
    if (json4folder != null) {
      if (selectNodeId != null) {
        //选择节点上传
        //不能上传文件夹到“共享给我”的根目录下
        if (selectNodeId != -3) {
          bc.netdiskFileForm.ajax4Uploadfolder(json4folder, selectNodeId, $form);
        }
      }
    }
  },
  /** 文件夹上传的ajax方法 */
  ajax4Uploadfolder: function (json, selectNodeId, form) {
    var fileInfo = {};
    var fileInfos = [];
    if (json.success) {
      fileInfo = {
        name: json.source,
        size: json.size,
        path: json.to,
        relativePath: json.relativePath,
        isDir: json.isDir,
        batchNo: json.batchNo,
        selectNodeId: selectNodeId
      };
      if (fileInfo)
        fileInfos.push(fileInfo);
      //上传文件
      bc.ajax({
        url: bc.root + "/bc/netdiskFile/uploadfolder",
        dataType: "json",
        data: {fileInfo: $.toJSON(fileInfos)},
        success: function (json) {
          logger.info("doLogout result=" + $.toJSON(json));
          //完成后提示用户
          bc.msg.slide(json.msg);
          if (json.success) bc.grid.reloadData(form);
          //刷新节点
          if (selectNodeId) {
            var tree = form.find(".bc-tree");
            bc.tree.reload(tree, selectNodeId);
          }

          return false;
        }
      });
    } else
      bc.msg.alert(json.msg);
  },
  //整理确定后的处理函数
  clickOk4clearUp: function () {
    var $form = $(this);
    var data = {};
    data.id = $form.find(":input[name='e.id']").val();
    data.pid = $form.find(":input[name='e.pid']").val();
    data.order = $form.find(":input[name='e.orderNo']").val();
    data.name = $form.find(":input[name='e.name']").val();
    bc.ajax({
      url: bc.root + "/bc/netdiskFile/clearUp",
      dataType: "json",
      data: data,
      success: function (json) {
        logger.info("doLogout result=" + $.toJSON(json));
        //完成后提示用户
        bc.msg.slide(json.msg);
        $form.data("data-status", "saved");
        $form.dialog("close");
//				if(json.success) bc.grid.reloadData($form);
        return false;
      }
    });
  },
  //共享确定后的处理函数
  clickOk4Share: function () {
    $page = $(this);
    //先将证件合并到隐藏域
    var visitors = [];
    //将补办证件表中的内容添加到certs里
    $page.find("#visitorTables tr:gt(0)").each(function () {
      var input = $(this).find("td>input");
      var td = $(this).find("td");
      var role = "";
      //编辑
      if ($(td).find(":input[name='edit']")[0].checked) {
        role = "1";
      } else {
        role = "0";
      }
      //查看check
//			if($(td).find(":input[name='check']")[0].checked){
//				role+=1;
//			}else{
//				role+=0;
//			}
//			//讨论discuss
//			if($(td).find(":input[name='discuss']")[0].checked){
//				role+=1;
//			}else{
//				role+=0;
//			}
      //查看，讨论，下载(默认有权限)
      role += "111";
      var json = {
        aid: $(td).find(":input[name='visitor']").attr("data-id"),
        role: role,
        authorId: $(td).find(":input[name='authorId']").attr("data-id"),
        fileDate: $(td).find(":input[name='fileDate']").val()
      };
      var id = $(this).attr("data-id");
      if (id && id.length > 0)
        json.id = id;
      visitors.push(json);

    });
    //访问者
    var fetcher = $.toJSON(visitors);
    //共享设置
    var editRole = $page.find("input[name='e.editRole']:checked").val();
    //id
    var id = $page.find("input[name='e.id']").val();
    bc.ajax({
      url: bc.root + "/bc/netdiskFile/share",
      dataType: "json",
      data: {visitors: fetcher, editRole: editRole, id: id},
      success: function (json) {
        logger.info("doLogout result=" + $.toJSON(json));
        //完成后提示用户
        if (json.success) {
          bc.msg.slide(json.msg);
          $page.data("data-status", "saved");
          $page.dialog("close");
        }

        return false;
      }
    });
  },
  //保存
  save: function () {
    var $form = $(this);
    //调用标准的方法执行保存
    bc.page.save.call($form, {
      callback: function (json) {
        bc.msg.slide(json.msg);
        $form.data("data-status", "saved");
        // 关闭当前窗口
        $form.dialog("close");

      }
    });

  }


};