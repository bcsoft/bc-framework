bc.syslogStats = {
  init: function () {
    var $page = $(this);
    var config = eval("(" + $page.find(".config").text() + ")");
    new Highcharts.Chart(config);
  }
};