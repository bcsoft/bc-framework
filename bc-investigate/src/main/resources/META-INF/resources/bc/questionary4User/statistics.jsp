<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="questionary.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/questionary4User/save" />'
	data-js='js:bc_identity,<s:url value="/bc/questionary4User/statistics.js"/>'
	data-initMethod='bc.questionary4StatisticsForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="questionary4StatisticsForm" theme="simple" cssStyle="width:630px;">
		<table class="formFields ui-widget-content" cellspacing="2" cellpadding="0">
			<tbody>
				<tr class="widthMarker">
					<td style="width:40%;">&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td style="text-align: center;font-size: 30px;" title="${e.subject}" colspan="2"><s:property value="e.subject"/></td>
				</tr>
				<tr>
				<td style="font-weight: normal;text-align: left;padding-left:15px;width: 50%;">总分:<s:property value="%{totalScore()}"/>
					&nbsp;&nbsp;得分:<s:property value="%{score4User}"/><s:if test="%{getIsNeedGrade()}"><span style="color: red;">(未完全评分)</span></s:if>
					&nbsp;&nbsp;答卷人数:<s:property value="%{getJoinCount()}"/>
				</td>
				<td style="position: relative;text-align: right;">答卷期限：<s:date name="e.startDate" format="yyyy-MM-dd"/>~<s:date name="e.endDate" format="yyyy-MM-dd"/> </td>
				</tr>
			</tbody>
		</table>
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
							<s:set name="answerItem" value="null"/>
							<s:iterator value="e.responds" var="r" >
								<s:if test="%{author.id==userId}">
									<s:iterator value="answers" var="a">
										<s:if test="%{#c.id==item.id}">
											<s:set name="answerItem" value="1"/>
										</s:if>
									</s:iterator>
								</s:if>
							</s:iterator>
							<s:if test="#answerItem==1">
								<tr class="option"><s:property value="%{e.responds.iterator().next().answers.iterator().next().qid}"/>
									<td class="value" style="padding-left: 30px;" title="${standard ? '标准答案':''}">
										<div style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;display: inline-block;border: 0;"
											class="${standard ? 'ui-state-default':''}">
											<s:radio cssClass="standard" name="%{'standard'+#b.orderNo}" value="true" list="#{'true':''}" cssStyle="width:auto;width:1em;" />
											<s:property value="subject"/>
										</div>
									</td>
									<td><span class="respond"><s:property value="%{getQuestItemRespondCount(id)}" escapeHtml="false"/></span>
										&nbsp;<div class="progressbar" style="height: 15px;width: 70%;display: inline-block;"></div>
										&nbsp;<span class="count"><s:property value="%{getJoinCount()}" escapeHtml="false"/></span>
									</td>
								</tr>
							</s:if><s:else>
								<tr class="option"><s:property value="%{e.responds.iterator().next().answers.iterator().next().qid}"/>
									<td class="value" style="padding-left: 30px;" title="${standard ? '标准答案':''}">
										<div style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;display: inline-block;border: 0;" 
											class="${standard ? 'ui-state-default':''}" >
											<s:radio cssClass="standard" name="%{'standard'+#b.orderNo}"  list="#{'true':''}" cssStyle="width:auto;width:1em;" />
											<s:property value="subject"/>
										</div>
									</td>
									<td><span class="respond"><s:property value="%{getQuestItemRespondCount(id)}" escapeHtml="false"/></span>
										&nbsp;<div class="progressbar" style="height: 15px;width: 70%;display: inline-block;"></div>
										&nbsp;<span class="count"><s:property value="%{getJoinCount()}" escapeHtml="false"/></span>
									</td>
								</tr>
							</s:else>
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
							<s:set name="answerItem" value="null"/>
							<s:iterator value="e.responds" var="r" >
								<s:if test="%{author.id==userId}">
									<s:iterator value="answers" var="a">
										<s:if test="%{#c.id==item.id}">
											<s:set name="answerItem" value="1"/>
										</s:if>
									</s:iterator>
								</s:if>
							</s:iterator>
							<s:if test="#answerItem==1">
								<tr class="option">
									<td class="value" style="padding-left: 30px;" title="${standard ? '标准答案':''}">
										<div style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;display: inline-block;border: 0;"
											class="${standard ? 'ui-state-default':''}">
											<s:checkbox cssClass="standard" name="%{'standard'+#b.orderNo}" value="true" cssStyle="width:1em;"/>
											<s:property value="subject" />
										</div>
										&nbsp;&nbsp;&nbsp;<span class="ui-priority-secondary"><s:property value="%{score}"></s:property>分</span>
									</td>
									<td><span class="respond"><s:property value="%{getQuestItemRespondCount(id)}" escapeHtml="false"/></span>
										&nbsp;<div class="progressbar" style="height: 15px;width: 70%;display: inline-block;"></div>
										&nbsp;<span class="count"><s:property value="%{getJoinCount()}" escapeHtml="false"/></span>
									</td>
								</tr>
							</s:if><s:else>
								<tr class="option"><s:property value="%{e.responds.iterator().next().answers.iterator().next().qid}"/>
									<td class="value" style="padding-left: 30px;" title="${standard ? '标准答案':''}">
										<div style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;display: inline-block;border: 0;" 
											class="${standard ? 'ui-state-default':''}" >
											<s:checkbox cssClass="standard" name="%{'standard'+#b.orderNo}" cssStyle="width:1em;"/>
											<s:property value="subject" />
										</div>
										&nbsp;&nbsp;&nbsp;<span class="ui-priority-secondary"><s:property value="%{score}"></s:property>分</span>
									</td>
									<td><span class="respond"><s:property value="%{getQuestItemRespondCount(id)}" escapeHtml="false"/></span>
										&nbsp;<div class="progressbar" style="height: 15px;width: 70%;display: inline-block;"></div>
										&nbsp;<span class="count"><s:property value="%{getJoinCount()}" escapeHtml="false"/></span>
									</td>
								</tr>
							</s:else>
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
							<s:property value="%{formatCompletionValue(items.iterator().next().subject,items.iterator().next().config,items.iterator().next().id)}" escapeHtml="false"/>
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
			               		&nbsp;<s:if test="%{isAlreadyScore(items.iterator().next().id)}"><span style="color: red;">(未评分)</span></s:if>
			               		&nbsp;(<s:property value="%{score}"></s:property>分)
			               	</td>
						</tr>
						<tr class="option">
							<td class="value" style="padding-left: 30px;">
								<textarea name="subject" placeholder='<s:property value="%{items.iterator().next().subject}"/>' rows="3" 
									class="ui-widget-content noresize"><s:property value="%{formatJQuizValue(items.iterator().next().id)}"/></textarea>
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
				归档人：<s:property value="e.pigeonholer.name" />(<s:date name="e.pigeonholeDate" format="yyyy-MM-dd HH:mm:ss"/>)<br/>
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