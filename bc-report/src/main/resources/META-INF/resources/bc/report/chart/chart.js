bc.namespace("bc.report");
bc.report.chart = {
	init: function() {
		var $page = $(this);
		var config = eval("("+$page.find(".config").text()+")");
		
		// 指定一些固定参数
		if(config.chart && !config.chart.renderTo)
			config.chart.renderTo = $page.find(".chartContainer")[0];
		config.credits = {enabled: false};
		config.exporting = {url: bc.root + "/bc/exportsvg"};
		
		// 显示图表
		new Highcharts.Chart(config);
	}
};