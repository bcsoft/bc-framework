<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="user.title"/>' data-type='form' class="bc-page" style="overflow:auto;"
	data-saveUrl='<s:url value="/bc/user/save" />'
	data-js='js:bc_identity,<s:url value="/bc/identity/user/form.js" />'
	data-initMethod='bc.userForm.init'
	data-option='<s:property value="formPageOption"/>'>
	<s:form name="userForm" theme="simple">
		<table class="formTable2 ui-widget-content" cellspacing="2" cellpadding="0" style="width:650px;">
			<tr class="widthMarker">
				<td >&nbsp;</td>
				<td style="width: 200px;">&nbsp;</td>
				<td style="width: 80px;">&nbsp;</td>
				<td style="width: 200px;">&nbsp;</td>
				<td style="width: 110px;">&nbsp;</td>
			</tr>
			<tr>
				<td class="label">*<s:text name="user.name"/>:</td>
				<td class="value"><s:textfield name="e.name" data-validate="required" cssClass="ui-widget-content"/></td>
				<td class="label">*<s:text name="actor.belong"/>:</td>
				<td class="value relative"><s:textfield name="belongNames" 
					data-validate="required" cssClass="ui-widget-content" readonly="true"/>
					<ul class="inputIcons">
						<li id="selectBelong" class="inputIcon ui-icon ui-icon-circle-plus"
							title='<s:text name="title.click2select"/>'></li>
					</ul>
				</td>
				<td rowspan="6" class="label" style="text-align: center;vertical-align: top;">
					<img id="portrait" style="width:110px;height:140px;cursor: pointer;" <s:if test="!readonly">title='<s:text name="image.click2change"/>'</s:if>
						src='<s:url value="/bc/image/download"><s:param name='puid' value='e.uid'/><s:param name='ptype' value='%{"portrait"}'/><s:param name='ts' value='ts'/></s:url>'/>
				</td>
			</tr>
			<tr>
				<td class="label">*<s:text name="user.code"/>:</td>
				<td class="value">
					<s:if test='e.id == null'>
						<s:textfield name="e.code" data-validate="required" cssClass="ui-widget-content" />
					</s:if><s:else>
						<s:textfield name="e.code" data-validate="required" cssClass="ui-widget-content" readonly="true" />
					</s:else> 	
				</td>
				<td class="label"><s:text name="user.duty"/>:</td>
				<td class="value">
					<s:select name="e.detail.duty.id" list="duties" listKey="id" listValue="name" value="e.detail.duty.id" cssClass="ui-widget-content"></s:select>
				</td>
			</tr>
			<tr>
				<td class="label">*<s:text name="label.order"/>:</td>
				<td class="value"><s:textfield name="e.orderNo" data-validate='required' cssClass="ui-widget-content"/></td>
				<td class="label"><s:text name="label.phone"/>:</td>
				<td class="value"><s:textfield name="e.phone" data-validate='{"type":"phone","required":false}' cssClass="ui-widget-content"/></td>
			</tr>
			<tr>
				<td class="label"><s:text name="user.card"/>:</td>
				<td class="value"><s:textfield name="e.detail.card" cssClass="ui-widget-content"/></td>
				<td class="label"><s:text name="label.email"/>:</td>
				<td class="value"><s:textfield name="e.email" data-validate='{"type":"email","required":false}' cssClass="ui-widget-content"/></td>
			</tr>
			<tr>
				<td class="label"><s:text name="user.workDate"/>:</td>
				<td class="value relative"><input type="text" name="e.detail.workDate" data-validate="date"
					class="bc-date ui-widget-content" value='<s:date format="yyyy-MM-dd" name="e.detail.workDate" />'/>
					<ul class="inputIcons">
						<li class="selectCalendar inputIcon ui-icon ui-icon-calendar" data-cfg='e.detail.workDate' title='<s:text name="title.click2selectDate"/>'></li>
					</ul>
				</td>
				<td class="label"><s:text name="user.gender"/>:</td>
				<td class="value"><s:radio name="e.detail.sex" list="#{'1':'男','2':'女','0':'不设置'}" 
					value="e.detail.sex" cssStyle="width:auto;"/></td>
			</tr>
			<tr>
				<td class="label"><s:text name="user.comment"/>:</td>
				<td class="value"><s:textfield name="e.detail.comment" cssClass="ui-widget-content"/></td>
				<td class="label"><s:text name="label.status"/>:</td>
				<td class="value"><s:radio name="e.status" list="#{'0':'启用','1':'禁用','2':'已删除'}" 
					value="e.status" cssStyle="width:auto;"/></td>
			</tr>
		</table>
		<!-- 已分派的岗位信息 -->
		<div id="assignGroups" class="formTable2 ui-widget-content"  style="width:650px;"
			data-removeTitle='<s:text name="title.click2remove"/>'>
			<div class="ui-state-active title" style="position:relative;">
				<span class="text"><s:text name="actor.headerLabel.assignGroups"/>：
					<s:if test="%{ownedGroups == null || ownedGroups.isEmpty()}"><s:text name="label.empty"/></s:if>
				</span>
				<s:if test="!readonly">
				<span id="addGroups" class="verticalMiddle ui-icon ui-icon-circle-plus" title='<s:text name="actor.title.click2addGroups"/>'></span>
				</s:if>
			</div>
			<s:if test="%{ownedGroups != null && !ownedGroups.isEmpty()}">
			<ul class="horizontal">
			<s:iterator value="ownedGroups">
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
		<!-- 已分配的角色信息 -->
		<div id="assignRoles" class="formTable2 ui-widget-content"  style="width:650px;"
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
		<div id="inheritRolesFromOU" class="formTable2 ui-widget-content"  style="width:650px;">
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
		<!-- 从已分派岗位间接获取的角色 -->
		<div id="inheritRolesFromGroup" class="formTable2 ui-widget-content"  style="width:650px;">
			<div class="ui-state-active title" style="position:relative;">
				<span class="text"><s:text name="actor.headerLabel.inheritRolesFromGroup"/>：
					<s:if test="%{inheritRolesFromGroup == null || inheritRolesFromGroup.isEmpty()}"><s:text name="label.empty"/></s:if>
				</span>
			</div>
			<s:if test="%{inheritRolesFromGroup != null && !inheritRolesFromGroup.isEmpty()}">
			<ul class="horizontal">
			<s:iterator value="inheritRolesFromGroup">
				<li class="horizontal ui-widget-content ui-corner-all ui-state-disabled" data-id='<s:property value="id" />'>
					<span class="text"><s:property value="name" /></span>
				</li>
			</s:iterator>
			</ul>
			</s:if>	
		</div>
		<s:hidden name="e.pinYin"/>
		<s:hidden name="e.type"/>
		<s:hidden name="e.inner" />
		<s:hidden name="e.uid" />
		<s:hidden name="e.id" />
		<s:hidden name="e.pcode" />
		<s:hidden name="e.pname" />
		<s:hidden name="e.detail.id" />
		<s:hidden name="belongIds" />
		<input type="hidden" name="e.detail.createDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.detail.createDate" />'/>
		<s:hidden name="assignGroupIds" />
		<s:hidden name="assignRoleIds" />
	</s:form>
</div>