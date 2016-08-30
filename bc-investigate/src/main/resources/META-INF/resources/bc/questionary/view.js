if(!window['bc'])window['bc']={};
bc.questionaryView = {
	/** 双击视图处理 */
	dblclick : function() {
		var $page = $(this);
		var $grid = $page.find(".bc-grid");
		var $trs = $grid.find(">.data>.right tr.ui-state-highlight");
		var status = $trs.find("td:eq(0)").attr("data-value");
		//如果是草稿状态的调用编辑方法其他的调用打开方法
		if(status==-1){
			bc.page.edit.call($page);
		}else{
			bc.page.open.call($page);
		}
	},
	//问答题评分
	score : function(){
		var $page = $(this);
		var data=null;
		var $tds = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		var $tds4Right = $page.find(".bc-grid>.data>.right tr.ui-state-highlight");
		var title = $tds4Right.children().eq(1).text();
		var id = $tds.attr("data-id");
		if($tds.length == 1){
			var url=bc.root +"/bc/grades/paging";
			var option = jQuery.extend({
				url: url,
				data:{id:id,title:title},
				name:"对"+title+"的问答题进行评分",
				mid: "gradeaction" + id,
				afterClose: function(){
					//重新加载列表
					bc.grid.reloadData($page);
				}
			},option);
			bc.page.newWin(option);
		}else if($tds.length > 1){
			alert("每次只能选择一张试卷进行问答题评分！")
		}else{
			alert("请选择需要进行问答题评分的试卷！");
		}
	
	}
		
};