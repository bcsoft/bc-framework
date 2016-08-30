<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:property value="title" />' data-type='form'
	class="bc-page spider"
	data-js='<s:url value="/bc/spider/common.css" />,<s:url value="/bc/spider/common.js" />'
	data-initMethod='bc.spider.init'
	data-option='<s:property value="pageOption"/>' style="overflow: hidden;">
	<div class="vlayout" style="width: 100%; height: 100%;">
	<div class="ui-widget-content" style="width: auto; border-width: 1px;">
		<div class="ui-widget-header title" style="position: relative;">
			<span class="text">条件设置:</span>
			<ul class="inputIcons">
				<li class="folder verticalMiddle ui-icon ui-icon-carat-1-n" title="点击折叠内容"></li>
			</ul>
		</div>
		<div class="params">
			<%--<s:text name="formData" />--%>
		</div>
	</div>
	<div class="ui-widget-content flex vlayout"
		style="width: auto; border-width: 0 1px 1px 1px;">
		<div class="ui-widget-header title" style="position: relative;">
			<span class="text">查询结果:</span>
			<input type="button" id="go" value="查询">
			<span id="status">状态信息在这里显示</span>
			<ul class="inputIcons">
				<li class="folder verticalMiddle ui-icon ui-icon-carat-1-n" title="点击折叠内容"></li>
			</ul>
		</div>
		<div class="result flex"></div>
	</div>
	<div class="hiddenFields">
		<s:hidden name='config'></s:hidden>
		<s:hidden name='code'></s:hidden>
		<s:hidden name='auto'></s:hidden>
		<s:hidden name='params'></s:hidden>
	</div>
	</div>
</div>