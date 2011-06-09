<div title='<@s.text name="XXX.form.title"/>' 
	data-option='{
		"buttons":[
			{"text":"<@s.text name="label.ok"/>","action":"save"},
			{"text":"<@s.text name="label.test"/>","click":"testFN"}
		],
		"minWidth":200,"minHeight":200,"modal":false
	}'>
<@s.form name="xxxForm">
	<@s.textfield name="b.name" key="duty.name"/>
	
	<@s.hidden name="b.status" />
	<@s.hidden name="b.inner" />
	<@s.hidden name="b.id" />
</@s.form>
</div>