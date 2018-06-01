bc.templateTypeForm = {
  init: function (option, readonly) {
    var $form = $(this);
    var isPath = $form.find(":radio[name='e.path']:checked").val();
    var $ext = $form.find(".templateTypeExt");
    //根据是否关联附件 显示或隐藏附件扩展名
    if (isPath == 'false') {
      $ext.hide();
      $form.find(":input[name='e.extension']").removeAttr("data-validate");
    }

    if (readonly) return;

    //绑定关联附件事件。
    $form.find(":radio[name='e.path']").change(function () {
      if ($(this).val() == 'true') {
        $ext.show();
        $form.find(":input[name='e.extension']").attr("data-validate", "required");
      } else if ($(this).val() == 'false') {
        $ext.hide();
        $form.find(":input[name='e.extension']").removeAttr("data-validate");
      }
    })

  },
  /**
   * 保存
   */
  save: function () {
    var $form = $(this);
    //验证表单
    if (!bc.validator.validate($form)) return;
    //模板类型的id
    var id = $form.find(":input[name='e.id']").val();
    //模板类型编码
    var code = $form.find(":input[name='e.code']").val();
    //保存
    $.ajax({
      url: bc.root + "/bc/templateType/isUniqueCode",
      data: {tid: id, code: code},
      dataType: "json",
      success: function (json) {
        var result = json.result;
        if (result == 'save') {
          bc.page.save.call($form);
        } else {
          //系统中已有此编码
          bc.msg.alert("此编码已被其它模板类型使用，请修改编码！");
        }
      }
    });
  }
};