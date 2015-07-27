define(["jquery", "bc", "bc.identity"], function ($, bc) {
	"use strict";
	return {
		/** 点击确认按钮后的处理函数 */
		onOk: function () {
			var $page = $(this);
			var $grid = $page.find(".bc-grid");

			// 获取选中的行的id单元格
			var selected = bc.grid.getSelected($grid, true);
			if (!selected || selected.length == 0) {
				bc.msg.alert("请先选择！");
				return false;
			}

			// 获取选中的数据
			var data;
			if (!$.isArray(selected)) {//单选
				data = {
					id: selected["id"],
					name: selected["name"],
					code: selected["code"]
				};
			} else {//多选
				data = [];
				for(var i = 0; i < selected.length; i++) {
					data.push({
						id: selected[i]["id"],
						name: selected[i]["name"],
						code: selected[i]["code"]
					});
				}
			}

			console.log("selected=%o, data=%o", selected, data);
			// 返回
			$page.data("data-status", data);
			$page.dialog("close");
		}
	};
});