<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div data-type='form' class="bc-page"
	data-js='<s:url value="/bc/email/manage/formr.js" />,js:redactor_css'
	data-initMethod='bc.email2ManageFormr.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;cursor: default;">
	<s:form name="emailManageFormr" theme="simple" style="margin:10px;" >
		<div tabindex="-1" class="email-subject" style='text-align:left;font-size:20px;outline:0;color:black;margin-bottom:8px;position:relative;'>
			<s:property value="e.subject" escapeHtml="false"/>
			<ul class="inputIcons email-open">
				<li class="emailFormr-fromNow inputIcon" style="width:auto;height:atuo;font-size:14px;cursor:default;font-weight:normal;margin-right:3px;"></li>
			</ul>
		</div>
		<table cellspacing="0" cellpadding="0" style="width:100%;">
			<tr class="widthMarker">
				<td style="width: 80px;">&nbsp;</td>
				<td >&nbsp;</td>
			</tr>
			<tr>
				<td class="label"><s:text name="email.sender"/>：</td>
				<td class="value"><s:property value="e.sender.name" /></td>
			</tr>
			
			<s:if test="%{receiverList != null}">
				<tr>
					<td class="topLabel"><s:text name="email.receiver"/>：</td>
					<td class="value">
						<s:iterator var="t" value="receiverList" status="st">
							<s:if test="%{#st.index > 0}">、</s:if>
							<s:property value="receiver.name" />
							<s:if test="%{upper != null}">
								[<s:property value="upper.name" />]
							</s:if>
							<s:if test="%{reciverReadCount == 0}">
								(已阅<b><s:property value="reciverReadCount" /></b>次)
							</s:if><s:else>
								(已阅<s:property value="reciverReadCount" />次)
							</s:else>
						</s:iterator>
					</td>
				</tr>
			</s:if>
			<s:if test="%{ccList != null}">
				<tr>
					<td class="topLabel">抄&nbsp;&nbsp;&nbsp;送：</td>
					<td class="value">
						<s:iterator var="t" value="ccList" status="st">
							<s:if test="%{#st.index > 0}">、</s:if>
							<s:property value="receiver.name" />
							<s:if test="%{upper != null}">
								[<s:property value="upper.name" />]
							</s:if>
							<s:if test="%{reciverReadCount == 0}">
								(已阅<b><s:property value="reciverReadCount" /></b>次)
							</s:if><s:else>
								(已阅<s:property value="reciverReadCount" />次)
							</s:else>
						</s:iterator>
					</td>
				</tr>
			</s:if>
			<s:if test="%{bccList != null }">
				<tr>
					<td class="topLabel">密&nbsp;&nbsp;&nbsp;送：</td>
					<td class="value">
						<s:iterator var="t" value="bccList" status="st">
							<s:if test="%{#st.index > 0}">、</s:if>
							<s:property value="receiver.name" />
							<s:if test="%{upper != null}">
								[<s:property value="upper.name" />]
							</s:if>
							<s:if test="%{reciverReadCount == 0}">
								(已阅<b><s:property value="reciverReadCount" /></b>次)
							</s:if><s:else>
								(已阅<s:property value="reciverReadCount" />次)
							</s:else>
						</s:iterator>
					</td>
				</tr>
			</s:if>

			<tr>
				<td class="label">日&nbsp;&nbsp;&nbsp;期：</td>
				<td class="value"><s:date format="yyyy-MM-dd HH:mm" name="e.sendDate" />&nbsp;&nbsp;(<s:property value="week4cn" />)</td>
			</tr>
		</table>
		
		<s:if test="%{e.content.length() > 0}">
			<div class="ui-widget-content" style="border-width: 0 0 1px 0;height:1px;margin-top:10px;"></div>
			<div class="email-content redactor_editor unselectable" unselectable="on" style='font-weight: normal;'>
				<s:property value="e.content" escapeHtml="false"/>
			</div>
		</s:if>
		
		<div class="ui-widget-content " style="border-width: 0 0 1px 0;height:1px;"></div>
		<s:property value="attachsUI" escapeHtml="false"/>
		<s:hidden name="e.id" />
		<input type="hidden" name="e.sendDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.sendDate" />'/>
	</s:form>
</div>