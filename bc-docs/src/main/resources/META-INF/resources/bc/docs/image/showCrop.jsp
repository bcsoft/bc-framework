<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div title='<s:text name="showCrop.title"/>' data-type='form'
	class="bc-page" data-js='<s:url value="/ui-libs/jcrop/0.9.9/themes/default/jquery.Jcrop.css?ts=0" />,<s:url value="/ui-libs/jcrop/0.9.9/jquery.Jcrop.min.js?ts=0" />,<s:url value="/bc/docs/image/showCrop.js" />'
	data-initMethod='bc.showCrop.init'
	data-option='<s:property value="pageOption" escapeHtml="false"/>'>
	<s:form name="showCropForm" theme="simple" style="height:100%;width:100%;">
		<table class="table" cellspacing="0" cellpadding="0" style="height:100%;">
			<tr style="line-height:1px;height:1px;font-size: 1px;">
				<td style="width:<s:property value="cropSize"/>px;">&nbsp</td>
				<td style="width:156px;">&nbsp</td>
				<td>&nbsp</td>
			</tr>
			<tr>
				<td rowspan="2" style="text-align: center; vertical-align: middle;overflow:hidden;border-right: 1px solid #ccc;padding:8px;">
					<div style="height:<s:property value="cropSize"/>px;width:<s:property value="cropSize"/>px;overflow: hidden;">
						<table style="width:100%;height:100%;">
						<tr>
						<td style="text-align: center; vertical-align: middle;">
							<div style="margin: auto;display: inline-block;max-height:400px;">
								<s:if test="%{id != null}">
									<img src='<s:url value="/bc/image/download"><s:param name='id' value='id'/><s:param name='ts' value='ts'/></s:url>' 
										id="source"/>
								</s:if>
								<s:else>
									<img src='<s:url value="%{empty}"><s:param name='ts' value='ts'/></s:url>' id="source" />
								</s:else>
							</div>
						</td>
						</tr>
						</table>
					</div>
				</td>
				<td style="text-align: center; vertical-align: top;padding:8px;color:#888;font-size: 14px;">
					<div class="" style="text-align: left;">提示：</div>
					<div style="text-align: left;text-indent: 2em;">在左侧图片上鼠标框选确定裁剪区域，拖拉裁剪区边缘的小四方手柄调整裁剪区的大小。</div>
					<div style="text-align: left;margin-top:8px;font-size: 12px;">原图：</div>
					<div style="margin:auto;font-size: 12px;"><span id="zoomInfo"></span></div>
				</td>
				<td>&nbsp</td>
			</tr>
			<tr>
				<td style="text-align: center; vertical-align: bottom;padding:8px;overflow: hidden;">
					<div style="min-width:110px;text-align: center;">
						<div style='background-color: black; margin:auto;width: <s:property value="preWidth"/>px; height: <s:property value="preHeight"/>px; overflow: hidden;border: 1px solid #ccc;'>
							<s:if test="%{id != null}">
								<img src='<s:url value="/bc/image/download"><s:param name='id' value='id'/><s:param name='ts' value='ts'/></s:url>' id="preview" />
							</s:if>
							<s:else>
								<img src='<s:url value="%{empty}"><s:param name='ts' value='ts'/></s:url>' id="preview" />
							</s:else>
						</div>
						<div style="margin:auto;font-size: 12px;color:#888;">预览(<s:property value="preWidth"/>x<s:property value="preHeight"/>)</div>
						<div class="bc-image ui-widget ui-state-default ui-corner-all"  
							data-puid='<s:property value="puid"/>' data-ptype='<s:property value="ptype"/>' data-callback='bc.showCrop.finishUpload' 
							data-extensions='<s:property value="extensions"/>'
							style="margin:auto;position: relative;color: #2A5DB0;line-height:40px;height:40px;width:5em;font-size: 22px;margin-top:16px;cursor:pointer;">更换图片<input 
							type="file" class="uploadImage" name="uploadImage" 
							style="position: absolute;left: 0;top: 0;width: 100%;height: 100%;filter: alpha(opacity = 10);opacity: 0;cursor: pointer;">
						</div>
					</div>
				</td>
				<td>&nbsp</td>
			</tr>
		</table>
		<s:hidden name="id"/>
		<s:hidden name="empty"/>
		<s:hidden name="preWidth"/>
		<s:hidden name="preHeight"/>
		<s:hidden name="puid"/>
		<s:hidden name="ptype"/>
		<s:hidden id="ignore" value="true"/>
	</s:form>
</div>