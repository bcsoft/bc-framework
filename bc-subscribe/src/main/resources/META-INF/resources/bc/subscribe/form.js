bc.subscribeForm = {
  init: function (option, readonly) {
    var $form = $(this);

    if (readonly) return;

    /*function add($ul,userid,name){
      var id = $form.find(":input[name='e.id']").val();
      bc.ajax({
        url:bc.root+"/bc/subscribe/add4manager",
        data:{id:id,actorId:userid},
        dataType:"json",
        success:function(json){
          if(json.success){
            bc.msg.slide(json.msg);
            $(liTpl.format(userid,name,'点击移除'))
            .appendTo($ul).find("span.click2remove")
            .click(function(){
              $(this).parent().remove();
            });
          }
        }
      });
    }
    
    //删除订阅人
    function delete_($li){
      var id = $form.find(":input[name='e.id']").val();
      var aid = $li.attr("data-id");
      bc.ajax({
        url:bc.root+"/bc/subscribe/delete4manager",
        data:{id:id,actorId:aid},
        dataType:"json",
        success:function(json){
          if(json.success){
            bc.msg.slide(json.msg);
            $li.remove();
          }
        }
      });
    }
    
    //绑定点击移除按钮
    $form.find(".ulActors>li>.click2remove").click(function(){
      delete_($(this).closest("li"));
    });
    
    
    //声明li
    var liTpl = '<li class="horizontal  ui-widget-content ui-corner-all ui-state-highlight" data-id="{0}"'+
    ' style="position: relative;margin:0 2px;float: left;padding: 0;border-width: 0;">'+
    '<span class="text">{1}</span>'+
    '<span class="click2remove verticalMiddle ui-icon ui-icon-close" style="margin: -8px -2px;" title={2}></span></li>';
    
    //绑定添加用户
    $form.delegate("ul>.subscribe-addUsers","click",function(){
      if($form.find(":input[name='e.id']").val()==""){
        bc.msg.alert('请先保存订阅信息！');
        return;
      }
        
      var $div = $(this).closest("div");
      var $ul = $div.find(".ulActors");
      var $lis = $form.find(".ulActors>li");
      var selecteds="";
      $lis.each(function(i){
        selecteds+=(i > 0 ? "," : "") + ($(this).attr("data-id"));//
      });
      bc.identity.selectUser({
        status:'0',
        history:false,
        selecteds: selecteds,
        onOk: function(user){
          if($lis.filter("[data-id='" + user.id + "']").size() > 0){//已存在
            logger.info("duplicate select: id=" + user.id + ",name=" + user.name);
          }else{//新添加的	
            add($ul,user.id,user.name);
          }
        }
      });
    });
    
    //绑定添加岗位
    $form.delegate("ul>.subscribe-addGroups","click",function(){
      if($form.find(":input[name='e.id']").val()==""){
        bc.msg.alert('请先保存订阅信息！');
        return;
      }
      
      var $div = $(this).closest("div");
      var $ul = $div.find(".ulActors");
      var $lis = $form.find(".ulActors>li");
      var selecteds="";
      $lis.each(function(i){
        selecteds+=(i > 0 ? "," : "") + ($(this).attr("data-id"));//
      });
      bc.identity.selectGroup({
        multiple:false,
        history:false,
        selecteds: selecteds,
        onOk: function(group){
          if($lis.filter("[data-id='" + group.id + "']").size() > 0){//已存在
            logger.info("duplicate select: id=" + group.id + ",name=" + group.name);
          }else{//新添加的
            add($ul,group.id,group.name);
          }
        }
      });
    });
    
    //绑定添加单位或部门
    $form.delegate("ul>.subscribe-addUnitOrDepartments","click",function(){
      if($form.find(":input[name='e.id']").val()==""){
        bc.msg.alert('请先保存订阅信息！');
        return;
      }
      
      var $div = $(this).closest("div");
      var $ul = $div.find(".ulActors");
      var $lis = $form.find(".ulActors>li");
      var selecteds="";
      $lis.each(function(i){
        selecteds+=(i > 0 ? "," : "") + ($(this).attr("data-id"));//
      });
      bc.identity.selectUnitOrDepartment({
        multiple: false,
        history:false,
        selecteds: selecteds,
        onOk: function(group){
          if($lis.filter("[data-id='" + group.id + "']").size() > 0){//已存在
            logger.info("duplicate select: id=" + group.id + ",name=" + group.name);
          }else{//新添加的
            add($ul,group.id,group.name);
          }
        }
      });
    });*/

  },
  /**
   * 发布
   */
  release: function () {
    var $form = $(this);
    //设置状态为 使用中
    $form.find(":input[name='e.status']").val(0);

    //调用标准的方法执行保存
    bc.page.save.call($form, {
      callback: function (json) {
        bc.msg.slide("发布成功");
        $form.dialog("close");
        return false;
      }
    });
  }
};