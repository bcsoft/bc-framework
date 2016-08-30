/**
 * 表单模块通用API
 */
bc.namespace("bc.cform");

/**
 * 渲染业务对象表单
 * 如果业务对象的表单不存在则自动创建一个
 * @param {Object} option 配置参数
 * @option {String} type [必填]表单业务类别，如：CarManCert（司机证件）
 * @option {String} code [必填]表单业务编码，如：certIdentity（身份证）
 * @option {Integer} pid [必填]表单业务ID，如：司机招聘表的ID
 * @option {String} ver [可选]版本号，没有指定代表版本1
 * @option {String} tpl [必填]模板编码
 * @option {String} subject [必填]标题
 * @option {String} role [可选]对表单进行编辑需要的角色，使用"|"连接多个角色编码代表或关系，使用"+"连接多个角色编码代表和关系
 * @option {boolean} readonly [可选]是否强制只读显示，默认自动根据 role 参数的值判断
 * @option {Object} data [可选]附加数据，格式为：[{name: "sex", value: 1, type: "int"}, ...]
 * @option {boolean} replace [可选]如果现存表单中含有data参数中的数据，是否优先使用data中的数据，默认false
 * @option {String} onOk [可选]表单成功修改后的回调函数
 */
bc.cform.render = function(option){
	if (!option || !option.type || !option.code || !option.pid || !option.subject || !option.tpl) {
		bc.msg.alert("必须配置 type、code、pid、subject、tpl 参数 - bc.cform.render");
		return;
	}

	// 组装参数
	var data = {
		type: option.type,
		code: option.code,
		pid: option.pid,
		tpl: option.tpl,
		ver: option.ver || 1,
		subject: option.subject
	};
	if(option.role) data.role = option.role;
	if(option.readonly) data.readonly = option.readonly;
	if(option.replace) data.replace = option.replace;
	if(option.extraData) data.extraData = $.toJSON(option.extraData);

	// 弹出渲染窗口
	bc.page.newWin({
		url: bc.root + "/bc/cform/render",
		data: data,
		mid: option.type + "." + option.code + "." + option.pid + "." + (option.ver ? option.ver : "newest"),
		name: option.subject,
		title: option.subject,
		afterClose: function(status){
			if(status && option.onOk){
				option.onOk.call(this, status);
			}
		}
	});
};

/**
 * 删除业务对象表单
 * @param {Object} option 配置参数
 * @option {String} type [必填]表单业务类别，如：CarManCert（司机证件）
 * @option {String} code [必填]表单业务编码，如：certIdentity（身份证）
 * @option {Integer} pid [必填]表单业务ID，如：司机招聘表的ID
 * @option {String} ver [可选]版本号，没有指定代表最新版本
 * @option {String} onOk [可选]表单成功删除后的回调函数
 */
bc.cform.delete_ = function(option){
	if (!option || !option.type || !option.code || !option.pid || !option.ver) {
		bc.msg.alert("必须配置 type、code、pid、ver 参数 - bc.cform.delete_");
		return;
	}

	// 组装参数
	var data = {
		type: option.type,
		code: option.code,
		pid: option.pid,
		ver: option.ver
	};
	bc.msg.confirm("确定要删除 <b>"+(option.name || "选定的条目")+"</b> 吗？",function(){
		$.ajax({
			method: "POST",
			dataType: "json",
			url: bc.root + "/bc/cform/delete",
			data: {type: option.type, code: option.code, pid: option.pid, ver: option.ver},
			success: function(json){
				if(json.success){
					if(typeof option.onOk == "function"){
						option.onOk.call(this, json);
					}
				}else{
					bc.msg.info(json.msg);
				}
			}
		})
	});
};

/**
 * 默认的表单初始化方法
 */
bc.cform.init = function(option, readonly){
	var $page = $(this);
	var ns = $page.attr("data-namespace");
	if(ns && ns.length > 0){
		ns = bc.getNested(ns);
		if(ns && ns["init"]){
			//console.debug("call " + $page.attr("data-namespace") + ".init");
			ns["init"].call(this);// 调用模块自定义的初始化方法
		}
	}

	// 监控记录表单的值
	if(readonly) return;
	bc.cform.monitorChange($page);
};

/**
 * 监控记录表单的值，方便日后修改后的对比
 */
bc.cform.monitorChange = function($page){
	// 判断是否新建状态: 新建状态时，不作记录
	var formId = $page.find("input:[name='id'][data-scope='form']").val();
	var isNew = !(formId && formId.length > 0);
	if(isNew) return;

	// 文本输入框、隐藏域、select
	$page.find("input:text:not(.ignore), input:hidden:not(.ignore), textarea:not(.ignore), select:not(.ignore)")
		.each(function() {
		$(this).data("oldValue", $(this).val());
	});

	var oldCheckValues = $page.data("oldCheckValues");
	if(!oldCheckValues){
		oldCheckValues = {};
		$page.data("oldCheckValues", oldCheckValues);
	}

	// 单选 radio
	$page.find("input:radio:not(.ignore):checked").each(function() {
		oldCheckValues[this.name] = this.value;
	});

	// 多选 checkbox
	$page.find("input:checkbox:not(.ignore):checked").each(function() {
		if(!oldCheckValues[this.name]){
			oldCheckValues[this.name] = [];
		}
		oldCheckValues[this.name] = this.value;
	});
};

/**
 * 获取已经修改的数据{【name】:{value、type、scope(field默认|form)}}, ...}
 */
bc.cform.getChangedData = function($page){
	var changedData = {}, c = 0, $this, value, type, scope, cfg;

	// 文本输入框、隐藏域、select
	$page.find("input:text:not(.ignore), input:hidden:not(.ignore), textarea:not(.ignore), select:not(.ignore)")
		.each(function() {
			$this = $(this);
			value = $this.val();
			if($this.data("oldValue") != value){
				scope = $this.attr("data-scope");
				type = $this.attr("data-type");
				if(!scope && !type){
					changedData[this.name] = value;
				}else{
					cfg = {value: value};
					if(scope) cfg.scope = scope;
					if(type) cfg.type = type;
					changedData[this.name] = cfg;
				}
				c++;
			}
		});

	var oldCheckValues = $page.data("oldCheckValues");

	// 单选 radio
	$page.find("input:radio:not(.ignore):checked").each(function() {
		if(!oldCheckValues[this.name] || oldCheckValues[this.name].value != this.value){
			var $cfg = $(this).closest(".cfg");// 找到上级容器的配置
			if($cfg.length == 0){
				throw ("没有找到 name=" + this.name + "  的单选框对应的父配置容器！");
			}
			changedData[this.name] = {
				value: this.value,
				type: $cfg.attr("data-type") || "string",
				scope: $cfg.attr("data-scope") || "field"
			};
			c++;
		}
	});

	// 多选 checkbox
	$page.find("input:checkbox:not(.ignore):checked").each(function() {
		$this = $(this);
		var changed = true;
		var ov = oldCheckValues[this.name];// 原来的值
		if (ov) {
			for (var i = 0; i < ov; i++) {
				// 如果原来有此值代表没有修改
				if (ov[i] == this.value) {
					changed = false;
					break;
				}
			}
		}

		if(changed){
			var $cfg = $(this).closest(".cfg");// 找到上级容器的配置
			if($cfg.length == 0){
				throw ("没有找到 name=" + this.name + "  的多选框对应的父配置容器！");
			}
			if(!changedData[this.name]){
				changedData[this.name] = {
					value: [this.value],// 数组格式
					type: $cfg.attr("data-type") || "boolean[]",
					scope: $cfg.attr("data-scope") || "field"
				};
			}else{
				changedData[this.name].value.push(this.value);// 添加新的选项值
			}
			c++;
		}
	});

	return c > 0 ? changedData : null;
};

/**
 * 保存按钮
 */
bc.cform.save = function(){
	bc.cform.doSave.call(this, false);
};

/**
 * 保存并关闭按钮
 */
bc.cform.saveAndClose = function(){
	bc.cform.doSave.call(this, true);
};

/**
 * 默认的表单保存方法
 * @param closeAfterSave 保存成功后是否关闭窗口
 */
bc.cform.doSave = function(closeAfterSave, callback){
	var $page = $(this);

	// 表单验证
	var ns = $page.attr("data-namespace");
	var customValidate;
	if(ns && ns.length > 0){
		ns = bc.getNested(ns);
		if(ns && ns["validate"]) customValidate = ns["validate"];
	}
	if(customValidate){
		if (!customValidate.call(this)) return;// 调用模块自定义的表单验证方法
	}else{
		if (!bc.validator.validate($page)) return;// 调用默认的表单验证方法
	}

	// 判断是否正在保存，若是就返回
	if ($page.data("saving"))
		return;

	// 获取变动的数据
	var changedData = bc.cform.getChangedData($page);
	if (!changedData) {
		bc.msg.info("数据还没有修改过，无需保存！");
		return;
	}
	//console.log(changedData);

	//表单是否为新建
	var formId = $page.find("input:[name='id'][data-scope='form']").val();
	var isNew = !(formId && formId.length > 0);

	// 设置正在保存中
	$page.data("saving", true);

	// 保存修改
	$.post(bc.root + "/bc/cform/save", {
		type: $page.find("input[name='type']").val(),
		code: $page.find("input[name='code']").val(),
		pid: $page.find("input[name='pid']").val(),
		ver: $page.find("input[name='ver']").val(),
		data: $.toJSON(changedData)
	}, null, "json")
	.done(function(result) {
		$page.data("saving", false);
		if(result.success) {
			bc.msg.slide(result.msg);
			if(isNew) {
				$page.find("input[name='id']").val(result.id);
			}
			$page.data("data-status", result);
			if(closeAfterSave === true){
				$page.dialog("close");
			}else{
				// 重新监控记录表单的值
				bc.cform.monitorChange($page);
			}

			callback && callback.call(this, result);
		}else{
			bc.msg.alert(result.msg);
		}
	})
	.fail(function(result) {
		$page.data("saving", false);
		bc.msg.alert("保存失败了！");
	});
};

/**
 * 获取表单当前的最新版本号
 * @param {Object} option 配置参数
 * @option {String} type [必填]表单业务类别，如：CarManCert（司机证件）
 * @option {String} code [必填]表单业务编码，如：certIdentity（身份证）
 * @option {Integer} pid [必填]表单业务ID，如：司机招聘表的ID
 */
bc.cform.nextVer = function(option){
	$.post(bc.root + "/bc/cform/nextver", {
		type: option.type,
		code: option.code,
		pid: option.pid
	}, null, "html")
	.done(function(newVer) {
		option.onOk && option.onOk.call(this, newVer);
	});
};

/**
 * 存为新版本按钮
 * @param {Boolean} closeAfterSave 成功后是否关闭窗口
 * @param {Function} callback 成功后执行的回调函数
 */
bc.cform.save2NewVersion = function(closeAfterSave, callback){
	var $page = $(this);
	var $ver = $page.find("input[name='ver']");
	var currentVer = $ver.val();
	bc.cform.nextVer({
		type: $page.find("input[name='type']").val(),
		code: $page.find("input[name='code']").val(),
		pid: $page.find("input[name='pid']").val(),
		onOk: function(newVer) {
			bc.msg.confirm("确定要将当前版本\"" + currentVer + "\"存为新版本\"" + newVer + "\"吗？", function(){
				bc.nextUid("Form",function(uid) {
					$ver.val(newVer);
					$page.find("input[name='uid']").val(uid);
					$page.find("input[name='id']").val("");

					// 去除当前值的监控
					$page.data("oldCheckValues", {});
					$page.find("input:not(.ignore)").removeData("oldValue");
					bc.cform.doSave.call($page, closeAfterSave, function () {
						callback && callback.apply(this, arguments);
					});
				});
			});
		}
	});
};
