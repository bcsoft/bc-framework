bc.questionary4UserForm = {
	init : function(option,readonly) {
		var $form = $(this);
		//只读权限控制
		//if(readonly) return;
		
	},
        
	//保存之前的操作
	beforeSave:function($page){
		var ok=true;
		//题目合并到隐藏域
		var topics=[];
		//将收费明细表中的内容添加到buyPlants里
		$page.find("#testArea").children().each(function(){
			var $this = $(this);
			//题目ID
			var questionId = $this.attr("data-id");
			//alert("题目ID" +questionId);
			//题型
			var type = $this.attr("data-type");
			//alert("题型：" +type);
			//是否必选
			var isRequired = $this.attr("data-required") == "true";
	
			var optionItems = [];
			var completionValue = {};
			var completions =[];
			var hadAnswer=false;// 是否已经作答
			//选项
			$this.children().find(".option").each(function(){
				//选项Id
				var itemId =  $(this).attr("data-id");
				
				var standard;//
				var optionItem = null;
				if(type==0){//单选题的答案
    				standard = $(this).find(":checked").size() > 0;
    				//alert("standard=" + standard + ",isRequired=" + isRequired);
    				if(isRequired && standard){
    					hadAnswer=true;
    				}
    				if(standard){
    					optionItem = {
   							itemId : itemId,
       						standard : standard
    					};
    				}
				}else if(type==1){//多选题的答案
    				standard = $(this).find(":checked").size() > 0;
    				if(isRequired && standard){
    					hadAnswer=true;
    				}
    				if(standard){
    					optionItem = {
  							itemId : itemId,
      						standard : standard
    					};
    				}
				}else if(type==2){//填空题
					//获取输入框
					var hadFill = true;
					var $input = $(this).find("input");
					$input.each(function(){
						var key = $(this).attr("name");
						var value = $(this).val();
						if(value.length == 0 && isRequired){
							hadFill = false;
							return false;
						}
						completionValue = {
								key : key,
								value : value
						};
						completions.push(completionValue);
					});
    				if(isRequired){
    					if(hadFill){
        					hadAnswer=true;
        				}
    				}else{
    					hadAnswer=true;
    				}
    				if(hadAnswer){
	    				optionItem = {
	   						 itemId : itemId,
	       					 completions : completions
	       				};
    				}
				}else if(type==3){//简答题
					var subject = $(this).find("textarea[name='subject']").val();
					var grade = $(this).attr("data-grade");
					if(subject.length > 0){
						hadAnswer=true;
					}
					if(hadAnswer){
	    				optionItem = {
	   						 itemId : itemId,
	       					 subject : subject,
	       					grade : grade
	       				};
					}
				}
				
				if(optionItem)
					optionItems.push(optionItem);
			}); 
			//alert("isRequired=" + isRequired + ",hadAnswer=" + hadAnswer);
			if(isRequired && (!hadAnswer)){
				alert("“" + $this.find("td:eq(0)").text().replace(/\n|\r|\s/g,"") + "” 必须作答！");
				ok =  false;
				return false;
			}
			
			//第条题目的问题项：
			var optionItemsValue = $.toJSON(optionItems);
			//alert(optionItemsValue);
			var json = {
				questionId : questionId,
				type: type,
				optionItemsValue: optionItemsValue
			};
//        			var id = $(this).attr("data-id");
//        			if(id && id.length > 0)
//        				json.id = id;
			topics.push(json);
		});
		//alert("topics ： "+$.toJSON(topics));
		
		$page.find(":input[name='topics']").val($.toJSON(topics));
		return ok;
	},
	//保存
	save : function(){
    	var $form = $(this);
    	if(!bc.questionary4UserForm.beforeSave($form)){
    		return;
    	}
    	bc.msg.confirm("确定要提交吗？",function(){
		//保存后重新打开表单
		var name = $form.find("#title").text();
		bc.page.save.call($form,{callback: function(json){
			bc.msg.slide("提交成功！");
			$form.dialog("close");
			//如果是新建入库就重新打开表单
				// 重新打开可编辑表单
				bc.page.newWin({
					name: name,
					mid: "questionary4User" + json.id,
					url: bc.root + "/bc/questionary4User/open",
					data: {id: json.id},
					afterClose: function(status){
						if(status) bc.grid.reloadData($form);
					}
				});

		return false;
	}});
	});
	}
};