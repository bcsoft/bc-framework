if (!window['bc']) window['bc'] = {};
bc.gradeView = {
  /** 双击视图处理 */
  dblclick: function () {
    var $page = $(this);
    var $grid = $page.find(".bc-grid");
    //试卷Id
    var $trs4Id = $grid.find(">.data>.left tr.ui-state-highlight");
    var id = $trs4Id.find(".id").attr("data-id");

    //隐藏域
    var $trs4Hidden = $grid.find(">.data>.right tr.ui-state-highlight");
    var hiddenJson = $.evalJSON($trs4Hidden.attr("data-hidden"));
    //试卷Id
    var qid = hiddenJson.qid;
    //作答表Id
    var rid = hiddenJson.rid;
    //回答表Id
    var aid = hiddenJson.aid;
    //分数 
    var score = hiddenJson.score;
    //试卷标题
    var testTitle = hiddenJson.testTitle;
    //试卷状态
    var status = hiddenJson.status;

    //内容
    var $trs4Content = $grid.find(">.data>.right tr.ui-state-highlight");
    //答卷人
    var answer = $trs4Content.find("td:eq(0)").text();
    //答卷时间
    var answerTime = $trs4Content.find("td:eq(1)").text();
    //题目
    var questionTitle = $trs4Content.find("td:eq(2)").text();
    //答案
    var result = $trs4Content.find("td:eq(3)").text();

    var data = {};
    data = {
      qid: qid,
      id: id,
      rid: rid,
      aid: aid,
      score: score,
      testTitle: testTitle,
      status: status,
      answer: answer,
      answerTime: answerTime,
      questionTitle: questionTitle,
      result: result
    };

    bc.page.newWin({
      name: "试卷评分",
      mid: "grade" + id,
      url: bc.root + "/bc/grade/edit",
      data: data,
      afterClose: function (status) {
        if (status) bc.grid.reloadData($page);
      }
    });
  },

  //点击评分按钮进行评分
  score: function () {
    var $page = $(this);
    var data = null;
    var $tds = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
    var $tds4Right = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
    if ($tds.length == 1) {
      //调用双击方法
      bc.gradeView.dblclick.call($page);
    } else if ($tds.length > 1) {
      alert("每次只能选择一条题目进行评分！")
    } else {
      alert("请选择需要评分的题目！");
    }

  }

};