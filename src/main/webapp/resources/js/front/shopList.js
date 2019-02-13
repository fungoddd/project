$(function () {
    //是否正在加载flag
    var loading = false;
    // 分页允许返回的最大条数
    var maxItems = 500;
    // 页码1开始
    var pageNum = 1;
    // 一页返回的最大条数
    var pageSize = 10;
    // 从url里获取parentId
    var parentId = getSelectIdString('parentId');
    //判断是否是点全部商店进来的是否携带parentId
    var isAllShopCategory = parentId ? true : false;
    // 获取商店类别列表以及区域列表的url
    var searchShopCategoryAndAreaListUrl = '/O2O/front/shopListInfo?parentId=' + parentId;
    // 获取商店列表的url(分页)
    var shopListUrl = '/O2O/front/shops';
    // 区域id
    var areaId = '';
    // 商店类别id
    var shopCategoryId = '';
    // 商店名字模糊查询
    var shopName = '';

    // 渲染商店类别列表以及区域列表提供搜索条件
    getShopCategoryAndArea();

    // 预先每页加载8条商店信息条目
    addItems(pageNum, pageSize);

    //获取商品类别列表和区域信息
    function getShopCategoryAndArea() {
        // 如果传入了parentId,则取出此商品类别一级类别下面的所有二级类别
        $.getJSON(searchShopCategoryAndAreaListUrl, function (data) {
            if (data.success) {
                // 获取返回的店铺类别列表
                var shopCategoryList = data.shopCategoryList;
                var html = '';
                html += '<a href="#" class="button" data-category-id=""> 全部类别  </a>';
                if (shopCategoryList.length != 0) {
                    //加载商品类别名
                    shopCategoryList.map(function (item) {
                        html += '<a href="#" class="button" data-category-id='
                            + item.shopCategoryId
                            + '>'
                            + item.shopCategoryName
                            + '</a>';
                    });
                }
                $('#shoplist-search-div').html(html);
                //加载区域信息
                var selectOptions = '<option value="">全部区域</option>';
                var areaList = data.areaList;
                if (areaList.length != 0) {
                    areaList.map(function (item) {
                        selectOptions += '<option value="' + item.areaId + '">' + item.areaName + '</option>';
                    });
                }
                $('#area-search').html(selectOptions);

            } else {
                $.toast("加载失败" + data.errMsg);
            }
        });
    }

    //添加商品条目
    function addItems(pageIndex, pageSize) {
        $.ajax({
            url: shopListUrl,
            type: 'GET',
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                parentId: parentId,
                areaId: areaId,
                shopCategoryId: shopCategoryId,
                shopName: shopName
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
                        data.shopList.map(function (item) {
                            html += '' + '<div class="card" data-shop-id="'
                                + item.shopId + '">' + '<div class="card-header">'
                                + item.shopName + '</div>'
                                + '<div class="card-content">'
                                + '<div class="list-block media-list">' + '<ul>'
                                + '<li class="item-content">'
                                + '<div class="item-media">' + '<img src="'
                                + getContextPath() + item.shopImg + '" width="44">' + '</div>'
                                + '<div class="item-inner">'
                                + '<div class="item-subtitle">' + item.shopDesc
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


    // 注册'infinite'事件处理函数
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

    //点击商店条,进入该商店详情页面
    $('.shop-list').on('click', '.card', function (e) {
        var shopId = e.currentTarget.dataset.shopId;
        window.location.href = '/O2O/front/frontShopDetail?shopId=' + shopId;
    });

    //选择新的商店类别重置页码,重新渲染页面搜索:重置页码,清空商店列表,按照新类别查询
    $('#shoplist-search-div').on('click', '.button', function (e) {
        //点击别的父类下的子类进来传入它的parentId,点击首页为null的商品大类进来的
        if (isAllShopCategory) {
            //如果传递过来的是一个父级类下的子就按照当前点击的shopCategory来查询
            shopCategoryId = e.target.dataset.categoryId;
            //如果之前选中了别的商品类别,先移除选中效果改为选定新选择的类别样式(点亮)
            if ($(e.target).hasClass('button-fill')) {
                $(e.target).removeClass('button-fill');
                shopCategoryId = '';
            } else {
                $(e.target).addClass('button-fill').siblings().removeClass('button-fill');
            }
            //查询条件改变,之前显示商品列表清空
            $('.list-div').empty();
            //重置页码查询第一页开始的数据
            pageNum = 1;
            addItems(pageNum, pageSize);
        } else {
            //如果传递过来的父级类别为空,则按照parentId查询(点击了全部商品进来的)
            //parentId为查询条件,查询parentId为当前点击卡片的shopCategoryId的类
            parentId = e.target.dataset.categoryId;
            //类别卡片的点亮操作,点击新的移除旧的
            if ($(e.target).hasClass('button-fill')) {
                $(e.target).removeClass('button-fill');
                parentId = '';
            } else {
                $(e.target).addClass('button-fill').siblings().removeClass('button-fill');
            }
            //查询条件改变所以要清空商店列表
            $('.list-div').empty();
            pageNum = 1;
            addItems(pageNum, pageSize);
        }
    });
    //区域搜索,发生改变情况之前信息重新搜索
    $('#area-search').on('change', function () {
        areaId = $('#area-search').val();
        //清空之前的信息
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageNum, pageSize);
    });
    //点击搜索,默认第一页开始显示,发生输入传入商品名字模糊搜索
    $('#search').on('input', function (e) {
        shopName = e.target.value;
        //清空之前的商品信息
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