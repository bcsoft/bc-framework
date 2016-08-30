bc.selectDateFrom = {
	init : function() {
		var $page = $(this);

		// 默认就弹出日期选择框
		$page.parent().find(".ui-dialog-buttonpane button").focus();
	},
	clickOk : function(option) {
		var $page = $(this);
		// 验证用户输入信息的正确性
		if (!bc.validator.validate($page)) {
			return;
		}

		$page.data("data-status", $page.find(":input[name='curDate']").val());
		$page.dialog("close");
	}
};