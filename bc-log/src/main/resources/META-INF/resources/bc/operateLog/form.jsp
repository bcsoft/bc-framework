<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="operateLog.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/operateLog/save" />'
	data-js='<s:url value="/bc/operateLog/form.js"/>'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="operateLogForm" theme="simple">
		<div class="formTopInfo">
			<s:property value="e.author.name" /> 创建于  <s:date name="e.fileDate" format="yyyy-MM-dd HH:mm:ss"/>
		</div>
		<div style="font-weight: normal;margin-bottom: -8px;padding-left: 6px;">标题:</div>
		<div class="formFields">
			<s:textfield name="e.subject" data-validate="required" style="width:99%" cssClass="ui-widget-content"/>
		</div>
		<div style="font-weight: normal;margin-bottom: -8px;padding-left: 6px;">详细内容:</div>
		<div class="formFields">
			<s:textarea name="e.content" rows="10" style="width:99%" cssClass="ui-widget-content autoHeight nooutline" />
		</div>
		<s:property value="attachsUI" escapeHtml="false"/>
		<s:hidden name="e.uid" />
		<s:hidden name="e.id" />
		<s:hidden name="e.author.id" />
		<s:hidden name="e.type" />
		<s:hidden name="e.way" />
		<s:hidden name="e.ptype" />
		<s:hidden name="e.pid" />
		<s:hidden name="e.operate" />
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>
	</s:form>
</div>