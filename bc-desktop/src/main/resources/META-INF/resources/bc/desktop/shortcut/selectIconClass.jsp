<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="shortcut.selectIconClass.title"/>'
	style='font-size: 12px;overflow: auto' class="bc-page selectIconClass" data-type='form'
	data-js='<s:url value="/bc/desktop/shortcut/selectIconClass.css" />,<s:url value="/bc/desktop/shortcut/selectIconClass.js" />'
	data-initMethod='bc.selectIconClass.init'
	data-option='{
		"buttons":[{"text":"<s:text name="label.ok"/>","click":"bc.selectIconClass.clickOk"}],
		"width":450,"height":350
	}'>
<s:iterator begin="0" end="9" var="row">
	<s:iterator begin="0" end="9" var="column">
		<a class="shortcut" title='i0<s:property value="#row" />0<s:property value="#column" />'>
			<span class='icon i0<s:property value="#row" />0<s:property value="#column" />'></span>
			<span class="text">i0<s:property value="#row" />0<s:property value="#column" /></span>
		</a>
	</s:iterator>
</s:iterator>
<s:iterator begin="10" end="10" var="row">
	<s:iterator begin="0" end="9" var="column">
		<a class="shortcut" title='i<s:property value="#row" />0<s:property value="#column" />'>
			<span class='icon i<s:property value="#row" />0<s:property value="#column" />'></span>
			<span class="text">i<s:property value="#row" />0<s:property value="#column" /></span>
		</a>
	</s:iterator>
</s:iterator>
</div>