/**
 * 奖品管理
 */
$(function () {

    var listUrl = '/O2O/shopAdmin/getAwardList';
    var removeUrl = '/O2O/shopAdmin/updateAward';
    var deleteUrl = '/O2O/shopAdmin/deleteAward';
    //奖品名字,模糊搜索请求参数
    var awardName = '';
    //初始显示第一页记录
    var pageNum = 1;
    //点击搜索,默认第一页开始显示,发生输入传入商品名字模糊搜索
    $('#search').on('change', function (e) {
        awardName = e.target.value;
        getList(1);
    });
    //初始获取第一页记录
    getList(1);
    //点击上一页重新渲染页面页码-1请求
    $("#up").click(function () {
        if (pageNum > 1) {
            getList(--pageNum);
        }
    });
    //点击一下页页码+1请求,当前页码小于分页的页码才能进行上一页
    $("#low").click(function () {
        if (pageNum < pageCount) {
            getList(++pageNum);
        }
    });

    function getList(pageIndex) {
        $.ajax({
            url: listUrl,
            type: "GET",
            cache: false,
            data: {
                pageIndex: pageIndex,
                pageSize: 5,//每页显示记录数
                awardName: awardName//奖品名模糊搜索
            },
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    var awardList = data.awardList;
                    var tempHtml = '';
                    if (data.count != 0) {
                        awardList.map(function (item) {
                            //奖品添加后默认上架状态1,上下架操作显示为下架,设置操作状态0
                            var textOp = "下架";
                            var contraryStatus = 0;
                            //如果该奖品状态为0显示上架,操作
                            if (item.enableStatus == 0) {
                                textOp = "上架";
                                contraryStatus = 1;
                            } else {
                                contraryStatus = 0;
                            }
                            tempHtml += '' + '<div class="row row-award">'
                                + '<div class="col-33" style="text-align: center" >'
                                + item.awardName
                                + '</div>'
                                + '<div class="col-33" style="text-align: center" >'
                                + item.point
                                + '</div>'
                                + '<div class="col-33" style="text-align: center">'
                                + '<a href="#" class="edit" onclick="updateAward(' + item.awardId + ')"data-id="'
                                + item.awardId
                                + '" data-status="'
                                + item.enableStatus
                                + '">编辑</a>'
                                + '<a href="#" class="delete" onclick="removeAward(' + item.awardId + ',' + contraryStatus + ')"data-id="'
                                + item.awardId
                                + '" data-status="'
                                + contraryStatus
                                + '">'
                                + textOp
                                + '</a>'
                                + '<a href="#" class="preview" data-id="'
                                + item.awardId
                                + '" data-status="'
                                + item.enableStatus
                                + '">预览</a>'
                                + '<a href="#" class="remove" onclick="deleteAward(' + item.awardId + ')" data-id="'
                                + item.awardId
                                + '" data-status="'
                                + item.enableStatus
                                + '">删除</a>'
                                + '</div>'
                                + '</div>';
                        });
                        $('.award-wrap').html(tempHtml);
                        //显示上下页
                        $('#up').attr("style", "");
                        $('#low').attr("style", "");
                        //查询结果集的分页数
                        pageCount = data.count;
                        $(".dianji").remove();
                        //动态添加页码,每次点击页码调用点击方法,传入自身对象的文本
                        for (var i = 1; i <= pageCount; i++) {
                            $('<li><a href="#" class="dianji" onclick="dianji(this)">' + i + '</a></li>').insertBefore($("#page"));
                        }
                    }
                    else {
                        $.alert("还没有奖品信息^_^");
                    }

                } else {
                    $.toast("获取失败!" + data.errMsg);
                }
            }
        });
    }

    $('#search').on('change', function (e) {
        awardName = e.target.value;
        //$('.award-wrap').empty();
        getList(1);
    });
    /**
     * 点击编辑跳转到奖品编辑页面,url拼接上该商品id
     * @param awardId
     */
    updateAward = function (awardId) {
        window.location.href = "/O2O/shopAdmin/awardOperation?awardId=" + awardId;
    };
    /**
     * 点击页码传入页码获取当前页码的数据
     * @param value
     */
    dianji = function (value) {
        //获取被点击对象的值
        pageNum = value.textContent;
        //重新获取商品列表渲染页面,传入页码值
        getList(pageNum);
    };

    /**
     * 删除当前商店下指定奖品
     * @param awardId
     */
    deleteAward = function (awardId) {
        var confir = "确定删除吗?";
        $.confirm(confir, function () {
            $.ajax({//请求修改
                url: deleteUrl,
                type: 'POST',
                cache: false,
                data: {
                    awardId: awardId
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        getList(pageNum);

                    } else {
                        $.toast("操作失败" + data.errMsg);
                    }
                }
            });
        });
    }

    /**
     * 奖品上下架操作,修改商品状态
     * @param id
     * @param enableStatus
     */
    removeAward = function (id, enableStatus) {
        var award = {};
        award.awardId = id;//传入当前商品id和状态进行修改
        award.enableStatus = enableStatus;

        var confir = "确定下架吗?";
        if (enableStatus != 0) {
            confir = "确定上架吗?";
        }
        $.confirm(confir, function () {
            $.ajax({//请求修改
                url: removeUrl,
                type: 'POST',
                cache: false,
                data: {
                    awardStr: JSON.stringify(award),
                    statusChange: true//上下架默认不需要验证码验证,验证码验证设为true
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        getList(pageNum);

                    } else {
                        $.toast("操作失败" + data.errMsg);
                    }
                }
            });
        });
    };
});
