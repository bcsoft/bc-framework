<#if (data?size > 0)>
<table cellspacing="0" cellpadding="0" style="border-collapse: collapse;">
<thead><tr><td>&nbsp;</td><#list data[0]?keys as key><td>${key}</td></#list></tr></thead>
<tbody>
<#list data as r>
	<tr>
	<td>${r_index + 1}</td>
	<#assign keys = r?values>
	<#list keys as k>
		<td>${k!'&nbsp;'}</td>
	</#list>
	</tr>
</#list>
</tbody>
</table>
<#else>
<span>无数据！</span>
</#if>