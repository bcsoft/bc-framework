bc.gradeForm = {
  init: function (option, readonly) {
    var $form = $(this);
    //题目的分数
    var score = $form.find("input:[name='score']").val();
    var amount = $form.find("input:[name='amount']").val();
    //滑动条进行评分
    $form.find("#slider").slider({
      min: 0,
      max: parseInt(score),
      value: parseInt(amount),
      slide: function (event, ui) {

        $form.find("#amount").val(ui.value);
      }
    });
    $form.find("#amount").val($("#slider").slider("value"));

  },
  //评分
  grade: function () {
    var $form = $(this);
    //题目的分数
    var amount = $form.find("input:[name='amount']").val();
    //作答记录Id
    var rid = $form.find("input:[name='rid']").val();
    //答案Id
    var aid = $form.find("input:[name='aid']").val();
    //试卷Id
    var qid = $form.find("input:[name='qid']").val();
    //评分表Id
    var gid = $form.find("input:[name='gid']").val();
    var data = {};
    data = {
      amount: amount,
      rid: rid,
      aid: aid,
      qid: qid,
      gid: gid
    };
    bc.ajax({
      url: bc.root + "/bc/grade/testGrade",
      dataType: "json",
      data: data,
      success: function (json) {
        if (json.success) {
          bc.msg.slide("评分成功！");
          $form.data("data-status", "saved");
          $form.dialog("close");
          //bc.grid.reloadData($form);
        }
      }
    });
  }

};





