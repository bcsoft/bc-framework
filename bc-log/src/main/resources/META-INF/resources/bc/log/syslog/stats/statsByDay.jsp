<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='按日统计登录帐号数'' 
	class="bc-page chart" style="overflow: auto;"
	data-type='chart' 
	data-js='js:highcharts,js:highcharts_exporting,<s:url value="/bc/log/syslog/stats/statsByDay.js" />'
	data-initMethod='bc.syslogStats.init'
	data-option='{"width":800,"height":500}'>
<div class="chartContainer" style="height:100%;width:100%;"></div>
<pre class="config hide">
{
	chart: {
		// 放置图表的容器
		renderTo: this.find(".chartContainer")[0],
		// 图表类型line, spline, area, areaspline, column, bar, pie , scatter
		defaultSeriesType: 'area',
		// 左右显示，默认上下正向
		inverted: false
	},
	title: {
		text: '登录日志统计',						// 图表的标题
		style:{font: 'normal 18px 微软雅黑,宋体'}	// 标题样式
	},
	subtitle: {                         
	    text: '每日登录帐号数统计'   	// 图表的副标题
	},
	xAxis: {
		// X轴的坐标值
		categories: [<s:iterator value="data" status="status" var="d"><s:if test="#status.index > 0">,</s:if>'<s:property value="#d[0]"/>'</s:iterator>],
		// X轴标尺
		labels: {
		    rotation: -45,
		    align: 'right',
		    style: {font: 'normal 12px 微软雅黑,宋体'}
		}
	},
	yAxis: {
		min: 0,
		title: {
			text: '登录帐号数(个/每天)'
		}
	},
	tooltip: {
		// //当鼠标悬置数据点时的格式化提示
		formatter: function() {
			return ''+
				 this.x +': '+ this.y +'';
		}
	},
	credits: {
		enabled: false
	},
	legend: {enabled: false},
	series: [{
		name: '统计',
		data: [<s:iterator value="data" status="status" var="d"><s:if test="#status.index > 0">,</s:if><s:property value="#d[1]"/></s:iterator>]
	}],
	exporting:{url:'<s:url value="/bc/exportsvg" />'}
}
</pre>
</div>