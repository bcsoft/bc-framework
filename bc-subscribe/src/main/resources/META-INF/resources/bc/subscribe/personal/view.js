bc.subscribePersonalView = {
	/** 新建 */
	create : function(){
		var $view = $(this);
		var $tds = $view.find(".bc-grid>.data>.left tr>td.id");
		var selected='';
		$tds.each(function(i){
			 selected += selected +$(this).attr("data-id")
			 +(i==$tds.size()-1?'':',');
		});
		
		bc.selectSubscribe({
			selected:selected,
			afterClose: function(subscribe){
				if(subscribe){
					//添加一个订阅的信息
					bc.ajax({
						url:bc.root+"/bc/subscribe/add4personal",
						data:{id:subscribe.id},
						dataType:"json",
						success:function(json){
							if(json.success){
								bc.msg.slide(json.msg);
								bc.grid.reloadData($view);
							}
						}
					});
				}
			}
		});

	},
	/** 删除 **/
	delete_ : function(){
		var $view = $(this);
		var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		
		if($tds.length==1){
			var type = $view.find(".bc-grid>.data>.right tr.ui-state-highlight>td:eq(1)").data("value");
			if(type != '1'){
				bc.msg.confirm("确定删除选中的订阅信息吗？",
					function(){
						bc.ajax({
							url:bc.root+"/bc/subscribe/delete4personal",
							data:{id:$tds.attr("data-id")},
							dataType:"json",
							success:function(json){
								if(json.success){
									bc.msg.slide(json.msg);
									bc.grid.reloadData($view);
								}
							}
						});
					}
				);
			}else{
				bc.msg.alert("没有权限删除系统推送的订阅！");
			}
		}else if($tds.length > 0){
			bc.msg.slide("一次只能删除一个订阅！");
		}else{
			bc.msg.slide("请先选择要删除的订阅！");
		}
	}
};