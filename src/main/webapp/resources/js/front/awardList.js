$(function () {
    //是否正在加载flag
    var loading = false;
    // 分页允许返回的最大条数
    var maxItems = 500;
    // 页码1开始
    var pageNum = 1;
    // 一页返回的最大条数
    var pageSize = 10;
    // 从url里获取商店id
    var shopId = getSelectIdString('shopId');
    // 获取该商店奖品列表
    var listUrl = '/O2O/front/getAwardList';
    //兑换奖品url
    var exchangeUrl = '/O2O/front/addUserAwardMap';
    // 奖品名字模糊查询
    var awardName = '';
    //积分
    var totalPoint = 0;
    var canProceed = false;

    var text = '请登录操作'
    // 预先每页加载8条奖品信息条目
    addItems(pageNum, pageSize);

    //添加奖品条目
    function addItems(pageIndex, pageSize) {
        $.ajax({
            url: listUrl,
            type: 'GET',
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                shopId: shopId,
                awardName: awardName
            },
            async: false,
            cache: false,
            success: function (data) {
                if (data.success) {
                    //最大加载商品条数
                    maxItems = data.count;
                    var html = '';
                    if (maxItems > 0) {
                        //遍历奖品列表动态添加
                        data.awardList.map(function (item) {
                            if (data.totalPoint != null && data.totalPoint != undefined) {
                                //用户如何有积分显示领取
                                text = '点击领取'
                            }
                            html += '<div class="card" data-award-id="' + item.awardId + '" data-point="' + item.point + '">'
                                + '<div class="card-header">' + item.awardName + '<span class="pull-right">所需积分' + item.point + '</span></div>'
                                + '<div class="card-content">'
                                + '<div class="list-block media-list"><ul>'
                                + '<li class="item-content">'
                                + '<div class="item-media"><img src="' + getContextPath() + item.awardImg + '" width="44"></div>'
                                + '<div class="item-inner">'
                                + '<div class="item-subtitle">' + item.awardDesc + '</div></div></li></ul>'
                                + '</div></div><div class="card-footer">'
                                + '<p class="color-gray">'
                                + new Date(item.lastEditTime).Format("yyyy-MM-dd")
                                + '日更新</p><span>' + text + '</span></div></div>'

                        });
                        if (data.totalPoint != null && data.totalPoint != undefined) {
                            //用户在该店有积分显示
                            canProceed = true;
                            $('#title').text('当前积分' + data.totalPoint);
                            totalPoint = data.totalPoint;
                        }
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

    //点击奖品条,兑换奖品
    $('.award-list').on('click', '.card', function (e) {
        //如果用户在该商店拥有积分并且大于该奖品兑换需要的积分
        if (canProceed && totalPoint > e.currentTarget.dataset.point) {
            $.confirm('需要消耗' + e.currentTarget.dataset.point + '积分,确定兑换吗?', function () {
                $.ajax({
                    url: exchangeUrl,
                    type: 'POST',
                    cache: false,
                    async: false,
                    data: {
                        awardId: e.currentTarget.dataset.awardId,
                        shopId: shopId,
                        point: e.currentTarget.dataset.point
                    },
                    success: function (data) {
                        if (data.success) {
                            $.toast('兑换成功!');

                            addItems(pageNum, pageSize);
                        } else {
                            $.toast('兑换失败!' + data.errMsg);
                        }
                    }
                });
            });
        } else {
            $.toast('积分余额不足');
        }
    });
    //点击搜索,默认第一页开始显示,发生输入传入奖品名字模糊搜索
    $('#search').on('input', function (e) {
        awardName = e.target.value;
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