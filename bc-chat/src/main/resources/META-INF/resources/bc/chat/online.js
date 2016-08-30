bc.online = {
	init : function() {
		var $page = $(this);
		//事件处理
		$page.delegate("li.item",{
			mouseover: function() {
				$(this).addClass("ui-state-hover");
			},
			mouseout: function() {
				$(this).removeClass("ui-state-hover");
			},
			dblclick: function() {
				//打开与此用户的聊天窗口
				var user = $(this).data("user");
				bc.page.newWin({
					name: "BQ " + user.name,
					mid: "chat-" + user.sid,
					url: bc.root + "/bc/chat/message",
					data: {toSid:user.sid,toName:user.name,toIp:user.ip}
				});
			}
		});
		
		$page.disableSelection();
	}
};