<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="accessControl.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/accessControl/save" />'
	data-js='<s:url value="/bc/acl/control/form.js" />,<s:url value="/bc/identity/identity.js" />'
	data-initMethod='bc.accessControlForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="accessControlForm" theme="simple" >
		<table  cellspacing="2" cellpadding="0" style="width:100%;" >
			<tbody>
				<tr class="widthMarker">
					<td style="width: 8em;"></td>
					<td >&nbsp;</td>
				</tr>
				<s:if test="%{isFromDoc}">
					<td class="label">要监控的对象:</td>
					<td class="value"><s:text name="e.docName"/></td>
				</s:if><s:else>
					<tr>
						<td class="label">*<s:text name="accessControl.docName"/>:</td>
						<td class="value"><s:textfield name="e.docName" cssStyle="ui-webgit-content" data-validate="required" /></td>
					</tr>
					<tr>
						<td class="label">*<s:text name="accessControl.docType"/>:</td>
						<td class="value">
							<s:select name="e.docType" list="categoryList" listKey="key" listValue="value" headerKey="" headerValue="" cssClass="ui-widget-content"></s:select>
						</td>
					</tr>
					<tr>
						<td class="label">*<s:text name="accessControl.docId"/>:</td>
						<td class="value"><s:textfield name="e.docId" cssStyle="ui-webgit-content" data-validate="required" /></td>
					</tr>
				</s:else>
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
		
		<!-- 访问人 -->
		<div class="ui-widget-content" style="border-bottom:0;">
			<div class="ui-widget-header title" style="position:relative;">
				<span class="text" style="padding-left: 5px;">谁有权访问：</span>
				<ul class="inputIcons">
					<li id="up" class="inputIcon ui-icon ui-icon-circle-arrow-n" title='上移选中项'></li>
					<li id="down" class="inputIcon ui-icon ui-icon-circle-arrow-s" title='下移选中项'></li>
					<li id="add" class="inputIcon ui-icon ui-icon-circle-plus" title='添加访问者'></li>
					<li id="delete" class="inputIcon ui-icon ui-icon-circle-close" title='删除访问者'></li>
				</ul>
			</div>
	    	<div class="bc-grid header">
			<table class="table" id="actorTables" cellspacing="0" cellpadding="0" style="width:100%">
				<tr class="ui-state-default row">
					<td class="first" style="width: 1em;"  title="点击全选或反选"><span class="ui-icon ui-icon-notice"></span></td>
					<td class="middle" style="width: 6em;">访问者</td>
					<td class="last"><s:text name="accessControl.role"/></td>
				</tr>
				<s:iterator var="a" value="accessActor4List">
					<tr class="ui-widget-content row">
						<td class="id first" ><span class="ui-icon"></span></td>
						<td class="middle" >
							<input type="text" class="access_actor" style="margin:0;border:none;background:none;" 
								 value='<s:property value="actor.name"/>' data-id='<s:property value="actor.id"/>' readonly="readonly"/>
						</td>
						<td class="last" >
							<s:if test='%{isFromDoc&&showRole == "01"}'>
								<label><input type="checkbox" class="actor_checkbox" checked onclick="return false;">查阅</label><input type="hidden" class="actor_checkbox">
							</s:if><s:else>
								<label><input type="checkbox" class="actor_checkbox" checked onclick="return false;">查阅</label>&nbsp;<label><input type="checkbox" class="actor_checkbox">编辑</label>
							</s:else>
							<input type="hidden" class="actor_role" value='<s:property value="role"/>'/>
						</td>
					</tr>
				</s:iterator>
			</table>
			</div>
		</div>
		
		<s:hidden name="e.id" />
		<s:hidden name="e.author.id" />
		<s:hidden name="isFromDoc" />
		<s:if test="%{isFromDoc}">
			<s:hidden name="e.docId" />
			<s:hidden name="e.docType" />
			<s:hidden name="e.docName" />
			<s:hidden name="showRole" />
		</s:if>
		
		<input type="hidden" name="accessActors" />	
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>	
	</s:form>
</div>