bc.reportHistoryList = {
	/** 在线预览 */
	inline: function(){
		var $view=$(this);
		var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		if($tds.length == 1){
			//获取选中的行
			var $tr = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
			var n = $tr.find(">td:eq(4)").attr("data-value");// 获取文件名
			var f = "report/history/" + $tr.find(">td:eq(5)").attr("data-value");// 获取附件相对路径
			
			// 预览文件
			var option = {f: f, n: n};
			var ext = f.substr(f.lastIndexOf("."));
			if(ext==".xml"){// Microsoft Word 2003 XML格式特殊处理
				option.from="docx";
			}
			bc.file.inline(option);
		}else if($tds.length > 1){
			bc.msg.slide("一次只可以预览一个历史报表，请确认您只选择了一个历史报表！");
			return;
		}else{
			bc.msg.slide("请先选择要预览的历史报表！");
			return;
		}
	},
	/** 下载选择的*/
	download: function(){
		var $view=$(this);
		var $tds = $(this).find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		if($tds.length == 1){
			//获取选中的行
			var $tr = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
			var n = $tr.find(">td:eq(4)").attr("data-value");// 获取文件名
			var f = "report/history/" + $tr.find(">td:eq(5)").attr("data-value");// 获取附件相对路径			
			// 下载文件
			bc.file.download({f: f, n: n});
		}else if($tds.length > 1){
			bc.msg.slide("一次只可以下载一个历史报表，请确认您只选择了一个历史报表！");
			return;
		}else{
			bc.msg.slide("请先选择要下载的历史报表！");
			return;
		}
	}
};