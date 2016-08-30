<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="questionary.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/questionary/save" />'
	data-js='js:bc_identity,<s:url value="/bc/questionary/form.js"/>'
	data-initMethod='bc.questionaryForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="questionaryForm" theme="simple" cssStyle="width:630px;">
		<div id="div1">
			<table class="formFields" cellspacing="2" cellpadding="0">
				<tbody>
					<tr class="widthMarker">
						<td style="width: 100px;">&nbsp;</td>
						<td >&nbsp;</td>
					</tr>
					<tr>
		               	<td class="label">*<s:text name="questionary.subject"/>:</td>
						<td class="value"><s:textfield name="e.subject" data-validate="required" cssClass="ui-widget-content"/></td>
					</tr>
					<tr>
		               	<td class="label">*<s:text name="questionary.Deadline"/>:</td>
						<td class="value" >
							<div style="position : relative; display: inline-block">
								&nbsp;从<input type="text" name="e.startDate"  data-validate='{"type":"date","required":true}' 
									value='<s:date format="yyyy-MM-dd" name="e.startDate" />' 
									style="width: 7em;" class="bc-date ui-widget-content" />
									<ul class="inputIcons" style="right : 0px;">
										<li class="selectCalendar inputIcon ui-icon ui-icon-calendar" data-cfg='e.startDate' ></li>
									</ul>
							</div>
							<div style="position : relative; display: inline-block">
								&nbsp;到<input type="text" name="e.endDate" data-validate='{"type":"date","required":true}'
									value='<s:date format="yyyy-MM-dd" name="e.endDate" />'
									style="width: 7em;" class="bc-date ui-widget-content" />
									<ul class="inputIcons" style="right : 0px;">
										<li class="selectCalendar inputIcon ui-icon ui-icon-calendar" data-cfg='e.endDate' ></li>
									</ul>
							</div>
							<div style="position:relative;right:-120px; display: inline-block;">
						       <s:checkbox name="e.permitted" cssStyle="width:1em;" />
						       <s:text name="questionary.permitted" />
							</div>
						</td>
					</tr>
					<!-- 限制参与人为-->
					<tr>
						<td class="topLabel"><s:text name="questionary.actors"/>:</td>
						<td class="value relative" >
							<div id="assignUsers" style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;font-weight: normal;width: 98%;" class="ui-widget-content" 
								data-removeTitle='<s:text name="title.click2remove"/>'>
								<ul class="inputIcons" style="top:10px">
									 	<li class="inputIcon ui-icon ui-icon-person" title='<s:text name="group.title.click2addUsers"/>' id="addUsers">
									 	<li class="inputIcon ui-icon ui-icon-contact" title='<s:text name="actor.title.click2addGroups"/>' id="addGroups">
									 	<li class="inputIcon ui-icon ui-icon-home" title='<s:text name="questionary.title.addUnitOrDepartment"/>' id="addUnitOrDepartments">
								</ul>
								<s:if test="%{ownedUsers != null && !ownedUsers.isEmpty()}">
									<ul class="horizontal reportUserUl" style="padding: 0 50px 0 0;">
									<s:iterator value="ownedUsers">
									<li class="horizontal reportUserLi" style="position: relative;margin:0 2px;float: left;padding: 0;"
										data-id=<s:property value="['id']"/>>
									<span class="text" ><s:property value="['name']" /></span>
									<s:if test="!isReadonly()">
										<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title='<s:text name="title.click2remove"/>'></span>
									</s:if>
									</li>
									</s:iterator>
									</ul>
								</s:if>	
							</div>					
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div id="testArea">
			<s:if test="%{e.isNew()}">
			<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;">
				<tbody>
					<tr class="widthMarker">
						<td style="width: 40px;">&nbsp;</td>
						<td style="width: 60px;">&nbsp;</td>
						<td >&nbsp;</td>
					</tr>
					<tr>
						<td style="font-weight: normal;text-align: left;">第<span style="color: red;">1</span>题</td>
		               	<td style="font-weight: normal;text-align: right;">题型:</td>
						<td class="value"><div class="ui-widget-content" style="display: inline-block;border-width: 0 1px 0 0;padding-right: 2px;">
							<s:checkbox name="required" cssStyle="width:1em;" value="true" cssClass="required"/><label>必选题</label></div>
							<s:radio cssClass="type" name="type1" value="0" list="#{'0':'单选','1':'多选','2':'填空','3':'问答题'}" cssStyle="width:auto;"/>
							<!--<div class="ui-widget-content" style="display: inline-block;border-width: 0 1px 0 1px;padding: 0 2px 0 2px;">
							<s:checkbox name="seperateScore" cssStyle="width:1em;" cssClass="seperateScore"/><label>全对方有分</label></div>
							<div style="position:relative;right:-100px; display: inline-block;">选项布局：
								<s:radio name="config1" cssClass="config" value="%{'vertical'}" list="#{'vertical':'垂直','horizontal':'水平'}" cssStyle="width:auto;"/>
							</div>-->
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td style="font-weight: normal;text-align: right;">*题目:</td>
						<td class="value relative" style="margin: 0;padding: 1px 0;min-height:19px;margin: 0;">
							<s:textfield name="subject" cssClass="ui-widget-content" cssStyle="width:463px;" data-validate="required"/>
							<div style="position:relative;right:-2px;width: 40px;display: inline-block;">
								<s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>分
							</div>
							<ul class="inputIcons" style="top:12px;right: 62px;">
								<li class="inputIcon ui-icon ui-icon-circle-arrow-n" title='<s:text name="questionary.title.click2upTopic"/>' id="upTopic"></li>
							 	<li class="inputIcon ui-icon ui-icon-circle-arrow-s" title='<s:text name="questionary.title.click2downTopic"/>' id="downTopic">
							 	<li class="inputIcon ui-icon ui-icon-circle-plus" title='<s:text name="questionary.title.click2addTopic"/>' id="addTopic">
							 	<li class="inputIcon ui-icon ui-icon-circle-close" title='<s:text name="questionary.title.click2deleteTopic"/>' id="deleteTopic">
							</ul>
						</td>
					</tr>
					<tr class="option">
						<td>&nbsp;</td>
						<td style="font-weight: normal;text-align: right;vertical-align: top;">选项:</td>
						<td class="value" >
							<div style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;display: inline-block;">
								<s:radio cssClass="standard" name="standard1" value="true" list="#{'true':''}" cssStyle="width:auto;width:1em;"/>
								<s:textfield name="subject" cssClass="ui-widget-content" cssStyle="width:446px;"/>
								<div style="position:relative;right:-2px;width: 40px;display: inline-block;">
									<s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>分
								</div>
								<ul class="inputIcons" style="top:12px;right: 49px;">
									<li class="inputIcon ui-icon ui-icon-circle-arrow-n" title='<s:text name="questionary.title.click2upOption"/>' id="upOption"></li>
								 	<li class="inputIcon ui-icon ui-icon-circle-arrow-s" title='<s:text name="questionary.title.click2downOption"/>' id="downOption"></li>
								 	<li class="inputIcon ui-icon ui-icon-circle-plus" title='<s:text name="questionary.title.click2addOption"/>' id="addOption"></li>
								 	<li class="inputIcon ui-icon ui-icon-circle-close" title='<s:text name="questionary.title.click2deleteOption"/>' id="deleteOption"></li>
								</ul>
							</div>
						</td>
					</tr>
					</tbody>
				</table>
					</s:if><s:else>
						<s:iterator var="b" value="e.questions">
						<!-- 单选题 -->
						<s:if test="type==0">
							<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;">
								<tbody>
									<tr class="widthMarker">
										<td style="width: 40px;">&nbsp;</td>
										<td style="width: 60px;">&nbsp;</td>
										<td >&nbsp;</td>
									</tr>
									<tr>
										<td style="font-weight: normal;text-align: left;">第<span style="color: red;"><s:property value="orderNo"/></span>题</td>
						               	<td style="font-weight: normal;text-align: right;">题型:</td>
										<td class="value"><div class="ui-widget-content" style="display: inline-block;border-width: 0 1px 0 0;padding-right: 2px;">
											<s:checkbox name="required" cssStyle="width:1em;" cssClass="required"/><label>必选题</label></div>
											<s:radio cssClass="type" name="%{'type'+orderNo}" value="%{type}" list="#{'0':'单选','1':'多选','2':'填空','3':'问答'}" cssStyle="width:auto;"/>
											<div class="ui-widget-content" style="display: inline-block;border-width: 0 1px 0 1px;padding: 0 2px 0 2px;">
											<s:checkbox name="seperateScore" cssStyle="width:1em;" cssClass="seperateScore"/><label>全对方有分</label></div>
											<!--<div style="position:relative;right:-20px; display: inline-block;">选项布局：
												<s:radio name="%{'config'+orderNo}" value="%{configJson.get('layout_orientation')}" cssClass="config"  list="#{'vertical':'垂直','horizontal':'水平'}" cssStyle="width:auto;"/>
											</div>-->
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td style="font-weight: normal;text-align: right;">题目:</td>
										<td class="value" style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;">
											<s:textfield name="subject" cssClass="ui-widget-content" cssStyle="width:463px;"/>
											<div style="position:relative;right:-2px;width: 40px;display: inline-block;">
												<s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>分
											</div>
											<ul class="inputIcons" style="top:12px;right: 62px;">
												<li class="inputIcon ui-icon ui-icon-circle-arrow-n" title='<s:text name="questionary.title.click2upTopic"/>' id="upTopic"></li>
											 	<li class="inputIcon ui-icon ui-icon-circle-arrow-s" title='<s:text name="questionary.title.click2downTopic"/>' id="downTopic">
											 	<li class="inputIcon ui-icon ui-icon-circle-plus" title='<s:text name="questionary.title.click2addTopic"/>' id="addTopic">
											 	<li class="inputIcon ui-icon ui-icon-circle-close" title='<s:text name="questionary.title.click2deleteTopic"/>' id="deleteTopic">
											</ul>
										</td>
									</tr>
									<tr class="option">
										<td>&nbsp;</td>
										<td style="font-weight: normal;text-align: right;vertical-align: top;">选项:</td>
										<td class="value" >
											<s:iterator var="c" value="items">
											<div style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;display: inline-block;">
												<s:radio cssClass="standard" name="%{'standard'+#b.orderNo}" value="%{standard}" list="#{'true':''}" cssStyle="width:auto;width:1em;"/>
												<s:textfield name="subject" cssClass="ui-widget-content" cssStyle="width:446px;"/>
												<div style="position:relative;right:-2px;width: 40px;display: inline-block;">
													<s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>分
												</div>
												<ul class="inputIcons" style="top:12px;right: 49px;">
													<li class="inputIcon ui-icon ui-icon-circle-arrow-n" title='<s:text name="questionary.title.click2upOption"/>' id="upOption"></li>
												 	<li class="inputIcon ui-icon ui-icon-circle-arrow-s" title='<s:text name="questionary.title.click2downOption"/>' id="downOption"></li>
												 	<li class="inputIcon ui-icon ui-icon-circle-plus" title='<s:text name="questionary.title.click2addOption"/>' id="addOption"></li>
												 	<li class="inputIcon ui-icon ui-icon-circle-close" title='<s:text name="questionary.title.click2deleteOption"/>' id="deleteOption"></li>
												</ul>
											</div>
											</s:iterator>
										</td>
									</tr>
									</tbody>
								</table>
							</s:if><s:elseif test="type==1">
							<!-- 多选 -->
							<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;">
								<tbody>
									<tr class="widthMarker">
										<td style="width: 40px;">&nbsp;</td>
										<td style="width: 60px;">&nbsp;</td>
										<td >&nbsp;</td>
									</tr>
									<tr>
										<td style="font-weight: normal;text-align: left;">第<span style="color: red;"><s:property value="orderNo"/></span>题</td>
						               	<td style="font-weight: normal;text-align: right;">题型:</td>
										<td class="value"><div class="ui-widget-content" style="display: inline-block;border-width: 0 1px 0 0;padding-right: 2px;">
											<s:checkbox name="required" cssStyle="width:1em;" cssClass="required"/><label>必选题</label></div>
											<s:radio cssClass="type" name="%{'type'+orderNo}" value="%{type}" list="#{'0':'单选','1':'多选','2':'填空','3':'问答题'}" cssStyle="width:auto;"/>
											<div class="ui-widget-content" style="display: inline-block;border-width: 0 1px 0 1px;padding: 0 2px 0 2px;">
											<s:checkbox name="seperateScore" cssStyle="width:1em;" cssClass="seperateScore"/><label>全对方有分</label></div>
											<!--  <div style="position:relative;right:-20px; display: inline-block;">选项布局：
												<s:radio name="%{'config'+orderNo}" value="%{configJson.get('layout_orientation')}" cssClass="config" list="#{'vertical':'垂直','horizontal':'水平'}" cssStyle="width:auto;"/>
											</div>-->
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td style="font-weight: normal;text-align: right;">题目:</td>
										<td class="value" style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;">
											<s:textfield name="subject" cssClass="ui-widget-content" cssStyle="width:463px;"/>
											<div style="position:relative;right:-2px;width: 40px;display: inline-block;">
												<s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>分
											</div>
											<ul class="inputIcons" style="top:12px;right: 62px;">
												<li class="inputIcon ui-icon ui-icon-circle-arrow-n" title='<s:text name="questionary.title.click2upTopic"/>' id="upTopic"></li>
											 	<li class="inputIcon ui-icon ui-icon-circle-arrow-s" title='<s:text name="questionary.title.click2downTopic"/>' id="downTopic">
											 	<li class="inputIcon ui-icon ui-icon-circle-plus" title='<s:text name="questionary.title.click2addTopic"/>' id="addTopic">
											 	<li class="inputIcon ui-icon ui-icon-circle-close" title='<s:text name="questionary.title.click2deleteTopic"/>' id="deleteTopic">
											</ul>
										</td>
									</tr>
									<tr class="option">
										<td>&nbsp;</td>
										<td style="font-weight: normal;text-align: right;vertical-align: top;">选项:</td>
										<td class="value" >
											<s:iterator var="c" value="items">
											<div style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;display: inline-block;">
												<s:checkbox cssClass="standard" name="standard" cssStyle="width:1em;"/>
												<s:textfield name="subject" cssClass="ui-widget-content" cssStyle="width:446px;"/>
												<div style="position:relative;right:-2px;width: 40px;display: inline-block;">
													<s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>分
												</div>
												<ul class="inputIcons" style="top:12px;right: 49px;">
													<li class="inputIcon ui-icon ui-icon-circle-arrow-n" title='<s:text name="questionary.title.click2upOption"/>' id="upOption"></li>
												 	<li class="inputIcon ui-icon ui-icon-circle-arrow-s" title='<s:text name="questionary.title.click2downOption"/>' id="downOption"></li>
												 	<li class="inputIcon ui-icon ui-icon-circle-plus" title='<s:text name="questionary.title.click2addOption"/>' id="addOption"></li>
												 	<li class="inputIcon ui-icon ui-icon-circle-close" title='<s:text name="questionary.title.click2deleteOption"/>' id="deleteOption"></li>
												</ul>
											</div>
											</s:iterator>
										</td>
									</tr>
									</tbody>
								</table>
							</s:elseif><s:elseif test="type==2">
							<!-- 填空 -->
							<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;">
								<tbody>
									<tr class="widthMarker">
										<td style="width: 40px;">&nbsp;</td>
										<td style="width: 60px;">&nbsp;</td>
										<td >&nbsp;</td>
									</tr>
									<tr>
										<td style="font-weight: normal;text-align: left;">第<span style="color: red;"><s:property value="orderNo"/></span>题</td>
						               	<td style="font-weight: normal;text-align: right;">题型:</td>
										<td class="value"><div class="ui-widget-content" style="display: inline-block;border-width: 0 1px 0 0;padding-right: 2px;">
											<s:checkbox name="required" cssStyle="width:1em;" cssClass="required"/><label>必选题</label></div>
											<s:radio cssClass="type" name="%{'type'+orderNo}" value="%{type}" list="#{'0':'单选','1':'多选','2':'填空','3':'问答题'}" cssStyle="width:auto;"/>
											<!--<div style="position:relative;right:-180px;width: 100px;display: inline-block;">
												<label>默认行数:</label><s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>
											</div>-->
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td style="font-weight: normal;text-align: right;">题目:</td>
										<td class="value" style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;">
											<s:textfield name="subject" cssClass="ui-widget-content" cssStyle="width:463px;"/>
											<div style="position:relative;right:-2px;width: 40px;display: inline-block;">
												<s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>分
											</div>
											<ul class="inputIcons" style="top:12px;right: 62px;">
												<li class="inputIcon ui-icon ui-icon-circle-arrow-n" title='<s:text name="questionary.title.click2upTopic"/>' id="upTopic"></li>
											 	<li class="inputIcon ui-icon ui-icon-circle-arrow-s" title='<s:text name="questionary.title.click2downTopic"/>' id="downTopic">
											 	<li class="inputIcon ui-icon ui-icon-circle-plus" title='<s:text name="questionary.title.click2addTopic"/>' id="addTopic">
											 	<li class="inputIcon ui-icon ui-icon-circle-close" title='<s:text name="questionary.title.click2deleteTopic"/>' id="deleteTopic">
											</ul>
										</td>
									</tr>
									<tr class="option">
										<td>&nbsp;</td>
										<td style="font-weight: normal;text-align: right;vertical-align: top;">内容:</td>
										<td class="value" >
											<s:textarea name="subject" value="%{items.iterator().next().subject}" rows="3" cssClass="ui-widget-content noresize"/>
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td style="font-weight: normal;text-align: right;vertical-align: top;">答案:</td>
										<td class="value" >
											<s:textarea name="config" value="%{items.iterator().next().config}" rows="3" cssClass="ui-widget-content noresize"/>
										</td>
									</tr>
									</tbody>
								</table>
							</s:elseif><s:elseif test="type==3">
							<!-- 简答 -->
							<table class="ui-widget-content" cellspacing="2" cellpadding="0" style="width:100%;border-width: 1px 0 0 0;">
								<tbody>
									<tr class="widthMarker">
										<td style="width: 40px;">&nbsp;</td>
										<td style="width: 60px;">&nbsp;</td>
										<td >&nbsp;</td>
									</tr>
									<tr>
										<td style="font-weight: normal;text-align: left;">第<span style="color: red;"><s:property value="orderNo"/></span>题</td>
						               	<td style="font-weight: normal;text-align: right;">题型:</td>
										<td class="value"><div class="ui-widget-content" style="display: inline-block;border-width: 0 1px 0 0;padding-right: 2px;">
											<s:checkbox name="required" cssStyle="width:1em;" cssClass="required"/><label>必选题</label></div>
											<s:radio cssClass="type" name="%{'type'+orderNo}" value="%{type}" list="#{'0':'单选','1':'多选','2':'填空','3':'问答题'}" cssStyle="width:auto;"/>
											<div class="ui-widget-content" style="display: inline-block;border-width: 0 1px 0 1px;padding: 0 2px 0 2px;">
												<s:checkbox name="grade" cssStyle="width:1em;" cssClass="seperateScore"/><label>是否要评分</label>
											</div>
											<!--<div style="position:relative;right:-108px;width: 100px;display: inline-block;">
												<label>默认行数:</label><s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>
											</div>-->
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td style="font-weight: normal;text-align: right;">题目:</td>
										<td class="value" style="position:relative;margin: 0;padding: 1px 0;min-height:19px;margin: 0;">
											<s:textfield name="subject" cssClass="ui-widget-content" cssStyle="width:463px;"/>
											<div style="position:relative;right:-2px;width: 40px;display: inline-block;">
												<s:textfield name="score" cssClass="ui-widget-content" cssStyle="width:25px;"/>分
											</div>
											<ul class="inputIcons" style="top:12px;right: 62px;">
												<li class="inputIcon ui-icon ui-icon-circle-arrow-n" title='<s:text name="questionary.title.click2upTopic"/>' id="upTopic"></li>
											 	<li class="inputIcon ui-icon ui-icon-circle-arrow-s" title='<s:text name="questionary.title.click2downTopic"/>' id="downTopic">
											 	<li class="inputIcon ui-icon ui-icon-circle-plus" title='<s:text name="questionary.title.click2addTopic"/>' id="addTopic">
											 	<li class="inputIcon ui-icon ui-icon-circle-close" title='<s:text name="questionary.title.click2deleteTopic"/>' id="deleteTopic">
											</ul>
										</td>
									</tr>
									<tr class="option">
										<td>&nbsp;</td>
										<td style="font-weight: normal;text-align: right;vertical-align: top;">内容:</td>
										<td class="value" >
											<s:textarea name="subject" value="%{items.iterator().next().subject}" rows="3" cssClass="ui-widget-content noresize"/>
										</td>
									</tr>
									</tbody>
								</table>
							</s:elseif>
						</s:iterator>
					</s:else>
			</div>
			<div class="formTopInfo">
				状态：<s:property value="%{statusesValue[e.status]}" />&nbsp;&nbsp;&nbsp;&nbsp;登记：<s:property value="e.author.name" />(<s:date name="e.fileDate" format="yyyy-MM-dd HH:mm:ss"/>)
				<s:if test="%{e.modifier != null}">
				，最后修改：<s:property value="e.modifier.name" />(<s:date name="e.modifiedDate" format="yyyy-MM-dd HH:mm:ss"/>)<br/>
				</s:if>
				<s:if test="%{e.status==0}">
				发布人：<s:property value="e.issuer.name" />(<s:date name="e.issueDate" format="yyyy-MM-dd HH:mm:ss"/>)
				</s:if>
			</div>
		<s:hidden name="e.id" />
		<s:hidden name="e.status"/>
		<s:hidden name="e.author.id" />
		<s:hidden name="e.type" />
		<s:hidden name="topics" />
		<s:hidden name="assignUserIds" />
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>
		
	</s:form>
</div>