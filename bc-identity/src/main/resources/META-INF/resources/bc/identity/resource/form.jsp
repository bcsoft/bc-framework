<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:form theme="simple" cssClass="bc-page ui-widget-content bc-autoScroll"
		title='%{getText("resource.title")}' data-type='form' data-namespace='%{pageNamespace}'
		data-js='%{pageJsCss}' data-initMethod="init" data-option='%{pageOption}'>
	<table class="formFields ui-widget-content" cellspacing="2" cellpadding="0" style="max-width:50em">
		<tr class="widthMarker">
			<td >&nbsp;</td>
			<td style="width: 200px;">&nbsp;</td>
			<td style="width: 80px;">&nbsp;</td>
			<td style="width: 200px;">&nbsp;</td>
		</tr>
		<tr>
			<td class="label">*<s:text name="resource.type"/>：</td>
			<td class="value" colspan="3"><s:radio name="e.type" list="types" listKey="key" listValue="value"
				value="e.type" cssStyle="width:auto;"/></td>
		</tr>
		<tr>
			<td class="label">*<s:text name="label.name"/>：</td>
			<td class="value"><s:textfield name="e.name" data-validate="required" cssClass="ui-widget-content"/></td>
			<td data-name="belong" class="label"><s:text name="resource.belong"/>:</td>
			<td data-name="belong" class="value relative"><s:textfield name="e.belong.name"
				readonly="true" cssClass="ui-widget-content"/>
				<ul class="inputIcons">
					<li id="selectBelong" class="inputIcon ui-icon ui-icon-circle-plus"
						title='<s:text name="title.click2select"/>'></li>
					<li class="clearSelect inputIcon ui-icon ui-icon-circle-close"
						data-cfg='e.belong.id,e.belong.name'
						title='<s:text name="title.click2clear"/>'></li>
				</ul>
			</td>
		</tr>
		<tr>
			<td class="label">*<s:text name="label.order"/>：</td>
			<td class="value"><s:textfield name="e.orderNo" data-validate="required" cssClass="ui-widget-content"/></td>
			<td data-name="iconClass" class="label"><s:text name="resource.iconClass"/>:</td>
			<td data-name="iconClass" class="value relative"><s:textfield name="e.iconClass"
				readonly="true" title='%{getText("resource.title.click2selectIconClass")}' cssClass="ui-widget-content"/>
				<ul class="inputIcons">
					<li id="selectIconClass" class="inputIcon ui-icon ui-icon-circle-plus"
						title='<s:text name="title.click2select"/>'></li>
					<li class="clearSelect inputIcon ui-icon ui-icon-circle-close"
						data-cfg='e.iconClass'
						title='<s:text name="title.click2clear"/>'></li>
				</ul>
			</td>
		</tr>
		<tr>
			<td data-name="url" id="urlText" class="label" data-text='<s:text name="resource.url"/>'>*<s:text name="resource.url"/>：</td>
			<td data-name="url" class="value" colspan="3"><s:textfield name="e.url" data-validate="required" cssClass="ui-widget-content"/></td>
		</tr>
		<tr>
			<td data-name="option" class="label"><s:text name="resource.option"/>：</td>
			<td data-name="option" class="value" colspan="3">(编写标准JavaScript代码)</td>
		</tr>
		<tr>
			<td data-name="option" class="value" colspan="4"><s:textarea name="e.option" cssClass="ui-widget-content autoHeight" /></td>
		</tr>
	</table>
	<s:hidden name="e.status" />
	<s:hidden name="e.inner" />
	<s:hidden name="e.uid" />
	<s:hidden name="e.id" />
	<s:hidden name="e.pname" />
	<s:hidden name="e.belong.id" />
	<p class="formComment"><s:text name="resource.form.comment"/></p>
</s:form>