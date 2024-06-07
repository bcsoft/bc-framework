if (!window['bc']) window['bc'] = {};
bc.copyUserRole = {
  init: function () {
    var $form = $(this);

    let data = $form.data("data");

    $form.find("input[name='copyFromId']").val(data.id)
    $form.find("input[name='copyFrom']").val(data.name)

    var liTpl = '<li class="horizontal ui-widget-content ui-corner-all ui-state-highlight" data-id="{0}">' +
      '<span class="text">{1}</span>' +
      '<span class="click2remove verticalMiddle ui-icon ui-icon-close" title={2}></span></li>';
    var ulTpl = '<ul class="horizontal"></ul>';

    var title = $form.find("#assignGroups").attr("data-removeTitle");
    //绑定添加岗位的按钮事件处理
    $form.find("#addGroups").click(function () {
      var $ul = $form.find("#assignGroups ul");
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
                $ul = $(ulTpl).appendTo($form.find("#assignGroups"));
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

    //绑定添加角色的按钮事件处理
    $form.find("#addRoles").click(function () {
      var $ul = $form.find("#assignRoles ul");
      var $lis = $ul.find("li");
      var selecteds = "";
      $lis.each(function (i) {
        selecteds += (i > 0 ? "," : "") + $(this).attr("data-id");//已选择的id
      });
      bc.identity.selectRole({
        multiple: true,
        selecteds: selecteds,
        onOk: function (roles) {
          //添加当前没有分派的岗位
          $.each(roles, function (i, role) {
            if ($lis.filter("[data-id='" + role.id + "']").size() > 0) {//已存在
              logger.info("duplicate select: id=" + role.id + ",name=" + role.name);
            } else {//新添加的
              if (!$ul.size()) {//先创建ul元素
                $ul = $(ulTpl).appendTo($form.find("#assignRoles"));
              }
              $(liTpl.format(role.id, role.name, title))
                .appendTo($ul).find("span.click2remove")
                .click(function () {
                  $(this).parent().remove();
                });
            }
          });
        }
      });
    });

    //绑定删除岗位或角色的按钮事件处理
    $form.find("span.click2remove").click(function () {
      $(this).parent().remove();
    });

    //绑定添加用户的按钮事件处理
    $form.find("#selectCopyTo").click(function () {
      bc.identity.selectUser({
        multiple: false,
        history: false,
        onOk: function (actor) {
          $form.find("input[name='copyTo']").val(actor.name)
          $form.find("input[name='copyToId']").val(actor.id)
        }
      });
    });
  },
  /** 点击确认按钮后的处理函数 */
  clickOk: function () {
    var $form = $(this);

    if (!bc.validator.validate($form)) return;

    let copyTo = $form.find("input[name='copyTo']").val();
    let copyFrom = $form.find("input[name='copyFrom']").val();
    bc.msg.confirm(`确定要复制<b>${copyFrom}</b>用户的岗位和角色给<b>${copyTo}</b>用户吗？`, function () {
      require(["bc/libs/request"], function success(request) {
        let id = $form.find("input[name='copyToId']").val();
        //先将岗位和角色的id合并到隐藏域
        let assignGroupIds = [], assignRoleIds = [];
        $form.find("#assignGroups li").each(function () {
          assignGroupIds.push($(this).attr("data-id"));
        });
        $form.find("#assignRoles li").each(function () {
          assignRoleIds.push($(this).attr("data-id"));
        });

        request.request({
          method: "POST",
          contentType: "application/x-www-form-urlencoded",
          credentials: "same-origin",
          url: `${bc.root}/bc/user/doCopyUserRole`,
          body: `id=${id}&assignGroupIds=${assignGroupIds}&assignRoleIds=${assignRoleIds}`,
        }).then(result => {
          result = JSON.parse(result)
          // 成功执行绑定或解绑操作
          if (result.success) {
            bc.msg.slide("权限复制成功");
            $form.dialog("close");
          } else bc.msg.info(`权限复制失败：${result.message}`);
        }).catch(e => {
          // 提示错误信息
          bc.msg.info(e.message);
        });
      });
    });
  }
};