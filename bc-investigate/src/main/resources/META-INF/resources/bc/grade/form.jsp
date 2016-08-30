<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="grade.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/grade/save" />'
	data-js='js:bc_identity,<s:url value="/bc/grade/form.js"/>'
	data-initMethod='bc.gradeForm.init'
	data-option='<s:property value="formPageOption"/>' style="overflow-y:auto;">
	<s:form name="gradeForm" theme="simple" cssStyle="width:445px;">
		<table class="formFields" cellspacing="2" cellpadding="0">
			<tbody>
				<tr class="widthMarker">
					<td style="width:80px;;">&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td class="label"><s:text name="grade.testPaper"/>:</td>
					<td class="value"><s:property value="%{testTitle}"/></td>
				</tr>
				<tr>
					<td class="label"><s:text name="grade.testQuestions"/>:</td>
					<td class="value"><s:property value="%{questionTitle}"/>&nbsp;(<s:property value="%{score}"/>分)</td>
				</tr>
				<tr>
					<td class="label"><s:text name="grade.answersPeople"/>:</td>
					<td class="value"><s:property value="%{answer}"/>&nbsp;(<s:property value="%{answerTime}"/>)</td>
				</tr>
				<tr>
					<td class="label"><s:text name="grade.assessScore"/>:</td>
					<td class="value"><div id="slider" style="width: 80%;display: inline-block;
						margin-left: 8px;"></div>&nbsp;<s:textfield type="text" id="amount" style="border:0;
						font-weight:bold;width: 30px;margin-left: 10px;" name="amount"/>分
					</td>
				</tr>
				<tr>
					<td class="topLabel"><s:text name="grade.answer"/>:</td>
					<td class="value">
						<s:textarea name="subject" value="%{result}" 
							rows="5" cssClass="ui-widget-content noresize" cssStyle="width:98%;" readonly="true"/>
					</td>
				</tr>
			</tbody>
		</table>
		<!-- <div class="formTopInfo">
			状态：<s:property value="%{statusesValue[e.status]}" />,登记：<s:property value="e.author.name" />(<s:date name="e.fileDate" format="yyyy-MM-dd HH:mm:ss"/>)<br/>
			<s:if test="%{e.modifier != null}">
			最后修改：<s:property value="e.modifier.name" />(<s:date name="e.modifiedDate" format="yyyy-MM-dd HH:mm:ss"/>)<br/>
			</s:if>
			<s:if test="%{e.status==0}">
			发布人：<s:property value="e.issuer.name" />(<s:date name="e.issueDate" format="yyyy-MM-dd HH:mm:ss"/>)<br/>
			</s:if>
		</div> -->
		<s:hidden name="qid" />
		<s:hidden name="aid"/>
		<s:hidden name="rid" />
		<s:hidden name="score" />
		<s:hidden name="gid" />
		<s:hidden name="amount" />
		<input type="hidden" name="e.fileDate" value='<s:date format="yyyy-MM-dd HH:mm:ss" name="e.fileDate" />'/>
		
	</s:form>
</div>