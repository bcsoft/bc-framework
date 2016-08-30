bc.syslogList = {
	/** 打开指定的日志 */
	checkWork : function() {
		bc.page.edit.call($(this), true);
	},
	/** 按日统计登录帐号数 */
	statsByDay : function() {
		bc.page.newWin({
			mid: "syslogStatsByDay",
			name: '按日统计登录日志',
			url: bc.root + "/bc/syslogStats/statsByDay"
		});
	}
};