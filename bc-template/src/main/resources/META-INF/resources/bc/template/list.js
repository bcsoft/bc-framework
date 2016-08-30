bc.templateList = {
	/** 在线查看 */
	inline: function(){
		var $view=$(this);
		var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		if($tds.length == 1){
			//获取选中的行
			var $tr = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
			var $hidden = $tr.data("hidden");
			var type=$hidden.typeCode;
			if(type=='custom'){
				var id=$tds.attr("data-id");
				if($hidden.isContent == "empty"){
					bc.msg.slide("模板内容为空！");
				}else{
					var url =bc.root+"/bc/templatefile/inline?id=" +id;
					url += "&custom=" + true;
					var win = window.open(url, "_blank");
					return win;
				}
			}else{
				var n = $tr.find(">td[data-column='t.subject']").attr("data-value");// 获取文件名
				var f = "template/" + $hidden.path;// 获取附件相对路径	
				
				// 预览文件
				var option = {f: f, n: n,ptype:"Template",puid:$hidden.uid};
				var ext = f.substr(f.lastIndexOf("."));
				if(type=='xls' && ext==".xml"){// Microsoft Word 2003 XML格式特殊处理
					option.from="docx";
				}
				bc.file.inline(option);
			}
		}else if($tds.length > 1){
			bc.msg.slide("一次只可以预览一个模板，请确认您只选择了一个模板！");
			return;
		}else{
			bc.msg.slide("请先选择要预览的模板！");
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
			var $hidden = $tr.data("hidden");
			var type=$hidden.typeCode;//类型
			if(type=='custom'){
				var id=$tds.attr("data-id");
				if($hidden.isContent == "empty"){
					bc.msg.slide("模板内容为空！");
				}else{
					var url =bc.root+"/bc/templatefile/download?id=" +id;
					url += "&custom=" + true;
					var win = window.open(url, "blank");
					return win;
				}
			}else{
				var n = $tr.find(">td[data-column='t.subject']").attr("data-value");// 获取文件名
				var f = "template/" + $hidden.path;// 获取附件相对路径			
				// 下载文件
				bc.file.download({f: f, n: n,ptype:"Template",puid:$hidden.uid});
			}
		}else if($tds.length > 1){
			bc.msg.slide("一次只可以下载一个模板，请确认您只选择了一个模板！");
			return;
		}else{
			bc.msg.slide("请先选择要下载的模板！");
			return;
		}
	},
	/** 点击创建图标 */
	createByIcon: function() {
		var $page = $(this).parents("div.bc-page");
		// 获得选中行
		$tr = $(this).parents("tr");
		// 取得隐藏列数据
		var $hidden=$tr.data("hidden");
		var data = {};
		// 获得所属分类列
		var categorys = $(this).parents("td[data-column = 'category']").text();
		// 弹出渲染窗口
		bc.page.newWin({
			url:bc.root + "/bc/template/create",
			mid: $hidden.uid,
			name: "新建模板配置",
			data: {
				cids: $hidden.cid,
				cNames: categorys,
				isReadonly: false
			},
			afterClose:function(status){
				if(status=="saved"){
					bc.grid.reloadData($page);
				}
			}
		});
	},
	/** 点击删除图标 */
	deleteByIcon: function() {
		var $page = $(this).parents("div.bc-page");
		// 获得当前元素的祖先元素tr在其兄弟元素的索引位置
		var index = $(this).parents("tr").index();
		// 获得序号Table被选中行的ID
		var id = $page.find(".data .table tr").eq(index).find("td").attr("data-id");
		
		bc.msg.confirm("确定要删除选定的 <b>"+$(this).parents("td").text()+"</b>吗？",function(){
			bc.ajax({
				url: bc.root + "/bc/template/delete",
				data: {'id': id},
				dateType:'json',
				success:function(jsonData){
					var json = JSON.parse(jsonData);
					if(json.success){
						bc.grid.reloadData($page);
					}else{
						bc.msg.slide(json.msg);
					}				
				}
			});
		});
	},
	/** 点击删除按钮*/
	deleteone: function(){
		var $view=$(this);
		var $tds = $view.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		if($tds.length > 0){
			//取内置列data-value的值
			var $inner = $view.find(".bc-grid>.data>.right tr.ui-state-highlight");
			var flag=false;
			$inner.each(function(index){	
				if($(this).find("td:eq(11)").attr("data-value")=='true'){
					flag=true;
				}	
			});
			//内置
           if(flag){
            	bc.msg.alert("内置模板不能删除！");
    			return;
            }
			bc.page.delete_.call($view);
		}else{
			bc.msg.slide("请先选择要删除的模板！");
			return;
		}
	},
	/** 视图表格双击行 */
	doubleClick : function() {
		var $page = $(this);
		var $tr = $page.find("div.data>.right tr.ui-state-highlight");
		var $hidden = $tr.data("hidden");
		var data = {};
		var url;

		// url
		if(!$page.data("extras").isReadonly)
			url = "/bc/template/edit";
		else {
			// 只读就判断ACL权限
			data['cids'] = $hidden.cid;
			url = ($hidden.acl && $hidden.acl != "00" && $hidden.acl != "01")
				? url = "/bc/template/edit"
				: url = "/bc/template/open";
		}

		var $td = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		data['id'] = $td.attr("data-id");
		// 是否只读
		data['isReadonly'] = !$page.data("extras").isReadonly ? false 
			: !($hidden.acl && $hidden.acl != "00" && $hidden.acl != "01");
		var fromMID = $page.attr("data-mid");
		bc.page.newWin({
				url: bc.root + url, data: data || null,
				from: fromMID,
				mid: fromMID + "." + $td.attr("data-id"),
				name: $td.attr("data-name") || "未定义",
				title: $td.attr("data-name"),
				afterClose: function(status){
					if(status)
						bc.grid.reloadData($page);
				}
			});
	},
	/** 配置 **/
	config : function(option){
		if(option.value == "type"){
			bc.page.newWin({
				url : bc.root+"/bc/templateTypes/list",
				name: "模板类型管理",
				mid : "templateTypeViews"		
			});
		} else if(option.value == "param"){
			bc.page.newWin({
				url : bc.root+"/bc/templateParams/list",
				name: "模板参数管理",
				mid : "templateParamViews"		
			});
		}else{
			alert("不支持的操作类型!");
		}
	}
	
};