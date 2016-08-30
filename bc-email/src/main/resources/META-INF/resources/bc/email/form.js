bc.emailForm = {
	init : function(option,readonly) {
		var $form = $(this);
		
		// 初始化Redactor编辑器
		var buttons = ['html', '|', 'formatting', '|', 'bold', 'italic', 'underline', 'deleted', '|', 
	       'unorderedlist', 'orderedlist', 'outdent', 'indent', '|',
	       'image', 'video', 'file', 'table', 'link', '|',
	       'fontcolor', 'backcolor', '|', 'alignment', '|', 'horizontalrule'];
		var type = $form.find("input[name='e.type']").val();
		var uid = $form.find("input[name='e.uid']").val()
		$form.find(".bc-redactor").redactor({
			lang: 'zh_cn',
			fixed: true,
			focus: false,
			autoresize: false,
			plugins: ['fullscreen'],
			minHeight: 80,
			buttons: buttons,
			imageUpload: bc.root + "/upload/?a=1&type=img&sp=editor&fn=file&ptype=email." + type + ".editor&puid=" + uid,
			autoresize: true
		});
		
		if(readonly)return;
		
		//绑定点击移除按钮
		$form.find(".ulReceiver>li>.click2remove").click(function(){
			$(this).closest("li").remove();
		});
		
		
		//声明li
		var liTpl = '<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" data-id="{0}"'+
		" data-hidden='{1}'"+
		' style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">'+
		'<span class="text">{2}</span>'+
		'<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title={3}></span></li>';
		
		//绑定添加用户
		$form.delegate("ul>.email-addUsers","click",function(){
			var $div = $(this).closest("div");
			var $ul = $div.find(".ulReceiver");
			var $lis = $form.find(".ulReceiver>li");
			var selecteds="";
			$lis.each(function(i){
				selecteds+=(i > 0 ? "," : "") + ($(this).attr("data-id"));//
			});
			bc.identity.selectUser({
				multiple: true,//可多选
				history:false,
				status:'0',
				selecteds: selecteds,
				onOk: function(users){
					$.each(users,function(i,user){
						if($lis.filter("[data-id='" + user.id + "']").size() > 0){//已存在
							logger.info("duplicate select: id=" + user.id + ",name=" + user.name);
						}else{//新添加的
							var data={
								id:user.id,
								code:user.account,
								name:user.name,
								type:4
							}
							$(liTpl.format(user.id,$.toJSON(data),user.name,'点击移除'))
							.appendTo($ul).find("span.click2remove")
							.click(function(){
								$(this).parent().remove();
							});
						}
					});
				}
			});
		});
		
		//绑定添加岗位
		$form.delegate("ul>.email-addGroups","click",function(){
			var $div = $(this).closest("div");
			var $ul = $div.find(".ulReceiver");
			var $lis = $form.find(".ulReceiver>li");
			var selecteds="";
			$lis.each(function(i){
				selecteds+=(i > 0 ? "," : "") + ($(this).attr("data-id"));//
			});
			bc.identity.selectGroup({
				multiple: true,
				selecteds: selecteds,
				onOk: function(groups){
					$.each(groups,function(i,group){
						if($lis.filter("[data-id='" + group.id + "']").size() > 0){//已存在
							logger.info("duplicate select: id=" + group.id + ",name=" + group.name);
						}else if(group.count=="0"){
							logger.info("size 0 select: id=" + group.id + ",name=" + group.name);
						}else{//新添加的
							var data={
								id:group.id,
								code:group.code,
								name:group.name,
								type:group.type
							}
							$(liTpl.format(group.id,$.toJSON(data),group.name,'点击移除'))
							.appendTo($ul).find("span.click2remove")
							.click(function(){
								$(this).parent().remove();
							});
						}
					});
				}
			});
		});
		
		//绑定添加单位或部门
		$form.delegate("ul>.email-addUnitOrDepartments","click",function(){
			var $div = $(this).closest("div");
			var $ul = $div.find(".ulReceiver");
			var $lis = $form.find(".ulReceiver>li");
			var selecteds="";
			$lis.each(function(i){
				selecteds+=(i > 0 ? "," : "") + ($(this).attr("data-id"));//
			});
			bc.identity.selectUnitOrDepartment({
				multiple: true,
				selecteds: selecteds,
				onOk: function(groups){
					$.each(groups,function(i,group){
						if($lis.filter("[data-id='" + group.id + "']").size() > 0){//已存在
							logger.info("duplicate select: id=" + group.id + ",name=" + group.name);
						}else if(group.count=="0"){
							logger.info("size 0 select: id=" + group.id + ",name=" + group.name);
						}else{//新添加的
							var data={
								id:group.id,
								code:group.code,
								name:group.name,
								type:group.type
							}
							$(liTpl.format(group.id,$.toJSON(data),group.name,'点击移除'))
							.appendTo($ul).find("span.click2remove")
							.click(function(){
								$(this).parent().remove();
							});
						}
					});
				}
			});
		});
	},
	/**
	 * 保存
	 */
	save : function(){
		var $form = $(this);
		if(!bc.validator.validate($form)) return;
		
		var $uls=$form.find("div>.ulReceiver");
		if($uls.length>0){
			var datas=[];
			var data;
			var $ul;
			var $lis;
			var toType;
			$uls.each(function(){
				$ul=$(this);
				$lis=$ul.find("li");
				toType=$ul.attr("data-type");
				$lis.each(function(){
					data={toType:toType},
					data=$.extend(data,eval("("+$(this).attr("data-hidden")+")"));
					datas.push(data);
				});
			});
			$form.find(":input[name='receivers']").val($.toJSON(datas));
		}
		
		bc.page.save.call($form,{callback:function(json){
			bc.msg.slide("保存草稿成功");
			return false;
		}});
	},
	/**
	 * 预览
	 */
	preview : function(){
		var $form = $(this);	
	},
	/**
	 *  发送
	 */
	send : function(){
		var $form = $(this);
		if(!bc.validator.validate($form)) return;
		
		var $uls=$form.find("div>.ulReceiver");
		var datas=[];
		var data;
		var $ul;
		var $lis;
		var toType;
		$uls.each(function(){
			$ul=$(this);
			$lis=$ul.find("li");
			toType=$ul.attr("data-type");
			$lis.each(function(){
				data={toType:toType},
				data=$.extend(data,eval("("+$(this).attr("data-hidden")+")"));
				datas.push(data);
			});
		});
		
		if(datas.length==0){
			bc.msg.alert("收件人不能为空！");
			return;
		}
		$form.find(":input[name='receivers']").val($.toJSON(datas));
		
		//设置状态为已发送
		$form.find(":input[name='e.status']").val(1);
		
		bc.msg.confirm("确认发送邮件",function(){
			var $processDlg = '<div data-type="custom" class="bc-page">';
			$processDlg += '<div class="info">正在发送邮件。。。</div>';
			$processDlg += '</div>';
			$processDlg=$($processDlg);
			$processDlg.dialog({title:"正在发送邮件", modal: true});
			bc.page.save.call($form,{callback:function(json){
				bc.msg.slide("发送成功");
				$processDlg.dialog("close");
				$form.dialog("close");
				return false;
			}});
			
		},function(){
			//不选择发送，将状态设为草稿
			$form.find(":input[name='e.status']").val(0);
		});
		
	}
};