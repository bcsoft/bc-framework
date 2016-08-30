<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='无' class="bc-page chart" style="overflow: auto;"
	data-type='chart' 
	data-js='js:highcharts,js:highcharts_exporting,<s:url value="/bc/report/chart/chart.js" />'
	data-initMethod='bc.report.chart.init'
	data-option='{"width":600,"height":400}'>
<div class="chartContainer" style="height:100%;width:100%;"></div>
<pre class="config hide"><s:property value="chartOption" escapeJavaScript="false"/></pre>
</div>