/**
 * 选择模板信息
 * @param {Object} option 配置参数
 * @option {String} category [可选]模板分类，默认为空，可设置单个或多个，单个：例如'经济合同',多个：逗号连接 如'经济合同,劳动合同'
 * @option {Boolean} multiple [可选]是否允许多选，默认false
 * @option {Boolean} paging [可选]是否分页，默认false
 * @option {String} status [可选]模板的状态，默认正常，设为空则代表所有状态
 * @option {Function} onOk 选择完毕后的回调函数，
 * 单选返回一个对象 格式为{
 * 					id:[id],				--模板id
 * 					typeName:[typeName],	--模板类型名称
 * 					subject:[subject],		--模板标题
 * 					code:[code],			--模板编码
 * 					version:[version],		--模板版本号
 * 					typeCode:[typeCode],	--模板类型编码
 * 					formatted:[formatted],	--允许格式化，值为true或false
 * 					size:[size],			--文件大小 int类型
 * 					category:[category],	--模板分类
 * 					path:[path],			--附件路径
 * 					desc:[desc]				--备注
 * 					}
 * 如果为多选则返回的是对象集合，[对象1,对象2]。
 */
bc.selectTemplate = function (option) {
  // 构建默认参数
  option = jQuery.extend({
    mid: 'selectTemplate',
    paging: false,
    title: '选择模板信息'
  }, option);

  // 将一些配置参数放到data参数内(这些参数是提交到服务器的参数)
  option.data = jQuery.extend({
    status: '0',
    multiple: false,
    category: option.category || ''
  }, option.data);
  if (option.title)
    option.data.title = option.title;
  if (option.multiple === true)
    option.data.multiple = true;

  //弹出选择对话框
  bc.page.newWin(jQuery.extend({
    url: bc.root + "/bc/selectTemplate/" + (option.paging ? "paging" : "list"),
    name: option.title,
    mid: option.mid,
    afterClose: function (status) {
      if (status && typeof(option.onOk) == "function") {
        option.onOk(status);
      }
    }
  }, option));
}

/**
 * 从模板库添加附件
 * @param {String} $atm 附件控件对象
 * @param {String} id 附件所属文档的id
 * @param {Object} option bc.selectTemplate的配置参数
 */
bc.addAttachFromTemplate = function ($atm, id, action, option) {
  if (!id || id.length == 0) {
    bc.msg.alert("要从模板添加附件，请先保存文档信息！");
    return false;
  }

  // 选择模板
  bc.selectTemplate(jQuery.extend({}, option, {
    onOk: function (tpls) {
      // 处理单选
      if (!option || option.multiple === false) {
        tpls = [tpls];
      }

      //显示所有要添加的模板
      var f;
      var batchNo = "k" + new Date().getTime() + "-";//批号
      for (var i = 0; i < tpls.length; i++) {
        f = tpls[i];
        //上传进度显示
        var attach = bc.attach.tabelTpl.format(f.size, bc.attach.getSizeInfo(f.size), f.path.substr(f.path.lastIndexOf(".") + 1).toLowerCase(), f.subject);
        $(attach).attr("data-tpl", batchNo + i).insertAfter($atm.find(".header")).find(".progressbar").progressbar();
      }

      //逐一处理模板
      var $newAttachs = $atm.find(".attach[data-tpl]");//含有data-tpl属性的代表还没处理
      var $totalCount = $atm.find("#totalCount");
      var $totalSize = $atm.find("#totalSize");
      var i = 0;

      function startUpload(key, tpl) {
        logger.info("startUpload:key=" + key);
        bc.ajax({
          url: action,
          dataType: "json",
          data: {id: id, tpl: tpl.code + ":" + tpl.version},
          success: function (json) {
            logger.info("finished:key=" + key + ",result=" + $.toJSON(json));

            var $attach = $newAttachs.filter("[data-tpl='" + key + "']");

            //附件总数加一
            $totalCount.text(parseInt($totalCount.text()) + 1);

            //附件总大小添加该附件的部分
            var newSize = parseInt($totalSize.attr("data-size")) + json.size;
            logger.info("s=" + $totalSize.attr("data-size") + ",a=" + json.size + ",n=" + newSize);
            $totalSize.attr("data-size", newSize).text(bc.attach.getSizeInfo(newSize));

            // 更新附件的实际大小
            $attach.find(".size").text(bc.attach.getSizeInfo(json.size));

            //删除进度条、显示附件操作按钮（延时后执行）
            setTimeout(function () {
              var $progressbar = $attach.find(".progressbar");
              var tds = $progressbar.parent();
              var $operations = tds.next();
              tds.remove();
              $operations.empty().append(bc.attach.operationsTpl);

              $attach.attr("data-id", json.id)
                .attr("data-name", json.subject)
                .attr("data-url", json.path)
                .removeAttr("data-tpl");
            }, 200);

            i++;
            if (i >= tpls.length) {//全部上传完毕
              return;
            } else {// 继续下一个附件的处理
              startUpload(batchNo + i, tpls[i]);
            }
          }
        });
      }

      startUpload(batchNo + i, tpls[0]);
    }
  }));
}

/**
 * 根据模板编码添加附件
 * @param {String} code 模板编码
 * @param {String} $atm 附件控件对象
 * @param {String} id 附件所属文档的id
 * @param {String} action 调用路径
 * @param {function} callback
 */
bc.addAttachFromTemplate4Code = function (code, $atm, id, action, callback) {
  if (!id || id.length == 0) {
    bc.msg.alert("要从模板添加附件，请先保存文档信息！");
    return false;
  }

  bc.ajax({
    url: action,
    dataType: "json",
    data: {id: id, tpl: code},
    success: function (json) {
      logger.info("result=" + $.toJSON(json));
      if (!json.success) {
        bc.msg.alert("附件生成失败！");
        return;
      }

      var $totalCount = $atm.find("#totalCount");
      var $totalSize = $atm.find("#totalSize");
      //附件总数加一
      $totalCount.text(parseInt($totalCount.text()) + 1);
      //附件总大小添加该附件的部分
      var newSize = parseInt($totalSize.attr("data-size")) + json.size;
      logger.info("s=" + $totalSize.attr("data-size") + ",a=" + json.size + ",n=" + newSize);
      $totalSize.attr("data-size", newSize).text(bc.attach.getSizeInfo(newSize));

      //构建新的附件
      var attach = bc.attach.tabelTpl.format(
        json.size
        , bc.attach.getSizeInfo(json.size)
        , json.path.substr(json.path.lastIndexOf(".") + 1).toLowerCase()
        , json.subject);
      var $attach = $(attach);
      $attach.attr("data-id", json.id);
      $attach.attr("data-name", json.subject);
      $attach.attr("data-url", json.path);
      //加进附件表
      $attach.insertAfter($atm.find(".header"));
      //删除进度跳
      var $progressbar = $attach.find(".progressbar");
      var tds = $progressbar.parent();
      var $operations = tds.next();
      tds.remove();
      $operations.empty().append(bc.attach.operationsTpl);

      //调用回调函数
      var showMsg = true;
      if (typeof callback == "function") {
        //返回false将禁止保存提示信息的显示
        if (callback.call($attach[0], json) === false)
          showMsg = false;
      }
      if (showMsg) {
        bc.msg.slide("已添加附件<b>《" + json.subject + "》</b>！");
      }
    }
  });

}

/**
 * 查看使用模板动态生成的附件
 * @param {Object} option 配置参数
 * @option {String} templateCode 模板编码  必须要设置
 * @option {boolean} isModule 控制是否需要到模块中获取数据，默认需要
 * @option {String} mserviceid 需要调用的模块中service的id名，isModule为true是需要设置
 * @option {String} moduleid 调用模块对象的id，isModule为true是需要设置
 * @option {String} markerValueJson 格式化的模板的参数json字符串对象
 * @option {String} formatSqlJson 格式化模板参数sql的json字符串对象，模板带有模板参数 必须要设置此参数
 * @option {boolean} isReplace 控制如果替换参数中没有可替换模板中的key，加上默认设置value值，默认false
 * @option {String} replaceValue 替换值
 * @option {String} to 查看文件格式化后的类型
 */
bc.inlineDynamicTemplate4Code = function (option) {
  // 构建默认参数
  option = jQuery.extend({
    isModule: true,
    isReplace: false
  }, option);

  if (!option.templateCode) {
    bc.msg.alert("请设置模板编码！");
    return;
  }

  if (option.isModule === true) {
    if (!option.mserviceid || !option.moduleid) {
      bc.msg.alert("请设置mserviceid 和  moduleid！");
      return;
    }
  }

  //在新的窗口中打开
  var url = bc.root + "/bc/templateDynamicViewFile/inline?code=" + option.templateCode;
  url += "&im=" + option.isModule;
  if (option.isModule === true) {
    url += "&msid=" + option.mserviceid;
    url += "&mid=" + option.moduleid;
  }
  if (option.markerValueJson) url += "&mvj=" + option.markerValueJson;
  if (option.formatSqlJson) url += "&fsj=" + option.formatSqlJson;
  url += "&ir=" + option.isReplace;
  if (option.isReplace === true) {
    if (option.replaceValue) url += "&rv=" + option.replaceValue;
  }
  if (option.to) url += "&to=" + option.to;
  if (option.from) url += "&from=" + option.from;
  var win = window.open(url, "_blank");
}

/**
 * 查看使用模板动态生成的附件
 * @param {Object} option 配置参数
 * @option {String} templateId 模板Id  必须要设置
 * @option {boolean} isModule 控制是否需要到模块中获取数据，默认需要
 * @option {String} mserviceid 需要调用的模块中service的id名，isModule为true是需要设置
 * @option {String} moduleid 调用模块对象的id，isModule为true是需要设置
 * @option {String} markerValueJson 格式化的模板的参数json字符串对象
 * @option {String} formatSqlJson 格式化模板参数sql的json字符串对象，模板带有模板参数 必须要设置此参数
 * @option {boolean} isReplace 控制如果替换参数中没有可替换模板中的key，加上默认设置value值，默认false
 * @option {String} replaceValue 替换值
 * @option {String} to 查看文件格式化后的类型
 */
bc.inlineDynamicTemplate4Id = function (option) {
  // 构建默认参数
  option = jQuery.extend({
    isModule: true,
    isReplace: false
  }, option);

  if (!option.templateId) {
    bc.msg.alert("请设置模板id！");
    return;
  }

  if (option.isModule === true) {
    if (!option.mserviceid || !option.moduleid) {
      bc.msg.alert("请设置mserviceid 和  moduleid！");
      return;
    }
  }

  //在新的窗口中打开
  var url = bc.root + "/bc/templateDynamicViewFile/inline?id=" + option.templateId;
  url += "&im=" + option.isModule;
  if (option.isModule === true) {
    url += "&msid=" + option.mserviceid;
    url += "&mid=" + option.moduleid;
  }
  if (option.markerValueJson) url += "&mvj=" + option.markerValueJson;
  if (option.formatSqlJson) url += "&fsj=" + option.formatSqlJson;
  url += "&ir=" + option.isReplace;
  if (option.isReplace === true) {
    if (option.replaceValue) url += "&rv=" + option.replaceValue;
  }
  if (option.to) url += "&to=" + option.to;
  if (option.from) url += "&from=" + option.from;
  var win = window.open(url, "_blank");
}

/**
 * 下载使用模板动态生成的附件
 * @param {Object} option 配置参数
 * @option {String} templateCode 模板编码  必须要设置
 * @option {boolean} isModule 控制是否需要到模块中获取数据，默认需要
 * @option {String} mserviceid 需要调用的模块中service的id名，isModule为true是需要设置
 * @option {String} moduleid 调用模块对象的id，isModule为true是需要设置
 * @option {String} markerValueJson 格式化的模板的参数json字符串对象
 * @option {String} formatSqlJson 格式化模板参数sql的json字符串对象，模板带有模板参数 必须要设置此参数
 * @option {boolean} isReplace 控制如果替换参数中没有可替换模板中的key，加上默认设置value值，默认false
 * @option {String} replaceValue 替换值
 * @option {String} to 查看文件格式化后的类型
 */
bc.downloadDynamicTemplate4Code = function (option) {
  // 构建默认参数
  option = jQuery.extend({
    isModule: true,
    isReplace: false
  }, option);

  if (!option.templateCode) {
    bc.msg.alert("请设置模板编码！");
    return;
  }

  if (option.isModule == true) {
    if (!option.mserviceid || !option.moduleid) {
      bc.msg.alert("请设置mserviceid 和  moduleid！");
      return;
    }
  }

  //在新的窗口中打开
  var url = bc.root + "/bc/templateDynamicViewFile/download?code=" + option.templateCode;
  url += "&im=" + option.isModule;
  if (option.isModule === true) {
    url += "&msid=" + option.mserviceid;
    url += "&mid=" + option.moduleid;
  }
  if (option.markerValueJson) url += "&mvj=" + option.markerValueJson;
  if (option.formatSqlJson) url += "&fsj=" + option.formatSqlJson;
  url += "&ir=" + option.isReplace;
  if (option.isReplace === true) {
    if (option.replaceValue) url += "&rv=" + option.replaceValue;
  }
  if (option.to) url += "&to=" + option.to;
  if (option.from) url += "&from=" + option.from;
  var win = window.open(url, "_blank");
}

/**
 * 下载使用模板动态生成的附件
 * @param {Object} option 配置参数
 * @option {String} templateId 模板Id  必须要设置
 * @option {boolean} isModule 控制是否需要到模块中获取数据，默认需要
 * @option {String} mserviceid 需要调用的模块中service的id名，isModule为true是需要设置
 * @option {String} moduleid 调用模块对象的id，isModule为true是需要设置
 * @option {String} markerValueJson 格式化的模板的参数json字符串对象
 * @option {String} formatSqlJson 格式化模板参数sql的json字符串对象，模板带有模板参数 必须要设置此参数
 * @option {boolean} isReplace 控制如果替换参数中没有可替换模板中的key，加上默认设置value值，默认false
 * @option {String} replaceValue 替换值
 * @option {String} to 查看文件格式化后的类型
 */
bc.downloadDynamicTemplate4Id = function (option) {
  // 构建默认参数
  option = jQuery.extend({
    isModule: true,
    isReplace: false
  }, option);

  if (!option.templateId) {
    bc.msg.alert("请设置模板id！");
    return;
  }

  if (option.isModule === true) {
    if (!option.mserviceid || !option.moduleid) {
      bc.msg.alert("请设置mserviceid 和  moduleid！");
      return;
    }
  }

  //在新的窗口中打开
  var url = bc.root + "/bc/templateDynamicViewFile/download?id=" + option.templateId;
  url += "&im=" + option.isModule;
  if (option.isModule === true) {
    url += "&msid=" + option.mserviceid;
    url += "&mid=" + option.moduleid;
  }
  if (option.markerValueJson) url += "&mvj=" + option.markerValueJson;
  if (option.formatSqlJson) url += "&fsj=" + option.formatSqlJson;
  url += "&ir=" + option.isReplace;
  if (option.isReplace === true) {
    if (option.replaceValue) url += "&rv=" + option.replaceValue;
  }
  if (option.to) url += "&to=" + option.to;
  if (option.from) url += "&from=" + option.from;
  var win = window.open(url, "_blank");
}