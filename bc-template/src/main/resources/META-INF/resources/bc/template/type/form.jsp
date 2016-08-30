<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="templateType.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/templateType/save" />'
	data-js='<s:url value="/bc/template/type/form.js" />'
	data-initMethod='bc.templateTypeForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="templateTypeForm" theme="simple" >
		<div style="margin:10px;">
			<table  cellspacing="2" cellpadding="0" style="width:100%;"  >
				<tbody>
					<colgroup>
						<col style="width: 5.7em;">
						<col style="width: auto;">
						<col style="width: 5.7em;">
						<col style="width: 10.5em;">
					</colgroup>
					<tr>
						<td class="label">*<s:text name="template.code"/>:</td>
						<td class="value" colspan="3"><s:textfield name="e.code" cssClass="ui-widget-content" data-validate="required" /></td>
					</tr>
					<tr>
						<td class="label">*<s:text name="template.name"/>:</td>
						<td class="value" colspan="3"><s:textfield name="e.name" cssClass="ui-widget-content" data-validate="required"/>
						</td>
					</tr>
					<tr>
						<td class="label"><s:text name="template.order"/>:</td>
						<td class="value" colspan="3"><s:textfield name="e.orderNo" cssClass="ui-widget-content" /></td>
					</tr>		
					<tr>
						<td class="label"><s:text name="templateType.isPath"/>:</td>
						<td class="value"><s:radio name="e.path" list="#{'true':'是','false':'否'}" cssStyle="width:auto;"/></td>
						<td class="label templateTypeExt">*<s:text name="templateType.ext"/>:</td>
						<td class="value templateTypeExt" style="padding-right:0.35em;" >
							<s:textfield name="e.extension" cssClass="ui-widget-content" data-validate="required"/>
						</td>
					</tr>
					<tr>
						<td class="label"><s:text name="templateType.isPureText"/>:</td>
						<td class="value"><s:radio name="e.pureText" list="#{'true':'是','false':'否'}" cssStyle="width:auto;"/></td>
					</tr>
					<tr>
						<td class="label"><s:text name="template.status"/>:</td>
						<td class="value"><s:radio name="e.status" list="#{'0':'正常','1':'禁用'}" cssStyle="width:auto;"/></td>
					</tr>
					<tr>
						<td class="topLabel">备注:</td>
						<td class="value" colspan="3" >
							<s:textarea rows="3" name="e.desc"  cssClass="ui-widget-content noresize" />
						</td>
					</tr>
					<tr>
						<td class="label" colspan="4">
							<div class="formTopInfo">
								创建：<s:property value="e.author.name" />(<s:date name="e.fileDate" format="yyyy-MM-dd HH:mm:ss"/>)
								<s:if test="%{e.modifier != null}">
								最后修改：<s:property value="e.modifier.name" />(<s:date name="e.modifiedDate" format="yyyy-MM-dd HH:mm:ss"/>)
								</s:if>
							</div>
						</td>
					</tr>		
				</tbody>
			</table>
		</div>
		<s:hidden name="e.id" />
		<s:hidden name="e.author.id" />
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>
	</s:form>
</div>