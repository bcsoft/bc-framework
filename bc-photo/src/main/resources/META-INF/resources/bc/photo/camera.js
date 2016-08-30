jQuery(function($) {
	// 浏览器兼容性处理
	window.URL = window.URL || window.webkitURL || window.mozURL
		|| window.msURL;
	navigator.getUserMedia = navigator.getUserMedia
		|| navigator.webkitGetUserMedia || navigator.mozGetUserMedia
		|| navigator.msGetUserMedia;
	console.log(navigator.getUserMedia);

	// 控件变量
	var scale = document.querySelector('#scale');
	var scaleValue = document.querySelector('#scaleValue');
	var msg = document.querySelector('#msg');
	var startButton = document.querySelector('#startButton');
	var stopButton = document.querySelector('#stopButton');
	var video = document.querySelector('video');
	var canvas = document.querySelector('canvas');
	var img = document.querySelector('img');
	var ctx = canvas.getContext('2d');
	ctx.globalAlpha=0;//globalAlpha属性的取值范围是[0, 1]，0表示完全透明，1表示完全不透明。
	var localMediaStream = null;
	var width, height;

	if (!navigator.getUserMedia) {
		msg.innerText = "你的浏览器不支持WebRTC！";
		startButton.disabled = true;
		stopButton.disabled = true;
		scale.disabled = true;
		return;
	}

	// 视频配置
	var constraints = {
		audio : false,
		video : {
			mandatory : {
				minWidth : "800",
				minHeight : "600"
			}
		}
	};

	// 成功的回调函数:
	function successCallback(stream) {
		console.log('successCallback：args=' + arguments.length);
		console.log(stream);

		// Firefox 使用mozSrcObject的属性，而Opera和Chrome使用src属性。
		// Chrome 使用createObjectURL的方法，而Firefox和Opera直接发送视频流。
		if (video.mozSrcObject !== undefined) {
			video.mozSrcObject = stream;
		} else {
			video.src = (window.URL && window.URL.createObjectURL(stream))
				|| stream;
		}
		localMediaStream = stream;

		// sizeCanvas();
		startButton.textContent = '截图';

		console.log('successCallback:Video dimensions: vw' + video.videoWidth
			+ ' x vh' + video.videoHeight + ', w' + $(video).width() + ' x h'
			+ $(video).height());
	}

	// 失败的回调函数
	function errorCallback(e) {
		msg.innerText = "无法连接摄像头：" + e;
		console.log('errorCallback：args=' + arguments.length);
		console.log('errorCallback：e.code=' + e.code);
		console.log(e);
		if (e.name = "PERMISSION_DENIED") {
			alert('User denied access to their camera');
		} else {
			alert('getUserMedia() not supported in your browser.');
		}
	}

	// 截图函数
	function snapshot() {
		var type = document.querySelector('input[name="type"]:checked');
		console.log('type=' + type.value + ',w=' + width + ',h=' + height);


		// 注：不是设置style.width|height属性，而是设置setAttribute('width|height')
		// 如果不设置会导致裁剪
		canvas.width = width * scale.value;
		canvas.height = height * scale.value;

		//drawImage函数有三种函数原型：
		//drawImage(image, dx, dy) 
		//drawImage(image, dx, dy, dw, dh) 
		//drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh)
		//第一个参数image可以用HTMLImageElement，HTMLCanvasElement或者HTMLVideoElement作为参数。
		//dx和dy是image在canvas中定位的坐标值；dw和dh是image在canvas中即将绘制区域（相对dx和dy坐标的偏移量）的宽度和高度值；
		//sx和sy是image所要绘制的起始位置，sw和sh是image所要绘制区域（相对image的sx和sy坐标的偏移量）的宽度和高度值。
		//ctx.drawImage(video, 0, 0);// 1：1全图显示
		ctx.drawImage(video, 0, 0, width * scale.value, height * scale.value);// 全图按比例缩放到指定区域显示
		//ctx.drawImage(video, 0, 0,400,300,0,0, width * scale.value, height * scale.value);// 显示选中的区域全图显示
		//ctx.drawImage(video, 400, 300,400,300,100,100, 400,300);// 显示选中的区域1:1显示
		
		// 显示截图
		img.src = canvas.toDataURL(type.value);
	}

	// 点击 startButton 按钮打开摄像头或截图
	startButton.addEventListener('click',
		function(e) {
			if (localMediaStream) {
				snapshot();
				return;
			} else {
				msg.innerText = "正在连接摄像头，请稍后...";
				// 打开摄像头
				navigator.getUserMedia(constraints, successCallback,
					errorCallback);
			}
		}, false);

	// 点击 stopButton 停止视频捕捉
	stopButton.addEventListener('click', function(e) {
		console.log('value=' + this.value);
		if (this.value == '0') {
			this.value = '1';
			this.innerText = '播放';
			video.pause();
			// if (localMediaStream) localMediaStream.stop();
		} else {
			this.value = '0';
			this.innerText = '停止';
			video.play();
		}
	}, false);

	scale.addEventListener('change', function() {
		scaleValue.innerText = this.value;
	}, false);

	// video事件：play、loadedmetadata、loadeddata、playing
	video.addEventListener('click', snapshot, false);
	video.addEventListener('loadeddata', function(e) {
		console.log('loadeddata：args=' + arguments.length);
		console.log(e);
		width = video.videoWidth;
		height = video.videoHeight;

		// 调整视频控件的大小为摄像头的实际分辨率大小
		//video.style.width = width + "px";
		//video.style.height = height + "px";

		// msg
		msg.innerText = "摄像头分辨率:" + width + " x " + height;

		// 可以使用流的尺寸做进一步的错误检查，例如检查是否宽度和高度都大于0。
		// 这将避免出现某些问题，如用户的网络摄像头被破坏或根本就没有插上。
		console.log('loadeddata:Video dimensions: vw' + video.videoWidth
			+ ' x vh' + video.videoHeight + ', w' + $(video).width() + ' x h'
			+ $(video).height());
	}, false);

	function draw(v, c, w, h) {
		if (v.paused || v.ended)
			return false;
		c.drawImage(v,0,0, w, h);
		setTimeout(draw, 20, v, c, w, h);
	}
	video.addEventListener('play', function() {
		console.log('play');
		//draw(video, ctx, width, height);
	}, false);

	var i = 0;
	var $sizer = $("div.sizer")
	var $info = $sizer.find(">.info");
	var $canvas = $(canvas);
	$sizer.draggable({
		containment : $canvas,
		scroll : false,
		cancel : ".sizer>.dice",
		delay : 100,
		start : function(e, ui) {
			console.log('start:' + i++);
		},
		drag : function(e, ui) {
			console.log('drag:' + i++);
			//console.log(ui.position);
			$info.html(ui.position.left + 'x' + ui.position.top + ',' + $canvas.position().left + 'x' + $canvas.position().top);
		},
		stop : function(e, ui) {
			console.log('stop:' + i++);
		}
	});
});