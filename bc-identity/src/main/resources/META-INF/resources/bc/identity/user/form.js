bc.userForm = {
	init : function(option,readonly) {
		if(readonly) return;
		
		var $form = $(this);
		//绑定选择上级的按钮事件处理
		$form.find("#selectBelong").click(function(){
			var selecteds = $form.find(":input[name='belongIds']").val();
			bc.identity.selectUnitOrDepartment({
				selecteds: selecteds,
				multiple:true,
				onOk: function(actors){
					//单选
					//$form.find(":input[name='belongNames']").val(actor.name);
					//$form.find(":input[name='belongIds']").val(actor.id);
					
					//多选
					var ids = [],names=[];
					for(var i=0;i<actors.length;i++){
						ids.push(actors[i].id);
						names.push(actors[i].name);
					}
					$form.find(":input[name='belongNames']").val(names.join(","));
					$form.find(":input[name='belongIds']").val(ids.join(","));
				}
			});
		});
		
		var liTpl = '<li class="horizontal ui-widget-content ui-corner-all ui-state-highlight" data-id="{0}">'+
			'<span class="text">{1}</span>'+
			'<span class="click2remove verticalMiddle ui-icon ui-icon-close" title={2}></span></li>';
		var ulTpl = '<ul class="horizontal"></ul>';

		var title = $form.find("#assignGroups").attr("data-removeTitle");
		//绑定添加岗位的按钮事件处理
		$form.find("#addGroups").click(function(){
			var $ul = $form.find("#assignGroups ul");
			var $lis = $ul.find("li");
			var selecteds = "";
			$lis.each(function(i){
				selecteds += (i > 0 ? "," : "") + $(this).attr("data-id");//已选择的id
			});
			bc.identity.selectGroup({
				multiple: true,
				selecteds: selecteds,
				onOk: function(groups){
					//添加当前没有分派的岗位
					$.each(groups,function(i,group){
						if($lis.filter("[data-id='" + group.id + "']").size() > 0){//已存在
							logger.info("duplicate select: id=" + group.id + ",name=" + group.name);
						}else{//新添加的
							if(!$ul.size()){//先创建ul元素
								$ul = $(ulTpl).appendTo($form.find("#assignGroups"));
							}
							$(liTpl.format(group.id,group.name,title))
							.appendTo($ul).find("span.click2remove")
							.click(function(){
								$(this).parent().remove();
							});
						}
					});
				}
			});
		});

		//绑定添加角色的按钮事件处理
		$form.find("#addRoles").click(function(){
			var $ul = $form.find("#assignRoles ul");
			var $lis = $ul.find("li");
			var selecteds = "";
			$lis.each(function(i){
				selecteds += (i > 0 ? "," : "") + $(this).attr("data-id");//已选择的id
			});
			bc.identity.selectRole({
				multiple: true,
				selecteds: selecteds,
				onOk: function(roles){
					//添加当前没有分派的岗位
					$.each(roles,function(i,role){
						if($lis.filter("[data-id='" + role.id + "']").size() > 0){//已存在
							logger.info("duplicate select: id=" + role.id + ",name=" + role.name);
						}else{//新添加的
							if(!$ul.size()){//先创建ul元素
								$ul = $(ulTpl).appendTo($form.find("#assignRoles"));
							}
							$(liTpl.format(role.id,role.name,title))
							.appendTo($ul).find("span.click2remove")
							.click(function(){
								$(this).parent().remove();
							});
						}
					});
				}
			});
		});

		//绑定删除岗位或角色的按钮事件处理
		$form.find("span.click2remove").click(function(){
			$(this).parent().remove();
		});
		
		//绑定图片的修改
		$form.find("#portrait").click(function(){
			bc.image.edit({
				puid: $form.find(":input[name='e.uid']").val(),
				ptype: "portrait",
				onOk: function(json){
					//更新图片的连接地址，注意要添加时间戳，应浏览器会缓存img的请求
					var newImgUrl = bc.root + '/bc/image/download?id=' + json.id + "&ts=" + new Date().getTime();
					$form.find("#portrait").attr("src",newImgUrl);
				}
			});
		});
	},
	/**保存的处理*/
	save:function(){
		$page = $(this);
		//先将岗位和角色的id合并到隐藏域
		var ids=[];
		$page.find("#assignGroups li").each(function(){
			ids.push($(this).attr("data-id"));
		});
		$page.find(":input[name=assignGroupIds]").val(ids.join(","));
		ids=[];
		$page.find("#assignRoles li").each(function(){
			ids.push($(this).attr("data-id"));
		});
		$page.find(":input[name=assignRoleIds]").val(ids.join(","));
		
		//调用标准的方法执行保存
		bc.page.save.call(this);
	}
};