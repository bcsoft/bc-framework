bc.placeOriginForm = {
  init: function () {
    var $form = $(this);
    // 点击选择上级小按钮弹出选择视图
    $form.find("#selectPname").click(function () {
      var type = $form.find("input:[name='e.type']:radio:[checked]").val();
      // 如果为省级，则无上级可选
      if (type == 1) {
        bc.msg.info("对于省级类型，不能选择上级！");
      } else {
        // 弹出选择对话框
        bc.page.newWin({
          url: bc.root + "/bc/selectSuperiorPlace/paging",
          name: '选择上级信息',
          mid: 'selectSuperiorPlace',
          data: {
            multiple: false,		// 单选
            status: '0',			// 选择正常状态的
            types: type - 1			// 只能选择上一个级别，不能跨级选择
          },
          afterClose: function (j) {
            if (j) {
              $form.find(":input[name='e.pid']").val(j.id);
              var pname = (j.pname && j.pname.length > 0 ? j.pname + "/" + j.name : j.name);
              $form.find(":input[name='e.pname']").val(pname);
            }
          }
        });
      }
    });

//		//点击清除上级按钮事件
//		$form.find("#clearPname").click(function() {
//			$form.find(":input[name='e.fullname']").val($form.find(":input[name='e.name']").val());
//		});
  }
};