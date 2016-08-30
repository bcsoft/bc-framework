<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="message.title"/>' class='bc-page' data-type='form'
	data-namespace='<s:property value="pageNamespace"/>'
	data-js='<s:property value="pageJsCss"/>'
	data-option='<s:property value="pageOption"/>'>
	<s:form name="form" theme="simple" cssClass="bc-form">
		<table class="formTable" cellspacing="2">
			<tbody>
				<tr>
					<td class="label"><s:text name="message.sender.name" />:</td>
					<td class="value"><s:textfield name="e.sender.name"
							readonly="true" />
					</td>
				</tr>
				<tr>
					<td class="label"><s:text name="message.sendDate" />:</td>
					<td class="value"><s:textfield name="e.sendDate"
							readonly="true">
							<s:param name="value">
								<s:date name="e.sendDate" format="yyyy-MM-dd HH:mm:ss" />
							</s:param>
						</s:textfield></td>
				</tr>
				<tr>
					<td class="label"><s:text name="message.read" />:</td>
					<td class="value"><s:textfield name="e.read" readonly="true" />
					</td>
				</tr>
				<tr>
					<td class="label"><s:text name="message.receiver.name" />:</td>
					<td class="value"><s:textfield name="e.receiver.name"
							readonly="true" />
					</td>
				</tr>
				<tr>
					<td class="label"><s:text name="message.subject" />:</td>
					<td class="value"><s:textfield name="e.subject"
							readonly="true" />
					</td>
				</tr>
				<tr>
					<td class="topLabel"><s:text name="message.content" />:</td>
					<td class="value"><s:textarea name="e.content" readonly="true"
							rows="10" />
					</td>
				</tr>
				<tr>
					<td class="label">&nbsp;</td>
					<td class="value">&nbsp;</td>
				</tr>
			</tbody>
		</table>
		<s:hidden name="e.id" />
	</s:form>
</div>