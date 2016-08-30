<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="share.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/netdiskFile/save" />'
	data-js='<s:url value="/bc/netdiskFile/form.js" />,<s:url value="/bc/identity/identity.js" />'
	data-initMethod="bc.netdiskFileForm.init"
	data-option='{<s:if test="isEditVisitorsAuthority">
		"buttons":[{"text":"<s:text name="label.ok"/>","click":"bc.netdiskFileForm.clickOk4Share"}],
		</s:if>"width":420,"height":520,"minWidth":320,"minHeight":200,"modal":false,minimizable:false,maximizable:false}'
	style="overflow-y:auto;">
	<s:form name="netdiskFileForm" theme="simple">
		<table class="formFields" cellspacing="2" cellpadding="0">
				<tr class="widthMarker">
	                <td style="width: 90px;">&nbsp;</td>
	                <td >&nbsp;</td>
                </tr>
				<tr>
					<td style="padding-left: 5px;"><s:text name="share.file"/>:</td>
					<td class="value">
						<s:label style="width:100%;height:100%;border:none;margin:0;padding:0 0 0 2px;" type="text" class="ui-widget-content"
							 	value="%{e.name}"/>
					</td>
				</tr>
				<tr>
					<td style="padding-left: 5px;"><s:text name="share.title"/>:</td>
					<td class="value" >
						&nbsp;
					</td>
				</tr>
		</table>
		<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;">
				<tr class="widthMarker">
	                <td style="width: 20px;">&nbsp;</td>
	                <td >&nbsp;</td>
                </tr>
				<tr>
					<td ><s:radio name="e.editRole" list="#{'0':''}" cssStyle="width:auto;" disabled="%{!isEditRole}"/></td>
					<td class="value" >编辑者可以添加访问者和更改权限</td>
				</tr>
				<tr>
					<td >&nbsp;</td>
					<td class="value ui-state-disabled">
						编辑者对于添加和删除访问者以及更改其访问权限具有完全控制权
					</td>
				</tr>
				<tr>
					<td ><s:radio name="e.editRole" list="#{'1':''}" cssStyle="width:auto;" disabled="%{!isEditRole}"/></td>
					<td class="value" >只有拥有者才能更改权限</td>
				</tr>
				<tr>
					<td >&nbsp;</td>
					<td class="value ui-state-disabled">
						编辑者无法添加或删除访问者，或者更改其访问权限
					</td>
				</tr>
		</table>
		<!-- 访问人 -->
		<div class="ui-widget-content" style="border-left-width:0;border-right-width:1px;">
			<div class="ui-widget-header title" style="position:relative;">
				<span class="text" style="padding-left: 5px;">谁有权访问：</span>
				<s:if test="%{isEditVisitorsAuthority}">
					<ul class="inputIcons">
						<li id="addVisitor" class="inputIcon ui-icon ui-icon-circle-plus"
							title='<s:text name="title.click2AddVisitor"/>'></li>
						<li id="deleteVisitor" class="inputIcon ui-icon ui-icon-circle-close"
							title='<s:text name="title.click2DeleteVisitor"/>'></li>
					</ul>
				</s:if>
			</div>
	    	<div class="bc-grid header">
			<table class="table" id="visitorTables" cellspacing="0" cellpadding="0" style="width: 100%">
				<tr class="ui-state-default row">
					<td class="first" style="width: 15px;" title="点击全选!"><span class="ui-icon ui-icon-notice"></span></td>
					<td class="middle" style="width: 80px;height: 20px;">访问者</td>
					<td class="middle" style="width: 120px;height: 20px;">访问权限</td>
					<td class="middle" style="width: 80px;height: 20px;">添加者</td>
					<td class="last" >添加时间</td>
				</tr>
				<s:iterator var="b" value="e.fileVisitors">
				<tr class="ui-widget-content row" data-id='<s:property value="id"/>'>
					<td class="id first" style="padding:0;text-align:left;"><span class="ui-icon"></span>
						<input style="width:90%;height:100%;border:none;margin:0;padding:0 0 0 2px;background:none;" readonly="readonly" 
							type="text" class="ui-widget-content" />
					</td>
					<td class="middle" style="padding:0;text-align:left;">
						<input style="width:100%;height:100%;border:none;margin:0;padding:0 0 0 2px;"type="text" class="ui-widget-content" 
							name="visitor" value='<s:property value="%{getVisitorName(aid)}"/>' data-id='<s:property value="aid"/>' readonly="true"/>
					</td>
					<td class="middle" style="padding:0;text-align:left;">
						<s:checkbox name="edit" cssStyle="width:1em;" value="%{haveAuthority(role,0)}" disabled="%{!isEditVisitorsAuthority}"/><label>编辑</label>	
						<!--  				
						<s:checkbox name="check" cssStyle="width:1em;" value="%{haveAuthority(role,1)}" /><label>查看</label>					
						<s:checkbox name="discuss" cssStyle="width:1em;" value="%{haveAuthority(role,2)}" /><label>评论</label>	
						-->
					</td>
					<td class="middle" style="padding:0;text-align:left;">
						<input style="width:100%;height:100%;border:none;margin:0;padding:0 0 0 2px;"type="text" class="ui-widget-content" 
							name="authorId" value='<s:property value="%{getAuthorName(authorId)}"/>' data-id='<s:property value="authorId"/>' readonly="true"/>
					</td>
					<td class="last" style="padding:0;text-align:left;">
						<input style="width:100%;height:100%;border:none;margin:0;padding:0 0 0 2px;"type="text" class="ui-widget-content" 
							name="fileDate" value='<s:date name="fileDate" format="yyyy-MM-dd"/>' readonly="true"/>
					</td>
				</tr>
				</s:iterator>
			</table>
			</div>
		</div>
		<s:hidden name="e.id" />
	</s:form>
</div>