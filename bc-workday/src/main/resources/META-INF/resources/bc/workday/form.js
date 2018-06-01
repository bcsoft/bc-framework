bc.namespace("bc.workday");
bc.workday = {
  init: function () {
  },
  save: function () {
    var $page = $(this);
    var $form = $("form", $page);
    if (!bc.validator.validate($form)) { //如果验证失败
      return false;
    }

    //调用标准的方法执行保存
    bc.page.save.call(this, {
      callback: function (json) {
        if (json.success) {
          bc.msg.alert(json.msg);
        } else {
          bc.msg.slide(json.msg);
        }

      }
    });
  },
  checkToDate: function (el, $form) {
    var $form = $(el).closest("form");
    var from_date = $form.find(":input[name='e.fromDate']").val();
    var to_date = $form.find(":input[name='e.toDate']").val();
    if (to_date == null) return false;
    if (new Date(to_date) < new Date(from_date)) return false;

    return true;
  },

}