bc.accessControlView = {
	/** 删除选择的*/
	deleteone: function(){
		var $view=$(this);
		var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		if($tds.length==1){
			bc.page.delete_.call($view);
		}else if($tds.length > 0){
			bc.msg.slide("只能选择一条删除！");
		}else{
			bc.msg.slide("请先选择要删除的信息！");
		}
	},
	/** 查看访问历史 */
	history: function(){
		var $view=$(this);
		bc.page.newWin({
			url:bc.root+"/bc/accessHistorys/paging",	
			mid:"accessHistorys::",
			name:"访问历史",
			title:"访问历史",
		});
	}
};