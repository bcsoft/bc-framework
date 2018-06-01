define(["bc", "jquery", "vue", "bc/vue/components"], function (bc, $, Vue) {
  "use strict";
  var vm;

  function Page($page) {
    this.$page = $page;
    vm = new Vue({
      el: $page[0],
      data: {
        e: $page.data("data") || {}
      },
      methods: {
        save: function () {
          var isNew = !vm.e.id;
          $.ajax({
            url: bc.root + "/rest/duty/" + (isNew ? "" : vm.e.id),
            type: isNew ? "POST" : "PUT",
            data: vm.e
          }).then(function (data) {
            if (isNew) vm.e.id = data.id;
            $page.data("status", true);
            bc.msg.slide(data.msg || "保存成功！");
          }, function (jqXHR, textStatus) {
            bc.msg.info(jqXHR.responseText || textStatus);
          });
        }
      }
    });
  }

  Page.prototype.save = function () {
    vm.save();
  };

  return Page;
});