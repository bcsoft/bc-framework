/**
 * 选择文件夹信息
 */
bc.selectFolder = function(option) {
	// 构建默认参数
	option = jQuery.extend({
		mid: 'selectFolder',
		paging: true,
		title: '选择文件夹',
		folderId:null
	},option);
	
	
	// 将一些配置参数放到data参数内(这些参数是提交到服务器的参数)
	option.data = jQuery.extend({
		status: '0',
		multiple: false,
		category:option.category||''
	},option.data);
	if (option.title)
		option.data.title = option.title;
	if (option.multiple === true)
		option.data.multiple = true;
	if(option.folderId)
		option.data.folderId = option.folderId;

	//弹出选择对话框
	bc.page.newWin(jQuery.extend({
		//url: bc.root + "/bc/selectFolders/"+ (option.paging ? "paging" : "list"),
		url: bc.root + "/bc/netdiskFiles/selectFolders",
		name: option.title,
		mid: option.mid,
		afterClose: function(status){
			if(status && typeof(option.onOk) == "function"){
				option.onOk(status);
			}
		}
	},option));
}
