bc.userPaswordForm = {
	/**保存的处理*/
	save:function(){
		$page = $(this);
		// 先确认两次输入的密码相同
		var p = $page.find(":input[name=password]").val();
		if(!p){
			bc.msg.slide("密码不能为空！");
			return;
		}
		var cp = $page.find(":input[name=confirmPassword]").val();
		if(cp != p){
			bc.msg.slide("您两次输入的密码不相等！");
			return;
		}
		
		//调用标准的方法执行保存
		bc.page.save.call(this,{callback: function(){
			// 保存成功后就直接关闭窗口
			$page.dialog("close");
		}});
	}
};