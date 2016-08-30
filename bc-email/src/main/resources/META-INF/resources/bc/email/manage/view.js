bc.email2ManageViewBase = {
	/** 查看邮件 **/
	open:function(){
		var $view = $(this);
		var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		if($tds.length==1){
			bc.page.newWin({
				url:bc.root+"/bc/email2Manage/open",
				data:{id: $tds.attr("data-id")},
				mid:"email2Manage::open::"+$tds.attr("data-id"),
				name:"查看邮件",
				title:"查看邮件"
			});
		}else if($tds.length > 1){
			bc.msg.slide("一次只能查看一封邮件！");
		}else{
			bc.msg.slide("请先选择要查看的信息！");
		}
	},
	/** 查看查阅历史 */
	openHistory: function(){
		var $form = $(this);
		var id=$form.find(":input[name='e.id']").val();
		bc.page.newWin({
			url:bc.root+"/bc/emailHistory2Manages/paging",
			mid:"emailHistory2Manage::paging::"+id,
			data:{emailId:id},
			name:"查看查阅历史",
			title:"查看查阅历史"
		});
	}
};