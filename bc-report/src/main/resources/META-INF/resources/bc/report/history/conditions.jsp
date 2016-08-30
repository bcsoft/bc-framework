<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="cn.bc.web.ui.html.toolbar.*"%>
<form class="bc-conditionsForm draggable ui-widget-content ui-state-highlight">
	<ul class="conditions" style="min-width:15.3em;">
		<li class="condition">
			<div class="value">
				<div class="bc-dateContainer">
					<div class="label">分类</div>
					<div class="value bc-dateContainer">
						<input type="text" class="bc-select ui-widget-content" readonly="readonly"
							data-maxHeight="150px" style="width:9em;"
							data-source='<s:property value="categorys"/>'>
						<input type="hidden" data-condition='{"type":"string","ql":"a.category=?"}'>
						<ul class="inputIcons">
							<li class="bc-select inputIcon ui-icon ui-icon-triangle-1-s" title='<s:text name="title.click2select"/>'></li>
							<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
						</ul>
					</div>
				</div><div style="width:12px;display:inline-block;"></div><div class="bc-dateContainer">
					<div class="label">来源</div>
					<div class="value bc-dateContainer">
						<input type="text" class="bc-select ui-widget-content" readonly="readonly"
							data-maxHeight="150px" style="width:9em;"
							data-source='<s:property value="sources"/>'>
						<input type="hidden" data-condition='{"type":"string","ql":"a.source_type=?"}'>
						<ul class="inputIcons">
							<li class="bc-select inputIcon ui-icon ui-icon-triangle-1-s" title='<s:text name="title.click2select"/>'></li>
							<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="clear"></div>
		</li>
		<li class="condition">
			<div class="label">创建日期</div>
			<div class="value">
				<div class="bc-dateContainer">
					<input type="text" class="bc-date ui-widget-content" data-validate="date" style="width:9em;"
						data-condition='{"type":"startDate","ql":"a.start_date>=?"}'>
					<ul class="inputIcons">
						<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"></li>
						<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
					</ul>
				</div><div style="width:12px;display:inline-block;">～</div><div class="bc-dateContainer">
					<input type="text" class="bc-date ui-widget-content" data-validate="date" style="width:9em;"
						data-condition='{"type":"endDate","ql":"a.start_date<=?"}'>
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