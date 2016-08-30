<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="user.password.title.reset"/>' data-type='form' class="bc-page"
	data-js='<s:url value="/bc/identity/user/password.js" />'
	data-saveUrl='<s:url value="/bc/auth/updatePassword" />'
	data-option='{
		"buttons":[{"text":"<s:text name="label.ok"/>","click":"bc.userPaswordForm.save"}],
		"minWidth":270,"minHeight":100,"modal":false,minimizable:false,maximizable:false
	}'>
	<s:form name="userForm" theme="simple">
		<table class="formFields" cellspacing="2">
			<tbody>
				<tr>
					<td class="label">输入新密码:</td>
					<td class="value"><s:password name="password" 
					data-validate='{required:true,type:"regexp",pattern:"([\\\\\\w]*\\\\\\d+[a-zA-Z_]+[\\\\\\w]*)|([\\\\\\w]*[a-zA-Z_]+\\\\\\d+[\\\\\\w]*)",minLen:6,maxLen:18,msg:"您输入的密码不符合系统设定的安全策略！"}'/></td>
				</tr>
				<tr>
					<td class="label">确认新密码:</td>
					<td class="value"><s:password name="confirmPassword"/></td>
				</tr>
			</tbody>
		</table>
		<p class="formComment">说明：密码由字母、数字或下划线组合而成，且必须至少包含1个数字和1个字母，长度限制在6～18位！</p>
		<s:hidden name="ids"/>
	</s:form>
</div>