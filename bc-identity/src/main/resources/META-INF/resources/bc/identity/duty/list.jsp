<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="duty.title"/>' data-name='<s:text name="duty"/>' 
	class="bc-page"
	data-type='list' 
	data-namespace='<s:url value="/bc/duty" />'
	data-deleteUrl='<s:url value="/bc/duty/delete" />'
	data-editUrl='<s:url value="/bc/duty/edit" />'
	data-createUrl='<s:url value="/bc/duty/create" />'
	data-option='{"minWidth":250,"width":500,"minHeight":200,"height":400,"modal":false}'>
	<!-- 工具条 -->
	<div class="bc-toolbar ui-widget-content">
		<button type="button"
			class='bc-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-primary 
				{action:"create",callback:"callback4create"}'
			data-action="create">
			<span class="ui-button-icon-primary ui-icon ui-icon-document"></span><span
				class="ui-button-text">新建</span>
		</button>
		<button type="button"
			class='bc-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-primary 
				{action:"edit",callback:"callback4edit"}'
			data-action="edit">
			<span class="ui-button-icon-primary ui-icon ui-icon-pencil"></span>
			<span class="ui-button-text">编辑</span>
		</button>
		<button type="button"
			class='bc-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-primary
				{action:"delete"}'
			data-action="delete">
			<span class="ui-button-icon-primary ui-icon ui-icon-trash"></span><span
				class="ui-button-text">删除</span>
		</button>
		<span class="bc-searchButton" data-action="search"><a href="#" title='<s:text name="title.click2search"/>'
			class="ui-icon ui-icon-search" id="searchBtn"></a><input id="searchText" type="text">
		</span>
	</div>
	<!-- 表格 -->
	<div class="bc-grid multipleSelect" name="单位" data-dblclickrow="bc.page.edit">
		<!-- 表格头 -->
		<div class="ui-state-default header">
			<!-- 表格头：id -->
			<div class="left">
				<table class="table" cellspacing="0" cellpadding="0">
					<tbody>
						<tr class='ui-state-default row'>
							<td class="id" title='<s:text name="title.toggleSelect"/>'><span class="ui-icon ui-icon-notice"></span>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<!-- 表格头：其他列 -->
			<div class="right">
				<table class="table" cellspacing="0" cellpadding="0" style="width:300px;">
					<tbody>
						<tr class='ui-state-default row'>
							<td class="first sortable" style="width: 60px" 
								data-id='code'
								data-label='<s:text name="duty.code" />'>
								<div class="wrapper"><s:text name="duty.code" />
								<span class="sortableIcon asc ui-icon ui-icon-triangle-1-n"></span></div>
							</td>
							<td class="middle" style="width: 60px" 
								data-id='code'
								data-label='<s:text name="duty.code" />'><div class="wrapper"><s:text name="duty.code" /></div>
							</td>
							<td class="last sortable"
								data-id='name'
								data-label='<s:text name="duty.name" />'><div class="wrapper"><s:text name="duty.name" />
								<span class="sortableIcon ui-icon hide"></span></div>
							</td>
<!-- 							<td class="empty">&nbsp;</td> -->
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		<!-- 表格数据行 -->
		<div class="data">
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
		<!-- 表格分页条 -->
		<ul class="pager ui-widget-content ui-widget ui-helper-clearfix">
			<li data-action="refresh" class='pagerIcon ui-state-default ui-corner-all' title="刷新"><span class="ui-icon ui-icon-refresh"></span></li>
			<li id="changeSortType" data-action="changeSortType" class='pagerIcon ui-state-default ui-corner-all' 
				title="当前为本地排序，点击切换为远程排序"
				title4clickToRemoteSort="当前为本地排序,点击切换为远程排序"
				title4clickToLocalSort="当前为远程排序,点击切换为本地排序"><span class="ui-icon ui-icon-transferthick-e-w"></span></li>
			<!-- 表格分页条：扩展按钮 -->
			<li data-action="export" class='pagerIcon ui-state-default ui-corner-all' title="导出"><span class="ui-icon ui-icon-arrowthickstop-1-s"></span></li>
			<li data-action="print" class='pagerIcon ui-state-default ui-corner-all' title="打印"><span class="ui-icon ui-icon-print"></span></li>
		</ul>
	</div>
</div>