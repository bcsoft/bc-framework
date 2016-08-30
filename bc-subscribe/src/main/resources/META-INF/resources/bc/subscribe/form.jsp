<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="subscribe.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/subscribe/save" />'
	data-js='<s:url value="/bc/subscribe/form.js" />,js:bc_identity'
	data-initMethod='bc.subscribeForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="subscribeForm" theme="simple" >
		<div id="formTabs" class="formTabs bc-tabs layout-top ui-widget ui-helper-reset" data-cfg="{height:300}">
	        <s:if test="!e.isNew()">
	        <div class="tabsContainer">
           	 	<div class="slideContainer">
                <ul class="tabs ui-helper-reset">
				    <li class="tab ui-widget-content first active"><a href="#otherFormFields" class="ui-state-default ui-state-active">订阅信息</a></li>
				    <s:if test="e.status != -1">
						<li class="tab ui-widget-content"><a href='<s:url value="/bc/subscribeActors/paging?sid=%{e.id}" />' class="ui-state-default">订阅人</a></li>
					</s:if>
					<li class="tab ui-widget-content"><a href='<s:url value="/bc/operateLogs/paging?pid=%{e.id}&module=%{e.class.getSimpleName()}" />' class="ui-state-default">操作日志</a></li>
                </ul>
            	</div>
	        </div>
	        </s:if>
	        <s:else>
	       	<!-- 新建时不显示页签 -->
	       		<div class="tabsContainer" style="display: none;">
					<div class="slideContainer">
						<ul class="tabs ui-helper-reset">
						<li class="tab ui-widget-content first active"></li>
	       				</ul>
		       		</div>
	       		</div>
	        </s:else>
	        <div class="contentContainer ui-helper-reset ui-widget-content noBottomBorder">
	        <div id="otherFormFields" class="content active">
			<table class="formFields" cellspacing="2" cellpadding="0" style="width:100%;">
				<tr class="widthMarker">
					<td style="width: 80px;">&nbsp;</td>
					<td >&nbsp;</td>
				</tr>
				<tr>
					<td class="label">*<s:text name="subscribe.subject"/>：</td>
					<td class="value">
							<s:textfield name="e.subject" data-validate="required" cssClass="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="label">*<s:text name="subscribe.eventCode"/>：</td>
					<td class="value" >
							<s:textfield name="e.eventCode" data-validate="required" cssClass="ui-widget-content"/>
					</td>
				</tr>
				<tr>
					<td class="label"><s:text name="subscribe.orderNo"/>：</td>
					<td class="value" >
							<s:textfield name="e.orderNo"  cssClass="ui-widget-content"/>
					</td>
				</tr>
				<%-- <tr>
					<td class="topLabel" title="什么是主动订阅：用户自主选择的订阅。" ><s:text name="subscribePersonal.active"/>：</td>
					<td class="value">
						<div class="ui-widget-content" style="position:relative;margin: 0;padding: 0;min-height:19px;margin: 0;font-weight: normal;" >
							<ul class="horizontal ulActors ulActors_active" style="padding:0;overflow:hidden;" data-type="0">
								<s:iterator value="actors" var="a" >
									<s:if test="%{type == 0}">
										<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" 
											data-id='<s:property value="actor.id"/>' 
											style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">
											<span class="text"><s:property value="actor.name"/></span>
											<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title="点击移除"></span>
										</li>
									</s:if>			
								</s:iterator>
							</ul>
						</div>
					</td>
				</tr>
				<tr>
					<td class="topLabel" title="什么是被动订阅：订阅管理员安排的订阅。"><s:text name="subscribePersonal.passive"/>：</td>
					<td class="value" colspan="3">  
						<div class="ui-widget-content" style="position:relative;margin: 0;padding: 0;min-height:19px;margin: 0;font-weight: normal;" >
							<ul class="inputIcons">
							 	<li class="subscribe-addUsers inputIcon ui-icon ui-icon-person" data-type="4" title='点击添加用户'>
							 	<li class="subscribe-addGroups inputIcon ui-icon ui-icon-contact" title='点击添加岗位'>
							 	<li class="subscribe-addUnitOrDepartments inputIcon ui-icon ui-icon-home" title='点击添加单位或部门'>
							</ul>
							<ul class="horizontal ulActors ulActors_passive" style="padding: 0;overflow:hidden;" data-type="1">
								<s:iterator value="actors" var="a" >
									<s:if test="%{type == 1}">
										<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" 
											data-id='<s:property value="actor.id"/>' 
											style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">
											<span class="text"><s:property value="actor.name"/></span>
											<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title="点击移除"></span>
										</li>
									</s:if>			
								</s:iterator>
							</ul>
						</div>
					</td>
				</tr> --%>
	
				<tr>
					<td class="label" colspan="4">
						<div class="formTopInfo">
							<div>状态：<s:property value="%{statusesValue[e.status]}" /></div>
							<div>创建人：<s:property value="e.author.name" /> <s:date name="e.fileDate" format="yyyy-MM-dd HH:mm:ss"/></div>
							<s:if test="%{e.modifier != null}">
							<div>最后修改：<s:property value="e.modifier.name" /> <s:date name="e.modifiedDate" format="yyyy-MM-dd HH:mm:ss"/></div>
							</s:if>
						</div>
					</td>
				</tr>
			</table>
			</div>
			</div>	
		</div>
		<s:hidden name="e.id" />
		<s:hidden name="e.status" />
		<s:hidden name="e.type"  />
		<s:hidden name="e.author.id" />
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>	
	</s:form>
</div>