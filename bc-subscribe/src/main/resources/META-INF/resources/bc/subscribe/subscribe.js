/**
 * 选择订阅信息
 * @param {Object} option 配置参数
 * @option {String} selected [可选]已选择订阅的id，多个逗号链接，如'1,2'
 * @option {Boolean} multiple [可选]是否允许多选，默认false
 * @option {Boolean} paging [可选]是否分页，默认false
 * @option {Function} onOk 选择完毕后的回调函数，
 * 单选返回一个对象 格式为{
 * 					id:[id],				--模板id
 * 					subject:[subject],		--主题
 * 					eventCode:[eventCode]	--事件编码
 * 					}
 * 如果为多选则返回的是对象集合，[对象1,对象2]。
 */
bc.selectSubscribe = function (option) {
  // 构建默认参数
  option = jQuery.extend({
    mid: 'selectSubscribe',
    paging: false,
    title: '选择订阅信息'
  }, option);

  // 将一些配置参数放到data参数内(这些参数是提交到服务器的参数)
  option.data = jQuery.extend({
    multiple: false,
    selected: option.selected || ''
  }, option.data);
  if (option.title)
    option.data.title = option.title;
  if (option.multiple === true)
    option.data.multiple = true;

  //弹出选择对话框
  bc.page.newWin(jQuery.extend({
    url: bc.root + "/bc/selectSubscribe/" + (option.paging ? "paging" : "list"),
    name: option.title,
    mid: option.mid,
    afterClose: function (status) {
      if (status && typeof(option.onOk) == "function") {
        option.onOk(status);
      }
    }
  }, option));
}
