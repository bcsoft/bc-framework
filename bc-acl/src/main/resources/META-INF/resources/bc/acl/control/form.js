bc.accessControlForm = {
  init: function (option, readonly) {
    var $form = $(this);
    //加载访问者配置的权限
    $form.find("#actorTables tr.ui-widget-content.row").each(function () {
      var $tr = $(this);
      var $checkboxes = $tr.find(".actor_checkbox");
      var role = $tr.find(".actor_role").val();
      var role_array = $tr.find(".actor_role").val().split("");
      //匹配选中,"查阅"为默认选中
      for (var i = 1; i < role_array.length; i++) {
        if (role_array[role_array.length - i - 1] == "1") {
          if (i < $checkboxes.size()) {
            $checkboxes[i].checked = true;
          }
        }
      }
    });

    if (readonly) return;

    var tableEl = $form.find("#actorTables")[0];
    $form.find("#add").click(function () {
      var $addeds = $form.find("#actorTables .row");
      //已添加的用户
      var selecteds = ",";
      $addeds.each(function (index) {
        selecteds += $(this).find(".access_actor").attr("data-id");
        selecteds += ',';
      });

      var isFromDoc = $form.find(":input[name='isFromDoc']").val();

      bc.identity.selectUser({
        multiple: true,//可多选
        history: false,
        status: '0',
        onOk: function (users) {
          $.each(users, function (i, user) {
            //添加未添加的用户
            if (selecteds.indexOf("," + user.id + ",") == -1) {
              //插入行
              var newRow = tableEl.insertRow(tableEl.rows.length);
              newRow.setAttribute("class", "ui-widget-content row");
              //插入列
              var cell = newRow.insertCell(0);
              cell.setAttribute("class", "id first");
              cell.innerHTML = '<span class="ui-icon"></span>';//空白头列

              cell = newRow.insertCell(1);
              cell.setAttribute("class", "middle");
              cell.innerHTML = '<input type="text" class="access_actor" style="margin:0;border:none;background:none;"'
                + 'value=' + user.name + ' data-id=' + user.id + ' readonly="readonly"/>';

              cell = newRow.insertCell(2);
              cell.setAttribute("class", "last");

              if (isFromDoc == "true" && $form.find(":input[name='showRole']").val() == "01") {
                cell.innerHTML = '<label><input type="checkbox" class="actor_checkbox" checked onclick="return false;">查阅</label>'
                  + '<input type="hidden" class="actor_checkbox">';
              } else {
                cell.innerHTML = '<label><input type="checkbox" class="actor_checkbox" checked onclick="return false;">查阅</label>'
                  + '&nbsp;<label><input type="checkbox" class="actor_checkbox">编辑</label>';
              }
            }
          });
        }
      });

    });

    //点击选中行
    $form.find("#actorTables").delegate("tr.ui-widget-content.row>td.id", "click", function () {
      $(this).parent().toggleClass("ui-state-highlight").find("td:eq(0)>span.ui-icon").toggleClass("ui-icon-check");
    });
    $form.find("#actorTables").delegate("tr.ui-widget-content.row input", "focus", function () {
      $(this).closest("tr.row").removeClass("ui-state-highlight").find("td:eq(0)>span.ui-icon").removeClass("ui-icon-check");
    });

    //删除表中选中的
    $form.find("#delete").click(function () {
      var $trs = $form.find("#actorTables tr.ui-state-highlight");
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

    //上移表中选中的明细项目
    $form.find("#up").click(function () {
      var $trs = $form.find("#actorTables tr.ui-state-highlight");
      if ($trs.length == 0) {
        bc.msg.slide("请先选择要上移的！");
        return;
      } else {
        $trs.each(function () {
          var $tr = $(this);
          if ($tr[0].rowIndex < 2) {
            bc.msg.slide("选中的为第一条,不能再上移！");
          } else {
            var $beroreTr = $tr.prev();
            $beroreTr.insertAfter($tr);
          }
        });
      }

    });
    //下移表中选中的明细项目
    $form.find("#down").click(function () {
      var $trs = $form.find("#actorTables tr.ui-state-highlight");
      if ($trs.length == 0) {
        bc.msg.slide("请先选择要下移的！");
        return;
      } else {
        for (var i = $trs.length; i > 0; i--) {
          var $tr = $($trs[i - 1]);
          var $beroreTr = $tr.next();
          if ($beroreTr.length == 0) {
            bc.msg.slide("选中的为一条项目,不能再下移！");
          } else {
            $beroreTr.insertBefore($tr);
          }
        }
      }
    });

    //全选
    $form.find("#actorTables").delegate("tr.ui-state-default.row>td.first", "click", function () {
      var spanClass = $form.find("#actorTables > tbody > tr.ui-state-default.row>td.first >span").attr("class");
      if (spanClass == "ui-icon ui-icon-notice") {
        $form.find("#actorTables > tbody > tr:gt(0)").addClass("ui-state-highlight").find("td:eq(0)>span.ui-icon").addClass("ui-icon-check");
        $form.find("#actorTables > tbody > tr.ui-state-default.row>td.first >span").addClass("ui-icon-check").removeClass("ui-icon-notice");
      } else {
        $form.find("#actorTables > tbody > tr:gt(0)").removeClass("ui-state-highlight").find("td:eq(0)>span.ui-icon").removeClass("ui-icon-check");
        $form.find("#actorTables > tbody > tr.ui-state-default.row>td.first >span").addClass("ui-icon-notice").removeClass("ui-icon-check");
      }
    });
  },
  /**
   * 保存
   */
  save: function () {
    var $form = $(this);

    if (!bc.validator.validate($form)) return;

    var $rows = $form.find("#actorTables tr.ui-widget-content.row");
    var data = [];
    $rows.each(function (index) {
      var role = "";
      var $checkboxes = $(this).find(".actor_checkbox");
      $checkboxes.each(function () {
        if ($(this)[0].checked == true) {
          role += "1";
        } else {
          role += "0";
        }
      });
      //字符串反转
      role = role.split("").reverse().join("");
      data.push({
        id: $(this).find(".access_actor").attr("data-id"),
        role: role
      });
    });

    $form.find(":input[name='accessActors']").val($.toJSON(data));
    var isFromDoc = $form.find(":input[name='isFromDoc']").val();

    var id = $form.find(":input[name='e.id']").val();
    if (id == "" && $rows.size() == 0 && isFromDoc == "true") {
      bc.msg.alert("请添加访问者！");
      return;
    }

    if (isFromDoc == "false") {
      //调用标准的方法执行保存
      bc.page.save.call($form);
      return;
    }

    if ($rows.size() == 0) {
      bc.msg.confirm("若不配置访问者，确定后系统将删除此监控配置！", function () {
        bc.ajax({
          url: bc.root + "/bc/accessControl/delete",
          data: {id: id},
          dataType: "json",
          success: function (json) {
            if (json.success) {
              bc.msg.slide("删除成功");
              $form.dialog("close");
            } else {
              bc.msg.alert(json.msg);
            }
          }
        });
      });
    } else {
      //调用标准的方法执行保存
      bc.page.save.call($form, {
        callback: function (json) {
          bc.msg.slide("确定成功");
          $form.dialog("close");
          return false;
        }
      });
    }
  },
  /**
   * 查看访问历史
   */
  history: function () {
    var $form = $(this);
    var pid = $form.find(":input[name='e.id']").val();

    if (pid == "") {
      bc.msg.slide("请先保存监控配置信息！");
    }

    bc.page.newWin({
      url: bc.root + "/bc/accessHistorys/paging",
      data: {pid: pid, isFromDoc: true},
      mid: "accessHistorys::" + pid,
      name: $form.find(":input[name='e.docName']").val() + "的访问历史",
      title: $form.find(":input[name='e.docName']").val() + "的访问历史"
    });
  }
};