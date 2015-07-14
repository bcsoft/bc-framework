define(["jquery", "bc", "bc.identity"], function ($, bc) {
	"use strict";

	/** 添加角色与Actor之间的关联关系
	 *
	 * @param {HTMLElement} $page - 当前页面
	 * @param {Number} roleId - 角色ID
	 * @param {Array} actorIds - Actor ID数组
	 */
	function addActor($page, roleId, actorIds) {
		$.ajax({
			url: bc.root + "/bc/identity/role/actor/add",
			data: {roleId: roleId, actorIds: actorIds.join(",")},
			dataType: "json"
		}).then(function (json) {
			console.log("r=", json);
			json && json.count > 0 && bc.grid.reloadData($page);
		});
	}

	return {
		/**
		 * 添加用户
		 */
		addUser: function () {
			var $page = $(this);
			var roleId = $page.data("extras").roleId;
			bc.identity.selectUser({
				multiple: true,
				history: false,
				onOk: function (actors) {
					var actorIds = [];
					for (var i = 0; i < actors.length; i++) {
						actorIds.push(actors[i].id);
					}
					addActor($page, roleId, actorIds);
				}
			});
		},

		/**
		 * 添加岗位
		 */
		addGroup: function () {
			var $page = $(this);
			var roleId = $page.data("extras").roleId;
			bc.identity.selectGroup({
				multiple: true,
				onOk: function (actors) {
					var actorIds = [];
					for (var i = 0; i < actors.length; i++) {
						actorIds.push(actors[i].id);
					}
					addActor($page, roleId, actorIds);
				}
			});
		},

		/**
		 * 添加单位或部门
		 */
		addUnitOrDepartment: function () {
			var $page = $(this);
			var roleId = $page.data("extras").roleId;
			bc.identity.selectUnitOrDepartment({
				multiple: true,
				onOk: function (actors) {
					var actorIds = [];
					for (var i = 0; i < actors.length; i++) {
						actorIds.push(actors[i].id);
					}
					addActor($page, roleId, actorIds);
				}
			});
		},

		/**
		 * 删除选中的Actor
		 */
		deleteActor: function () {
			var $page = $(this);
			var roleId = $page.data("extras").roleId;
			var actorIds = bc.grid.getSelected($page.find(".bc-grid"));
			if (actorIds.length == 0) {
				bc.msg.slide("请先选择要删除的条目！");
				return false;
			}

			bc.msg.confirm("确定要删除选定的" + actorIds.length + "项吗？", function () {
				$.ajax({
					url: bc.root + "/bc/identity/role/actor/delete",
					data: {roleId: roleId, actorIds: actorIds.join(",")},
					dataType: "json"
				}).then(function (json) {
					json && json.count > 0 && bc.grid.reloadData($page);
				});
			});
		}
	};
});