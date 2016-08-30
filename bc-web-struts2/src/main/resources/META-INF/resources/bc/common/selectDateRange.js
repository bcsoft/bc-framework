bc.selectDateRangeFrom = {
	init : function() {
		var $page = $(this);
		
		//默认就弹出日期选择框
		var $startDate = $page.find(":input[name='startDate']");
		if($startDate.val().length > 0)
			$page.find(":input[name='endDate']").focus();
		else
			$startDate.focus();
	},
	clickOk : function(option) {
		var $page = $(this);
		//验证用户输入信息的正确性
		if(!bc.validator.validate($page)){
			return;
		}

		$page.data("data-status",{
			startDate: $page.find(":input[name='startDate']").val(),
			endDate: $page.find(":input[name='endDate']").val(),
		});
		$page.dialog("close");
	}
};