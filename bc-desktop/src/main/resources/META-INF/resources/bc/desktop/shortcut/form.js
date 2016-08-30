define(["bc"], function (bc) {
	/**
	 * 页面模块
	 * @param $page 页面的jq对象
	 * @param option 初始化参数
	 * @param readonly 是否为只读页面
	 * @constructor
	 */
	function Form($page, option, readonly) {
		//绑定选择图标样式的按钮事件处理
		$page.find("#selectIconClass").click(function () {
			bc.page.newWin({
				url: bc.root + "/bc/shortcut/selectIconClass",
				name: "选择图标样式",
				mid: "selectShortcutIconClass",
				afterClose: function (iconClass) {
					logger.info("iconClass=" + iconClass);
					if (iconClass) {
						$page.find(":input[name='e.iconClass']").val(iconClass);
					}
				}
			});
		});
	}

	return Form;
});