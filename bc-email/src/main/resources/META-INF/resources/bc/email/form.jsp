<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="email.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/email/save" />'
	data-js='<s:url value="/bc/email/form.js" />,js:bc_identity,js:redactor_css,js:redactor,js:redactor_cn,js:redactor_plugins_fullscreen'
	data-initMethod='bc.emailForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="emailForm" theme="simple" >
		<table cellspacing="2" cellpadding="0" style="width:100%;">
			<tr class="widthMarker">
				<td style="width: 80px;">&nbsp;</td>
				<td >&nbsp;</td>
			</tr>
			<tr>
				<td class="label"><s:text name="email.receiver"/>：</td>
				<td class="value">
					<div class="ui-widget-content" style="position:relative;margin: 0;padding: 0;min-height:19px;margin: 0;font-weight: normal;" >
						<ul class="inputIcons">
						 	<li class="email-addUsers inputIcon ui-icon ui-icon-person" data-type="4" title='点击添加用户'>
						 	<li class="email-addGroups inputIcon ui-icon ui-icon-contact" title='点击添加岗位'>
						 	<li class="email-addUnitOrDepartments inputIcon ui-icon ui-icon-home" title='点击添加单位或部门'>
						</ul>
						<ul class="horizontal ulReceiver" style="padding:0;overflow:hidden;" data-type="0">
							<s:iterator value="e.tos" var="t" >
								<s:if test="%{type == 0}">
									<s:if test="%{receiver != null}">
										<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" 
											data-id='<s:property value="receiver.id"/>' 
											data-hidden='{"id":"<s:property value='receiver.id'/>"
														,"code":"<s:property value='receiver.code'/>"
														,"name":"<s:property value='receiver.name'/>"
														,"type":<s:property value='receiver.type'/>}' 
											style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">
											<span class="text"><s:property value="receiver.name"/></span>
											<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title="点击移除"></span>
										</li>
									</s:if><s:else>
										<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" 
											data-id='<s:property value="receiver.id"/>' 
											data-hidden='{"id":"<s:property value='upper.id'/>"
														,"code":"<s:property value='upper.code'/>"
														,"name":"<s:property value='upper.name'/>"
														,"type":<s:property value='upper.type'/>}' 
											style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">
											<span class="text"><s:property value="upper.name"/></span>
											<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title="点击移除"></span>
										</li>
									</s:else>
								</s:if>			
							</s:iterator>
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<td class="label" title="什么是抄送：同时将这一封邮件发送给其他联系人。"><s:text name="email.cc"/>：</td>
				<td class="value">
					<div class="ui-widget-content" style="position:relative;margin: 0;padding: 0;min-height:19px;margin: 0;font-weight: normal;" >
						<ul class="inputIcons">
						 	<li class="email-addUsers inputIcon ui-icon ui-icon-person" data-type="4" title='点击添加用户'>
						 	<li class="email-addGroups inputIcon ui-icon ui-icon-contact" title='点击添加岗位'>
						 	<li class="email-addUnitOrDepartments inputIcon ui-icon ui-icon-home" title='点击添加单位或部门'>
						</ul>
						<ul class="horizontal ulReceiver" style="padding: 0;overflow:hidden;" data-type="1">
							<s:iterator value="e.tos" var="t" >
								<s:if test="%{type == 1}">
									<s:if test="%{receiver != null}">
										<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" 
											data-id='<s:property value="receiver.id"/>' 
											data-hidden='{"id":"<s:property value='receiver.id'/>"
														,"code":"<s:property value='receiver.code'/>"
														,"name":"<s:property value='receiver.name'/>"
														,"type":<s:property value='receiver.type'/>}' 
											style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">
											<span class="text"><s:property value="receiver.name"/></span>
											<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title="点击移除"></span>
										</li>
									</s:if><s:else>
										<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" 
											data-id='<s:property value="receiver.id"/>' 
											data-hidden='{"id":"<s:property value='upper.id'/>"
														,"code":"<s:property value='upper.code'/>"
														,"name":"<s:property value='upper.name'/>"
														,"type":<s:property value='upper.type'/>}' 
											style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">
											<span class="text"><s:property value="upper.name"/></span>
											<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title="点击移除"></span>
										</li>
									</s:else>
								</s:if>			
							</s:iterator>
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<td class="label" title="什么是密送：同时将这一封邮件发送给其他联系人，但收件人及抄送人不会看到密送人。"><s:text name="email.bcc"/>：</td>
				<td class="value">
					<div class="ui-widget-content" style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;font-weight: normal;" >
						<ul class="inputIcons">
						 	<li class="email-addUsers inputIcon ui-icon ui-icon-person" data-type="4" title='点击添加用户'>
						 	<li class="email-addGroups inputIcon ui-icon ui-icon-contact" title='点击添加岗位'>
						 	<li class="email-addUnitOrDepartments inputIcon ui-icon ui-icon-home" title='点击添加单位或部门'>
						</ul>
						<ul class="horizontal ulReceiver" style="padding: 0;overflow:hidden;" data-type="2">
							<s:iterator value="e.tos" var="t" >
								<s:if test="%{type == 2}">
									<s:if test="%{receiver != null}">
										<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" 
											data-id='<s:property value="receiver.id"/>' 
											data-hidden='{"id":"<s:property value='receiver.id'/>"
														,"code":"<s:property value='receiver.code'/>"
														,"name":"<s:property value='receiver.name'/>"
														,"type":<s:property value='receiver.type'/>}' 
											style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">
											<span class="text"><s:property value="receiver.name"/></span>
											<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title="点击移除"></span>
										</li>
									</s:if><s:else>
										<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" 
											data-id='<s:property value="receiver.id"/>' 
											data-hidden='{"id":"<s:property value='upper.id'/>"
														,"code":"<s:property value='upper.code'/>"
														,"name":"<s:property value='upper.name'/>"
														,"type":<s:property value='upper.type'/>}' 
											style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">
											<span class="text"><s:property value="upper.name"/></span>
											<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title="点击移除"></span>
										</li>
									</s:else>
								</s:if>			
							</s:iterator>
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<td class="label">*<s:text name="email.subject"/>：</td>
				<td class="value" >
						<s:textfield name="e.subject" data-validate="required" cssClass="ui-widget-content"/>
				</td>
			</tr>
		</table>
		
		<div class="formEditor" style="min-width:590px;font-weight: normal;overflow: hidden;">
			<textarea name="e.content" class="bc-redactor"
				 data-ptype="companyFile.editor" data-puid='${e.uid}' 
				 style="min-height:150px;"
				 data-readonly='${readonly}' data-tools='simple'>${e.content}</textarea>
		</div>
		
		<!-- 附件  -->
		<s:property value="attachsUI" escapeHtml="false"/>
		
		<s:hidden name="e.id" />
		<s:hidden name="e.uid" />
		<s:hidden name="e.status" />
		<s:hidden name="e.type" />
		<s:hidden name="e.sender.id" />
		
		<s:hidden name="receivers" />

		<input type="hidden" name="e.sendDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.sendDate" />'/>	
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>	
	</s:form>
</div>