bc.selectGroup = {
	init : function() {
		var $page = $(this);
		//绑定双击事件
		$page.find("select").dblclick(function(){
			bc.selectGroup.clickOk.call($page[0]);
		});
	},
	clickOk : function() {
		var $page = $(this);
		var select = $page.find("select")[0];
		if(select.selectedIndex == -1){
			bc.msg.slide("必须先选择岗位信息！");
			return false;
		}
		var item;
		if(select.multiple){
			item=[];
			// 循环选定的每一个项目，将该项添加到列表中
			for (var i = 0; i < select.length; i++){
				if (select.options[i].selected){
					var value = select.options[i].value.split(",");
					item.push({id: value[0],name: value[1],fname: select.options[i].text});
				}
			}
		}else{
			var value = select.value.split(",");
			item={id: value[0],name: value[1],fname: select.options[select.selectedIndex].text};
		}
		$page.data("data-status",item);
		$page.dialog("close");
	}
}