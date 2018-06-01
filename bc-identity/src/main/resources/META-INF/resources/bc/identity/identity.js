bc.identity = {
  /**
   * 选择单位信息
   * @param {Object} option 配置参数
   * @option {String} selecteds 当前应选中的项的值，多个值用逗号连接
   * @option {String} excludes 要排除显示的项的值，多个值用逗号连接
   * @option {Function} onOk 选择完毕后的回调函数，函数第一个参数为选中的单位信息
   */
  selectUnit: function (option) {
    option.data = jQuery.extend({
      multiple: false,
      history: false
    }, option.data);
    if (option.selecteds)
      option.data.selecteds = option.selecteds;
    if (option.excludes)
      option.data.excludes = option.excludes;
    if (option.history === true)
      option.data.history = true;
    if (option.multiple === true)
      option.data.multiple = true;

    option = jQuery.extend({
      url: bc.root + "/bc/selectUnit",
      name: "选择单位信息",
      mid: "selectUnit",
      afterClose: function (status) {
        if (status && typeof(option.onOk) == "function") {
          option.onOk(status);
        }
      }
    }, option);

    bc.page.newWin(option);
  },

  /**
   * 选择单位或部门信息
   * @param {Object} option 配置参数
   * @option {String} selecteds 当前应选中的项的值，多个值用逗号连接
   * @option {String} excludes 要排除显示的项的值，多个值用逗号连接
   * @option {Function} onOk 选择完毕后的回调函数，函数第一个参数为选中的单位信息
   */
  selectUnitOrDepartment: function (option) {
    option.data = jQuery.extend({
      multiple: false,
      history: false
    }, option.data);
    if (option.selecteds)
      option.data.selecteds = option.selecteds;
    if (option.excludes)
      option.data.excludes = option.excludes;
    if (option.history === true)
      option.data.history = true;
    if (option.multiple === true)
      option.data.multiple = true;

    option = jQuery.extend({
      url: bc.root + "/bc/selectUnitOrDepartments/paging",
      name: "选择单位或部门信息",
      mid: "selectUnitOrDepartment",
      afterClose: function (status) {
        if (status && typeof(option.onOk) == "function") {
          option.onOk(status);
        }
      }
    }, option);

    bc.page.newWin(option);
  },

  /**
   * 选择部门信息
   * @param {Object} option 配置参数
   * @option {String} selecteds 当前应选中的项的值，多个值用逗号连接
   * @option {String} excludes 要排除显示的项的值，多个值用逗号连接
   * @option {Function} onOk 选择完毕后的回调函数，函数第一个参数为选中的单位信息
   */
  selectDepartment: function (option) {
    option.data = jQuery.extend({
      multiple: false,
      history: false
    }, option.data);
    if (option.selecteds)
      option.data.selecteds = option.selecteds;
    if (option.excludes)
      option.data.excludes = option.excludes;
    if (option.history === true)
      option.data.history = true;
    if (option.multiple === true)
      option.data.multiple = true;

    option = jQuery.extend({
      url: bc.root + "/bc/selectDepartment",
      name: "选择部门信息",
      mid: "selectDepartment",
      afterClose: function (status) {
        if (status && typeof(option.onOk) == "function") {
          option.onOk(status);
        }
      }
    }, option);

    bc.page.newWin(option);
  },

  /**
   * 选择岗位信息
   * @param {Object} option 配置参数
   * @option {String} actorId 用户的id
   * @option {String} assignedGroupIds 已分派岗位的id列表，多个值用逗号连接
   * @option {Boolean} multiple 是否允许多选，默认false
   * @option {Function} onOk 选择完毕后的回调函数，函数第一个参数为选中的岗位信息(数组)
   */
  selectGroup: function (option) {
    option.data = jQuery.extend({
      multiple: false,
      history: false
    }, option.data);
    if (option.selecteds)
      option.data.selecteds = option.selecteds;
    if (option.excludes)
      option.data.excludes = option.excludes;
    if (option.history === true)
      option.data.history = true;
    if (option.multiple === true)
      option.data.multiple = true;

    option = jQuery.extend({
      url: bc.root + "/bc/selectGroups/paging",
      name: "选择岗位信息",
      mid: "selectGroup",
      afterClose: function (status) {
        if (status && typeof(option.onOk) == "function") {
          option.onOk(status);
        }
      }
    }, option);

    bc.page.newWin(option);
  },

  /**
   * 给指定的actor(单位、部门、岗位或用户)分配角色
   * @param {Object} option 配置参数
   * @option {String} selecteds 已选择的id列表，多个值用逗号连接
   * @option {String} excludes 要排除显示的项的值，多个值用逗号连接
   * @option {Boolean} multiple 是否允许多选，默认false
   * @option {Function} onOk 选择完毕后的回调函数，函数第一个参数为选中的角色信息(数组)
   */
  selectRole: function (option) {
    option.data = jQuery.extend({
      multiple: false
    }, option.data);
    if (option.selecteds)
      option.data.selecteds = option.selecteds;
    if (option.excludes)
      option.data.excludes = option.excludes;
    if (option.multiple === true)
      option.data.multiple = true;

    option = jQuery.extend({
      url: bc.root + "/bc/identity/role/select/paging",
      name: "选择角色信息",
      mid: "selectRole",
      afterClose: function (status) {
        if (status && typeof(option.onOk) == "function") {
          option.onOk(status);
        }
      }
    }, option);

    bc.page.newWin(option);
  },

  /**
   * 选择模块信息
   * @param {Object} option 配置参数
   * @option {String} selecteds 已选择的id列表，多个值用逗号连接
   * @option {String} excludes 要排除显示的项的值，多个值用逗号连接
   * @option {Boolean} multiple 是否允许多选，默认false
   * @option {String} types 选择的资源类型，多个值用逗号连接，为空代表选择所有类型，默认为非分类资源
   * @option {Function} onOk 选择完毕后的回调函数，函数第一个参数为选中的角色信息(数组)
   */
  selectResource: function (option) {
    option.data = jQuery.extend({
      multiple: false,
      types: "2,3,4"
    }, option.data);
    if (option.selecteds)
      option.data.selecteds = option.selecteds;
    if (option.excludes)
      option.data.excludes = option.excludes;
    if (option.multiple === true)
      option.data.multiple = true;
    if (option.types)
      option.data.types = option.types;

    option = jQuery.extend({
      url: bc.root + "/bc/identity/resource/select/paging",
      name: "选择资源信息",
      mid: "selectResource",
      afterClose: function (status) {
        if (status && typeof(option.onOk) == "function") {
          option.onOk(status);
        }
      }
    }, option);

    bc.page.newWin(option);
  },

  /**
   * 选择用户信息
   * @param {Object} option 配置参数
   * @option {String} selecteds 已选择用户的id列表，多个值用逗号连接
   * @option {String} excludes 要排除显示的项的值，多个值用逗号连接
   * @option {String} group 所属岗位
   * @option {Boolean} multiple 是否允许多选，默认false
   * @option {Boolean} history 是否选择ActorHistory信息，默认true，设为false选择Actor信息
   * @option {Function} onOk 选择完毕后的回调函数，函数第一个参数为选中的用户信息(多选时数组，单选时时对象)
   */
  selectUser: function (option) {
    option.data = jQuery.extend({
      multiple: false,
      history: true,
      status: "0,1"
    }, option.data);
    if (option.selecteds)
      option.data.selecteds = option.selecteds;
    if (option.excludes)
      option.data.excludes = option.excludes;
    if (option.group)
      option.data.group = option.group;
    if (option.history === false)
      option.data.history = false;
    if (option.multiple === true)
      option.data.multiple = true;
    if (option.status)
      option.data.status = option.status;

    option = jQuery.extend({
      url: bc.root + "/bc/selectUsers/paging",
      name: "选择用户信息",
      mid: "selectUser",
      history: true,
      afterClose: function (status) {
        if (status && typeof(option.onOk) == "function") {
          option.onOk(status);
        }
      }
    }, option);

    bc.page.newWin(option);
  },

  /**
   * 获取用户信息
   * @param {Object} option 配置参数
   * @option {String} group 所属岗位
   * @option {String} status 用户状态 0-启用中,1-已禁用,2-已删除
   */
  getUser: function (option) {
    option.data = jQuery.extend({
      status: "0,1"
    }, option.data);

    bc.ajax({
      url: option.url,
      data: option.data,
      dataType: "json",
      success: function (json) {
        option.onSuccess(json);
      }
    });
  }
};

var $document = $(document);

/**
 * 选择用户的自动处理。
 * 需要在dom元素中配置data-cfg属性，格式为：
 *  {"valueField":"[值对应的表单域的name]","mapping":"域1name=id,域2name=name"}}
 *  如{"valueField":"e.unit.id","mapping":"e.unit.id=id,e.unit.name=name"}}
 * mapping中等于号后的值为用户信息中的key
 */
$document.delegate(".selectUser", {
  click: function () {
    var $this = $(this);
    var cfg = $this.data("cfg");
    if (!cfg) {
      alert("没有配置dom元素data-cfg属性的值，无法处理！");
      return;
    }
    if (typeof cfg == "string")
      cfg = {mapping: cfg};

    var $form = $this.closest("form");
    bc.identity.selectUser({
      history: cfg.history || false,
      selecteds: cfg.valueField ? $form.find(":input[name='" + cfg.valueField + "']").val() : null,
      onOk: function (user) {
        var mapping = cfg.mapping.split(",");
        var c;
        for (var i = 0; i < mapping.length; i++) {
          c = mapping[i].split("=");
          if (c.length != 2) {
            alert("mapping的格式配置错误，无法处理！请检查dom元素data-cfg的配置,mapping=" + mapping[i]);
            return;
          }
          $form.find(":input[name='" + c[0] + "']").val(user[c[1]]);
        }
      }
    });
  }
});

/**
 * 选择单位的自动处理。
 * 需要在dom元素中配置data-cfg属性，格式为：
 *  {"valueField":"[值对应的表单域的name]","mapping":"域1name=id,域2name=name"}}
 *  如{"valueField":"e.unit.id","mapping":"e.unit.id=id,e.unit.name=name"}}
 * mapping中等于号后的值为用户信息中的key
 */
$document.delegate(".selectUnit", {
  click: function () {
    var $this = $(this);
    var cfg = $this.data("cfg");
    if (!cfg) {
      alert("没有配置dom元素data-cfg属性的值，无法处理！");
      return;
    }
    if (typeof cfg == "string")
      cfg = {mapping: cfg};

    var $form = $this.closest("form");
    bc.identity.selectUnit({
      selecteds: cfg.valueField ? $form.find(":input[name='" + cfg.valueField + "']").val() : null,
      onOk: function (user) {
        var mapping = cfg.mapping.split(",");
        var c;
        for (var i = 0; i < mapping.length; i++) {
          c = mapping[i].split("=");
          if (c.length != 2) {
            alert("mapping的格式配置错误，无法处理！请检查dom元素data-cfg的配置,mapping=" + mapping[i]);
            return;
          }
          $form.find(":input[name='" + c[0] + "']").val(user[c[1]]);
        }
      }
    });
  }
});

/**
 * 选择单位或部门的自动处理。
 * 需要在dom元素中配置data-cfg属性，格式为：
 *  {"valueField":"[值对应的表单域的name]","mapping":"域1name=id,域2name=name"}}
 *  如{"valueField":"e.unit.id","mapping":"e.unit.id=id,e.unit.name=name"}}
 * mapping中等于号后的值为用户信息中的key
 */
$document.delegate(".selectUnitOrDepartment", {
  click: function () {
    var $this = $(this);
    var cfg = $this.data("cfg");
    if (!cfg) {
      alert("没有配置dom元素data-cfg属性的值，无法处理！");
      return;
    }
    if (typeof cfg == "string")
      cfg = {mapping: cfg};

    var $form = $this.closest("form");
    bc.identity.selectUnitOrDepartment({
      selecteds: cfg.valueField ? $form.find(":input[name='" + cfg.valueField + "']").val() : null,
      onOk: function (user) {
        var mapping = cfg.mapping.split(",");
        var c;
        for (var i = 0; i < mapping.length; i++) {
          c = mapping[i].split("=");
          if (c.length != 2) {
            alert("mapping的格式配置错误，无法处理！请检查dom元素data-cfg的配置,mapping=" + mapping[i]);
            return;
          }
          $form.find(":input[name='" + c[0] + "']").val(user[c[1]]);
        }
      }
    });
  }
});

/**
 * 选择部门的自动处理。
 * 需要在dom元素中配置data-cfg属性，格式为：
 *  {"valueField":"[值对应的表单域的name]","mapping":"域1name=id,域2name=name"}}
 *  如{"valueField":"e.unit.id","mapping":"e.unit.id=id,e.unit.name=name"}}
 * mapping中等于号后的值为用户信息中的key
 */
$document.delegate(".selectDepartment", {
  click: function () {
    var $this = $(this);
    var cfg = $this.data("cfg");
    if (!cfg) {
      alert("没有配置dom元素data-cfg属性的值，无法处理！");
      return;
    }
    if (typeof cfg == "string")
      cfg = {mapping: cfg};

    var $form = $this.closest("form");
    bc.identity.selectDepartment({
      selecteds: cfg.valueField ? $form.find(":input[name='" + cfg.valueField + "']").val() : null,
      onOk: function (user) {
        var mapping = cfg.mapping.split(",");
        var c;
        for (var i = 0; i < mapping.length; i++) {
          c = mapping[i].split("=");
          if (c.length != 2) {
            alert("mapping的格式配置错误，无法处理！请检查dom元素data-cfg的配置,mapping=" + mapping[i]);
            return;
          }
          $form.find(":input[name='" + c[0] + "']").val(user[c[1]]);
        }
      }
    });
  }
});

/**
 * 选择岗位的自动处理。
 * 需要在dom元素中配置data-cfg属性，格式为：
 *  {"valueField":"[值对应的表单域的name]","mapping":"域1name=id,域2name=name"}}
 *  如{"valueField":"e.unit.id","mapping":"e.unit.id=id,e.unit.name=name"}}
 * mapping中等于号后的值为用户信息中的key
 */
$document.delegate(".selectGroup", {
  click: function () {
    var $this = $(this);
    var cfg = $this.data("cfg");
    if (!cfg) {
      alert("没有配置dom元素data-cfg属性的值，无法处理！");
      return;
    }
    if (typeof cfg == "string")
      cfg = {mapping: cfg};

    var $form = $this.closest("form");
    bc.identity.selectGroup({
      selecteds: cfg.valueField ? $form.find(":input[name='" + cfg.valueField + "']").val() : null,
      onOk: function (user) {
        var mapping = cfg.mapping.split(",");
        var c;
        for (var i = 0; i < mapping.length; i++) {
          c = mapping[i].split("=");
          if (c.length != 2) {
            alert("mapping的格式配置错误，无法处理！请检查dom元素data-cfg的配置,mapping=" + mapping[i]);
            return;
          }
          $form.find(":input[name='" + c[0] + "']").val(user[c[1]]);
        }
      }
    });
  }
});