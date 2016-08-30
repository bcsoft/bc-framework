bc.feedbackForm = {
	init : function() {
		var $page = $(this);
		
		// 发表回复的处理
		$page.find("#replyBtn").click(function(){
			bc.feedbackForm.doReply.call($page,this);
		});
		
		// 回到顶部的处理
		$page.find("#gotoTopBtn").click(function(){
			$page.animate({scrollTop: 0}, 200);
		});
		$page.find("#replies").delegate("#deleteBtn","click",function(){
			var $deleteBtn = $(this);
			bc.ajax({
				dataType: "json",
				url: bc.root + "/bc/feedback/deleteReply",
				data: {id: $deleteBtn.attr("data-id")},
				success: function(json){
					if(json.success){
						bc.msg.slide("成功删除回复信息！");
						
						// 处理界面显示的信息
						$deleteBtn.parent().next().html("<span class='deleteInfo'>此回复已被删除</span>");
						$deleteBtn.remove();
					}else{
						bc.msg.alert(json.msg);
					}
				}
			});
			return false;
		});
	},
	
	/** 发表回复 */
	doReply: function(btn){
		var $page = this;
		btn.disabled = true;
		var $content = $page.find("#replyContent");
		var content = $content.val();
		if($.trim(content).length == 0){
			bc.msg.slide("必须填写回复的内容");
			btn.disabled = false;
			return;
		}
		
		bc.ajax({
			dataType: "json",
			url: bc.root + "/bc/feedback/doReply",
			data: {id: $page.find('input[name="e.id"]').val(),content: content},
			success: function(json){
				if(json.success){
					var $replies = $page.find("#replies");
					var c = parseInt($replies.attr("data-count"))+1;
					$replies.attr("data-count",c)
					$replies.append('<div class="reply ui-widget-content">'
							+'<div class="replyHeader ui-widget-header">'+json.authorName+' 回复于 '+json.fileDate
							+($page.find("#hasDeletePriviledge").val() == "true" ? '<a id="deleteBtn" class="right" href="#" data-id="'+json.id+'">删除</a>':"")
							+'<span class="right">'+c+'楼</span>'
							+'</div>'
							+'<pre class="replyContent ui-widget-content">'+json.content+'</pre>'
						+'</div>');
					$content.val("");
					
					// 滚动到底部
					$page.animate({scrollTop: $page[0].scrollHeight - $page.height()}, 200);
					
					$page.data("data-status","saved");
				}else{
					bc.msg.alert(json.msg);
				}
				btn.disabled = false;
			}
		});
	}
};