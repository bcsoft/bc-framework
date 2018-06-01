/**
 * ACL模块通用API
 */
bc.namespace("bc.acl");

/**
 * 弹出对话框对文档进行ACL配置
 * @param {Object} option 配置参数
 * @option {String} docId [必填] 文档ID
 * @option {String} docType [必填] 文档类型
 * @option {String} docName [必填] 文档名称
 * @option {String} bit [可选] ACL的控制点，默认为"01"，仅控制查阅点
 * @option {String} role [可选] 修改ACL配置需要的额外角色，没有指定时为查看ACL配置
 *        如果为任意其中一个角色即可，则多个角色编码间用符号"|"链接，
 *        如果为必须拥有每一个角色，则多个角色编码间用符号"+"链接
 * @option {String} name [可选] 对话框的标题
 */
bc.acl.config = function (option) {
  if (!option || !option.docType || !option.docId || !option.docName) {
    bc.msg.alert("必须配置文档标识、类型、名称信息 - bc.acl.control");
    return;
  }

  // 组装参数
  var aclData = {
    docId: option.docId,
    docType: option.docType,
    docName: option.docName,
    bit: option.bit || '01',
    name: option.name || "[" + option.docName + "]的访问控制",
    title: option.title || "[" + option.docName + "]的访问控制"
  };
  if (option.role) aclData.role = option.role;

  // 弹出配置对话框
  var name = option.name || "[" + option.docName + "]的访问控制";
  bc.page.newWin({
    url: bc.root + "/bc/acl/config",
    data: aclData,
    mid: option.mid || "aclConfig:" + option.docType + ":" + option.docId,
    name: name,
    title: name,
    afterClose: function (status) {
      if (status && option.onOk) {
        option.onOk.call(this, status);
      }
    }
  });
};