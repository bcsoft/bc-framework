bc.doneWorkList = {
	/** 打开指定的已办文档*/
	checkWork : function() {
		var $page = $(this);
		
		//获取选中的待办id
		var $tds = $page.find(".bc-grid>.data>.left tr.ui-state-focus>td.id");
		var data = null;
		if($tds.length == 1){
			data = "ids=" + $tds.attr("data-id");
		}else if($tds.length > 1){
			data = "ids=";
			$tds.each(function(i){
				data += $(this).attr("data-id") + (i == $tds.length-1 ? "" : ",");
			});
		}
		if(data == null){
			bc.msg.slide("请先选择已办事项！");
			return;
		}
		if(logger.infoEnabled) logger.info("todoWorkList：ids=" + data);

		// TODO 打开已办文档
	}
};