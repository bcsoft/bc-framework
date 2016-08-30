/** ACL模块配置访问权限的对话框 */
bc.namespace("bc.aclConfigDialog");
bc.aclConfigDialog = {
	init : function(option,readonly) {
		if(readonly) return;
		var $page = $(this);

		// 点击切换行的选择状态
		$page.on("click", ".toggleSellect", function(){
			$(this).parent().toggleClass("ui-state-highlight")
				.find("td:eq(0)>span.ui-icon").toggleClass("ui-icon-check");
		});

		// 全选|反选
		$page.find("input[name='toggleSellectAll'").change(function(){
			console.log(this.checked);
			$page.find(".data tr.row").toggleClass("ui-state-highlight", this.checked)
				.find("td:eq(0)>span.ui-icon").toggleClass("ui-icon-check", this.checked);
		});

		// 删除选中行
		var $data = $page.find(".data");
		$page.find("#delete").click(function() {
			var $trs = $data.find("tr.ui-state-highlight");
			if($trs.length == 0){
				bc.msg.slide("请先选择要删除的访问者配置！");
				return;
			}
			bc.msg.confirm("确定要删除选定的 <b>"+$trs.length+"</b>个访问者配置吗？",function(){
				$trs.remove();
				bc.aclConfigDialog.rebuildIndex($data);	// 重建序列号
			});
		});

		// 上移选中的行
		$page.find("#up").click(function() {
			var $trs = $data.find("tr.ui-state-highlight");
			if($trs.length == 0){
				bc.msg.slide("请先选择要上移的行！");
				return;
			}else{
				var changed = false;
				$trs.each(function(){
					var $tr = $(this);
					if(this.rowIndex > 0){
						$tr.prev().insertAfter($tr);
						changed = true;
					}else{
						bc.msg.slide("不能上移第一条！");
					}
				});
				if(changed) bc.aclConfigDialog.rebuildIndex($data);	// 重建序列号
			}
		});

		// 下移选中的行
		$page.find("#down").click(function() {
			var $trs = $data.find("tr.ui-state-highlight");
			if($trs.length == 0){
				bc.msg.slide("请先选择要下移的行！");
				return;
			}else{
				var changed = false;
				var $afterTr;
				for(var i = $trs.length - 1; i >= 0; i--){
					var $tr = $($trs[i]);
					$afterTr = $tr.next();
					if($afterTr.length > 0){
						$afterTr.insertBefore($tr);
						changed = true;
					}else{
						bc.msg.slide("不能下移最后一行！");
					}
				}
				if(changed) bc.aclConfigDialog.rebuildIndex($data);	// 重建序列号
			}
		});
	},

	/**
	 * 添加 Actor 到 table 的行中
	 */
	addActors : function($page, actors){
		var $tableBody = $page.find(".data tbody");
		if($tableBody.length == 0)
			$tableBody = $("<tbody></tbody>").appendTo($page.find(".data table"));
		$.each(actors, function(i, actor){
			//console.debug(actor);
			var $tr = $tableBody.find(":hidden[name='actor.id'][value='" + actor.id + "']");
			if($tr.length == 0) {
				var control = "";
				if($page.find("input[name='b1']").val() == "true"){
					control += '<label><input type="checkbox" name="BIT_1" checked>查阅</label>';
				}
				if($page.find("input[name='b2']").val() == "true"){
					control += '\r\n<label><input type="checkbox" name="BIT_2" checked>编辑</label>';
				}

				$tr = $(bc.formatTpl(bc.aclConfigDialog.DATA_TR_TPL, $.extend({
					index: $tableBody.children().length + 1,
					control: control
				}, actor)));
				$tableBody.append($tr);
			}

			// 选中已存在的行或新加的行
			$tr.closest("tr").toggleClass("ui-state-highlight", true)
				.find("td:eq(0)>span.ui-icon").toggleClass("ui-icon-check", true);
		});
	},

	/** 添加用户 */
	addUsers : function(){
		var $page = $(this).closest(".bc-page");
		bc.identity.selectUser({
			multiple: true, history: false, status: '0',
			onOk: function (users) {
				bc.aclConfigDialog.addActors($page, users);
			}
		});
	},

	/** 添加岗位 */
	addGroups : function(){
		var $page = $(this).closest(".bc-page");
		bc.identity.selectGroup({
			multiple: true, history: false, status: '0',
			onOk: function (groups) {
				bc.aclConfigDialog.addActors($page, groups);
			}
		});
	},

	/** 添加单位或部门 */
	addUnitOrDepartments : function(){
		var $page = $(this).closest(".bc-page");
		bc.identity.selectUnitOrDepartment({
			multiple: true, history: false, status: '0',
			onOk: function (ous) {
				bc.aclConfigDialog.addActors($page, ous);
			}
		});
	},

	/** 数据行的 DOM 模板 */
	DATA_TR_TPL: '<tr class="ui-widget-content row">'
		+ '	<td class="id first toggleSellect" style="text-align: center"><span class="ui-icon"></span><span class="index">{{index}}</span></td>'
		+ '	<td class="middle" title="{{name}}"><input type="hidden" name="actor.id" value="{{id}}">{{name}}</td>'
		+ '	<td class="middle">{{&control}}</td>'
		+ '	<td class="last"></td>'
		+ '</tr>',

	/**
	 * 重建序列号
	 */
	rebuildIndex : function($data){
		$data.find(".index").each(function(i){
			this.innerText = i + 1;
		});
	},

	/**
	 * 点击确认按钮的回调函数
	 */
	onOk : function(){
		var $page = $(this);

		// 组装配置明细的数据，格式为：[{id: 123, role: "01"},...]
		var details = [];
		var $tr, b1, b2;
		$page.find(".data tr").each(function(i){
			$tr = $(this);
			b1 = $tr.find("input[name='BIT_1']");
			b2 = $tr.find("input[name='BIT_2']");
			details.push({
				id: $tr.find("input[name='actor.id']").val(),
				role: (b2.length > 0 && b2[0].checked ? "1" : "0") + (b1.length > 0 && b1[0].checked ? "1" : "0")
			});
		});

		// 保存
		$.post(bc.root + "/bc/acl/saveConfig", {
			docType: $page.find("input[name='docType']").val(),
			docId: $page.find("input[name='docId']").val(),
			docName: $page.find("input[name='docName']").val(),
			role: $page.find("input[name='role']").val(),
			details: $.toJSON(details)
		}, null, "json")
		.done(function(result) {
			if(result.success) {
				bc.msg.slide(result.msg);
				if(result.changed) {
					$page.data("data-status", "saved");
				}
				$page.dialog("close");
			}else{
				bc.msg.alert("保存失败：" + result.msg);
			}
		})
		.fail(function(result) {
			bc.msg.alert("保存失败了！");
		});
	}
};