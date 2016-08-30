bc.showCrop = {
	/** 初始化 */
	init : function(option) {
		$page = $(this);
		//初始化上传图片文件控件的选择事件
		$page.find(".bc-image :file.uploadImage").bind("change",function(e){
			logger.info("uploadImage");
			var $bcImage = $(this).closest(".bc-image");
			
			var ptype = $bcImage.attr("data-ptype");
			var puid = $bcImage.attr("data-puid");
			var extensions = $bcImage.attr("data-extensions");
			var callback = $bcImage.attr("data-callback");
			var _callback = bc.getNested(callback);
			if(typeof _callback != "function"){
				alert("data-callback值“" + callback + "”对应的函数没有定义");
				return false;
			}
			
			if(bc.showCrop.isHtml5Upload()){
				//alert("isHtml5Upload:" + this.value);
				logger.info("uploadFile with html5");
				bc.showCrop.upload4html5.call($bcImage.closest(".bc-page")[0],{
					file: e.target.files[0],
					ptype: ptype,
					puid: puid,
					extensions: extensions,
					callback: _callback
				});
			}else{
				alert("todo:" + this.value);
			}
		});

		//上传按钮的鼠标样式控制
		$page.find(".bc-image").bind("mouseover", function() {
			$(this).addClass("ui-state-hover");
		}).bind("mouseout", function() {
			$(this).removeClass("ui-state-hover");
		})
		
		var preWidth = option.preWidth;// 预览区的宽度
		var preHeight = option.preHeight;// 预览区的高度
		var ratio = false;
		var jcrop_api;

		// Jcrop初始化
		var jcropOption = {
			onChange : doUpdate,
			onSelect : doUpdate,
			boxWidth : 400,
			boxHeight : 350,
			minSize: [16,16]
		};
		if (option.preWidth && option.preHeight) {
			// 设置裁剪区的长宽比例
			ratio = option.preWidth / option.preHeight;
			jcropOption.aspectRatio = ratio;
		}
		$page.find('#source').Jcrop(jcropOption, function() {
			jcrop_api = this;
			$page.data("jcrop_api", this);// 保存为页面数据
			// 获取img图片的css尺寸并记录
			var bounds = this.getBounds();
			//var widgetSize = jcrop_api.getWidgetSize();
			$page.find("#zoomInfo").html(Math.round(bounds[0]) + "x" + Math.round(bounds[1]));// + " --> " + widgetSize[0] + "x" + widgetSize[1] + ")");

			//setSelect | animateTo
			jcrop_api.animateTo(bc.showCrop.getRandomArea.call($page[0]),function(){
				$page.find("#ignore").val("true");
			});
		});

		// 根据裁剪区的参数更新预览区的图片
		function doUpdate(crop) {
			$page.find("#ignore").val("false");
			var bounds = jcrop_api.getBounds();
			updatePreview(crop, bounds[0], bounds[1], preWidth, preHeight);
		}

		/**
		 * 更新预览区的图片
		 * 
		 * @param crop
		 *            裁剪区的尺寸和定位
		 * @param boundWidth
		 *            原图所在img的css宽度
		 * @param boundHeight
		 *            原图所在img的css高度
		 * @param preWidth
		 *            裁剪预览区的宽度
		 * @param preHeight
		 *            裁剪预览区的高度
		 */
		function updatePreview(crop, boundWidth, boundHeight, preWidth, preHeight) {
			if (parseInt(crop.w) > 0) {
				logger.info("bound:w=" + boundWidth + ",h=" + boundHeight);
				logger.info("pre:w=" + preWidth + ",h=" + preHeight);
				logger.info("crop:w=" + crop.w + ",h=" + crop.h + ",x=" + crop.x + ",y=" + crop.y);
				var rx = preWidth / crop.w;
				var ry = preHeight / crop.h;
				$page.find('#preview').css({
					width : Math.round(rx * boundWidth) + 'px',
					height : Math.round(ry * boundHeight) + 'px',
					marginLeft : '-' + Math.round(rx * crop.x) + 'px',
					marginTop : '-' + Math.round(ry * crop.y) + 'px'
				});
			}
		}
	},

	/** 点击确认按钮的处理 */
	onOk : function() {
		$page = $(this);
		var ignore = $page.find("#ignore").val();
		if("true" == ignore){
			$page.dialog("close");
			return;
		}

		// 获取本页的jcrop对象
		var jcrop_api = $page.data("jcrop_api");
		var bounds = jcrop_api.getBounds();
		var crop = jcrop_api.tellSelect();
		var imgRealSize = bc.showCrop.getImageRealSize(document
				.getElementById("source"));
		logger.info("real:w=" + imgRealSize[0] + ",h=" + imgRealSize[1]);
		logger.info("bound:w=" + bounds[0] + ",h=" + bounds[1]);
		logger.info("crop:w=" + crop.w + ",h=" + crop.h + ",x=" + crop.x
				+ ",y=" + crop.y);
		
		var data = {
			cw:crop.w,
			ch:crop.h,
			cx:crop.x,
			cy:crop.y,
			preWidth:$page.find("input:hidden[name='preWidth']").val(),
			preHeight:$page.find("input:hidden[name='preHeight']").val(),
			puid:$page.find("input:hidden[name='puid']").val(),
			ptype:$page.find("input:hidden[name='ptype']").val()
		};
		var id = $page.find("input:hidden[name='id']").val();
		if(id && id.length > 0){// 编辑现有附件
			data.id = id;
		}else{//编辑空的模板文件
			var empty = $page.find("input:hidden[name='empty']").val();
			if(empty && empty.length > 0)
				data.empty = empty;
		}
		
		// 使用ajax上传处理参数
		bc.ajax({
			dataType:"json",
			data:data,
			url:bc.root + "/bc/image/doCrop",
			success : function(json) {
				logger.info("success:" + $.param(json));
				jcrop_api.destroy();
				$page.data("data-status",json);
				$page.removeData("jcrop_api");
				$page.dialog("close");
			}
		});
	},

	originImage : null,
	/** 获取图片原始尺寸的大小 */
	getImageRealSize : function(img) {
		if (bc.showCrop.originImage == null)
			bc.showCrop.originImage = new Image();

		var oImg = bc.showCrop.originImage;
		if (oImg.src != img.src) {
			oImg.src = img.src;
		}

		return [ oImg.width, oImg.height ];
	},
	
	/** 获取靠近中间的区域 */
	getRandomArea : function(jcrop_api) {
		//return [0,0,110,140];
		var $page = $(this);
		var jcrop_api = $page.data("jcrop_api");
		var preWidth = parseInt($page.find("input:hidden[name='preWidth']").val());
		var preHeight = parseInt($page.find("input:hidden[name='preHeight']").val());
		var ratio = preWidth / preHeight;// 裁剪区的长宽比例
		var bounds = jcrop_api.getBounds();//原图尺寸
		var widgetSize = jcrop_api.getWidgetSize();//crop区的尺寸
		logger.info("-bound:w=" + bounds[0] + ",h=" + bounds[1]);
		logger.info("-widget:w=" + widgetSize[0] + ",h=" + widgetSize[1]);
		logger.info("-pre:w=" + preWidth + ",h=" + preHeight);
		
		var w,h,x,y;
		if(bounds[0] > preWidth){
			if(bounds[1] > preHeight){
				w = preWidth;
				h = preHeight;
				x = Math.round((bounds[0] - w)/2);
				y = Math.round((bounds[1] - h)/2);
			}else{
				h = Math.round(bounds[1]);
				w = Math.round(h * ratio);
				x = Math.round((bounds[0] - w)/2);
				y = 0;
			}
		}else{
			if(bounds[1] > preHeight){
				w = Math.round(bounds[0]);
				h = Math.round(w / ratio);
				x = 0;
				y = Math.round((bounds[1] - h)/2);
			}else{
				w = Math.round(bounds[0]);
				h = Math.round(bounds[1]);
				x = 0;
				y = 0;
			}
		}
		logger.info("-random:w=" + w + ",h=" + h + ",x=" + x + ",y=" + y + ",x2=" + (x+w) + ",y2=" + (y+h));

		// [x,y,x2,y2]
		return [x,y,x+w,y+h];
	},
	
	/** 文件上传完毕后的回调函数 */
	finishUpload: function(json,text){
		//alert(text);
		var $page = $(this);
		
		// 更改图片
		var newImgUrl = bc.root + '/bc/image/download?id=' + json.msg.id;
		var srcImg = $page.find("#source,#preview").attr("src",newImgUrl);
		var jcrop_api = $page.data("jcrop_api");
		jcrop_api.setImage(newImgUrl,function(){
			// 图片加载完毕后的处理
			var b = this.getBounds();
			//logger.info("b:w=" + b[0] + ",h=" + b[1]);
			
			// 自动选中靠近中间的区域
			this.animateTo(bc.showCrop.getRandomArea.call($page[0]),function(){
				$page.find("#ignore").val("false");
			});
			
			//界面显示新图片的尺寸
			$page.find("#zoomInfo").html(Math.round(b[0]) + "x" + Math.round(b[1]));
			
			//记录新的附件id
			$page.find("input:hidden[name='id']").val(json.msg.id);
		});
	},
	
    /**判断浏览器是否可使用html5上传文件*/
	isHtml5Upload: function(){
		return $.browser.safari || $.browser.mozilla;//Chrome12、Safari5、Firefox4
	},
	
    /**上传图片的url*/
	uploadUrl: bc.root + "/upload/?a=1",
	
	/**
	 * 基于html5的文件上传处理
	 * <p>函数上下文为附件控件的容器dom</p>
	 * @param {Object} option 配置参数
	 * @option {Object} file 要上传的文件 
	 * @option {String} ptype 
	 * @option {String} puid 
	 * @option {String} extensions 扩展名限制，多个用逗号连接 
	 * @option {Function} callback 回调函数，第一个参数为服务器返回的json对象，上下文为页面对象
	 * @option {Element} progressbar 进度条对象
	 * @option {String} url 
	 */
	upload4html5:function(option){
		var _this = this;
	    //将参数附加到上传文件的url后
	    var url = option.url || bc.showCrop.uploadUrl;
	    if(option.ptype) url+="&ptype=" + option.ptype;
	    if(option.puid) url+="&puid=" + option.puid;
	    
	    //检测文件类型的限制
	    var fileName = option.file.name;
		logger.info("file.name=" + option.file.name + ",file.fileName=" + option.file.fileName);
	    if(option.extensions && option.extensions.length > 0){
	    	var extensions = option.extensions.toLowerCase();
    		if(extensions.indexOf(fileName.substr(fileName.lastIndexOf(".") + 1).toLowerCase()) == -1){
	    		alert("只能上传扩展名为\"" + extensions.replace(/,/g,"、") + "\"的文件！");
	    		
	    		//清空file控件:file.outerHTML=file.outerHTML; 
	    		
	    		return false;
    		}
	    }
		
    	var xhr = new XMLHttpRequest();
    	
    	//上传进度处理
    	if(option.progressbar){
    	    //初始化进度条
    	    var $progressbar = $(option.progressbar).show().progressbar();
			if($.browser.safari){//Chrome12、Safari5
				xhr.upload.onprogress=function(e){
					var progressbarValue = Math.round((e.loaded / e.total) * 100);
					logger.info(":upload.onprogress:" + progressbarValue + "%");
					$progressbar.progressbar("option","value",progressbarValue);
				};
			}else if($.browser.mozilla){//Firefox4
				xhr.onuploadprogress=function(e){
					var progressbarValue = Math.round((e.loaded / e.total) * 100);
					logger.info(i + ":upload.onprogress:" + progressbarValue + "%");
					$progressbar.progressbar("option","value",progressbarValue);
				};
			}
    	}
		
		//上传完毕的处理
		xhr.onreadystatechange=function(){
			if(xhr.readyState != 4){
				logger.error(":uploadError:readyState=" + xhr.readyState);
				return;
			}
			
			logger.info("responseText=" + xhr.responseText);
			var json = eval("(" + xhr.responseText + ")");
			
			//删除进度条钮（延时1秒后执行）
			if(option.progressbar){
				setTimeout(function(){
					$progressbar.hide();
				},1000);
			}
			
			//调用回调函数
			if(typeof option.callback == "function")
				option.callback.call(_this, json,xhr.responseText);
		};
		
		// 执行上传文件的操作
		xhr.open("POST", url);
		xhr.setRequestHeader('Content-Type', 'application/octet-stream');
		//对文件名进行URI编码避免后台中文乱码（后台需URI解码）
		xhr.setRequestHeader('Content-Disposition', 'attachment; name="filedata"; filename="'+encodeURIComponent(fileName)+'"');
		logger.info("jQuery.browser.version=" + jQuery.browser.version);
		if(xhr.sendAsBinary && $.browser.mozilla && $.browser.version <=9)//Firefox4
			xhr.sendAsBinary(option.file.getAsBinary());
		else //Chrome12+,firfox10+,opera,safari
			xhr.send(option.file);
	}
};