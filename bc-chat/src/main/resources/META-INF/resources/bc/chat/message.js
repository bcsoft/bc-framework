bc.chatMessage = {
  init: function () {
    var $page = $(this);
    //绑定发送按钮事件
    var $text = $page.find("textarea");
    var $history = $page.find("div.history");
    var toSid = $page.find("input:hidden[name='toSid']").val();
    var toName = $page.find("input:hidden[name='toName']").val();

    function sendMessage() {
      var msg = $text.val();
      if (msg.length == 0)
        return false;

      //替换换行符为<br/>
      msg = msg.replace(/\r\n|\r|\n/g, "<br/>");

      if (bc.ws) {
        //组装要发送的信息为一个json字符串
        var json = {};
        json.type = 2;	//用户发到用户的信息
        json.msg = msg;
        json.toSid = toSid;

        //当前客户端时间
        json.time = bc.getTime();

        //发送消息
        var _msg = $.toJSON(json);
        logger.info("send=" + _msg);
        bc.ws.send(_msg);

        //清空输入
        $text.val("");

        //添加到历史信息
        bc.chat.addHistory($page, bc.chat.historyItemTpl.format("local", "我", json.time, json.msg));
      } else {
        bc.chat.addHistory($page, bc.chat.historyItemTpl.format("sys", "系统", bc.getTime(), "连接已断开，无法发送消息！"));
      }
    }

    $page.find("input#btnSend").click(sendMessage);
    $text.keyup(function (e) {
      if (e.which == 13 && e.ctrlKey) { //Ctrl + Enter发送消息
        sendMessage();
      }
    });
  }
};