/**
 * 选择区域信息
 * @param {Object} option 配置参数
 * @option {String} types [可选]区域的类型，0-国家,1-省级,2-地级,3-县级,4-乡级,5-村级，默认为空
 * 						选择多区域类型用逗号相连，如省级，地级 则为 '1,2'						
 * @option {Boolean} multiple [可选]是否允许多选，默认false
 * @option {Boolean} paging [可选]是否分页，默认true
 * @option {String} status [可选]状态，默认正常，设为空则代表所有状态
 * @option {Function} onOk 选择完毕后的回调函数，，
  * 单选返回一个对象 格式为{
 * 					id:[id],				--区域id
 * 					type:[type],			--区域类型
 * 					status:[status],		--状态
 * 					pname:[pname],			--区域上级
 * 					code:[code],			--区域编码
 * 					name:[name],			--区域名称
 * 					}
 * 如果为多选则返回的是对象集合，[对象1,对象2]。
 */
bc.selectPlaceOrigin = function(option) {
	// 构建默认参数
	option = jQuery.extend({
		mid: 'selectPlaceOrigin',
		paging: true,
		title: '选择区域信息'
	},option);
	
	// 将一些配置参数放到data参数内(这些参数是提交到服务器的参数)
	option.data = jQuery.extend({
		paging: option.paging || true,
		status: option.status || '0',
		multiple: option.multiple || false,
		types: option.types || ''
	},option.data);
	
	//弹出选择对话框
	bc.page.newWin(jQuery.extend({
		url: bc.root + "/bc/selectSuperiorPlace/"+ (option.paging ? "paging" : "list"),
		name: option.title,
		mid: option.mid,
		afterClose: function(status){
			if(status && typeof(option.onOk) == "function"){
				option.onOk(status);
			}
		}
	},option));
}
