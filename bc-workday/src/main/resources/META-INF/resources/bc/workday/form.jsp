<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="workday.title"/>' data-type='form' class="bc-page"
	data-saveUrl='<s:url value="/bc/workday/save" />'
	data-js='<s:url value="/bc/workday/form.js" />'
	data-initMethod='bc.workday.init'
	data-option='<s:property value="formPageOption"/>' 
	style="overflow-y:auto;">
	<s:form name="workdayForm" theme="simple" >
			<table class="ui-widget-content" cellspacing="8" cellpadding="0">
				<tr class="widthMarker">
					<td style="width: 80px;">&nbsp;</td>
					<td style="width: 220px;">&nbsp;</td>
					<td style="width: 100px;">&nbsp;</td>
					<td >&nbsp;</td>
				</tr>
				<tr>
					<td class="label">*<s:text name="workday.dayoff"/>:</td>
					<td class="value" colspan="3">
						<s:if test="e.isNew()">
							<s:radio name="e.dayOff" list="dayoffList"  cssStyle="width:auto;" data-validate="required" value="2"></s:radio>
						</s:if>
						<s:else>
							<s:radio name="e.dayOff" list="dayoffList"  cssStyle="width:auto;" data-validate="required"></s:radio>
						</s:else>
					</td>									
				</tr>	
				<tr>
					<td class="label">*<s:text name="workday.date"/>:</td>
					<td class="value" colspan="3">
						从<div class="bc-dateContainer">
							<input type="text" name="e.fromDate" value='<s:date format="yyyy-MM-dd" name="e.fromDate" />'  class="bc-date ui-widget-content" data-validate='{"type":"date","required":true}' style="width:9em;">
							<ul class="inputIcons">
								<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"></li>
								<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
							</ul>
						</div>到<div class="bc-dateContainer">
							<input type="text" name="e.toDate" value='<s:date format="yyyy-MM-dd" name="e.toDate" />' class="bc-date ui-widget-content" data-validate='{"type":"custom","method":"bc.workday.checkToDate","msg" :"结束日期必须大于或等于开始日期" ,"required":true}' style="width:9em;">
							<ul class="inputIcons">
								<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"></li>
								<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
							</ul>
						</div>
						<div class="clear"></div>
					</td>	
				</tr>
				
				<tr>
					<td class="label">*<s:text name="workday.desc"/>:</td>
					<td class="value" colspan="3">
						<s:textfield name="e.desc_" cssStyle="width:20em;" cssClass="ui-widget-content" data-validate="required" readonly="false"/>
					</td>		
				</tr>	
				<tr>
					<td colspan="4" style="text-align: center;color: #999999;">注意：标准工作日(如周一到周五)不需要配置</td>	
				</tr>
			</table>

		<s:hidden name="e.id" />
	</s:form>
</div>