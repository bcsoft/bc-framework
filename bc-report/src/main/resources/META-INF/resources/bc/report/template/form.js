bc.reportTemplateForm = {
  init: function (option, readonly) {
    var $form = $(this);

    // 让详细配自动高度
    var $cfg = $form.find(":input[name='e.config']");
    var cfgEl = $cfg.get(0);
    if (cfgEl.scrollHeight > 170) {
      cfgEl.style.height = cfgEl.scrollHeight + "px";
    }

    //只读权限控制
    if (readonly) return;

    var liTpl = '<li class="horizontal reportUserLi ui-widget-content ui-corner-all ui-state-highlight" data-id="{0}"' +
      'style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">' +
      '<span class="text">{1}</span>' +
      '<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title={2}></span></li>';
    var ulTpl = '<ul class="horizontal reportUserUl" style="padding: 0 45px 0 0;"></ul>';
    var title = $form.find("#assignUsers").attr("data-removeTitle");

    //绑定添加用户的按钮事件处理
    $form.find("#addUsers").click(function () {
      var $ul = $form.find("#assignUsers .reportUserUl");
      var $lis = $ul.find("li");
      var selecteds = "";
      $lis.each(function (i) {
        selecteds += (i > 0 ? "," : "") + ($(this).attr("data-id"));//
      });
      bc.identity.selectUser({
        multiple: true,//可多选
        history: false,
        selecteds: selecteds,
        onOk: function (users) {
          $.each(users, function (i, user) {
            if ($lis.filter("[data-id='" + user.id + "']").size() > 0) {//已存在
              logger.info("duplicate select: id=" + user.id + ",name=" + user.name);
            } else {//新添加的
              if (!$ul.size()) {//先创建ul元素
                $ul = $(ulTpl).appendTo($form.find("#assignUsers"));
              }
              $(liTpl.format(user.id, user.name, title))
                .appendTo($ul).find("span.click2remove")
                .click(function () {
                  $(this).parent().remove();
                });
            }
          });
        }
      });
    });

    //绑定添加岗位的按钮事件处理
    $form.find("#addGroups").click(function () {
      var $ul = $form.find("#assignUsers .reportUserUl");
      var $lis = $ul.find("li");
      var selecteds = "";
      $lis.each(function (i) {
        selecteds += (i > 0 ? "," : "") + $(this).attr("data-id");//已选择的id
      });
      bc.identity.selectGroup({
        multiple: true,
        selecteds: selecteds,
        onOk: function (groups) {
          //添加当前没有分派的岗位
          $.each(groups, function (i, group) {
            if ($lis.filter("[data-id='" + group.id + "']").size() > 0) {//已存在
              logger.info("duplicate select: id=" + group.id + ",name=" + group.name);
            } else {//新添加的
              if (!$ul.size()) {//先创建ul元素
                $ul = $(ulTpl).appendTo($form.find("#assignUsers"));
              }
              $(liTpl.format(group.id, group.name, title))
                .appendTo($ul).find("span.click2remove")
                .click(function () {
                  $(this).parent().remove();
                });
            }
          });
        }
      });
    });

    //绑定添加单位或部门的按钮事件处理
    $form.find("#addUnitOrDepartments").click(function () {
      var $ul = $form.find("#assignUsers .reportUserUl");
      var $lis = $ul.find("li");
      var selecteds = "";
      $lis.each(function (i) {
        selecteds += (i > 0 ? "," : "") + $(this).attr("data-id");//已选择的id
      });
      bc.identity.selectUnitOrDepartment({
        multiple: true,
        selecteds: selecteds,
        onOk: function (groups) {
          //添加当前没有分派的岗位
          $.each(groups, function (i, group) {
            if ($lis.filter("[data-id='" + group.id + "']").size() > 0) {//已存在
              logger.info("duplicate select: id=" + group.id + ",name=" + group.name);
            } else {//新添加的
              if (!$ul.size()) {//先创建ul元素
                $ul = $(ulTpl).appendTo($form.find("#assignUsers"));
              }
              $(liTpl.format(group.id, group.name, title))
                .appendTo($ul).find("span.click2remove")
                .click(function () {
                  $(this).parent().remove();
                });
            }
          });
        }
      });
    });

    //绑定删除角色、用户的按钮事件处理
    $form.find("span.click2remove").click(function () {
      $(this).parent().remove();
    });

    // 将详细配置变为代码高亮编辑器
    bc.reportTemplateForm.editor = CodeMirror.fromTextArea($form.find("textarea[name='e.config']")[0], {
      json: true,
      lineNumbers: true,
      matchBrackets: true,
      theme: "eclipse"
    });
  },
  /**保存的处理*/
  save: function () {
    var $page = $(this);

    //详细配置赋值
    if (bc.reportTemplateForm.editor) bc.reportTemplateForm.editor.save();

    //将用户的id合并到隐藏域
    ids = [];
    $page.find("#assignUsers .reportUserLi").each(function () {
      ids.push($(this).attr("data-id"));
    });
    $page.find(":input[name=assignUserIds]").val(ids.join(","));
    var code = $page.find(":input[name='e.code']").val();
    var id = $page.find(":input[name='e.id']").val();
    var url = bc.root + "/bc/reportTemplate/isUniqueCode";

    if (code == '') {
      bc.msg.slide("请输入编码");
      return null;
    }

    //检查编码唯一性ajax请求
    $.ajax({
      url: url,
      data: {rid: id, code: code},
      dataType: "json",
      success: function (json) {
        if (json.result) {
          //调用标准的方法执行保存
          bc.page.save.call($page);
        } else {
          bc.msg.alert("系统中其它报表模板已使用此编码！");
        }
      }
    });
  },
  /**执行的处理**/
  execute: function () {
    var $form = $(this);
    var code = $form.find(":input[name='e.code']").val();
    var name = $form.find(":input[name='e.name']").val();
    bc.msg.confirm("确定要执行<b>" + name + "</b>吗？", function () {
      //弹出选择对话框
      bc.page.newWin({
        url: bc.root + "/bc/report/run?code=" + code,
        name: name,
        mid: code
      });
    });
  }
};