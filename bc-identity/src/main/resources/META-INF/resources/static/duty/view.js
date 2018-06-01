define(["bc", "vue", "bc/vue/components"], function (bc, Vue) {
  "use strict";

  function showForm(mid, name, data) {
    var self = this;
    require(["text!" + bc.root + "/static/duty/form.htm"], function success(html) {
      bc.page.newWin({
        html: html,
        mid: mid,
        name: name,
        data: data,
        afterClose: function (status) {
          if (status) self.$refs.grid.reload();
        }
      });
    });
  }

  return function ($page) {
    this.$page = $page;
    var vm = new Vue({
      el: $page[0],
      data: {
        url: bc.root + "/rest/duty",
        params: {
          searchText: ""
        },
        columns: [
          {id: "code", label: "编码", width: "10em"},
          {id: "name", label: "名称", width: "20em"}
        ]
      },
      methods: {
        create: function () {
          showForm.call(this, "duty-new", "新建职务");
        },
        open: function () {
          var sel = this.$refs.grid.selection;
          if (sel.length == 1) showForm.call(this, "duty-" + sel[0].id, sel[0].name, sel[0]);
          else if (sel.length == 0) bc.msg.slide("请先选定职务信息！");
          else if (sel.length > 1) bc.msg.slide("只能选定一条职务信息！");
        },
        delete: function () {
          var sel = this.$refs.grid.selection;
          if (sel.length == 0) {
            bc.msg.slide("请先选定职务信息！");
            return;
          }
          bc.msg.confirm("确定要删除选定的 <b>" + sel.length + "</b> 条职务信息吗？", function () {
            var ids = [];
            sel.forEach(function (s) {
              ids.push(s.id)
            });
            $.ajax({
              url: bc.root + "/rest/duty/" + ids.join(","),
              type: "DELETE"
            }).then(function () {
              bc.msg.slide("删除成功！");
              vm.$refs.grid.reload();
            }, function (jqXHR, textStatus) {
              bc.msg.info(jqXHR.responseText || textStatus);
            });
          });
        },
        dblclickRow: function (row) {
          showForm.call(this, "duty-" + row.id, row.name, row);
        },
        search: function () {
          this.$refs.grid.reload();
        },
        export: function (scope) {
          console.log("[duty] export: scope=%s", scope);
        }
      }
    });
  }
});