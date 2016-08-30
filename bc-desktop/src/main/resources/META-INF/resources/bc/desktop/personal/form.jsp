<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="personal.title"/>' style='font-size: 12px;'
	class="bc-page" data-type='form'
	data-js='<s:url value="/bc/desktop/personal/form.css" />,<s:url value="/bc/desktop/personal/form.js" />'
	data-saveUrl='<s:url value="/bc/personal/save" />'
	data-initMethod='bc.personal.init'
	data-option='{
		"buttons":[{"text":"<s:text name="label.changePassword"/>","click":"bc.personal.changePassword"}
			,{"text":"<s:text name="label.save"/>","click":"bc.personal.save"}],
		"minWidth":560,"minHeight":250,"modal":false,minimizable:true,maximizable:false
	}'>
	<s:form name="personalForm" theme="simple" style="height:100%;">
		<div style="margin: 10px;">
			<div style="margin-bottom: 8px; float: left;">
				<s:text name="personal.font" />
				：
			</div>
			<div id="fontSlider" data-value='<s:property value="e.font" />'
				style="float: left; width: 150px; margin: 4px 15px 0 10px;"></div>
			<div style="float: left;">
				<span id="fontSize"><s:property value="e.font" /> </span>
			</div>
		</div>
		<div style="margin: 10px; clear: both;">
			<s:text name="personal.theme" />
			：
		</div>
		<div style="height: 250px; overflow-x: hidden; overflow-y: auto;">
			<table class="themes" cellpadding="4" cellspacing="8">
				<tbody>
					<tr>
						<td class="theme ui-corner-all" data-theme="smoothness"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/smoothness/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_smoothness.png" />'>
							<div>平滑</div>
						</td>
						<td class="theme ui-corner-all" data-theme="cupertino"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/cupertino/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_cupertino.png" />'>
							<div>苹果</div>
						</td>
						<td class="theme ui-corner-all" data-theme="flick"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/flick/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_flick.png" />'>
							<div>轻弹</div>
						</td>
						<td class="theme ui-corner-all" data-theme="pepper-grinder"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/pepper-grinder/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_pepper_grinder.png" />'>
							<div>胡椒磨</div>
						</td>
						<td class="theme ui-corner-all" data-theme="south-street"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/south-street/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_south_street.png" />'>
							<div>南路边</div>
						</td>
					</tr>
					<tr>
						<td class="theme ui-corner-all" data-theme="ui-darkness"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/ui-darkness/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_ui_dark.png" />'>
							<div>暗黑</div>
						</td>
						<td class="theme ui-corner-all" data-theme="dark-hive"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/dark-hive/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_dark_hive.png" />'>
							<div>黑蜂巢</div>
						</td>
						<td class="theme ui-corner-all" data-theme="trontastic"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/trontastic/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_trontastic.png" />'>
							<div>trontastic</div>
						</td>
						<td class="theme ui-corner-all" data-theme="ui-lightness"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/ui-lightness/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_ui_light.png" />'>
							<div>明亮</div>
						</td>
						<td class="theme ui-corner-all" data-theme="redmond"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/redmond/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_windoze.png" />'>
							<div>微软</div>
						</td>
					</tr>
					<tr>
						<td class="theme ui-corner-all" data-theme="eggplant"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/eggplant/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_eggplant.png" />'>
							<div>茄子</div>
						</td>
						<td class="theme ui-corner-all" data-theme="le-frog"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/le-frog/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_le_frog.png" />'>
							<div>乐青蛙</div>
						</td>
						<td class="theme ui-corner-all" data-theme="sunny"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/sunny/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_sunny.png" />'>
							<div>晴天</div>
						</td>
						<td class="theme ui-corner-all" data-theme="overcast"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/overcast/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_overcast.png" />'>
							<div>阴天</div>
						</td>
						<td class="theme ui-corner-all" data-theme="blitzer"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/blitzer/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_blitzer.png" />'>
							<div>布雷泽</div>
						</td>
					</tr>
					<tr>
						<td class="theme ui-corner-all" data-theme="humanity"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/humanity/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_humanity.png" />'>
							<div>仁慈</div>
						</td>
						<td class="theme ui-corner-all" data-theme="mint-choc"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/mint-choc/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_mint_choco.png" />'>
							<div>薄荷巧克力</div>
						</td>
						<td class="theme ui-corner-all" data-theme="excite-bike"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/excite-bike/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_excite_bike.png" />'>
							<div>刺激的自行车</div>
						</td>
						<td class="theme ui-corner-all" data-theme="vader"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/vader/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_black_matte.png" />'>
							<div>维达</div>
						</td>
						<td class="theme ui-corner-all" data-theme="black-tie"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/black-tie/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_black_tie.png" />'>
							<div>黑领带</div>
						</td>
					</tr>
					<tr>
						<td class="theme ui-corner-all" data-theme="start"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/start/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_start_menu.png" />'>
							<div>起点</div>
						</td>
						<td class="theme ui-corner-all" data-theme="hot-sneaks"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/hot-sneaks/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_hot_sneaks.png" />'>
							<div>猛蛇</div>
						</td>
						<td class="theme ui-corner-all" data-theme="dot-luv"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/dot-luv/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_dot_luv.png" />'>
							<div>斑点</div>
						</td>
						<td class="theme ui-corner-all" data-theme="swanky-purse"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/swanky-purse/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_swanky_purse.png" />'>
							<div>时髦包包</div>
						</td>
						<td class="theme ui-corner-all" data-theme="bootstrap"
							data-css='<s:url value="/ui-libs/jquery-ui/1.8.16/themes/bootstrap/jquery.ui.theme.css" />'>
							<img
							src='<s:url value="/ui-libs/jquery-ui/themeSwitcher/images/theme_90_bootstrap.png" />'>
							<div>Bootstrap</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<s:hidden name="e.id" />
		<s:hidden name="e.status" />
		<s:hidden name="e.inner" />
		<s:hidden name="e.font" />
		<s:hidden name="e.theme" />
		<s:hidden name="e.actorId" />
	</s:form>
</div>