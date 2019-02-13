$(function () {

    var userName = '';

    var pageNum = 1;
    //点击上一页重新渲染页面页码-1请求
    $("#up").click(function () {
        if (pageNum > 1) {
            getList(--pageNum);
        }
    });
    //点击一下页页码+1请求,当前页小分页页数才能进行下一页
    $("#low").click(function () {
        if (pageNum < pageCount) {
            getList(++pageNum);
        }
    });

    getList(1);

    function getList(pageIndex) {
        var listUrl = '/O2O/shopAdmin/getUserShopMapList';
        $.ajax({
            url: listUrl,
            type: "GET",
            cache: false,
            data: {
                pageIndex: pageIndex,
                pageSize: 5,//默认每页五条数据
                username: userName
            },
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    var userShopMapList = data.userShopMapList;
                    var tempHtml = '';
                    if (userShopMapList.length != 0) {
                        userShopMapList.map(function (item) {
                            tempHtml += ''
                                + '<div class="row row-usershopcheck">'
                                + '<div class="col-50" style="text-align: center">' + item.user.name + '</div>'
                                + '<div class="col-50" style="text-align: center">' + item.point + '</div>'
                                + '</div>';
                        });
                        $('.usershopcheck-wrap').html(tempHtml);
                        pageCount = data.count;
                        $(".dianji").remove();
                        //动态添加页码,每次点击页码调用点击方法,传入自身对象
                        for (var i = 1; i <= pageCount; i++) {
                            $('<li><a href="#" class="dianji" onclick="dianji(this)">' + i + '</a></li>').insertBefore($("#page"));
                        }
                        //查到的页数不为零显示上一页下一页
                        $('#up').attr("style", "");
                        $('#low').attr("style", "");
                    } else {

                        $.alert("还没有积分信息^_^");
                    }

                } else {
                    $.toast("获取失败!" + data.errMsg);
                }
            }
        });
    }

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

    $('#search').on('change', function (e) {
        userName = e.target.value;
        //$('.usershopcheck-wrap').empty();
        getList(1);
    });


});