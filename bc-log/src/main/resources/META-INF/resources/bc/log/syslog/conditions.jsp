<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<form class="bc-conditionsForm draggable ui-widget-content ui-state-highlight">
	<ul class="conditions" style="min-width:25.3em;">
		<li class="condition">
			<div class="label">类别</div>
			<div class="value checkboxes" data-condition='{"type":"int","key":"l.type_"}' >
				<label><input type="checkbox" name="checkboxField1" value="0"><span>登录</span></label>
				<label><input type="checkbox" name="checkboxField1" value="1"><span>注销</span></label>
				<label><input type="checkbox" name="checkboxField1" value="2"><span>超时</span></label>
				<label><input type="checkbox" name="checkboxField1" value="3"><span>重登录</span></label>
			</div>
		</li>
		<li class="condition">
			<div class="label">登录日期</div>
			<div class="value">
				<div class="bc-dateContainer">
					<input type="text" class="bc-date ui-widget-content" data-validate="date" style="width:12em;"
						data-condition='{"type":"startDate","ql":"l.file_date>=?"}'>
					<ul class="inputIcons">
						<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"></li>
						<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
					</ul>
				</div>～<div class="bc-dateContainer">
					<input type="text" class="bc-date ui-widget-content" data-validate="date" style="width:12em;"
						data-condition='{"type":"endDate","ql":"l.file_date<=?"}'>
					<ul class="inputIcons">
						<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"></li>
						<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
					</ul>
				</div>
			</div>
			<div class="clear"></div>
		</li>
	</ul>
</form>