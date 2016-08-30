<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="bc-page" title='<s:text name="user.title.select"/>'
	data-type='dialog' data-initMethod='bc.selectUser.init'
	data-js='<s:url value="/bc/identity/user/select.js" />'
	data-option='{
		"buttons":[{"text":"<s:text name="label.ok"/>","click":"bc.selectUser.clickOk"}],
		"width":300,"modal":true
	}'>
	<div style="margin: 4px;">
		<s:select name="es" list="es" listKey="id + ',' + name + ',' + pname"
			listValue="pname == '' ? name + ' (' + code + ')' : pname + '/' + name + ' (' + code + ')'"
			theme="simple" size="10" cssStyle="width:100%;height:100%;" 
			value="selected" multiple="%{multiple}"></s:select>
	</div>
</div>