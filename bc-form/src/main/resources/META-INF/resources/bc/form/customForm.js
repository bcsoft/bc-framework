/**
 * 自定义变淡操作方法
 * 
 */
bc.customForm = {
	/**
	 * 自定义表单创建方法
	 * 
	 * @param {Object}
	 *            option 配置参数
	 * @option {String} tpl [必填]模板编码
	 * @option {String} subject [必填]标题
	 * @option {String} type [必填]类别
	 * @option {Integer} pid [必填]pid 新建为时0
	 * @option {String} code [必填]编码 新建为时空字符窜
	 * @option {boolean} isNew [可选]强制指定表单为新建状态 true时指定为新建状态 false时指定为非新建状态
	 * 								,默认为undefined,即不对表单状态作指定,表单的状态有后台action控制
	 * @option {boolean} readonly [可选]是否只读-- true为只读 false为可编辑,默认为false
	 * @option {Object} data [可选]附带数据 如[{name : "sex",value : 1,type :
	 *         "int"}]
	 * @option {String} afterOpen [可选]窗口新建好后的回调函数
	 * @option {String} afterClose [可选]窗口关闭后的回调函数。function(event, ui)
	 * @option {String} beforeClose
	 *         [可选]窗口关闭前的回调函数，返回false将阻止关闭窗口。function(event, ui)
	 * @option {Object} buttons [可选]扩展按钮如[{click : saveClick, text :"保存"
	 *         }(click为自定义点击事件，text为按钮名称) 默认添加保存按钮
	 * 
	 */
	render : function(option) {
		if (!(option && option.tpl && option.subject && option.type
				&& option.pid && option.code)) {
			alert("必须设置option参数！");
			return;
		}

		if (!option.name)
			option.name = option.subject;
		if (!option.title)
			option.title = option.subject;
		if (option.pid && option.code) {
			option.mid = option.type + option.pid + option.code;
		}
		if (!option.data) {
			option.data = {}
		}

		if (option.extraData) {
			option.data = {
				tpl : option.tpl,
				subject : option.subject,
				type : option.type,
				pid : option.pid,
				code : option.code,
				extraData : $.toJSON(option.extraData)
			};
		} else {
			option.data = {
				tpl : option.tpl,
				subject : option.subject,
				type : option.type,
				pid : option.pid,
				code : option.code
			};
		}

		if (typeof (option.readonly) == "undefined") {
			option.readonly = false;
		}
		// 如果为只读状态
		if (option.readonly == true) {
			bc.customForm.open(option);
			return;
		}

		option.url = bc.root + "/bc/customForm/render";
		var saveBtn = {
			click : "bc.customForm.save",
			text : "保存"
		};
		if (!option.buttons) {
			option.buttons = [];
			option.buttons.push(saveBtn);
		} else {
			var isHasSaveBtn = false;
			for(var i=0; i<option.buttons.length;i++) {
				var btnText = option.buttons[i].text;
				if(btnText == "保存") {
					isHasSaveBtn = true;
					break;
				} 
			}
			if(isHasSaveBtn == false) {
				option.buttons.push(saveBtn);
			}
		}
		

		var afterOpen;
		if (option.afterOpen) {
			afterOpen = option.afterOpen;
		}

		// 对模板表单中form-info加入模板编码、类别、标题 、pid、code
		option.afterOpen = function() {
			var $page = $(this);
			var $form = $("form", $page);
			
			//设置表单是否为新建状态
			if(option.isNew != undefined) {
				$form.data("form-info").isNew = option.isNew;
				$form.attr("data-form-info",JSON.stringify($form.data("form-info")))
			}
			
			//监测表单内容是否有修改
			bc.customForm.monitorFormChange($form);
			
			if (afterOpen) {
				afterOpen.call($page);
			}
		}

		bc.page.newWin(option);

	},

	/** 打开表单 */
	open : function(option) {
		option.url = bc.root + "/bc/customForm/open";

		var afterOpen;
		if (option.afterOpen) {
			afterOpen = option.afterOpen;
		}
		option.afterOpen = function() {
			var $page = $(this);
			var $form = $("form", $page);
			// 只读表单的处理
			$form.find(":input:visible:not('.custom')").each(function() {
				logger.debug("disabled:" + this.name);
				var $in = $(this);
				if ($in.is("select,:checkbox,:radio"))
					this.disabled = true;
				else
					this.readOnly = true;
			});
			$form.find("ul.inputIcons,span.selectButton").each(function() {
				$(this).hide();
			});
			$form.find(".bc-date").each(function() {
				$(this).unbind();
			});

			if (afterOpen) {
				afterOpen.call($page);
			}
		}
		bc.page.newWin(option);
	},
	
	/** 保存表单 */
	save : function(option) {
		option = option || {};
		
		var $page = $(this);
		var $form = $("form", $page);
		// 表单验证
		if (!bc.validator.validate($form))
			return;
		// 判断是否正在保存，若是就返回
		if ($page.data("saving"))
			return;

		var formData = bc.customForm.getFormData($form);
		if (formData.length == 0) {
			bc.msg.alert("没有可保存的表单数据！");
			return;
		}
		// 将表单的状态设为正常
		var formInfo = bc.customForm.setFormInfo($form, {
			status : 0
		});	
		
		//表单是否为新建
		var isNew = jQuery.parseJSON($form.attr("data-form-info")).isNew;
		
		// 设置是否正在保存的标识为true[正在保存]
		$page.data("saving", true);
		bc.ajax({
			url : bc.root + "/bc/customForm/save",
			data : {
				formInfo : formInfo,
				formData : $.toJSON(formData)
			},
			dataType : "json",
			success : function(json) {
				if (logger.debugEnabled)
					logger.debug("save success.json=" + jQuery.param(json));
				if (json.success === false) {
					bc.msg.info(json.msg);
				} else {
					// 记录已保存状态
					$page.attr("data-status", "saved").data("data-status",
							"saved");
					// 调用回调函数
					var showMsg = true;
					if (typeof option.callback == "function") {
						// 返回false将禁止保存提示信息的显示
						if (option.callback.call($page[0], json) === false)
							showMsg = false;
					}
					if (showMsg) {
						bc.msg.slide(json.msg);
					}
					if(isNew) {//如果表单为新建，保存后，isNew变为false
						bc.customForm.setFormInfo($form, {
								isNew : false
						});	
					}
					//保存后表单控件"changed"标识变为false,防止相同的数据二次提交
					$form.find(":input,select,texterea").each(function() {
					     $(this).removeData();
					});
					//重新监测表单内容是否有修改
					bc.customForm.monitorFormChange($form);
				}

				// 将正在保存标识设为false[已保存]
				$page.data("saving", false);
			}
		});
	},

	/**
	 * 自定义表单删除方法
	 * 
	 * @param {Object}
	 *            option [可选]配置参数
	 * @option {String} tpl 模板编码
	 * @option {String} subject 标题
	 * @option {String} type 类别
	 * @option {Integer} pid pid 新建为时0
	 * @option {String} code 编码 新建为时空字符窜
	 * @option {boolean} readonly [可选]是否只读-- true为只读 false为可编辑,默认为true
	 * @option {String} afterDelete [可选]删除后的回调函数。function(event, ui)
	 */
	delete_ : function(option) {
		var $page = option.page || $(this);
		option = option || {};
		option.page = $page;
		if (option.tpl && option.subject && option.type && option.pid
				&& option.code) {
			bc.customForm.deleteByTpc(option);
		} else {
			bc.customForm.deleteById(option);
		}
	},
	
	// 通过id删除
	deleteById : function(option) {
		var url = bc.root + "/bc/customForm/delete";
		var $page = option.page;
		var data = null;
		var $tds = $page
				.find(".bc-grid>.data>.left tr.ui-state-highlight>td.id");
		if ($tds.length == 1) {
			data = "id=" + $tds.attr("data-id");
		} else if ($tds.length > 1) {
			data = "ids=";
			$tds.each(function(i) {
				data += $(this).attr("data-id")
						+ (i == $tds.length - 1 ? "" : ",");
			});
		}
		if (logger.infoEnabled)
			logger.info("bc.page.delete_: data=" + data);
		if (data == null) {
			bc.msg.slide("请先选择要删除的条目！");
			return;
		}
		bc.msg.confirm("确定要删除选定的 <b>" + $tds.length + "</b> 项吗？", function() {
			bc.ajax({
				url : url,
				data : data,
				dataType : "json",
				success : function(json) {
					if (logger.debugEnabled)
						logger.debug("delete success.json=" + $.toJSON(json));
					if (json.success === false) {
						bc.msg.alert(json.msg);// 仅显示失败信息
					} else {
						// 调用回调函数
						var showMsg = true;
						if (typeof option.callback == "function") {
							// 返回false将禁止保存提示信息的显示
							if (option.callback.call($page[0], json) === false)
								showMsg = false;
						}
						if (showMsg)
							bc.msg.slide(json.msg);
						// 重新加载列表
						bc.grid.reloadData($page);
					}
				}
			});
		});
	},
	
	// 通过传入的(type,pid,code)参数进行删除
	deleteByTpc : function(option) {
		var $page = option.page;
		var url = bc.root + "/bc/customForm/delete";

		if (!option) {
			option.data = {};
		}

		var formInfo = {
			tpl : option.tpl,
			subject : option.subject,
			type : option.type,
			pid : option.pid,
			code : option.code
		}

		var data = $.extend(option.data, formInfo);

		if (logger.infoEnabled)
			logger.info("bc.page.delete_: data=" + data);
		if (data == null) {
			bc.msg.slide("请先选择要删除的条目！");
			return;
		}
		bc.msg.confirm("确定要删除选定的 <b>" + 1 + "</b> 项吗？", function() {
			bc.ajax({
				url : url,
				data : data,
				dataType : "json",
				success : function(json) {
					if (logger.debugEnabled)
						logger.debug("delete success.json=" + $.toJSON(json));
					if (json.success === false) {
						bc.msg.alert(json.msg);// 仅显示失败信息
					} else {
						// 调用回调函数
						var showMsg = true;
						if (typeof option.callback == "function") {
							// 返回false将禁止保存提示信息的显示
							if (option.callback.call($page[0], json) === false)
								showMsg = false;
						}
						if (showMsg)
							bc.msg.slide(json.msg);

						// 重新加载列表
						bc.grid.reloadData($page);
					}
				}
			});
		});
	},

	/**
	 * 打印方法
	 */
	print : function(key) {
		var $page = $(this);
		var $form = $("form", $page);
		var form_info = $form.attr("data-form-info");
		form_info = eval("(" + form_info + ")");
		var tpl = form_info.tpl;
		var type = form_info.type;
		var code = form_info.code;
		var pid = form_info.pid;
		var url = bc.root + "/bc/customForm/open?tpl=" + tpl + "&type=" + type
				+ "&pid=" + pid + "&code=" + code;
		var title = form_info.subject;
		// 加载js、css文件
		var printCss = $page.attr("data-print-printCss");// 打印样式
		var pageCss = $page.attr("data-print-pageCss");// 页面样式
		var appTs = $page.attr("data-appTs");// 时间戳
		var win = window.open(url, "_blank");
		// 新建打印窗口
		bc.customForm.winPrint(win, printCss, pageCss, title, appTs);
	},
	
	/**
	 * 自定义setTimeout方法 功能：修改 window.setTimeout，使之可以传递参数和对象参数 使用方法：
	 * setTimeout(回调函数,时间,参数1,,参数n)
	 */
	customSetTimeout : function(callback, timeout, param) {
		var args = Array.prototype.slice.call(arguments, 2);
		var _cb = function() {
			callback.apply(null, args);
		}
		setTimeout(_cb, timeout);
	},
	
	// 加载css文件,并调用窗口打印方法
	winPrint : function(win, printCss, pageCss, title, appTs) {
		// 先判断返回window中获取需要操作的Form是否为null
		if (win.document.getElementsByTagName("form")[0]) {
			// 对返回的window对象进行操作
			if (printCss && printCss.length > 0) {
				// 逗号分隔多个文件
				printCss = printCss.split(",");
				for ( var i = 0; i < printCss.length; i++) {
					var cssUrl = bc.root + printCss[i] + "?ts=" + appTs;
					var link = document.createElement("link");
					link.rel = "stylesheet";
					link.type = "text/css";
					link.media = "print";
					link.href = cssUrl;
					win.document.getElementsByTagName("head")[0]
							.appendChild(link);
				}
			} else {
				var cssUrl = bc.root + "/bc/form/print/page.css?ts=" + appTs;
				var link = document.createElement("link");
				link.rel = "stylesheet";
				link.type = "text/css";
				link.media = "print";
				link.href = cssUrl;
				win.document.getElementsByTagName("head")[0].appendChild(link);
			}
			if (pageCss && pageCss.length > 0) {
				// 逗号分隔多个文件
				pageCss = pageCss.split(",");
				for ( var i = 0; i < pageCss.length; i++) {
					var cssUrl = bc.root + pageCss[i] + "?ts=" + appTs;
					var link = document.createElement("link");
					link.rel = "stylesheet";
					link.type = "text/css";
					link.href = cssUrl;
					win.document.getElementsByTagName("head")[0]
							.appendChild(link);
				}
			}

			win.document.title = title;
			win.print();
		} else {
			// 设置延迟加载500毫秒
			bc.customForm.customSetTimeout(bc.customForm.winPrint, 500, win,
					printCss, pageCss, title, appTs);
		}
	},

	/** 获取表单数据 */
	getFormData : function($form) {
		// 默认的表单数据获取方法
		var datas = [];
		//表单是否为新建
		var isNew = jQuery.parseJSON($form.attr("data-form-info")).isNew;

		// input类型为text
		var $texts = $form.find(":text:not(.ignore)");
		if ($texts.size() != 0) {
			$texts.each(function() {
				var $text = $(this);
				if (isNew && $text.val() != "") { // 表单为新建状态时
					var data = {};
					data.name = this.name;
					data.value = $text.val();
					data.type = $text.attr("data-type") || "string";
					data.scope = $text.attr("data-scope") || "field";
					var label = $text.attr("data-label");
					if (label)
						data.label = label;
					datas.push(data);
				} else if (typeof($text.data("newData")) != "undefined" && $text.data("oldData") != $text.data("newData")) { //表单为编辑状态时
					var data = {};
					data.name = this.name;
					data.value = $text.val();
					data.type = $text.attr("data-type") || "string";
                    data.scope = $text.attr("data-scope") || "field";
					var label = $text.attr("data-label");
					if (label)
						data.label = label;
					datas.push(data); 
				} 				
			});
		}

		// input类型为radio
		var $radios = $form.find(":radio:not(.ignore):checked");
		if ($radios.size() != 0) {
			$radios.each(function() {
				var $radio = $(this);
				if(isNew || (typeof($radio.data("newData")) != "undefined" && $radio.data("oldData") != $radio.data("newData"))) { //表单为编辑状态时
					var data = {};
					data.name = this.name;
					data.value = $radio.val();
					data.type = $radio.attr("data-type") || "string";
					var label = $radio.attr("data-label");
					if (label)
						data.label = label;
					datas.push(data);
				}
				
			});
		}

		// input类型为checkbox
		if(isNew) {  //表单为新建状态时
			var $checkboxes = $form.find(":checkbox:not(.ignore):checked");
			if ($checkboxes.size() != 0) {
				
				for ( var i = 0; i < $checkboxes.size();) {
					var $checkbox = $checkboxes.eq(i);
					var data = {};
					data.name = $checkbox.attr("name");
					data.value = [];
					data.type = $checkbox.attr("data-type") || "string[]";
					
					var $brocheckboxes = $checkbox.parent().find(":checkbox:not(.ignore):checked");//找出同一组的被选中的checkbox
					$brocheckboxes.each(function() {
						var $brocheckbox = $(this);
						if (data.type == "int" || data.type == "int[]"
							|| data.type == "long" || data.type == "long[]") {
							data.value.push(parseInt($brocheckbox.val()));
						} else if (data.type == "float" || data.type == "float[]"
							|| data.type == "double" || data.type == "double[]") {
							data.value.push(parseFloat($brocheckbox.val()));
						} else {
							data.value.push($brocheckbox.val());
						}
					});
					datas.push(data);
					i = i + $brocheckboxes.size();
				}	
			}
		} else {  //表单为编辑状态时
			var $checkboxes = $form.find(":checkbox:not(.ignore)");
			if ($checkboxes.size() != 0) {
				for ( var i = 0; i < $checkboxes.size(); i++) {
					var $checkbox = $checkboxes.eq(i);
					
					if(typeof($checkbox.data("newData")) != "undefined" && $checkbox.data("oldData") != $checkbox.data("newData")) {
						var data = {};
						data.name = $checkbox.attr("name");
						data.value = [];
						data.type = $checkbox.attr("data-type") || "string[]";
						var $changeCheckboxes =  $checkbox.parent().find(":checkbox:not(.ignore):checked");
						
						if($changeCheckboxes.size() > 0 ) {   //如果被修改的checkbox组有选中项
							$changeCheckboxes.each(function(){
								var $changeCheckbox = $(this);
								if (data.type == "int" || data.type == "int[]"
									|| data.type == "long" || data.type == "long[]") {
									data.value.push(parseInt($changeCheckbox.val()));
								} else if (data.type == "float" || data.type == "float[]"
									|| data.type == "double" || data.type == "double[]") {
									data.value.push(parseFloat($changeCheckbox.val()));
								} else {
									data.value.push($changeCheckbox.val());
								}
							});
							datas.push(data);
						} else {  //如果被修改的checkbox组没有选中项
							if (data.type == "int" || data.type == "int[]"
								|| data.type == "long" || data.type == "long[]") {
								data.value.push(0);
							} else if (data.type == "float" || data.type == "float[]"
								|| data.type == "double" || data.type == "double[]") {
								data.value.push(0.0);
							} else {
								data.value.push("");
							}
							datas.push(data);
							var $changeCheckboxes =  $checkbox.parent().find(":checkbox:not(.ignore)");
						}
						i = i + $changeCheckboxes.size() - 1;
					}					
				}				
			}
		}
		
		// input类型为hidden
		var $hiddens = $form.find("input:hidden:not(.ignore)");
		if ($hiddens.size() != 0) {
			$hiddens.each(function() {
				var $hidden = $(this);
				if (isNew && $hidden.val() != "") { // 表单为新建状态时
					var data = {};
					data.name = this.name;
					data.value = $hidden.val();
					data.type = $hidden.attr("data-type") || "string";
					var label = $hidden.attr("data-label");
					if (label)
						data.label = label;
					datas.push(data);
				} else if ($hidden.data("oldData") != $hidden.val()) { //表单为编辑状态时
					var data = {};
					data.name = this.name;
					data.value = $hidden.val();
					data.type = $hidden.attr("data-type") || "string";
					var label = $hidden.attr("data-label");
					if (label)
						data.label = label;
					datas.push(data); 
				} 				
			});
		}
		
		// select
		var $selects = $form.find("select:not(.ignore)");
		if ($selects.size() != 0) {
			$selects.each(function() {
				var $select = $(this);
				if(isNew || (typeof($select.data("newData")) != "undefined" && $select.data("oldData") != $select.data("newData"))) {
					
					if (!$select.attr("multiple")) { // select为 单选
						var $option = $select
								.find("option:selected:not(.ignore)");
						var data = {};
						data.name = $option.attr("name");
						data.value = $option.val();
						data.type = $option.attr("data-type") || "string";
						var label = $option.attr("data-label");
						if (label)
							data.label = label;
						datas.push(data);
					} else { // select为 多选
						var $options = $select.find("option:selected:not(.ignore)");
						
						if ($options.size() != 0) {
							var data ={};
							data.name = $options.eq(0).attr("name");
							data.value = [];
							data.type = $options.eq(0).attr("data-type") || "string[]";
							var label = $options.eq(0).attr("data-label");
							if (label)
								data.label = label;
							$options.each(function() {
								var $option = $(this);
								if (data.type == "int" || data.type == "int[]"
									|| data.type == "long" || data.type == "long[]") {
								data.value.push(parseInt($option.val()));
								} else if (data.type == "float"
										|| data.type == "float[]"
										|| data.type == "double"
										|| data.type == "double[]") {
									data.value.push(parseFloat($option.val()));
								} else {
									data.value.push($option.val());
								}
								
							});
							datas.push(data);
						}
					}
				}				
			});
		}

		// textarea
		var $textareas = $form.find("textarea:not(.ignore)");
		if ($textareas.size() != 0) {
			$textareas.each(function() {
				var $textarea = $(this);
				if(isNew || (typeof($textarea.data("newData")) != "undefined" && $textarea.data("oldData") != $textarea.data("newData"))) {
					var data = {};
					data.name = $textarea.attr("name");

					if ($textarea.val() == null || $textarea.val() == "") {
						data.value = "";
					} else {
						data.value = $textarea.val();
					}
					data.type = $textarea.attr("data-type") || "string";
					var label = $textarea.attr("data-label");
					if (label)
						data.label = label;

					datas.push(data);
				}	
			});
		}
		
		return datas;
	},

	/** 设置data-form-info 信息 * */
	setFormInfo : function($form, extData) {
		var form_info = $form.attr("data-form-info");
		if (form_info && /^\{/.test($.trim(form_info))) {
			// 对json格式进行解释
			form_info = eval("(" + form_info + ")");
		} else {
			form_info = {};
		}
		form_info = $.extend(form_info, extData);
		var to_form_info = $.toJSON(form_info);
		$form.attr("data-form-info", to_form_info);
		return to_form_info;
	},
	
	/**
	 * 根据formData信息id信息 加载到对应input标签的字段中
	 */
	loadFormData : function() {
		var $page = $(this);
		var $form = $("form", $page);
		var form_info = $form.attr("data-form-info");
		if (form_info && /^\{/.test($.trim(form_info))) {
			// 对json格式进行解释
			form_info = eval("(" + form_info + ")");
		} else {
			form_info = {};
		}
		var $formData = form_info.formData;
		$.each($formData, function(index, value) {
			var $input = $form.find(':input[name=' + value.name + ']');
			$input.attr("data-id", value.id);
		});
	},
	
	//监测表单内容变化
	monitorFormChange : function($form) {
		//input类型为text
		$form.find("input:text:not(.ignore)").each(function() {
			$(this).data("oldData",$(this).val());
		});
		$form.find("input:text:not(.ignore)").change(function() {
			$(this).data("newData",$(this).val());
		});
		
		//input类型为radio
		$form.find("input:radio:not(.ignore)").each(function() {
			$(this).data("oldData",$(this)[0].checked);
		});
		$form.find("input:radio:not(.ignore)").change(function() {
			$(this).data("newData",$(this)[0].checked);
		});
		
		//input类型为checkbox
		$form.find("input:checkbox:not(.ignore)").each(function() {
			$(this).data("oldData",$(this)[0].checked);
		});
		$form.find("input:checkbox:not(.ignore)").change(function() {			
			$(this).data("newData",$(this)[0].checked);
		});
		
		//input类型为hidden
		$form.find("input:hidden:not(.ignore)").each(function() {
			$(this).data("oldData",$(this).val());			
		});
		//hidden不能触发change事件,另作处理
		
		//select
		$form.find("select:not(.ignore)").each(function() {
			$(this).data("oldData",String($(this).val()));
		});
		$form.find("select:not(.ignore)").change(function() {
			$(this).data("newData",String($(this).val()));
		});
		
		//textarea
		$form.find("textarea:not(.ignore)").each(function() {
			$(this).data("oldData",$(this).val());
		});
		$form.find("textarea:not(.ignore)").change(function() {
			$(this).data("newData",$(this).val());
		});	
	}	
}