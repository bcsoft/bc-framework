define(["bc"], function (bc) {
	return {
		/** 启动/重置 */
		start : function() {
			var $page = $(this);
			var selectedId = bc.grid.getSelected($page.find(".bc-grid"));
			if(!selectedId){
				bc.msg.slide("请先选择要处理的任务！");
				return;
			}
			bc.ajax({
				url: bc.root + "/bc/schedule/job/start",
				data:{ids:selectedId},
				dataType:"json",
				success: function(json) {
					//重新加载列表
					bc.grid.reloadData($page);
					//显示提示信息
					bc.msg.slide(json.msg);
				}
			});
		},
		/** 停止 */
		stop: function(){
			var $page = $(this);
			var selectedId = bc.grid.getSelected($page.find(".bc-grid"));
			if(!selectedId){
				bc.msg.slide("请先选择要处理的任务！");
				return;
			}
			bc.ajax({
				url: bc.root + "/bc/schedule/job/stop",
				data:{ids:selectedId},
				dataType:"json",
				success: function(json) {
					//重新加载列表
					bc.grid.reloadData($page);
					//显示提示信息
					bc.msg.slide(json.msg);
				}
			});
		},
		/** 查看调度日志 */
		showLog: function(){
			bc.page.newWin({
				url: bc.root + "/bc/schedule/log/paging",
				mid: "scheduleLog.paging",
				name: "定时任务调度日志"
			});
		}
	};
});