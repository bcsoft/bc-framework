<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="%{title}"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/netdiskFile/save" />'
	data-js='<s:url value="/bc/netdiskFile/form.js" />,<s:url value="/bc/netdiskFile/folder.js" />,<s:url value="/bc/identity/identity.js" />'
	data-initMethod='bc.netdiskFileForm.init'
	data-option='{
		"buttons":[{"text":"<s:text name="label.ok"/>","click":"bc.netdiskFileForm.save"}],
		"width":300,"minWidth":320,"minHeight":200,"modal":false,
		minimizable:false,maximizable:false}' style="overflow-y:auto;">
	<s:form name="netdiskFileForm" theme="simple">
		<div class="formFields ui-widget-content" >
			<table class="formFields" cellspacing="2" cellpadding="0">
				<tbody>
					<tr class="widthMarker">
		                <td style="width: 80px;">&nbsp;</td>
		                <td >&nbsp;</td>
	                </tr>
					<tr>
						<td class="label" >*<s:text name="netdisk.folderName"/>:</td>
						<td class="value" >
							<s:textfield name="e.name" data-validate="required" cssClass="ui-widget-content"/>
						</td>
					</tr>
					<tr>
						<td class="label" ><s:text name="netdisk.SubordinateFolder"/>:</td>
						<td class="value" style="position:relative;display: block;"><s:textfield name="folder"
				    		 cssClass="ui-widget-content" readonly="true" />
				    		<span id="selectFolder" style="position:absolute;top:50%;margin-top:-8px;width:16px;height:16px;right:22px;cursor:pointer;" 
				    			class="selectButton ui-icon ui-icon-circle-plus" title='<s:text name="title.click2select"/>'></span>
				    		<span id="clearFolder" style="position:absolute;top:50%;margin-top:-8px;width:16px;height:16px;right:8px;cursor:pointer;"
				    			class="selectButton verticalMiddle ui-icon ui-icon-circle-close" title='<s:text name="title.click2clear"/>'></span>
				    	</td>
					</tr>
					<tr>
						<td class="label" ><s:text name="netdisk.order"/>:</td>
						<td class="value" >
							<s:textfield name="e.order" cssClass="ui-widget-content"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<s:hidden name="e.id" />
		<s:hidden name="e.author.id" />
		<s:hidden name="e.type" />
		<s:hidden name="e.status" />
		<s:hidden name="e.size" />
		<s:hidden name="e.folderType" />
		<s:hidden name="e.pid" />
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>
	</s:form>
</div>