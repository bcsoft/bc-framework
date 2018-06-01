bc.templateParamForm = {
  init: function (option, readonly) {
    var $form = $(this);

    // 让详细配自动高度
    var $cfg = $form.find(":input[name='e.config']");
    var cfgEl = $cfg.get(0);
    if (cfgEl.scrollHeight > 170) {
      cfgEl.style.height = cfgEl.scrollHeight + "px";
    }

    if (readonly) return;

    // 将详细配置变为代码高亮编辑器
    bc.templateParamForm.editor = CodeMirror.fromTextArea($form.find("textarea[name='e.config']")[0], {
      json: true,
      lineNumbers: true,
      matchBrackets: true,
      theme: "eclipse"
    });
  },
  /**
   * 保存
   */
  save: function () {
    var $form = $(this);
    //详细配置赋值
    if (bc.templateParamForm.editor) bc.templateParamForm.editor.save();
    bc.page.save.call($form);
  }
};