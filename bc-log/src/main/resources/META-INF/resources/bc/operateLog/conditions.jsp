<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="cn.bc.web.ui.html.toolbar.*"%>
<form class="bc-conditionsForm draggable ui-widget-content ui-state-highlight" 
		data-js='<s:url value="/bc/identity/identity.js" />'>
	<ul class="conditions" style="min-width:19.3em;">
		<li class="condition">
			<div class="value">
				<div class="bc-dateContainer">
				<div class="label">所属模块</div>
					<div class="value bc-dateContainer">
						<input type="text" class="bc-select ui-widget-content" readonly="readonly"
							data-maxHeight="150px" style="width:9em;"
							data-source='<s:property value="subordinateModule"/>'>
						<input type="hidden" data-condition='{"type":"string","ql":"l.ptype=?"}'>
						<ul class="inputIcons">
							<li class="bc-select inputIcon ui-icon ui-icon-triangle-1-s" title='<s:text name="title.click2select"/>'></li>
							<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
						</ul>
					</div>
				</div><div style="width:12px;display:inline-block;"></div><div class="bc-dateContainer">
					<div class="label">日志类型</div>
					<div class="value bc-dateContainer">
						<input type="text" class="bc-select ui-widget-content" readonly="readonly"
							data-maxHeight="150px" style="width:9em;"
							data-source='<s:property value="logType"/>'>
						<input type="hidden" data-condition='{"type":"long","ql":"l.type_=?"}'>
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
			<div class="value">
				<div class="bc-dateContainer">
					<div class="label">操作人</div>
					<div class="value bc-dateContainer value relative">
						<input type="text" name="userName" class="bc-select ui-widget-content" readonly="readonly"
							 data-maxHeight="150px" style="width:9em;">
						<input type="hidden" name="userId" data-condition='{"type":"long","ql":"a.id=?"}'>
						<ul class="inputIcons">
							<li class="selectUser inputIcon ui-icon ui-icon-circle-plus" data-cfg='userId=id,userName=name'
								 title='<s:text name="title.click2select"/>'></li>
							<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
						</ul>
					</div>
				</div><div style="width:12px;display:inline-block;"></div><div class="bc-dateContainer">
					<div class="label">创建方式</div>
					<div class="value bc-dateContainer">
						<input type="text" class="bc-select ui-widget-content" readonly="readonly"
							data-maxHeight="150px" style="width:9em;" 
							data-source='<s:property value="createWay"/>'>
						<input type="hidden" data-condition='{"type":"long","ql":"l.way=?"}'>
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
			<div class="label">操作时间</div>
			<div class="value">
				<div class="bc-dateContainer">
					<input type="text" class="bc-date ui-widget-content" data-validate="date" style="width:9em;"
						data-condition='{"type":"startDate","ql":"l.file_date>=?"}'>
					<ul class="inputIcons">
						<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"></li>
						<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
					</ul>
				</div>～<div class="bc-dateContainer">
					<input type="text" class="bc-date ui-widget-content" data-validate="date" style="width:9em;"
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