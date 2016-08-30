<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%><!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>图片打印</title>
    <style type="text/css">
        img{margin: 2px;}
    </style>
</head>
<body>
 <img src='<s:url value="/bc/image/download?id=" />${id}&ts=${ts}' style="width:${width / 1.059}mm;">
</body>
</html>