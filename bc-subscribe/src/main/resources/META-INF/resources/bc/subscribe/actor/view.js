bc.subscribeActorView = {
  /** 选择用户 */
  selectUser: function () {
    var $view = $(this);
    bc.identity.selectUser({
      status: '0',
      multiple: true,
      history: false,
      onOk: function (users) {
        bc.subscribeActorView.add.call($view, users);
      }
    });
  },
  /** 选择岗位 */
  selectGroup: function () {
    var $view = $(this);
    bc.identity.selectGroup({
      multiple: true,
      history: false,
      onOk: function (groups) {
        bc.subscribeActorView.add.call($view, groups);
      }
    });
  },
  /** 选择部门 或 单位 */
  selectUnitOrDepartment: function () {
    var $view = $(this);
    bc.identity.selectUnitOrDepartment({
      multiple: true,
      history: false,
      onOk: function (groups) {
        bc.subscribeActorView.add.call($view, groups);
      }
    });
  },
  /** 添加 **/
  add: function (actors) {
    var $view = $(this);
    var data = {id: $view.data("extras").sid};

    if (actors.length == 1) {
      data = $.extend({
        actorId: actors[0].id
      }, data);
    } else {
      var actorIds = "";
      $.each(actors, function (i, actor) {
        if (i > 0) actorIds += ",";
        actorIds += actor.id;
      });
      data = $.extend({
        actorIds: actorIds
      }, data);
    }

    bc.ajax({
      url: bc.root + "/bc/subscribe/add4manager",
      data: data,
      dataType: "json",
      success: function (json) {
        if (json.success) {
          bc.msg.slide(json.msg);
          bc.grid.reloadData($view);
        } else {
          bc.msg.alert(json.msg);
        }
      }
    });
  },
  /** 删除 */
  _delete: function () {
    var $view = $(this);
    var data = {id: $view.data("extras").sid};

    // 获取用户选中的条目
    var $trs = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
    if ($trs.size() == 0) {
      bc.msg.slide("请先选择需要删除的订阅人！");
      return;
    }

    var $trs = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");

    if ($trs.size() == 1) {
      data = $.extend({
        actorId: $trs.data("hidden").aid
      }, data);
    } else {
      var actorIds = "";
      $trs.each(function (i) {
        if (actorIds != "") actorIds += ",";
        actorIds += $(this).data("hidden").aid;
      });
      data = $.extend({
        actorIds: actorIds
      }, data);
    }

    bc.msg.confirm("确定删除选中的订阅人吗？",
      function () {
        bc.ajax({
          url: bc.root + "/bc/subscribe/delete4manager",
          data: data,
          dataType: "json",
          success: function (json) {
            if (json.success) {
              bc.msg.slide(json.msg);
              bc.grid.reloadData($view);
            } else {
              bc.msg.alert(json.msg);
            }
          }
        });
      }
    );
  }
};