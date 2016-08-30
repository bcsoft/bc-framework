<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="cn.bc.web.ui.html.toolbar.*"%>
<form class="bc-conditionsForm draggable ui-widget-content ui-state-highlight">
	<ul class="conditions" style="min-width:17.3em;">
		<li class="condition">
			<div class="bc-relativeContainer" style="width:100%;">
				<div class="label">文档类型</div>
				<div class="value bc-relativeContainer" style="width:100%;">
					<input type="text" class="bc-select ui-widget-content" readonly="readonly"
						data-maxHeight="150px" style="width:100%;"
						data-source='<s:property value="categorys"/>'>
					<input type="hidden" data-condition='{"type":"string","ql":"b.doc_type=?"}'>
					<ul class="inputIcons">
						<li class="bc-select inputIcon ui-icon ui-icon-triangle-1-s" title='<s:text name="title.click2select"/>'></li>
						<li class="clearSelect inputIcon ui-icon ui-icon-close" title='<s:text name="title.click2clear"/>'></li>
					</ul>
				</div>
			</div>
		</li>
	</ul>
</form>