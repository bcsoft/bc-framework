bc.superiorPlaceSelectDialog = {
	/** 点击确认按钮后的处理函数 */
	clickOk : function() {
		var $page = $(this);

		// 获取选中的行的id单元格
		var $tds = $page.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		if ($tds.length == 0) {
			alert("请先选择！");
			return false;
		}

		// 获取选中的数据
		var data;
		var $grid = $page.find(".bc-grid");
		if ($grid.hasClass("singleSelect")) {// 单选
			var $tr = $grid.find(">.data>.right tr.ui-state-highlight");
			data = {};
			data.id = $tds.attr("data-id");
			data.type= $tr.find("td:eq(0)").text();
			data.code= $tr.find("td:eq(1)").text();
			data.name = $tr.find("td:eq(2)").text();
			data.pname= $tr.find("td:eq(3)").text();
			data.fullname= (data.pname && data.pname.length > 0) ? data.pname + "/" + data.name : data.name;
		}else{
			data=[];
			var $right = $grid.find(">.data>.right");;
            var pname,name;
			$tds.each(function(i){
				var $this = $(this);
				var $row = $right.find("tr.row:eq(" + $this.parent().index() + ")");
                name =  $row.find("td:eq(2)").text();
                pname =  $row.find("td:eq(3)").text();
				data.push({
					id: $this.attr("data-id"),
					type: $row.find("td:eq(0)").text(),
					code: $row.find("td:eq(1)").text(),
					name: name,
					pname:pname,
                    fullname: (pname && pname.length > 0) ? pname + "/" + name : name
				});
			});	
		}
		logger.info($.toJSON(data));

		// 返回
	    $page.data("data-status", data);
		$page.dialog("close");
	}
};