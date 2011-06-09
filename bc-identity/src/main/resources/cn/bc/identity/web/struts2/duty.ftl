<div title='<@s.text name="duty.form.title"/>'
	data-type='form'
	data-action='<@s.url value="/duty/save" />'
	data-option='{
		"buttons":[{"text":"<@s.text name="label.save"/>","action":"save"}],
		"minWidth":400,"minHeight":250,"modal":false
	}'>
<@s.form name="dutyForm">
	<@s.textfield name="b.name" key="duty.name" validate="{required:true}"/>
	<@s.textfield name="b.code" key="duty.code" aa1="aa" />
	
	<@s.hidden name="b.status" />
	<@s.hidden name="b.inner" />
	<@s.hidden name="b.id" />
</@s.form>
</div>