<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="questionary.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/questionary/save" />'
	data-js='js:bc_identity,<s:url value="/bc/questionary/form.js"/>'
	data-initMethod='bc.questionaryForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="questionaryForm" theme="simple" cssStyle="width:630px;">
		<div id="testArea">
			<table class="formFields" cellspacing="2" cellpadding="0">
				<tbody>
					<tr class="widthMarker">
						<td style="width:40%;">&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td style="text-align: center;font-size: 30px;" title="${e.subject}" colspan="2"><s:property value="e.subject"/></td>
					</tr>
					<tr>
						<td style="font-weight: normal;text-align: left;padding-left:15px;width: 40%;">总分:<s:property value="%{totalScore()}"/>
							&nbsp;答卷人数:<s:property value="%{getJoinCount()}"/>
							&nbsp;&nbsp;<s:a id="checkRespond" href="#">答卷人</s:a>
						</td>
						<td style="font-weight: normal;position: relative;text-align: right;">答卷期限：<s:date name="e.startDate" format="yyyy-MM-dd"/>~
							<s:date name="e.endDate" format="yyyy-MM-dd"/> 
						</td>
					</tr>
					<tr>
						<td style="font-weight: normal;text-align: left;padding-left:15px;" colspan="2">
							<s:text name="questionary.actors"/>:
							<s:if test="%{ownedUsers != null && !ownedUsers.isEmpty()}">
								<s:iterator value="ownedUsers">
								<span class="text" ><s:property value="['name']" /></span>&nbsp;
								</s:iterator>
							</s:if>
							<s:else>
								<span>所有用户</span>
							</s:else>	
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div id="testArea">
			<s:iterator var="b" value="e.questions">
			<!-- 单选题 -->
			<s:if test="type==0">
				<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;" data-type="<s:property value='type'/>">
					<tbody>
						<tr class="widthMarker">
							<td style="width: 380px;">&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
						<tr>
			               	<td style="font-weight: normal;text-align: left;padding-left:15px;"><span style="color: red;"><s:property value="orderNo"/>.</span>
			               		<s:property value="subject"/>
			               		&nbsp;<s:if test="required==true"><span style="color: red;">(必选)</span></s:if>
			               		&nbsp;(<s:property value="%{score}"></s:property>分)
		               		</td>
		               		<td>&nbsp;</td>
						</tr>
						<s:iterator var="c" value="items" >
							<tr class="option">
								<td class="value" style="padding-left: 30px;">
									<div style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;display: inline-block;">
										<s:radio cssClass="standard" name="%{'standard'+#b.orderNo}" value="%{standard}" list="#{'true':''}" cssStyle="width:auto;width:1em;"/>
										<s:property value="subject"/>
									</div>
								</td>
								<td><span class="respond"><s:property value="%{getQuestItemRespondCount(id)}" escapeHtml="false"/></span>
									&nbsp;<div class="progressbar" style="height: 15px;width: 70%;display: inline-block;"></div>
									&nbsp;<span class="count"><s:property value="%{getJoinCount()}" escapeHtml="false"/></span>
								</td>
							</tr>
						</s:iterator>
						</tbody>
					</table>
				</s:if><s:elseif test="type==1">
				<!-- 多选 -->
				<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;" data-type="<s:property value='type'/>">
					<tbody>
						<tr class="widthMarker">
							<td style="width: 380px;">&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
						<tr>
			               	<td style="font-weight: normal;text-align: left;padding-left:15px;"><span style="color: red;"><s:property value="orderNo"/>.</span>
			               		<s:property value="subject"/>
			               		&nbsp;<s:if test="required==true"><span style="color: red;">(必选)</span></s:if>
			               		&nbsp;<s:if test="seperateScore==true"><span style="color: red;">(全对方得分)</span></s:if>
			               		&nbsp;(<s:property value="%{score}"></s:property>分)
		               		</td>
		               		<td>&nbsp;</td>
						</tr>
						<s:iterator var="c" value="items">
						<tr class="option">
							<td class="value" style="padding-left: 30px;">
								<div style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;display: inline-block;">
									<s:checkbox cssClass="standard" name="%{'standard'+#b.orderNo}" value="%{standard}" cssStyle="width:1em;"/>
									<s:property value="subject" />&nbsp;&nbsp;&nbsp;<span class="ui-priority-secondary">(<s:property value="%{score}"></s:property>分)</span>
								</div>
							</td>
							<td><span class="respond"><s:property value="%{getQuestItemRespondCount(id)}" escapeHtml="false"/></span>
								&nbsp;<div class="progressbar" style="height: 15px;width: 70%;display: inline-block;"></div>
								&nbsp;<span class="count"><s:property value="%{getJoinCount()}" escapeHtml="false"/></span>
							</td>
						</tr>
						</s:iterator>
						</tbody>
					</table>
				</s:elseif><s:elseif test="type==2">
				<!-- 填空 -->
				<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;" data-type="<s:property value='type'/>">
					<tbody>
						<tr>
			               	<td style="font-weight: normal;text-align: left;padding-left:15px;"><span style="color: red;"><s:property value="orderNo"/>.</span>
			               		<s:property value="subject"/>
			               		&nbsp;<s:if test="required==true"><span style="color: red;">(必答)</span></s:if>
			               		&nbsp;(<s:property value="%{score}"></s:property>分)
		               		</td>
						</tr>
						<tr class="option">
							<td class="value" style="padding-left: 30px;">
							<s:property value="%{formatCompletionValue(items.iterator().next().subject,items.iterator().next().config)}" escapeHtml="false"/>
							</td>
						</tr>
						</tbody>
					</table>
				</s:elseif><s:elseif test="type==3">
				<!-- 简答 -->
				<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;" data-type="<s:property value='type'/>">
					<tbody>
						<tr>
			               	<td style="font-weight: normal;text-align: left;padding-left:15px;"><span style="color: red;"><s:property value="orderNo"/>.</span>
			               		<s:property value="subject"/>
			               		&nbsp;<s:if test="required==true"><span style="color: red;">(必答)</span></s:if>
			               		&nbsp;<s:if test="grade==true"><span style="color: red;">(需要评分)</span></s:if>
			               		&nbsp;(<s:property value="%{score}"></s:property>分)
			               	</td>
						</tr>
						<tr class="option">
							<td class="value" style="padding-left: 30px;">
								<textarea name="subject" placeholder='<s:property value="%{items.iterator().next().subject}"/>' rows="3" class="ui-widget-content noresize"></textarea>
							</td>
						</tr>
						</tbody>
					</table>
				</s:elseif>
				</s:iterator>
			</div>
			<div class="formTopInfo">
				状态：<s:property value="%{statusesValue[e.status]}" />,登记：<s:property value="e.author.name" />(<s:date name="e.fileDate" format="yyyy-MM-dd HH:mm:ss"/>)<br/>
				<s:if test="%{e.modifier != null}">
				最后修改：<s:property value="e.modifier.name" />(<s:date name="e.modifiedDate" format="yyyy-MM-dd HH:mm:ss"/>)<br/>
				</s:if>
				<s:if test="%{e.issuer!=null}">
				发布人：<s:property value="e.issuer.name" />(<s:date name="e.issueDate" format="yyyy-MM-dd HH:mm:ss"/>)<br/>
				</s:if>
				<s:if test="%{e.pigeonholer!=null}">
				归档人：<s:property value="e.pigeonholer.name" />(<s:date name="e.pigeonholeDate" format="yyyy-MM-dd HH:mm:ss"/>)
				</s:if>
			</div>
		<s:hidden name="e.id"/>
		<s:hidden name="e.author.id"/>
		<s:hidden name="e.type"/>
		<s:hidden name="topics"/>
		<s:hidden name="e.status"/>
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>
		
	</s:form>
</div>