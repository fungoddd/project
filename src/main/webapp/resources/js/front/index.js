$(function () {
    //获取头条列表以及一级类别列表的url
    var url = "/O2O/front/homePageInfo";

    $.getJSON(url, function (data) {
        if (data.success) {
            // 获取后台传递过来的头条列表
            const headLineList = data.headLineList;
            var swiperHtml = '';
            // 遍历头条列表，并拼接出轮播图组
            headLineList.map(function (item) {
                swiperHtml += ''
                    + '<div class="swiper-slide img-wrap">'
                    + '<img class="banner-img" src="' + getContextPath() + item.lineImg + '" alt="' + item.lineName + '">'
                    + '</div>';
            });
            // 将轮播图组赋值给前端HTML控件
            $('.swiper-wrapper').html(swiperHtml);
            // 设定轮播图轮换时间为3
            $(".swiper-container").swiper({
                autoplay: 3000,
                // 用户对轮播图操作时，是否自动停止autoplay
                autoplayDisableOnInteraction: false
            });
            // 获取后台传递过来的大类列表
            var shopCategoryList = data.shopCategoryList;
            var categoryHtml = '';
            // 遍历大类列表，拼接出俩俩（col-50）一行的类别
            shopCategoryList.map(function (item) {
                categoryHtml += ''
                    + '<div class="col-50 shop-classify" data-category=' + item.shopCategoryId + '>'
                    + '<div class="word">'
                    + '<p class="shop-title">' + item.shopCategoryName + '</p>'
                    + '<p class="shop-desc">' + item.shopCategoryDesc + '</p>'
                    + '</div>'
                    + '<div class="shop-classify-img-warp">'
                    + '<img class="shop-img" src="' + getContextPath() + item.shopCategoryImg + '">'
                    + '</div>'
                    + '</div>';
            });
            // 将拼接好的类别赋值给前端HTML控件进行展示
            $('.row').html(categoryHtml);
        }
    });

    // 若点击"我的",则显示侧栏
    $('#me').click(function () {
        $.openPanel('#panel-left-demo');
    });
    //点击父类(首页显示的)商品类别名字,传入当前行的商品类别id,打开下级子类页面
    $('.row').on('click', '.shop-classify', function (e) {
        var shopCategoryId = e.currentTarget.dataset.category;
        var newUrl = "/O2O/front/frontShopList?parentId=" + shopCategoryId;
        window.location.href = newUrl;
    });

});