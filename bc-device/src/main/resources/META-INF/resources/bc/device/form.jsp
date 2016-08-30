<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="device.title"/>' data-type='form'
	class="bc-page bc-autoScroll"
	data-saveUrl='<s:url value="/bc/device/save" />'
	data-js='<s:url value="/bc/device/form.js" />'
	data-initMethod='bc.device.form.init'
	data-option='<s:property value="%{formPageOption}"/>'
	style="overflow: hidden;">
	<s:form name="deviceForm" theme="simple">
		<div class="formFields ui-widget-content">
			<table class="formFields" cellspacing="2" cellpadding="0">
				<tbody>
					<tr>
						<td class="label">*<s:text name="device.code" />:
						</td>
						<td class="value"><s:textfield name="e.code"
								data-validate="required" cssClass="ui-widget-content" /></td>
					</tr>
					<tr>
						<td class="label" style="width: 6em">*<s:text
								name="device.model" />:
						</td>
						<td class="value"><s:textfield name="e.model"
								data-validate="required" cssClass="ui-widget-content" /></td>
					</tr>
					<tr>
						<td class="label">*<s:text name="device.name" />:
						</td>
						<td class="value"><s:textfield name="e.name"
								data-validate="required" cssClass="ui-widget-content" /></td>
					</tr>
					<tr>
						<td class="label">*<s:text name="device.purpose" />:
						</td>
						<td class="value"><s:textfield name="e.purpose"
								data-validate="required" cssClass="ui-widget-content" /></td>
					</tr>
					<tr>
						<td class="label">*<s:text name="device.sn" />:
						</td>
						<td class="value"><s:textfield name="e.sn"
								data-validate="required" cssClass="ui-widget-content" /></td>
					</tr>
					<tr>
						<td class="label">*<s:text name="device.buyDate" />:
						</td>
						<td class="value relative"><s:if test="%{!e.isNew()}">
								<input type="text" name="buyDate"
									data-validate='{"type":"date","required":true}'
									value='<s:date name="e.buyDate" format="yyyy-MM-dd"/>'
									class="bc-date ui-widget-content"
									data-cfg='{changeYear:true,"onSelect":"bc.device.form.selectedDate"}' />
							</s:if> <s:else>
								<input type="text" name="buyDate"
									data-validate='{"type":"date","required":true}' value=""
									class="bc-date ui-widget-content"
									data-cfg='{changeYear:true,"onSelect":"bc.device.form.selectedDate"}' />
							</s:else>
							<ul class="inputIcons">
								<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"></li>
							</ul></td>
					</tr>
					<tr>
						<td class="topLabel"><s:text name="device.desc" />:</td>
						<td class="value"><s:textarea name="e.desc" rows="3"
								cssClass="ui-widget-content noresize" /></td>
					</tr>
					<tr>
						<td class="label"><s:text name="device.status" />:</td>
						<td class="value"><s:radio name="e.status"
								list="#{'0':'使用中','1':'已禁用'}" cssStyle="width:auto;"></s:radio>
						</td>
					</tr>
				</tbody>
			</table>
			<div class="fileInfo">
				<s:if test="%{e.author.name != null}">
				创建：<s:property value="e.author.name" />(<s:date name="e.fileDate"
						format="yyyy-MM-dd HH:mm:ss" />)<br>
				</s:if>
				<s:if test="%{e.modifier != null}">
				最后修改：<s:property value="e.modifier.name" />(<s:date
						name="e.modifiedDate" format="yyyy-MM-dd HH:mm:ss" />)
				</s:if>
			</div>
		</div>
		<s:hidden name="e.id" />
		<s:hidden name="e.author.id" />
		<s:hidden name="e.uid" />
		<input type="hidden" name="e.fileDate"
			value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />' />
		<input type="hidden" name="e.buyDate"
			value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.buyDate" />' />
	</s:form>
</div>