/**
 * 商品详情信息
 */
$(function () {
    //获取url中的商品id
    var productId = getSelectIdString('productId');
    //获取url中的shopId
    var shopId = getSelectIdString('shopId');
    //商品详情Url
    //var productDetailUrl = "/O2O/shopAdmin/getProductById?productId=" + productId;
    var productDetailUrl = "/O2O/front/getProductDetail?productId=" + productId;
    //点击积分兑换
    exchange = function () {
        window.location.href = "/O2O/front/awardList?shopId=" + shopId;
    }
    $.getJSON(productDetailUrl, function (data) {

        if (data.success) {
            //获取商品信息
            var product = data.product;
            //商品图片
            $('#product-img').attr('src', getContextPath() + product.imgAddr);
            //商品更新时间
            $('#product-time').text(new Date(product.lastEditTime).Format("yyyy-MM-dd") + "日");
            if (product.point != null && product.point != undefined) {
                $("#product-point").text("购买可获得" + product.point + "积分");
            }
            //商品名称
            $('#product-name').text("商品名称: " + product.productName);
            //商品描述
            $('#product-desc').text("商品描述: " + product.productDesc);
            $('#product-normal').text("原价:" + product.normalPrice + "元")
            $('#product-promotion').text("现价只要:" + product.promotionPrice + "元");
            var imgListHtml = '';
            product.productImgList.map(function (item) {
                imgListHtml += '<div> <img src="' + getContextPath() + item.imgAddr + '" width="100%"/></div>';
            });
            //如果已经登录生成二维码
            if (data.needQRCode) {
                // 生成购买商品的二维码供商家扫描
                imgListHtml += '<div style="text-align: center;"> <img src="/O2O/front/generateQRCode4product?shopId=' + shopId + '&productId='
                    + product.productId + '" width="40%"/></div>';
            }
            $('#imgList').html(imgListHtml);
        }
    });

    $('#me').click(function () {
        $.openPanel('#panel-left-demo');
    });
    $.init();
});
