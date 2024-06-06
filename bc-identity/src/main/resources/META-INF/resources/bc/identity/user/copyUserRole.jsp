<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div class="bc-page" style="overflow:auto;" title="复制用户权限" data-type='form' data-initMethod='bc.copyUserRole.init'
     data-js='<s:url value="/bc/identity/user/copyUserRole.js" />'
     data-option='{"buttons":[{"text":"<s:text name="label.ok"/>","click":"bc.copyUserRole.clickOk"}],"width":665, "maxHeight":600,"modal":true}'>
  <s:form name="userForm" theme="simple">
    <table class="formTable2 ui-widget-content" cellspacing="2" cellpadding="0" style="width:650px;">
      <tr>
        <td class="label"><s:text name="user.copyFrom"/>:</td>
        <td class="value"><s:textfield name="copyFrom" cssClass="ui-widget-content" readonly="true"/></td>
        <td class="label"><s:text name="user.copyTo"/>:</td>
        <td class="value relative">
          <s:textfield name="copyTo" data-validate="required" cssClass="ui-widget-content" readonly="true"/>
          <ul class="inputIcons">
            <li id="selectCopyTo" class="inputIcon ui-icon ui-icon-circle-plus"
                title='选择接受权限的用户'></li>
          </ul>
        </td>
      </tr>
    </table>
    <!-- 岗位信息 -->
    <div id="assignGroups" class="formTable2 ui-widget-content" style="width:650px;"
         data-removeTitle='<s:text name="title.click2remove"/>'>
      <div class="ui-state-active title" style="position:relative;">
      <span class="text"><s:text name="actor.headerLabel.copyGroups"/>：
        <s:if test="%{ownedGroups == null || ownedGroups.isEmpty()}"><s:text name="label.empty"/></s:if>
      </span>
        <span id="addGroups" class="verticalMiddle ui-icon ui-icon-circle-plus"
              title='<s:text name="actor.title.click2addGroups"/>'></span>
      </div>
      <s:if test="%{ownedGroups != null && !ownedGroups.isEmpty()}">
        <ul class="horizontal">
          <s:iterator value="ownedGroups">
            <li class="horizontal ui-widget-content ui-corner-all" data-id='<s:property value="id" />'>
              <span class="text"><s:property value="name"/></span>
              <s:if test="!readonly">
              <span class="click2remove verticalMiddle ui-icon ui-icon-close"
                    title='<s:text name="title.click2remove"/>'></span>
              </s:if>
            </li>
          </s:iterator>
        </ul>
      </s:if>
    </div>
    <!-- 已分配的角色信息 -->
    <div id="assignRoles" class="formTable2 ui-widget-content" style="width:650px;"
         data-removeTitle='<s:text name="title.click2remove"/>'>
      <div class="ui-state-active title" style="position:relative;">
      <span class="text"><s:text name="actor.headerLabel.copyRoles"/>：
        <s:if test="%{ownedRoles == null || ownedRoles.isEmpty()}"><s:text name="label.empty"/></s:if>
      </span>
        <span id="addRoles" class="verticalMiddle ui-icon ui-icon-circle-plus"
              title='<s:text name="actor.title.click2addRoles"/>'></span>
      </div>
      <s:if test="%{ownedRoles != null && !ownedRoles.isEmpty()}">
        <ul class="horizontal">
          <s:iterator value="ownedRoles">
            <li class="horizontal ui-widget-content ui-corner-all" data-id='<s:property value="id" />'>
              <span class="text"><s:property value="name"/></span>
              <s:if test="!readonly">
              <span class="click2remove verticalMiddle ui-icon ui-icon-close"
                    title='<s:text name="title.click2remove"/>'></span>
              </s:if>
            </li>
          </s:iterator>
        </ul>
      </s:if>
    </div>
    <s:hidden name="copyFromId"/>
    <s:hidden name="copyToId"/>
  </s:form>
</div>