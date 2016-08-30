<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="shortcut.form.title"/>' data-type='form' class='bc-page' style="overflow:auto;"
	data-namespace='<s:property value="pageNamespace"/>'
	data-js='<s:property value="pageJsCss"/>'
	data-option='<s:property value="pageOption"/>'>
	<s:form name="form" theme="simple">
		<table class="formTable ui-widget-content" cellspacing="2" cellpadding="0">
			<tr class="widthMarker">
				<td style="width: 80px;">&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="label">* <s:text name="shortcut.name" />:</td>
				<td class="value"><s:textfield name="e.name" data-validate="required" cssClass="ui-widget-content"/></td>
			</tr>
			<tr>
				<td class="label">* <s:text name="shortcut.url" />:</td>
				<td class="value"><s:textfield name="e.url" data-validate="required" cssClass="ui-widget-content"/></td>
			</tr>
			<tr>
				<td class="label">* <s:text name="shortcut.iconClass" />:</td>
				<td class="value" style="position:relative;display: block;"><s:textfield name="e.iconClass" 
					data-validate="required" cssClass="ui-widget-content" readonly="true"/>
					<span id="selectIconClass" class="verticalMiddle ui-icon ui-icon-circle-plus" title='<s:text name="title.click2select"/>'></span>
				</td>
			</tr>
			<tr>
				<td class="label"><s:text name="shortcut.standalone" />:</td>
				<td class="value"><s:radio name="e.standalone"
					list="#{'true':'外部链接','false':'内部链接'}" value="e.standalone"
					cssStyle="width:auto;" /></td>
			</tr>
			<tr>
				<td class="label">* <s:text name="label.order" />:</td>
				<td class="value"><s:textfield name="e.order" data-validate="required" cssClass="ui-widget-content"/></td>
			</tr>
		</table>
		<s:hidden name="e.status" />
		<s:hidden name="e.inner" />
		<s:hidden name="e.uid" />
		<s:hidden name="e.id" />
		<s:hidden name="e.actorId" />
		<s:hidden name="e.resourceId" />
		<s:hidden name="e.cfg" />
		<p class="formComment"><s:text name="shortcut.form.comment"/></p>
	</s:form>
</div>