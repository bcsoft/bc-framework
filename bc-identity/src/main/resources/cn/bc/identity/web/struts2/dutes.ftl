<div title='<@s.text name="duty.list.title"/>'>
<ul>
<@s.iterator value="bs" status="stuts">
	<@s.if test="#stuts.odd == true">
		<li style="background-color:green;"><@s.property value="name" /></li>
	</@s.if>
	<@s.else>
		<li><@s.property value="name" /></li>
	</@s.else>
</@s.iterator>
</ul>
</div>