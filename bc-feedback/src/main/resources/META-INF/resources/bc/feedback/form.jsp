<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="feedback.title"/>' data-type='form' class="bc-page feedback"
	data-saveUrl='<s:url value="/bc/feedback/save" />'
	data-js='js:editor,<s:url value="/bc/feedback/form.css"/>,<s:url value="/bc/feedback/form.js"/>'
	data-initMethod='bc.feedbackForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="feedbackForm" theme="simple">
		<div class="formTopInfo">
			<s:property value="e.author.name" /> 创建于  <s:date name="e.fileDate" format="yyyy-MM-dd HH:mm:ss"/>
		</div>
		<div class="formFields">
			<s:textfield name="e.subject" data-validate="required" style="width:99%" cssClass="ui-widget-content"/>
		</div>
		<div class="formEditor">
			<textarea style="height:180px;" name="e.content" class="bc-editor" data-validate="required"
				 data-ptype="feedback.editor" data-puid='${e.uid}' 
				 data-readonly='${e.id != null}' data-tools='simple'>${e.content}</textarea>
		</div>
		<s:property value="attachsUI" escapeHtml="false"/>
		<s:hidden name="e.uid" />
		<s:hidden name="e.id" />
		<s:hidden name="e.author.id" />
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>
		<s:hidden name="e.modifier.id" />
		<input type="hidden" name="e.modifiedDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.modifiedDate" />'/>
		<input type="hidden" id="hasDeletePriviledge" value='<s:property value="hasDeletePriviledge"/>'/>
		
		<s:if test="!e.isNew()">
			<div id="replies" class="replies" data-count='<s:property value="e.replies.size()"/>'>
				<s:iterator value="e.replies" var="r" status="status">
				<div class="reply ui-widget-content">
					<div class="replyHeader ui-widget-header">
						<s:property value="author.name"/> 回复于 <s:date format="yyyy-MM-dd HH:mm" name="fileDate" />
						<s:if test="hasDeletePriviledge"><a id="deleteBtn" class="right" href="#" data-id='<s:property value="id"/>'>删除</a></s:if>
						<span class="right"><s:property value="#status.count"/>楼</span>
					</div>
					<pre class="replyContent ui-widget-content"><s:if test="%{status == 0}"><s:property value="content" escapeHtml="false"/></s:if><s:else><span 
						class='deleteInfo'>此回复已被删除</span></s:else></pre>
				</div>
				</s:iterator>
			</div>
			<div class="newReply ui-widget-content">
				<textarea id="replyContent" class="custom ui-widget-content noresize"></textarea>
				<br/><input type="button" value="发表回复" id="replyBtn"/><input type="button" value="回到顶部" id="gotoTopBtn"/>
			</div>
		</s:if>
	</s:form>
</div>