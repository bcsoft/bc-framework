<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="photo.title"/>' data-type='form' class="bc-page"
	data-js='js:jcrop_css,js:jcrop,<s:url value="/bc/photo/main.js" />'
	data-initMethod='bc.photo.handler.init'
	data-option='<s:property value="pageOption"/>' style="overflow:hidden;">
<div class="hlayout" style="width: 100%; height: 100%;font-weight:normal;position:relative;">
    <div class="container flex vlayout hcenter vcenter unselectable" unselectable="on"
        style="position:relative;overflow:hidden;background-color:#2B2B2B;cursor:default;">
        <img style="width:0;"/>
        <div class="helper" style="width: 100%; height: 100%;position:absolute;left:0;top:0;">
            <div class="indicator vlayout hcenter vcenter" style="width: 100%; height: 100%;position:absolute;left:0;top:0;display:none;font-size:48px;color:#000;text-align:center;box-sizing: border-box;">
                <pre style="margin:0" class="msg">请将图片<br>拖放到这里</pre>
                <a href="#" style="font-size:24px;color:blue;">取消</a>
            </div>
            <%--<video autoplay style="display:none;width: 100%; height: 100%;position:absolute;left:0;top:0;background-color:#000" title="双击截图"></video>--%>
        </div>
    </div>
    <div class="nav ui-widget-content vlayout hleft" style="width: 180px;border-color:#27292A;border-width:0 0 0 1px;/*background-image:none;background-color:#3C3F41;*/">
        <div class="flex vlayout hleft" style="width:100%;text-align:left;">
            <div class="flex">
                <button class="crop">裁剪</button>
                <button class="destroy" disabled>完成裁剪</button>
            </div>
            <div class="statusBar flex"></div>
        </div>
        <div style="width:100%;text-align:left;padding:0.4em;box-sizing: border-box;">
            <div>图片名称：</div>
            <div style="text-align:center;">
                <input type="text" name="fname" style="width:100%;box-sizing: border-box;margin:0;" value="${fname}" />
            </div>
            <div>图片信息：</div>
            <div class="info">无</div>
        </div>
    </div>
</div>
<div class="hiddenContainer" style="display:none;">
    <img class="proxy" src="${url}"/>
    <canvas class="proxy"/>
    <input type="hidden" name="id" value="${id}"/>
    <input type="hidden" name="dir" value="${dir}"/>
    <input type="hidden" name="path" value="${path}"/>
    <input type="hidden" name="format" value="${format}"/>
    <input type="hidden" name="size" value="${size}"/>
    <input type="hidden" name="url" value="${url}"/>
    <input type="hidden" name="fname" value="${fname}"/>
    <input type="hidden" name="ptype" value="${ptype}"/>
    <input type="hidden" name="puid" value="${puid}"/>
    <input type="hidden" name="data" value="${data}"/>
</div>
</div>