<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="browser.title"/>' data-type='form' class="bc-page"
	data-js='<s:url value="/bc/libs/themes/default/browser.css" />,<s:url value="/bc/browser/list.js" />'
	data-initMethod="bc.browser.init"
	data-option='{"width":530,"minWidth":250,"minHeight":250,"height":500,"modal":true,minimizable:true,maximizable:false}'
	style="overflow-y:auto;">
	<s:form name="browserForm" theme="simple">
		<ul class="browsers">
			<s:iterator value="browsers">
			<li class="browser ui-corner-all ui-state-default" data-id='<s:property value="id"/>' title="点击下载">
				<span class='browser-icon <s:property value="icon"/>'></span>
				<div class="text">
					<div class="info">
						<s:property value="name"/> <s:property value="ver_"/> For <s:property value="os_type"/>
						<s:if test='%{os_bit == 1}'> 32位</s:if><s:elseif test='%{os_bit == 2}'> 64位</s:elseif>
					</div>
					<div class="detail">
						发布：<s:date format="yyyy-MM-dd" name="release_date" />，大小：<s:property value="size_info"/>
					</div>
				</div>
			</li>
			</s:iterator>
		</ul>
	</s:form>
</div>