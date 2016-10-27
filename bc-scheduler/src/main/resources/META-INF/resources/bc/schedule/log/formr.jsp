<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="scheduleLog.title"/>' data-type='form' class="bc-page"
	data-namespace='<s:property value="pageNamespace"/>'
	data-js='<s:property value="pageJsCss"/>'
	data-option='<s:property value="pageOption"/>'>
	<s:form name="scheduleLogForm" theme="simple">
		<table class="formTable2 ui-widget-content" cellspacing="2" cellpadding="0">
			<tbody>
				<tr>
					<td class="label"><s:text name="scheduleLog.cfgName"/>:</td>
					<td class="value w200"><s:textfield name="e.cfgName" readonly="true" cssClass="ui-state-disabled"/></td>
					<td class="label"><s:text name="scheduleLog.cfgGroup"/>:</td>
					<td class="value w200"><s:textfield name="e.cfgGroup" readonly="true" cssClass="ui-state-disabled"/></td>
				</tr>
				<tr>
					<td class="label"><s:text name="scheduleLog.startDate"/>:</td>
					<td class="value"><input type="text" name="e.startDate" readonly="readonly" class="ui-state-disabled"
						value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.startDate" />'/></td>
					<td class="label"><s:text name="scheduleLog.cfgCron"/>:</td>
					<td class="value"><s:textfield name="e.cfgCron" readonly="true" cssClass="ui-state-disabled"/></td>
				</tr>
				<tr>
					<td class="label"><s:text name="scheduleLog.endDate"/>:</td>
					<td class="value"><input type="text" name="e.endDate" readonly="readonly" class="ui-state-disabled"
						value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.endDate" />'/></td>
					<td class="label"><s:text name="scheduleLog.cfgBean"/>:</td>
					<td class="value"><s:textfield name="e.cfgBean" readonly="true" cssClass="ui-state-disabled"/></td>
				</tr>
				<tr>
					<td class="label"><s:text name="scheduleLog.success"/>:</td>
					<td class="value"><s:radio disabled="true" name="e.success" list="#{'true':'成功','false':'失败'}" cssStyle="width:auto;"/></td>
					<td class="label"><s:text name="scheduleLog.cfgMethod"/>:</td>
					<td class="value"><s:textfield name="e.cfgMethod" readonly="true" cssClass="ui-state-disabled"/></td>
				</tr>
			</tbody>
		</table>
		<s:if test="!e.success">
		<div class="formTable2 ui-widget-content">
			<s:text name="scheduleLog.msg"/>：<a id="showError" href="#" class="link" ><s:text name="scheduleLog.seeInNewWindow"/></a>
			<pre id="msg" style="height:200px;overflow: auto;margin:0;padding:4px;font-weight: normal"><s:property value="e.msg"/></pre>
		</div>
		</s:if>
		<s:hidden name="e.id" />
	</s:form>
</div>