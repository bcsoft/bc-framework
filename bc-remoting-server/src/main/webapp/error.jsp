<%@ page contentType="text/html; charset=UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no">
	<title>BC异常</title>
	<link rel="stylesheet" type="text/css" href="index.css">
</head>
<body style="background-color: #2c2c2c; color: #000">
<div class="valign">
	<img src="logo.png"/>
	<h1>出错了！</h1>
	<span><%= response.getStatus()%></span>
</div>
<footer>©2011 广州市宝城汽车出租有限公司</footer>
</body>
</html>
