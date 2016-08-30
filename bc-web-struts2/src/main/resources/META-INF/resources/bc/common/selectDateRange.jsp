<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:property value="title"/>' data-type='form'
	class="bc-page"
	data-js='<s:url value="/bc/common/selectDateRange.js" />'
	data-initMethod='bc.selectDateRangeFrom.init'
	data-option='{
		"buttons":[{"text":"<s:text name="label.ok"/>","click":"bc.selectDateRangeFrom.clickOk"}],
		"width":330,height:130,modal:true
	}'
	style="overflow-y: hidden;">
	<s:form name="selectDateRangeFrom" theme="simple"
		cssStyle="padding:8px;">
		<div style="position: relative; display: inline-block">
			&nbsp;从<input type="text" name="startDate"
				data-validate='{"type":"date","required":true}'
				data-cfg='{"changeYear":true}'
				value='<s:date format="yyyy-MM-dd" name="startDate" />'
				style="width: 9em;" class="bc-date ui-widget-content" />
			<ul class="inputIcons" style="right: 0px;">
				<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"
					data-cfg='startDate'></li>
			</ul>
		</div>
		<div style="position: relative; display: inline-block">
			&nbsp;到<input type="text" name="endDate"
				data-validate='{"type":"date","required":true}'
				data-cfg='{"changeYear":true}'
				value='<s:date format="yyyy-MM-dd" name="endDate" />'
				style="width: 9em;" class="bc-date ui-widget-content" />
			<ul class="inputIcons" style="right: 0px;">
				<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"
					data-cfg='endDate'></li>
			</ul>
		</div>
	</s:form>
</div>