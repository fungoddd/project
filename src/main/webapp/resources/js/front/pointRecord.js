/**
 * 用户积分兑换记录
 */
$(function () {
    //是否正在加载flag
    var loading = false;
    // 分页允许返回的最大条数
    var maxItems = 500;
    // 页码1开始
    var pageNum = 1;
    // 一页返回的最大条数
    var pageSize = 10;

    // 获取个人的积分兑换列表
    var listUrl = '/O2O/front/getUserAwardMapListByUser';
    // 奖品名字模糊查询
    var awardName = '';

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
                        data.userAwardMapList.map(function (item) {
                            var status = "";
                            //根据usedStatus显示是否领取
                            if (item.usedStatus == 0) {
                                status = '未领取';
                            } else if (item.usedStatus == 1) {
                                status = '已领取';
                            }
                            html += '<div class="card" data-user-award-id="' + item.userAwardId + '" data-shop-id="' + item.shop.shopId + '">'
                                + '<div class="card-header">' + item.shop.shopName + '<span class="pull-right">' + status + '</span></div>'
                                + '<div class="card-content">'
                                + '<div class="list-block media-list"><ul>'
                                + '<li class="item-content">'
                                + '<div class="item-inner">'
                                + '<div class="item-subtitle">' + item.award.awardName + '</div></div></li></ul>'
                                + '</div></div><div class="card-footer">'
                                + '<p class="color-gray">'
                                + new Date(item.createTime).Format("yyyy-MM-dd hh:mm:ss")
                                + '</p><span>消耗积分:' + item.point + '</span></div></div>'

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

    //点击卡片跳到奖品详情
    $('.list-div').on('click', '.card', function (e) {
        var userAwardId = e.currentTarget.dataset.userAwardId;
        var shopId=e.currentTarget.dataset.shopId;
        window.location.href = '/O2O/front/myAwardDetail?shopId='+shopId+'&userAwardId=' + userAwardId;
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