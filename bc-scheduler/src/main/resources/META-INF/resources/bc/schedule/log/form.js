define(function () {
	/**
	 * 页面模块
	 * @param $page 页面的jq对象
	 * @param option 初始化参数
	 * @param readonly 是否为只读页面
	 * @constructor
	 */
	function Form($page, option, readonly) {
		//绑定“在新窗口中查看”链接的点击事件
		$page.find("#showError").click(function () {
			var errorWin = window.open('', 'bcErrorShow');
			var errorDoc = errorWin.document;
			errorDoc.open();
			errorDoc.write("<pre>" + $page.find("#msg").html() + "</pre>");
			errorDoc.close();
			errorWin.focus();
			return false;
			;
		});
	}

	return Form;
});