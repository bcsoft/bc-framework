bc.reportTaskForm = {
	init : function(option,readonly) {
		var $form = $(this);
		
		// 让默认配置自动高度
		var $cfg = $form.find(":input[name='e.config']");
		var cfgEl = $cfg.get(0);
		if(cfgEl.scrollHeight > 170){
			cfgEl.style.height = cfgEl.scrollHeight + "px";
		}
		
		//只读权限控制
		if(readonly) return;
		
		// 点击选择报表模板小按钮弹出选择视图
		$form.find("#selectReportTemplate").click(function() {
			bc.selectReportTemplate({
				onOk: function(template){
					if (template) {
						$form.find(":input[name='e.template.id']").val(template.id);
						$form.find(":input[name='category']").val(template.category+'/'+template.name);
						$form.find(":input[name='e.name']").val(template.name);
					}
				}
			});	
		});
		
	},
	/**查看执行记录**/
	viewExcuteRecode:function(){
		var $form = $(this);
		var id = $form.find(":input[name='e.id']").val();
		var name = $form.find(":input[name='e.name']").val();
		
		if(id==''){
			bc.msg.slide("此报表任务还没保存！");
			return;
		}
		var url = bc.root + "/bc/reportHistorys/paging";
		// 构建默认参数
		var option = {};

		// 将一些配置参数放到data参数内(这些参数是提交到服务器的参数)
		option.data = jQuery.extend({
			success : 'true',
			sourceId: id,
			sourceType: '报表任务'
		}, option.data);

		// 弹出选择对话框
		bc.page.newWin(jQuery.extend({
			mid : id,
			paging : true,
			title : '报表任务:'+name+'的执行记录',
			name : '报表任务:'+name+'的执行记录',
			url : url,
		}, option));
	}
};