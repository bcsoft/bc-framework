define(["jquery", "bc", "bc.identity"], function ($, bc) {
	"use strict";
	return {
		/**
		 * 添加资源
		 */
		addResource: function () {
			var $page = $(this);
			var roleId = $page.data("extras").roleId;
			bc.identity.selectResource({
				multiple: true,
				onOk: function (resources) {
					var resourceIds = [];
					for (var i = 0; i < resources.length; i++) {
						resourceIds.push(resources[i].id);
					}
					$.ajax({
						url: bc.root + "/bc/identity/role/resource/add",
						data: {roleId: roleId, resourceIds: resourceIds.join(",")},
						dataType: "json"
					}).then(function (json) {
						json && json.count > 0 && bc.grid.reloadData($page);
					});
				}
			});
		},

		/**
		 * 删除选中的资源
		 */
		deleteResource: function () {
			var $page = $(this);
			var roleId = $page.data("extras").roleId;
			var resourceIds = bc.grid.getSelected($page.find(".bc-grid"));
			if (resourceIds.length == 0) {
				bc.msg.slide("请先选择要删除的条目！");
				return false;
			}

			bc.msg.confirm("确定要删除选定的" + resourceIds.length + "项吗？", function () {
				$.ajax({
					url: bc.root + "/bc/identity/role/resource/delete",
					data: {roleId: roleId, resourceIds: resourceIds.join(",")},
					dataType: "json"
				}).then(function (json) {
					json && json.count > 0 && bc.grid.reloadData($page);
				});
			});
		}
	};
});