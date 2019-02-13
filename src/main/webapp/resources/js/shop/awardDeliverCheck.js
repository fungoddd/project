/**
 * 顾客积分兑换记录
 */
$(function () {
    var awardName = '';
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
        var listUrl = '/O2O/shopAdmin/getUserAwardMapList';
        $.ajax({
            url: listUrl,
            type: "GET",
            cache: false,
            data: {
                pageIndex: pageIndex,
                pageSize: 5,//默认每页五条数据
                awardName: awardName

            },
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    var userAwardMapList = data.userAwardMapList;
                    var tempHtml = '';
                    if (userAwardMapList.length != 0) {
                        userAwardMapList.map(function (item) {
                            tempHtml += '' + '<div class="row row-awarddeliver">'
                                + '<div class="col-20" style="text-align: center">' + item.award.awardName + '</div>'
                                + '<div class="col-40 awarddeliver-time" style="text-align: center">' + new Date(item.createTime).Format("yyyy-MM-dd hh:mm:ss") + '</div>'
                                + '<div class="col-15" style="text-align: center">' + item.user.name + '</div>'
                                + '<div class="col-10" style="text-align: center">' + item.point + '</div>'
                                + '<div class="col-15" style="text-align: center">' + item.operator.name + '</div>'
                                + '</div>';
                        });
                        $('.awarddeliver-wrap').html(tempHtml);
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
                        //隐藏上下页
                        /*$('#up').attr("style", "display:none");
                        $('#low').attr("style", "display:none");*/
                        $.alert("还没有积分兑换记录^_^");
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
        awardName = e.target.value;
        //$('.awarddeliver-wrap').empty();
        getList(1);
    });


});