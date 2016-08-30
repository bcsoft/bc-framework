<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="templateParam.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/templateParam/save" />'
	data-js='<s:url value="/ui-libs/codeMirror/2.24/lib/codemirror.css" />,<s:url value="/ui-libs/codeMirror/2.24/lib/codemirror.js" />,<s:url value="/ui-libs/codeMirror/2.24/mode/javascript/javascript.js" />,<s:url value="/ui-libs/codeMirror/2.24/theme/eclipse.css" />,<s:url value="/bc/template/param/form.js" />'
	data-initMethod='bc.templateParamForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="templateParamForm" theme="simple" >
		<table  cellspacing="2" cellpadding="0" style="width:645px;"  >
			<tbody>
				<tr class="widthMarker">
					<td style="width: 80px;"></td>
					<td >&nbsp;</td>
				</tr>
				<tr>
					<td class="label">*<s:text name="template.name"/>:</td>
					<td class="value"><s:textfield name="e.name" cssClass="ui-widget-content" data-validate="required"/>
					</td>
				</tr>
				<tr>
					<td class="label"><s:text name="template.order"/>:</td>
					<td class="value"><s:textfield name="e.orderNo" cssClass="ui-widget-content" /></td>
				</tr>		
				<tr>
					<td class="topLabel">备注:</td>
					<td class="value">
						<s:textarea rows="3" name="e.desc"  cssClass="ui-widget-content noresize" />
					</td>
				</tr>
				<!-- 详细配置-->
				<tr>
					<td class="topLabel"><s:text name="templateParam.config"/>:</td>
					<td class="value" >(使用json格式)</td>
				</tr>
				<tr>
					<td class="topLabel"></td>
					<td class="value" >
						<div class="ui-widget-content" style="height: auto;width:547px;">
						<s:textarea name="e.config"  cssClass="ui-widget-content" style="overflow-y: visible;height:170px;"/> </div>
					</td>
				</tr>
				<tr>
					<td class="label" colspan="2" style="padding-right:10px"><s:text name="template.status"/>:<s:radio name="e.status" list="#{'0':'正常','1':'禁用'}" cssStyle="width:auto;"/></td>
				</tr>
				<tr>
					<td class="label" colspan="2">
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
		<s:hidden name="e.id" />
		<s:hidden name="e.author.id" />
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>
	</s:form>
</div>