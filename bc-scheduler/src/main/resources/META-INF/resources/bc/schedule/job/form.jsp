<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="scheduleJob.title"/>' data-type='form' class="bc-page bc-autoScroll"
	data-namespace='<s:property value="pageNamespace"/>'
	data-js='<s:property value="pageJsCss"/>'
	data-option='<s:property value="pageOption"/>'>
	<s:form name="form" theme="simple">
		<div class="formFields ui-widget-content">
			<table class="formTable2 ui-widget-content" cellspacing="2" cellpadding="0">
				<tbody>
					<tr>
						<td class="label w100">* <s:text name="scheduleJob.name"/>:</td>
						<td class="value"><s:textfield name="e.name" data-validate="required"/></td>
						<td class="label w100">* <s:text name="scheduleJob.bean"/>:</td>
						<td class="value"><s:textfield name="e.bean" data-validate="required"/></td>
					</tr>
					<tr>
						<td class="label">* <s:text name="scheduleJob.cron"/>:</td>
						<td class="value"><s:textfield name="e.cron" data-validate="required"/></td>
						<td class="label">* <s:text name="scheduleJob.method"/>:</td>
						<td class="value"><s:textfield name="e.method" data-validate="required"/></td>
					</tr>
					<tr>
						<td class="label"><s:text name="scheduleJob.ignoreError"/>:</td>
						<td class="value"><s:radio name="e.ignoreError" list="#{'false':'否','true':'是'}" 
								value="e.ignoreError" cssStyle="width:auto;"/></td>
						<td class="label"><s:text name="label.order"/>:</td>
						<td class="value"><s:textfield name="e.orderNo"/></td>
					</tr>
					<tr>
						<td class="label top"><s:text name="scheduleJob.memo"/>:</td>
						<td class="value" colspan="3"><s:textarea name="e.memo" rows="5"/></td>
					</tr>
				</tbody>
			</table>
		</div>
		<s:hidden name="e.id"/>
		<s:hidden name="e.status"/>
		<s:hidden name="e.groupn"/>
		<p class="formComment"><s:text name="scheduleJob.form.comment"/></p>
	</s:form>
</div>