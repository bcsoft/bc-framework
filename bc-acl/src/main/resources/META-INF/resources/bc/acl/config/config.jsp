<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="accessControl.title"/>' data-type='form' class="bc-page"
	data-js='<s:url value="/bc/identity/identity.js" />,<s:url value="/bc/acl/config/config.js" />'
	data-initMethod='bc.aclConfigDialog.init'
	data-option='<s:property value="pageOption"/>' style="overflow: hidden;">
<s:set name="r" value="readonly"/>
<s:set name="b1" value="isControlBit(1)"/>
<s:set name="b2" value="isControlBit(2)"/>
<s:hidden name="r" />
<s:hidden name="b1"/>
<s:hidden name="b2"/>
<s:hidden name="docId" />
<s:hidden name="docType" />
<s:hidden name="docName" />
<s:hidden name="role" />
<s:form name="accessControlForm" theme="simple" cssClass="vlayout" style="width: 100%;height:100%">
    <s:if test="r"></s:if><s:else>
	<div class="ui-widget-content title" style="position:relative;">
		<span class="text">访问权限配置：</span>
		<ul class="inputIcons">
			<li class="inputIcon ui-icon ui-icon-person" title='添加用户' data-click="bc.aclConfigDialog.addUsers"></li>
			<li class="inputIcon ui-icon ui-icon-contact" title='添加岗位' data-click="bc.aclConfigDialog.addGroups"></li>
			<li class="inputIcon ui-icon ui-icon-home" title='添加单位或部门' data-click="bc.aclConfigDialog.addUnitOrDepartments"></li>
            <li id="up" class="inputIcon ui-icon ui-icon-arrowthick-1-n" title='上移选中项'></li>
            <li id="down" class="inputIcon ui-icon ui-icon-arrowthick-1-s" title='下移选中项'></li>
			<li id="delete" class="inputIcon ui-icon ui-icon-close" title='删除选中项'></li>
		</ul>
	</div>
    </s:else>
    <div class="bc-grid header">
        <table class="table" cellspacing="0" cellpadding="0" style="width:100%">
            <colgroup>
                <col style="width: 3em;"/>
                <col style="width: 10em;"/>
                <col style="width: 10em;"/>
                <col style="width: auto;"/>
            </colgroup>
            <tr class="ui-widget-content row">
                <td class="first" title="点击全选或反选" style="text-align: center;vertical-align: middle"><input type="checkbox" name="toggleSellectAll"/></td>
                <td class="middle" style="text-align: center">访问者</td>
                <td class="middle" style="text-align: center"><s:text name="accessControl.role"/></td>
                <td class="last"><s:text name="accessControl.lastModified"/></td>
            </tr>
        </table>
    </div>
	<div class="bc-grid data flex" style="border:0; overflow-y:auto;">
        <table class="table" id="accessActors" cellspacing="0" cellpadding="0" style="width:100%">
            <colgroup>
                <col style="width: 3em;"/>
                <col style="width: 10em;"/>
                <col style="width: 10em;"/>
                <col style="width: auto;"/>
            </colgroup>
			<s:iterator value="accessActors" status="s">
			<tr class="ui-widget-content row" data-origin-order='<s:property value="orderNo" />'>
				<td class="id first toggleSellect" style="text-align: center"><span class="ui-icon"></span><span
                        class="index"><s:property value="#s.index + 1" /></span></td>
				<td class="middle" title='<s:property value="actor.name" />'><s:hidden name="actor.id"/><s:property value="actor.name" /></td>
				<td class="middle">
                    <s:if test='b1'>
                        <label><input type="checkbox" name="BIT_1"<s:if test="isAllowableBit(role, 1)"> checked</s:if>
                        <s:if test="r"> disabled="disabled"</s:if>>查阅</label>
                    </s:if>
                    <s:if test='b2'>
                        <label><input type="checkbox" name="BIT_2"<s:if test="isAllowableBit(role, 2)"> checked</s:if>
                        <s:if test="r"> disabled="disabled"</s:if>>编辑</label>
                    </s:if>
				</td>
				<td class="last"><s:property value="modifier.name" />(<s:date name="modifiedDate" format="yyyy-MM-dd HH:mm"/>)</td>
			</tr>
			</s:iterator>
		</table>
	</div>
</s:form>
</div>