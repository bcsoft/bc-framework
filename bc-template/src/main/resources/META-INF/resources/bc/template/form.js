bc.templateForm = {
  init: function (option, readonly) {
    var $form = $(this);

    //根据模板类型显示模板文件或模板内容
    if ($form.find("#templateTypeCode").val() == 'custom') {
      $form.find(".tplFile").hide();
      $form.find(":input[name='e.path']").removeAttr("data-validate");
    } else
      $form.find("#idTplContent").hide();

    //根据格式化的值，显示后隐藏参数
    if ($form.find(":input[name='e.formatted']:checked").val() == "false")
      $form.find("#idTplParam").hide();


    //绑定清除按钮事件
    $form.find("#cleanFileId").click(function () {
      bc.file.clearFileSelect($form.find("#uploadFile"));
      $form.find(":input[name='e.path']").val('');
      $form.find(":input[name='e.subject']").val('');
    });

    //绑定下载按钮事件
    $form.find(".downLoadFileId").click(function () {
      var type = $form.find("#templateTypeCode").val();
      var subject = $form.find(":input[name='e.subject']").val();
      var path = $form.find(":input[name='e.path']").val();
      var id = $form.find(":input[name='e.id']").val();
      var content = $form.find(":input[name='e.content']").val();
      if (id == "") {
        bc.msg.slide('请先保存模板！');
        return;
      }

      if (type == 'custom') {
        if (content == "") {
          bc.msg.slide('模板内容为空！');
          return;
        }
        var url = bc.root + "/bc/templatefile/download?id=" + id
        url += "&custom=" + true;
        var win = window.open(url, "blank");
        return win;
      } else {
        if (!bc.validator.validate($form)) return;

        var n = subject;// 获取文件名
        var f = "template/" + path;// 获取附件相对路径			
        // 下载文件
        bc.file.download({f: f, n: n, ptype: "Template", puid: $form.find(":input[name='e.uid']").val()});
      }
    });

    //绑定选择模板类型事件
    $form.find("select[name='e.templateType.id']").change(function () {
      bc.file.clearFileSelect($form.find("#uploadFile"));
      var id = $(this).val();
      bc.ajax({
        url: bc.root + "/bc/templateType/loadOneById",
        data: {tid: id},
        dataType: "json",
        success: function (json) {
          $form.find("#templateTypeCode").val(json.code);
          $form.find("#templateTypeExt").val(json.ext);
          //自定义文本类型显示模板内容，隐藏附件
          if (json.code != 'custom') {
            $form.find(".tplFile").show();
            $form.find("#idTplContent").hide();
            $form.find(":input[name='e.subject']").val('');
            $form.find(":input[name='e.path']").val('');
            $form.find(":input[name='e.content']").val('');
            $form.find(":input[name='e.path']").attr("data-validate", "required");
          } else {
            $form.find(".tplFile").hide();
            $form.find("#idTplContent").show();
            $form.find(":input[name='e.path']").val('');
            $form.find(":input[name='e.subject']").val('');
            $form.find(":input[name='e.path']").removeAttr("data-validate");
          }
        }
      });
    });

    //绑定格式化radio按钮组事件
    $form.find(":input[name='e.formatted']").change(function () {
      if ($(this).val() == 'true') {
        $form.find("#idTplParam").show();
      } else {
        $form.find("#idTplParam").hide();
        $form.find(".templateParamLi").remove();
      }
    });


    var liTplCate = '<li class="horizontal categoryLi ui-widget-content ui-corner-all ui-state-highlight" data-id="{0}"' +
      'style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">' +
      '<span class="text"><a href="#">{1}</a></span>' +
      '<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title={2}></span></li>';
    var ulTplCate = '<ul class="horizontal categoryUl" style="padding: 0 45px 0 0;"></ul>';
    var titleCate = $form.find("#categorys").attr("data-removeTitle");

    //绑定添加模板分类的按钮事件处理
    $form.find("#addCategory").click(function () {
      var $ul = $form.find("#categorys .categoryUl");
      var $lis = $ul.find("li");
      var selecteds = "";
      $lis.each(function (i) {
        selecteds += (i > 0 ? "," : "") + ($(this).attr("data-id"));
      });
      bc.page.newWin({
        url: bc.root + "/bc/tpl/selectCategory/paging",
        multiple: true,
        title: '选择所属分类',
        name: '选择所属分类',
        data: {'multiSelect': 'yes'},
        mid: 'selectTemplateCategorys',
        afterClose: function (params) {
          $.each(params, function (i, param) {
            if ($lis.filter("[data-id='" + param.id + "']").size() > 0) {//已存在
              logger.info("duplicate select: id=" + param.id + ",name=" + param.name);
            } else {//新添加的
              if (!$ul.size())//先创建ul元素
                $ul = $(ulTplCate).appendTo($form.find("#categorys"));

              var $liObj = $(liTplCate.format(param.id, param.name, titleCate))
                .appendTo($ul);

              //绑定查看事件
              $liObj.find("span.text").click(function () {
                bc.page.newWin({
                  url: bc.root + "/bc/tpl/form?id=" + param.id,
                  title: "所属分类",
                  name: "所属分类",
                  mid: "templateCategory" + param.id
                })
              });

              //绑定删除事件
              $liObj.find("span.click2remove")
                .click(function () {
                  $(this).parent().remove();
                });
            }
          });
        }
      });
    });


    var liTpl = '<li class="horizontal templateParamLi ui-widget-content ui-corner-all ui-state-highlight" data-id="{0}"' +
      'style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">' +
      '<span class="text"><a href="#">{1}</a></span>' +
      '<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title={2}></span></li>';
    var ulTpl = '<ul class="horizontal templateParamUl" style="padding: 0 45px 0 0;"></ul>';
    var title = $form.find("#templateParams").attr("data-removeTitle");

    //绑定添加模板参数的按钮事件处理
    $form.find("#addParam").click(function () {
      var $ul = $form.find("#templateParams .templateParamUl");
      var $lis = $ul.find("li");
      var selecteds = "";
      $lis.each(function (i) {
        selecteds += (i > 0 ? "," : "") + ($(this).attr("data-id"));
      });
      bc.page.newWin({
        url: bc.root + "/bc/selectTemplateParam/list",
        multiple: true,
        title: '选择模板参数',
        name: '选择模板参数',
        mid: 'selectTemplateParams',
        afterClose: function (params) {
          $.each(params, function (i, param) {
            if ($lis.filter("[data-id='" + param.id + "']").size() > 0) {//已存在
              logger.info("duplicate select: id=" + param.id + ",name=" + param.name);
            } else {//新添加的
              if (!$ul.size())//先创建ul元素
                $ul = $(ulTpl).appendTo($form.find("#templateParams"));

              var $liObj = $(liTpl.format(param.id, param.name, title))
                .appendTo($ul);

              //绑定查看事件
              $liObj.find("span.text").click(function () {
                bc.page.newWin({
                  url: bc.root + "/bc/templateParam/edit?id=" + param.id,
                  name: "模板参数",
                  mid: "templateParam" + param.id
                })
              });

              //绑定删除事件
              $liObj.find("span.click2remove")
                .click(function () {
                  $(this).parent().remove();
                });
            }
          });
        }
      });
    });

    //绑定查看模板分类的按钮事件处理
    var $objs = $form.find('.horizontal.categoryLi').children('span.text');
    $.each($objs, function (i, obj) {
      //绑定查看
      $(obj).delegate($(this), "click", function () {
        bc.page.newWin({
          url: bc.root + "/bc/tpl/form?id=" + $(obj).parent().attr('data-id'),
          title: "所属分类",
          name: "所属分类",
          mid: "templateParam" + $(obj).parent().attr('data-id')
        })
      });
    });

    //绑定查看模板参数的按钮事件处理
    var $objs = $form.find('.horizontal.templateParamLi').children('span.text');
    $.each($objs, function (i, obj) {
      //绑定查看
      $(obj).click(function () {
        bc.page.newWin({
          url: bc.root + "/bc/templateParam/edit?id=" + $(obj).parent().attr('data-id'),
          name: "模板参数",
          mid: "templateParam" + $(obj).parent().attr('data-id')
        })
      });
    });

    //绑定删除参数按钮事件处理
    $form.find("span.click2remove").click(function () {
      $(this).parent().remove();
    });
  },
  /** 文件上传完毕后 */
  afterUploadfile: function (json) {
    logger.info($.toJSON(json));
    if (json.success) {
      var $page = this.closest(".bc-page");
      var i = json.source.lastIndexOf(".");
      if (i != -1) {
        $page.find(':input[name="e.subject"]').val(json.source.substring(0, i));
      } else {
        $page.find(':input[name="e.subject"]').val(json.source);
      }
      $page.find(':input[name="e.path"]').val(json.to);
    } else
      bc.msg.alert(json.msg);
  },
  /**
   * 保存
   */
  save: function () {
    var $form = $(this);

    //定义函数
    function saveInfo() {
      var id = $form.find(":input[name='e.id']").val();
      var code = $form.find(":input[name='e.code']").val();
      var version = $form.find(":input[name='e.version']").val();
      var url = bc.root + "/bc/template/isUniqueCodeAndVersion";
      $.ajax({
        url: url,
        data: {tid: id, code: code, version: version},
        dataType: "json",
        success: function (json) {
          var result = json.result;
          if (result == 'save') {
            bc.page.save.call($form);
          } else {
            //系统中已有此编码
            bc.msg.alert("此编码、版本号已被其它模板使用，请修改编码或版本号！");
          }
        }
      });
    }

    //验证表单
    if (!bc.validator.validate($form)) return;

    //验证所属分类
    var $categorys = $form.find("#categorys ul.horizontal>li");
    if ($categorys.size() == 0) {
      bc.msg.alert('请选择所属分类');
      return;
    }

    //模板分类
    //将分类的id合并到隐藏域
    ids = [];
    $form.find("#categorys .categoryLi").each(function () {
      ids.push($(this).attr("data-id"));
    });
    $form.find(":input[name='templateCategoryIds']").val(ids.join(","));

    //模板参数
    //将用户的id合并到隐藏域
    ids = [];
    $form.find("#templateParams .templateParamLi").each(function () {
      ids.push($(this).attr("data-id"));
    });
    $form.find(":input[name='templateParamIds']").val(ids.join(","));


    //模板类型编码
    var typeCode = $form.find("#templateTypeCode").val();
    //模板类型后缀名
    var typeExt = $form.find("#templateTypeExt").val();
    var path = $form.find(":input[name='e.path']").val();

    //自定义文本
    if (typeCode == "custom") {
      saveInfo();
      return;
    }
    //其它附件不需检测上传文件的后缀名
    if (typeCode == "other") {
      saveInfo();
      return;
    }

    //验证后缀名
    var lastIndex = path.lastIndexOf(".");
    if (lastIndex == -1) {
      bc.msg.alert('上传的文件后缀名错误！');
      return;
    }
    //后缀名
    var ext = path.substr(lastIndex + 1);

    //判断上传文件的后缀名是否与模板类型的后缀名相同
    if (ext == typeExt) {
      saveInfo();
    } else
      bc.msg.alert("这种模板类型需要保存后缀名为" + typeExt + "!");
  },
  /** 查看历史版本号 **/
  showVersion: function () {
    var $form = $(this);
    var url = bc.root + "/bc/templates/list";
    var code = $form.find(":input[name='e.code']").val();
    var subject = $form.find(":input[name='e.subject']").val();
    if (code == '') {
      bc.msg.slide('编码不能为空');
      return;
    }
    option = {};
    // 将一些配置参数放到data参数内(这些参数是提交到服务器的参数)
    option.data = jQuery.extend({
      code: code,
    }, option.data);
    //弹出选择对话框
    bc.page.newWin(jQuery.extend({
      mid: code,
      url: url,
      name: subject + '的版本历史',
      title: subject + '的版本历史'
    }, option));
  },
  /** 预览 **/
  inline: function () {
    var $form = $(this);
    var url = bc.root + "/bc/template/loadTplConfigParam";
    var type = $form.find("#templateTypeCode").val();
    var path = $form.find(":input[name='e.path']").val();
    var tid = $form.find(":input[name='e.id']").val();
    var content = $form.find(":input[name='e.content']").val();
    var formatted = $form.find(":radio[name='e.formatted']:checked").val();

    // 无配置参数的预览方法 
    function notConfigInline() {
      if (type == "custom") {//自定义文本类型调用自定义的预览方法
        var url = bc.root + "/bc/templatefile/inline?id=" + tid
        url += "&custom=" + true;
        var win = window.open(url, "_blank");
        return win;
      }
      if (!bc.validator.validate($form)) return;
      var n = $form.find(":input[name='e.subject']").val();// 获取文件名
      var f = "template/" + path;// 获取附件相对路径
      // 预览文件
      var option = {f: f, n: n};
      var ext = f.substr(f.lastIndexOf("."));
      if (type == "xls" && ext == ".xml") {// Microsoft Word 2003 XML格式特殊处理
        option.from = "docx";
      }
      bc.file.inline(option);
    }

    // 配置参数窗口 
    function openConfigWindow(param) {
      //生成对话框的html代码
      var html = [];
      html.push('<div class="bc-page" data-type="dialog" style="overflow-y:auto;">');
      html.push('<div style="margin: 4px;max-height: 400px;max-width: 400px;">');
      html.push('<table id="inlineTemplates" style="width:100%;height:100%;">');
      html.push('<tbody>');
      var arrParam = param.split(",");
      for (var i = 0; i < arrParam.length; i++) {
        html.push('<tr>')
        html.push('<td class="label">' + arrParam[i] + '</td>');
        html.push('<td class="value">');
        html.push('<input type="text" class="ui-widget-content" style="width:180px">');
        html.push('</td>');
        html.push('<td class="value">');
        html.push('<select type="text" class="ui-widget-content" style="width:80px">');
        html.push('<option value="string">string</option>');
        html.push('<option value="boolean">boolean</option>');
        html.push('<option value="int">int</option>');
        html.push('<option value="float">float</option>');
        html.push('<option value="jsonObj">jsonObj</option>');
        html.push('<option value="jsonArr">jsonArr</option>');
        html.push('</select>');
        html.push('</td>');
        html.push('</tr>')
      }

      html.push('</tbody>');
      html.push('</table>');
      html.push('</div>');
      html.push('</div>');
      html = $(html.join("")).appendTo("body");

      //绑定双击事件
      function onClick() {
        var $trs = paramsEl.find("tr");
        var dataGlobalObj = {};
        $trs.each(function () {
          var key = $(this).find(".label").html();
          var value = $(this).find("input").val();
          var type = $(this).find("select").val();

          var dataObj = dataGlobalObj;
          var arrk = key.split('.');
          for (var i = 0; i < arrk.length; i++) {
            var subKey = arrk[i];
            // 如果是.后的最后一个，就设置返回
            if (i == arrk.length - 1) {
              if (type == "string") {
                dataObj[subKey] = value;
              } else if (type == "jsonObj" || type == "jsonArr") {
                dataObj[subKey] = $.evalJSON(value);
              } else if (type == "boolean") {
                if (value == "true") {
                  dataObj[subKey] = true;
                } else {
                  dataObj[subKey] = false;
                }
              } else if (type == "int") {
                if (!isNaN(val)) {
                  dataObj[subKey] = parseInt(value);
                } else {
                  alert("输入的不是数值！");
                  return;
                }
              } else if (type == "faloat") {
                if (!isNaN(val)) {
                  dataObj[subKey] = parseFloat(value);
                } else {
                  alert("输入的不是数值！");
                  return;
                }
              }
              continue;
            }

            // 如果当前key没有就初始化一个
            if (!dataObj[subKey]) {
              dataObj[subKey] = {};
            }
            // 设为找到的对象或最后创建的对象
            dataObj = dataObj[subKey];
          }
        });

        logger.info("markerValueJsons=" + $.toJSON(dataGlobalObj));

        var url = bc.root + "/bc/templatefile/inline"

        // Get方法打开窗口：会有url长度限制的问题
        //url+="?tid="+tid;
        //url+="&markerValueJsons="+$.toJSON(dataArr);
        //url+="&content="+content;
        //var win = window.open(url, "_blank");
        // return win;

        // Post方法打开窗口：参数无限制
        var $post = ['<form name="templateInline" method="post">'];
        $post.push('<input type="hidden" name="id" value="' + tid + '">');
        $post.push('<input type="hidden" name="markerValueJsons" value="">');
        $post.push('</form>');
        $post = $($post.join(""));
        $post.children("[name='markerValueJsons']").val($.toJSON(dataGlobalObj));
        var _form = $post.get(0);
        _form.action = url;
        _form.target = "_blank";	// 新窗口打开
        _form.submit();				//提交表单
        return null;

        //销毁对话框
        html.dialog("destroy").remove();
      }

      var paramsEl = html.find("#inlineTemplates");

      //弹出对话框让用户选择司机
      html.dialog({
        id: "inlineTemplateParams",
        title: "请输入模板配置参数对应的值",
        dialogClass: 'bc-ui-dialog ui-widget-header',
        width: 300, modal: true,
        minWidth: 300,
        buttons: [{text: "确定", click: onClick}]
      });
    }

    if (tid == '') {
      bc.msg.slide('请先保存模板！');
      return;
    }

    if (type == "custom" && content == "") {
      bc.msg.slide('模板内容为空！');
      return;
    }

    //不允许格式化 直接预览文件
    if (formatted == 'false') {
      notConfigInline();
      return;
    }

    //先加载一次配置参数
    $.ajax({
      url: url,
      data: {tid: tid},
      dataType: "json",
      success: function (json) {
        if (json.value) {//有配置参数打开配置参数窗口
          openConfigWindow(json.value);
        } else {//无配置参数调用默认的方法
          notConfigInline();
        }
      }
    });
  }
};