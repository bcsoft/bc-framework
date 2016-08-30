/**
 * 图像模块通用API
 */
bc.namespace("bc.image");

/**
 * 打印图片
 * @param {Integer} attachId 图片对应的附件ID
 * @param {Float} attachWidth 图片的实物宽度
 */
bc.image.print = function(attachId, attachWidth){
    if(!attachId || attachId == ""){
        alert("bc.image.prin: 必须设置 attachId 参数！");
        return;
    }
    bc.print({
        method: "POST",
        url: bc.root + "/bc/image/print",
        data: {id: attachId, width: attachWidth}
    });
};