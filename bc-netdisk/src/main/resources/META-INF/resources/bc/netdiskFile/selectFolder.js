if (!window['bc']) window['bc'] = {};
bc.folderSelectDialog = {
  /** 点击确认按钮后的处理函数 */
  clickOk: function () {
    var $page = $(this);

    // 获取选中文件夹的信息
    var node = bc.tree.getSelected($page, true);
    var data = {};
    if (node == null) {
      //没有选中的文件夹提示
      bc.msg.alert("请先选择文件夹！");
      return false;
    } else {
      //不能将文件管理到分享给我的文件的根目录下
      if (node.id == -3) {
        bc.msg.alert("不能将文件管理到分享给我的文件的根目录下！");
        return false;
      }
      //不能将文件管理到公共硬盘的根目录下
//			if(node.id==-2){
//				bc.msg.alert("不能将文件管理到公共硬盘的根目录下！");
//				return false;
//			}
      //组装文件夹数据
      if (node.id == -1 || node.id == -2) {
      } else {
        data.id = node.id;
      }
      data.name = node.name;

    }

    // 返回
    $page.data("data-status", data);
    $page.dialog("close");
  }
};