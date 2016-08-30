<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="chat.online.title"/>' data-type='form'
	class="bc-page online"
	data-js='<s:url value="/bc/chat/online.css" />,<s:url value="/bc/chat/online.js" />'
	data-initMethod='bc.online.init' data-option='{"width":350,"height":400,minimizable:true,maximizable:false}'
	style="overflow-y: auto;">
	<div class='container'>
		<ul class="items">
			<s:iterator var="u" value="users">
				<li class="item"
					data-sid='<s:property value="sid"/>' data-user='<s:property value="json"/>'>
					<img style=""
						src='<s:url value="/bc/image/download"><s:param name='puid' value='uid'/><s:param name='ptype' value='%{"portrait"}'/><s:param name='ts' value='ts'/></s:url>' />
					<span class="text"><s:date name="loginTime" format="HH:mm:ss"/>&nbsp;<s:property value="name" /> - <s:property value="ip" />
				</span></li>
			</s:iterator>
		</ul>
		<div class="bottom ui-widget-header">
			<div class="info">(<span id="count"><s:property value="users.size()"/></span>) 双击在线用户打开聊天窗口！</div>
		</div>
	</div>
</div>