<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:property value="title" />' data-type='form' class="bc-page chatMsg"
	data-js='<s:url value="/bc/chat/message.css" />,<s:url value="/bc/chat/message.js" />'
	data-initMethod='bc.chatMessage.init' data-option='{"width":420,"height":420,"minWidth":300,"minHeight":300}'>
	<div class='container'>
		<div class="history">
			<!-- 
			<div class="item">
				<div class="header remote">张三 10:30:10</div>
				<div class="content">详细的消息内容</div>
			</div>
			<div class="item">
				<div class="header local">我 10:30:20</div>
				<div class="content">详细的消息内容 详细的消息内容 详细</div>
			</div>
			<div class="item">
				<div class="header sys">系统 10:30:10</div>
				<div class="content">详细的消息内容</div>
			</div>
			<div class="item">
				<div class="header local">我 10:30:20</div>
				<div class="content">详细的消息内容</div>
			</div> -->
		</div>
		<div class="editor ui-widget-header">
		</div>
		<div class="inputText">
			<textarea class="ui-widget-content"></textarea>
		</div>
		<div class="bottom ui-widget-header">
			<div class="info">按 Ctrl+Enter 键发送消息</div>
			<input type="button" id="btnSend" value="发送">
		</div>
	</div>
	<s:hidden name="toSid"/>
	<s:hidden name="toName"/>
</div>