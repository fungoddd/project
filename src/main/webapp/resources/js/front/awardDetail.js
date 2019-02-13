/**
 * 奖品详情信息
 */
$(function () {
    //获取url中的兑换记录id
    var userAwardId = getSelectIdString('userAwardId');
    //获取shopId
    var shopId = getSelectIdString('shopId');
    //奖品详情Url
    var awardDetailUrl = "/O2O/front/getUserAwardMapById?userAwardId=" + userAwardId;
    $.getJSON(awardDetailUrl, function (data) {

        if (data.success) {
            //获取奖品信息
            var award = data.award;
            //奖品图片
            $('#award-img').attr('src', getContextPath() + award.awardImg);
            //兑换时间
            $('#create-time').text(new Date(data.userAwardMap.createTime).Format("yyyy-MM-dd hh:mm:ss"));
            //奖品名称
            $('#award-name').text("奖品名称: " + award.awardName);
            //奖品描述
            $('#award-desc').text("奖品描述: " + award.awardDesc);
            var imgListHtml = '';
            //如果没在实体店兑换,则生成二维码
            if (data.usedStatus == 0) {
                imgListHtml += '<div style="text-align: center"> <img src="/O2O/front/generateQRCode4award?shopId=' + shopId + '&userAwardId='
                    + userAwardId + '" width="40%"/></div>';
            }
            $('#imgList').html(imgListHtml);
        }
    });

    $('#me').click(function () {
        $.openPanel('#panel-left-demo');
    });
    $.init();
});
