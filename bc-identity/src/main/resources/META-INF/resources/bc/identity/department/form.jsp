<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="department.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/department/save" />'
	data-js='js:bc_identity,<s:url value="/bc/identity/department/form.js" />'
	data-initMethod='bc.departmentForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow:auto;">
	<s:form name="departmentForm" theme="simple">
		<table class="formTable2 ui-widget-content" cellspacing="2" cellpadding="0">
			<tr class="widthMarker">
				<td >&nbsp;</td>
				<td style="width: 200px;">&nbsp;</td>
				<td style="width: 80px;">&nbsp;</td>
				<td style="width: 200px;">&nbsp;</td>
			</tr>
			<tr>
				<td class="label">*<s:text name="label.name"/>:</td>
				<td class="value"><s:textfield name="e.name" data-validate="required" cssClass="ui-widget-content"/></td>
				<td class="label">*<s:text name="actor.belong"/>:</td>
				<td class="value relative"><s:textfield name="belongNames" 
					data-validate="required" cssClass="ui-widget-content" readonly="true"/>
					<ul class="inputIcons">
						<li id="selectBelong" class="inputIcon ui-icon ui-icon-circle-plus"
							title='<s:text name="title.click2select"/>'></li>
					</ul>
				</td>
			</tr>
			<tr>
				<td class="label">*<s:text name="label.code"/>:</td>
				<td class="value"><s:textfield name="e.code" data-validate="required" cssClass="ui-widget-content"/></td>
				<td class="label"><s:text name="label.phone"/>:</td>
				<td class="value"><s:textfield name="e.phone" data-validate='{"type":"phone","required":false}' cssClass="ui-widget-content"/></td>
			</tr>
			<tr>
				<td class="label">*<s:text name="label.order"/>:</td>
				<td class="value"><s:textfield name="e.orderNo" data-validate='required' cssClass="ui-widget-content"/></td>
				<td class="label"><s:text name="label.email"/>:</td>
				<td class="value"><s:textfield name="e.email" data-validate='{"type":"email","required":false}' cssClass="ui-widget-content"/></td>
			</tr>
		</table>
		<!-- 已分配的角色信息 -->
		<div id="assignRoles" class="formTable2 ui-widget-content" 
			data-removeTitle='<s:text name="title.click2remove"/>'>
			<div class="ui-state-active title" style="position:relative;">
				<span class="text"><s:text name="actor.headerLabel.assignRoles"/>：
					<s:if test="%{ownedRoles == null || ownedRoles.isEmpty()}"><s:text name="label.empty"/></s:if>
				</span>
				<s:if test="!readonly">
				<span id="addRoles" class="verticalMiddle ui-icon ui-icon-circle-plus" title='<s:text name="actor.title.click2addRoles"/>'></span>
				</s:if>
			</div>
			<s:if test="%{ownedRoles != null && !ownedRoles.isEmpty()}">
			<ul class="horizontal">
			<s:iterator value="ownedRoles">
				<li class="horizontal ui-widget-content ui-corner-all" data-id='<s:property value="id" />'>
					<span class="text"><s:property value="name" /></span>
					<s:if test="!readonly">
					<span class="click2remove verticalMiddle ui-icon ui-icon-close" title='<s:text name="title.click2remove"/>'></span>
					</s:if>
				</li>
			</s:iterator>
			</ul>
			</s:if>	
		</div>
		<!-- 从上级组织继承的角色信息 -->
		<div id="inheritRolesFromOU" class="formTable2 ui-widget-content" >
			<div class="ui-state-active title" style="position:relative;">
				<span class="text"><s:text name="actor.headerLabel.inheritRolesFromOU"/>：
					<s:if test="%{inheritRolesFromOU == null || inheritRolesFromOU.isEmpty()}"><s:text name="label.empty"/></s:if>
				</span>
			</div>
			<s:if test="%{inheritRolesFromOU != null && !inheritRolesFromOU.isEmpty()}">
			<ul class="horizontal">
			<s:iterator value="inheritRolesFromOU">
				<li class="horizontal ui-widget-content ui-corner-all ui-state-disabled" data-id='<s:property value="id" />'>
					<span class="text2"><s:property value="name" /></span>
				</li>
			</s:iterator>
			</ul>
			</s:if>	
		</div>
		<s:hidden name="e.type"/>
		<s:hidden name="e.status" />
		<s:hidden name="e.inner" />
		<s:hidden name="e.uid" />
		<s:hidden name="e.id" />
		<s:hidden name="e.pcode" />
		<s:hidden name="e.pname" />
		<s:hidden name="belongIds" />
		<s:hidden name="assignRoleIds" />
		<p class="formComment"><s:text name="department.form.comment"/></p>
	</s:form>
</div>