<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="data"
	<s:if test="page != null">data-pageNo="${page.pageNo}" data-pageCount="${page.pageCount}" data-totalCount="${page.totalCount}"</s:if>>
	<!-- 表格数据行：id列数据 -->
	<div class="left">
		<table class="table" cellspacing="0" cellpadding="0">
			<tbody>
				<s:iterator value="es" status="stuts">
					<tr
						class='ui-state-default row <s:if test="#stuts.odd == true">odd</s:if>'>
						<td class="id" data-id='<s:property value="id" />'
							data-name='<s:text name="duty" /> - <s:property value="name" />'><span
							class="ui-icon"></span>
						<s:property value="#stuts.index+1" />
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</div>
	<!-- 表格数据行：其他列数据 -->
	<div class="right">
		<table class="table" cellspacing="0" cellpadding="0" style="width:300px" originWidth="300">
			<tbody>
				<s:iterator value="es" status="stuts">
					<tr
						class='ui-state-default row <s:if test="#stuts.odd == true">odd</s:if>'>
						<td class="first" style="width: 60px"><s:property value="code" />
						</td>
						<td class="middle" style="width: 60px"><s:property value="code" />
						</td>
						<td class="last"><s:property value="name" />
						</td>
<!-- 								<td class="empty">&nbsp;</td> -->
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</div>
</div>