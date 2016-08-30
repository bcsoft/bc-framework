bc.namespace("bc.spider");
bc.spider = {
	searching : false,
	init : function() {
		var $page = $(this);
		var $status=$page.find("#status");
		var $result=$page.find(".result");
		console.log("spider");
		
		// 折叠展开处理
		$page.on("click",".folder",function(){
			var $this = $(this);
			var up = $this.is(".ui-icon-carat-1-n");
			console.log("up="+up);
			$this.toggleClass("ui-icon-carat-1-n",!up)
				.toggleClass("ui-icon-carat-1-s",up)
				.attr("title",up ? "点击展开内容" : "点击折叠内容");
			if(up){
				$this.closest(".title").next().hide();
			}else{
				$this.closest(".title").next().show();
			}
		});

		// 初始化请求参数的显示
		var code = $page.find(".hiddenFields>input[name=code]").val();
		var $params = $page.find(".params");
		var config = $page.find(".hiddenFields>input[name=config]").val();
		config = eval("(" + config + ")");
		var params = $page.find(".hiddenFields>input[name=params]").val();
		if(params && params.length>0){
			params = eval("(" + params + ")");
		}else{
			params = {};
		}
		var html=[];
		var f,v;
		var formData = config.formData;
		var captchaField;
		if(formData){
			for ( var i = 0; i < formData.length; i++) {
				f = formData[i];
				v = f.validate||{};
				console.log(f);
				if(v=="required")
					v={required:true,type:"string"};
				html.push('<div class="item">');
				html.push('  <div class="field"'+(f.title ? ' title="' + f.title + '"' : '')+'>');
				html.push('    <div class="label">' + (v.required?'*':'') + (f.label||f.name) + ':</div>');
				html.push('    <div class="value">');
				html.push(bc.spider.createField.call(this,f,params,v)||'');// 构建输入框
				html.push('    </div>');
				if(f.captcha){
					captchaField = f;
				}
				html.push('  </div>');
				html.push('</div>');
				
			}
			$params.html(html.join(""));
			
			//绑定日期选择
			bc.form.initCalendarSelect($page);
		}
		
		// 描述信息
		if(config.description){
			$params.parent().append('<div class="ui-widget-content desc">'+config.description+'</div>');
			$page.find(".desc a").click(function(){
				window.open(this.href, "_blank");
				return false;
			});
		}
		
		// 通过ajax获取验证码图片
		console.log("captchaField=" + captchaField);
		if(captchaField){
			//$params.append('<div class="captcha"><img class="captcha" title="点击换一张" src="'+bc.root+'/bc/spider/captcha.jpg"></div>');
			var $captchaImg = $page.find("img.captcha").click(function(){
				bc.spider.refreshCaptcha.call($page,config.group,captchaField);
			});
			
			// 获取一次验证码图片
			if(captchaField.captcha.auto)
				$captchaImg.click();
		}
		
		// 查询按钮
		var $go = $page.find("#go").click(function(){
			// 必填验证
			if(!bc.validator.validate($params)){
				return;
			}
			
			var _this = this;
			_this.disabled = true;
			var from = new Date();
			$status.removeClass("error").text("正在查询,请耐心等候！(" + from.toLocaleString()+")");
			$result.html("").removeClass("error");

			// 构建查询参数
			var params = {};
			var v;
			$params.find(":input:not(:button,.ignore)").each(function(){
				v = $(this).val();
				if(v && v.length > 0){
					params[this.name] = v;
				}
			});
			
			$.ajax({
				type: "POST",
				url: bc.root + "/bc/spider/run",
				data: {code: code, params: $.toJSON(params)},
				dataType: "json",
				success: function(json){
					if(json.success){
						$status.text("查询完成！(" + from.toLocaleString() + " ～ " + new Date().toLocaleString()+")");
						if(typeof(json.html) == "string"){
							$result.html(json.html);

                            // 重置验证码
                            if(captchaField){
                                $params.find("input[name='"+captchaField.name+"']").val("");
                                $params.find("img.captcha").attr("src",bc.root+"/bc/spider/captcha.jpg");
                            }
						}else{
							
						}
					}else{
						$status.addClass("error").text("查询出错了！(" + from.toLocaleString() + " ～ " + new Date().toLocaleString()+")");
						$result.addClass("error").html("==出错了==<br>" + json.error);
					}
				},
				error: function(jqXHR, textStatus, errorThrown){
					//http://www.myexception.cn/ajax/774915.html
					$status.addClass("error").text("查询出错了！(" + from.toLocaleString() + " ～ " + new Date().toLocaleString()+")");
					$result.addClass("error").html("status: " + jqXHR.status + "<br/>" + (textStatus || errorThrown));
				},
				complete: function(){
					_this.disabled = false;
				}
			})
		});
		
		// 自动请求
		var auto = $page.find(".hiddenFields>input[name=auto]").val();
		if(auto && auto.length > 0){// 有强制配置
			auto = (auto === "true");
		}else{// 无强制配置就使用系统配置
			auto = config.auto;
		}
		if(auto){
			$go.click();
		}
	},
	/* 根据配置构建输入框 */
	createField: function(f,params,v){
		var html=[];
		if(!f.type) f.type="text";
		
		html.push("<");
		switch (f.type){
		case "password":
		case "text":
			html.push('input name="'+f.name+'" type="'+f.type+'"');
			break;
		case "select":
			html.push('select name="'+f.name+'"');
			break;
		case "textarea":
			html.push('textarea name="'+f.name+'"');
			break;
		}
		
		// 类样式设置
		html.push(' class="ui-widget-content');
		if(f.clazz)
			html.push(' '+f.clazz);
		if(f.captcha)
			html.push(' captcha');
		html.push('"');
		
		// 值
		var val = params[f.name]||f.value;
		if(val)
			html.push(' value="' + val + '"');
		
		// 验证配置
		if(!$.isEmptyObject(v))
			html.push(" data-validate='"+$.toJSON(v) + "'");
		
		// placeholder
		if(f.placeholder)
			html.push(" placeholder='"+f.placeholder+"'");
		html.push(">");
		
		// 特殊类型
		if(f.type == "select"){// select
			if(f.options){
				var o;
				for(var i=0;i<f.options.length;i++){
					o = f.options[i];
					if(typeof(o) == "string"){
						html.push('<option>'+o+'</option>');
					}else{
						html.push('<option value="'+(o.value||'')+'"');
						if(val){
							if(val == o.value)
								html.push(' selected="selected"');
						}else{
							if(o.selected)
								html.push(' selected="selected"');
						}
						html.push('">'+(o.text||o.label||'')+'</option>');
					}
				}
			}
			html.push("</select>");
		}else if(f.type == "textarea"){// textarea
			html.push("</textarea>");
		}
		
		// 日期字段
		if(v.type=="date" || v.type=="datetime"){
			html.push('<ul class="inputIcons">');
			html.push('	<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"></li>');
			html.push('</ul>');
		}
		
		// 验证码
		if(f.captcha){
			html.push('<img class="captcha" title="点击换一张" src="'+bc.root+'/bc/spider/captcha.jpg">');
		}

		return html.join("");
	},
	refreshCaptcha: function(group,field){
		var $page = $(this);
		console.log("refreshCaptcha...");
		var $status=$page.find("#status").html("正在获取验证码图片！");
		var $captcha=$page.find("img.captcha");
		var url;
		if(typeof(field.captcha) == "string")
			url = field.captcha;
		else
			url = field.captcha.url;
		$.ajax({
			dataType: "json",
			url: bc.root+"/bc/spider/captcha",
			data: {group: group, url: url},
			success: function(json){
				if(json.success) {
					console.log(json);
					$status.html("获取验证码成功！");
					$captcha.attr("src", bc.root + "/bc/file/inline?f=" + json.path);
					// 显示破解出来的验证码
					if (json.captcha) {
						$captcha.siblings().val(json.captcha);
					}
				}else{
					$status.html("获取验证码失败，请重新重试！");
				}
			},
			error: function(jHRX,status,e){
				console.log("error:refreshCaptcha:" + status||e);
				$status.html("获取验证码失败，请重新重试！");
			}
		});
	}
};