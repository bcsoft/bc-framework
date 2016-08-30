bc.questionary4StatisticsForm = {
	init : function(option,readonly) {
		var $form = $(this);

		//将多选框和单选框设置为只读
		if(readonly){
			$form.find("input[type='radio']").each(function(){
				this.disabled=true;
				});
			$form.find("input[type='checkbox']").each(function(){
				this.disabled=true;
				});
			}
		
		//不是草稿的状态下进行统计
		if($form.find("input[name='e.status']").val()!=-1){
		$form.find("#testArea").children().each(function(){
		var type =$(this).attr("data-type");
		if(type==0||type==1){
			//显示进度条
		$(this).find(".option").each(function(){
		//该选项的作答人数:
		var amount = $(this).find("span[class='respond']").text();
		//选择项总人数
		var all = $(this).find("span[class='count']").text();
		//统计
		$(this).find(".progressbar").progressbar({
			value:(amount/all)*100
		});
		if(all!=0 && amount!=0){
			$(this).find(".count").text(bc.formatNumber(amount/all*100,"#.#")+"%");
		}else{
			$(this).find(".count").text("");
							}
						});
					
					}
				});
			}
		
		}
	};





