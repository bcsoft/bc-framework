<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="reportHistory.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/reportHistory/save" />'
	data-js='<s:url value="/bc/report/history/form.js" />'
	data-initMethod='bc.reportHistoryForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="reportHistoryForm" theme="simple" >
		<table  cellspacing="2" cellpadding="0" style="width:36.7em;"  >
			<tbody>
				<tr class="widthMarker">
					<td style="width: 5.7em;"></td>
					<td style="width: 11.7em;"></td>
					<td style="width: 5.7em;"></td>
					<td >&nbsp;</td>
				</tr>
				<tr>
					<td class="label"><s:text name="report.category"/>:</td>
					<td class="value" colspan="3" ><s:textfield name="e.category" cssClass="ui-widget-content" readonly="true" /></td>
				</tr>
				<tr>
					<td class="label"><s:text name="reportHistory.subject"/>:</td>
					<td class="value" colspan="3" ><s:textfield name="e.subject" cssClass="ui-widget-content" readonly="true" /></td>
				</tr>
				<tr>
					<td class="label"><s:text name="reportHistory.path"/>:</td>
					<td class="value" colspan="3" >
						<div style="position: relative;display:block;" >
							<s:textfield name="e.path"  readonly="true" cssClass="ui-widget-content" />
							<ul class="inputIcons" style="padding-right:6px;">
								<li class="inputIcon ui-icon ui-icon-arrowthickstop-1-s" title='<s:text name="reportHistory.download"/>' id="reportHistoryDownLoad">
								<li class="inputIcon ui-icon ui-icon-lightbulb" title='<s:text name="reportHistory.inline"/>' id="reportHistoryInline">
							</ul>
						</div>
				    </td>
				</tr>
				<tr>
					<td class="label"><s:text name="reportHistory.source"/>:</td>
					<td class="value">
						<s:textfield name="e.sourceType" readonly="readonly" class="ui-widget-content" />
					</td>
					<td class="label"><s:text name="reportHistory.sourceId"/>:</td>
					<td class="value" style="padding-right:5px">
						<s:textfield name="e.sourceId" cssClass="ui-widget-content" readonly="true" />
					</td>
				</tr>
				<tr>
					<td class="label"><s:text name="report.status"/>:</td>
					<td class="value">
						<s:if test="%{e.success}">
							<s:text name="reportHistory.status.success"/>
						</s:if><s:else>
							<s:text name="reportHistory.status.lost"/>
						</s:else>
					</td>
					<td class="label" colspan="2">
						<div class="formTopInfo">
							<s:date name="e.fileDate" format="yyyy-MM-dd HH:mm:ss"/> 由
							<s:if test="%{e.sourceType=='用户生成'}">
								<s:property value="e.author.name" />
							</s:if><s:elseif test="%{e.sourceType=='报表任务'}">
									报表任务
							</s:elseif><s:else>
								<s:property value="e.sourceType" />	
							</s:else>
							生成
						</div>
					</td>
				</tr>
				<tr id="idReportMsgError">
					<td class="topLabel"><s:text name="reportHistory.msg.error"/>:</td>
					<td class="value" colspan="3">
						<s:textarea name="e.msg"  cssClass="ui-widget-content" style="overflow-y: visible;height:170px;"/>
					</td>
				</tr>
			</tbody>
		</table>
		<s:hidden name="e.id" />
		<s:hidden name="e.taskId" />
		<s:hidden name="e.author.id" />
		<s:hidden name="e.success" />
	</s:form>
</div>