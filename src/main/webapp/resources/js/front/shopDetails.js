$(function () {
    //是否正在加载flag
    var loading = false;
    // 分页允许返回的最大条数
    var maxItems = 500;
    // 页码1开始
    var pageNum = 1;
    // 一页返回的最大条数
    var pageSize = 10;
    // 从url里获取shopId
    var shopId = getSelectIdString('shopId');
    // 获取商店详细信息已经其商品类别列表url
    var searchProductCategoryUrl = '/O2O/front/shopDetails?shopId=' + shopId;
    // 获取商品列表的url(分页)
    var productListUrl = '/O2O/front/getProductByShop';
    // 商店类别id
    var productCategoryId = '';
    // 商店名字模糊查询
    var productName = '';

    //点击积分兑换
    exchange = function () {
        window.location.href = "/O2O/front/awardList?shopId=" + shopId;
    }


    // 渲染商品类别列表提供搜索条件
    getProductCategoryList();

    // 预先每页加载8条商店信息条目
    addItems(pageNum, pageSize);

    //获取当前商店信息以及商品类别列表
    function getProductCategoryList() {
        // 获取本店铺信息以及商品类别信息列表
        $.getJSON(searchProductCategoryUrl, function (data) {
            if (data.success) {
                //动态加载出该商店的详细信息以及其下的商品类别列表
                var shop = data.shop;
                $('#shop-cover-pic').attr('src', getContextPath() + shop.shopImg);
                $('#shop-update-time').html(new Date(shop.lastEditTime).Format("yyyy-MM-dd") + '日');
                $('#shop-name').html("本店大名:&nbsp;&nbsp;" + shop.shopName);
                $('#shop-desc').html("商家描述:&nbsp;&nbsp;" + shop.shopDesc);
                $('#shop-addr').html("商家地址:&nbsp;&nbsp;" + shop.shopAddr);
                $('#shop-phone').html("联系方式:&nbsp;&nbsp;" + shop.phone);

                var productCategoryList = data.productCategoryList;
                var html = ''
                html += '<a href="#" class="button" data-product-search-id="">全部商品</a>';
                if (productCategoryList.length > 0) {
                    productCategoryList.map(function (item) {
                        html += '<a href="#" class="button" data-product-search-id='
                            + item.productCategoryId
                            + '>'
                            + item.productCategoryName
                            + '</a>';
                    });
                }
                $('#shopdetail-button-div').html(html);

            } else {
                $.toast("加载失败" + data.errMsg);
            }
        });
    }

    //添加商品条目
    function addItems(pageIndex, pageSize) {
        $.ajax({
            url: productListUrl,
            type: 'GET',
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                productCategoryId: productCategoryId,
                productName: productName,
                shopId: shopId
            },
            async: false,
            cache: false,
            success: function (data) {
                if (data.success) {
                    //最大加载商品条数
                    maxItems = data.count;
                    var html = '';
                    if (maxItems > 0) {
                        //遍历商品列表动态添加
                        data.productList.map(function (item) {
                            html += '' + '<div class="card" data-product-id='
                                + item.productId + '>'
                                + '<div class="card-header">' + item.productName
                                + '</div>' + '<div class="card-content">'
                                + '<div class="list-block media-list">' + '<ul>'
                                + '<li class="item-content">'
                                + '<div class="item-media">' + '<img src="'
                                + getContextPath() + item.imgAddr + '" width="44">' + '</div>'
                                + '<div class="item-inner">'
                                + '<div class="item-subtitle">' + item.productDesc
                                + '</div>' + '</div>' + '</li>' + '</ul>'
                                + '</div>' + '</div>' + '<div class="card-footer">'
                                + '<p class="color-gray">'
                                + new Date(item.lastEditTime).Format("yyyy-MM-dd")
                                + '日更新</p>' + '<span>点击查看</span>' + '</div>'
                                + '</div>';
                        });
                        $('.list-div').append(html);

                        //每次加载的条数
                        var lastIndex = $('.list-div .card').length;
                        //如果加载的条数>=查到的总记录数
                        if (lastIndex >= maxItems) {
                            // 加载完毕，则注销无限加载事件，以防不必要的加载
                            //$.detachInfiniteScroll($('.infinite-scroll'));
                            // 删除加载提示符
                            $('.infinite-scroll-preloader').hide();
                            loading = true;
                        } else {
                            $('.infinite-scroll-preloader').show();
                            //重置加载flag
                            loading = false;
                        }
                        pageNum += 1;
                    }
                    else {
                        // 隐藏加载提示符
                        $('.infinite-scroll-preloader').hide();
                    }
                } else {
                    $.toast("加载失败" + data.errMsg);
                }
            }
        });
    }

    // 注册'infinite'事件处理函数,下滑屏幕自动进行分页搜索
    $(document).on('infinite', '.infinite-scroll-bottom', function () {
        // 如果正在加载，则退出
        if (loading) {
            return;
        }
        // 设置flag
        loading = true;
        // 模拟1s的加载过程
        setTimeout(function () {
            // 添加新条目
            addItems(pageNum, pageSize);
            //更新上次加载的条数
            //lastIndex = $('.list-div .card').length;
            //容器发生改变,如果是js滚动，需要刷新滚动
            $.refreshScroller();
        }, 1000);
    });

    // 选择新的商品类别之后,重置页码,清空原先的商品列表,按照新的条件去查询
    $('#shopdetail-button-div').on('click', '.button', function (e) {
        productCategoryId = e.target.dataset.productSearchId;
        if (productCategoryId) {
            if ($(e.target).hasClass('button-fill')) {
                $(e.target).removeClass('button-fill');
                productCategoryId = '';
            } else {
                $(e.target).addClass('button-fill').siblings().removeClass('button-fill');
            }
            $('.list-div').empty();
            pageNum = 1;
            addItems(pageNum, pageSize);
        } else {
            //点击全部类别卡片的点亮操作,点击新的移除旧的
            if ($(e.target).hasClass('button-fill')) {
                $(e.target).removeClass('button-fill');
                productCategoryId = '';
            } else {
                $(e.target).addClass('button-fill').siblings().removeClass('button-fill');
            }
            //查询条件改变所以要清空商店列表
            $('.list-div').empty();
            pageNum = 1;
            addItems(pageNum, pageSize);
        }
    });

    // 点击商品的卡片进入该商品的详情页面
    $('.list-div').on('click', '.card', function (e) {
        var productId = e.currentTarget.dataset.productId;
        window.location.href = '/O2O/front/productListDetail?shopId=' + shopId + '&productId=' + productId;
    });
    //点击搜索,默认第一页开始显示,发生输入传入商品名字模糊搜索
    $('#search').on('input', function (e) {
        //商品名参数为输入的值
        productName = e.target.value;
        //清空之前显示的信息,从1页开始搜索
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageNum, pageSize);
    });
    //点击"我"弹出侧栏
    $('#me').click(function () {
        $.openPanel('#panel-left-demo');
    });
    //初始化页面
    $.init();


});