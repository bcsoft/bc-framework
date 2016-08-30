<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="bulletin.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/bulletin/save" />'
	data-js='js:editor,<s:url value="/bc/bulletin/form.js" />'
	data-initMethod='bc.bulletinForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="bulletinForm" theme="simple">
		<div class="formTopInfo">
			状态：<s:property value="statusDesc"/>,
			<s:if test="%{e.issuer == null}">
			创建：<s:property value="e.author.name" />(<s:date name="e.fileDate" format="yyyy-MM-dd HH:mm:ss"/>)
			</s:if>
			<s:else>
			发布：<s:property value="e.issuer.name" />(<s:date name="e.issueDate" format="yyyy-MM-dd HH:mm:ss"/>)
			</s:else>
		</div>
		<table class="formTable2 ui-widget-content" cellspacing="2" cellpadding="0" style="width:665px;">
			<tr class="widthMarker">
				<td >&nbsp;</td>
				<td style="width: 200px;">&nbsp;</td>
				<td style="width: 80px;">&nbsp;</td>
				<td style="width: 200px;">&nbsp;</td>
			</tr>
			<tr>
				<td class="label">*<s:text name="bulletin.subject"/>:</td>
				<td class="value" colspan="3"><s:textfield name="e.subject" data-validate="required" cssClass="ui-widget-content"/></td>
			</tr>
			<tr>
				<td class="label"><s:text name="bulletin.overdueDate"/>:</td>
				<td class="value" style="position:relative;display: block;"><input type="text" name="e.overdueDate" 
					data-validate='{required:false,type:"date"}'
					value='<s:date format="yyyy-MM-dd" name="e.overdueDate"/>'
					class="bc-date ui-widget-content"/>
					<span id="selectOverdueDate" class="selectButton verticalMiddle ui-icon ui-icon-calendar"></span>
				</td>
				<td class="label"><s:text name="bulletin.scope"/>:</td>
				<td class="value"><s:radio name="e.scope" list="#{'1':'全系统','0':'本单位'}" 
					value="e.scope" cssStyle="width:auto;"/></td>
			</tr>
		</table>
		<div class="formEditor">
			<textarea name="e.content" class="bc-editor" data-validate="required"
				 data-ptype="bulletin.editor" data-puid='${e.uid}' 
				 data-readonly='${readonly}' data-tools='simple'>${e.content}</textarea>
		</div>
		<s:property value="attachsUI" escapeHtml="false"/>
		<s:hidden name="e.uid" />
		<s:hidden name="e.id" />
		<s:hidden name="e.status" />
		<s:hidden name="e.unit.id" />
		<s:hidden name="e.author.id" />
		<s:hidden name="e.issueDate" />
		<s:hidden name="e.issuer.id" />
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>
		<s:hidden name="e.modifier.id" />
		<input type="hidden" name="e.modifiedDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.modifiedDate" />'/>
	</s:form>
</div>