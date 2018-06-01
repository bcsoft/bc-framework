bc.questionaryForm = {
  init: function (option, readonly) {
    var $form = $(this);
    //查找填空题
    var completion = $form.find("#testArea").find("table[data-type='2']");
    completion.each(function () {

      //查找填空题的内容
      var contant = $(this).find("textarea[name='subject']").val();
      alert("内容： " + contant);
    });
  }
};





