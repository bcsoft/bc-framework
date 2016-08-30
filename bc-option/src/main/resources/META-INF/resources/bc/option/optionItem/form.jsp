<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="optionItem.title"/>' data-type='form' class="bc-page bc-autoScroll"
	data-saveUrl='<s:url value="/bc/optionItem/save" />'
	data-js='<s:url value="/bc/option/optionItem/form.js" />'
	data-initMethod='bc.optionItemForm.init'
	data-option='{
		"buttons":[{"text":"<s:text name="label.save"/>","action":"save"}],
		"width":400,"minWidth":280,"minHeight":200,"modal":false
	}'>
	<s:form name="optionItemForm" theme="simple">
		<div class="formFields ui-widget-content">
			<table class="formFields" cellspacing="2" cellpadding="0">
				<tbody>
					<tr>
						<td class="label w80">* <s:text name="option.optionGroup"/>:</td>
						<td class="value relative"><s:textfield name="e.optionGroup.value" data-validate="required" readonly="true"/>
							<ul class="inputIcons">
								<li id="selectOptionGroup" class="inputIcon ui-icon ui-icon-circle-plus"
									title='<s:text name="title.click2select"/>'></li>
							</ul>
						</td>
					</tr>
					<tr>
						<td class="label">* <s:text name="option.key"/>:</td>
						<td class="value"><s:textfield name="e.key" data-validate="required"/></td>
					</tr>
					<tr>
						<td class="label">* <s:text name="option.value"/>:</td>
						<td class="value"><s:textfield name="e.value" data-validate="required"/></td>
					</tr>
					<tr>
						<td class="label"><s:text name="label.status"/>:</td>
						<td class="value"><s:radio name="e.status" list="#{'0':'启用','1':'禁用','2':'已删除'}" 
								value="e.status" cssStyle="width:auto;"/></td>
					</tr>
					<tr>
						<td class="label"><s:text name="label.order"/>:</td>
						<td class="value"><s:textfield name="e.orderNo"/></td>
					</tr>
					<tr>
						<td class="label"><s:text name="option.icon"/>:</td>
						<td class="value relative"><s:textfield name="e.icon" />
							<ul class="inputIcons">
								<li id="selectIconClass" class="inputIcon ui-icon ui-icon-circle-plus"
									title='<s:text name="title.click2select"/>'></li>
							</ul>
						</td>
					</tr>
					<tr>
						<td class="label"><s:text name="option.desc"/>:</td>
						<td class="value relative"><s:textfield name="e.description" /></td>
					</tr>
				</tbody>
			</table>
		</div>
		<s:hidden name="e.id"/>
		<s:hidden name="e.optionGroup.id"/>
	</s:form>
</div>