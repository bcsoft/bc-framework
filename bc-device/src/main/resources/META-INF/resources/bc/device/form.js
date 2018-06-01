bc.namespace("bc.device.form");
bc.device.form = {
  init: function () {
    var $form = $(this);
    console.log("bc.device.form.init");
  },
  // 选中日期， 如：2013-05-06
  selectedDate: function (dateText) {
    console.log("dateText=" + dateText);
    var $form = $(this).closest(".bc-page");
    //console.log("form=" + $form.get(0).outerHTML);
    $form.find(":input[name='e.buyDate']").val(dateText);
  },
};