/**
 * 商店员工授权管理
 */
$(function () {

    //获取该商店授权信息列表
    var listUrl = '/O2O/shopAdmin/getShopAuthMapListByShopId';
    //取消授权
    var deleteUrl = '/O2O/shopAdmin/updateShopAuthMap';
    //删除
    var removeUrl = '/O2O/shopAdmin/removeShopAuthMap';
    var pageNum = 1;
    //点击上一页重新渲染页面页码-1请求
    $("#up").click(function () {
        if (pageNum > 1) {
            getShopListByPageHelper(--pageNum);
        }
    });
    //点击一下页页码+1请求,当前页小分页页数才能进行下一页
    $("#low").click(function () {
        if (pageNum < pageCount) {
            getShopListByPageHelper(++pageNum);
        }
    });

    function getList(pageIndex) {
        $.ajax({
            url: listUrl,
            type: "GET",
            cache: false,
            data: {
                pageIndex: pageIndex,
                pageSize: 5,//默认每页五条数据

            },
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    var shopauthList = data.shopAuthMapList;
                    var tempHtml = '';
                    if (shopauthList.length > 0) {
                        shopauthList.map(function (item) {
                            //初始员工为授权状态
                            var textOp = '取消授权';
                            var status = 0;
                            if (item.enableStatus == 0) {
                                //如果状态为0,说明当前已经被取消,显示恢复权限按钮
                                textOp = '恢复权限';
                                status = 1;
                            } else {
                                status = 0;
                            }
                            tempHtml += '<div class="row row-shopauth"><div class="col-33" style="text-align: center">' + item.name + '</div>';
                            //如果职称flag不为0说明是员工,显示编辑等操作
                            if (item.titleFlag != 0) {
                                tempHtml += '<div class="col-33" style="text-align: center">' + item.title + '</div>' + '<div class="col-33" style="text-align: center">'
                                    + '<a href="#" class="edit" data-employee-id="' + item.employeeId + '" data-auth-id="' + item.shopAuthId + '" style="text-align: center" >编辑</a>'
                                    + '<a href="#" class="status"  data-auth-id="' + item.shopAuthId + '" data-status="' + status + '" style="text-align: center">' + textOp + '</a>'
                                    + '<a href="#" class="delete" data-employee-id="' + item.employeeId + '" data-auth-id="' + item.shopAuthId + '" style="text-align: center" >删除</a>'
                                    + '</div>';
                            } else {
                                //否则是店家本人,不允许操作
                                tempHtml += '<div class="col-33" style="text-align: center">' + item.title + '</div><div class="col-33" style="text-align: center"><span>不可操作</span></div>';
                            }
                            tempHtml += '</div>'
                        });
                        $('.shopauth-wrap').html(tempHtml);
                        pageCount = data.count;
                        $(".dianji").remove();
                        //动态添加页码,每次点击页码调用点击方法,传入自身对象
                        for (var i = 1; i <= pageCount; i++) {
                            $('<li><a href="#" class="dianji" onclick="dianji(this)">' + i + '</a></li>').insertBefore($("#page"));
                        }
                        //查到的页数不为零显示上一页下一页
                        $('#up').attr("style", "");
                        $('#low').attr("style", "");

                    }
                    else {
                        $.alert("该商店还没有授权信息");
                    }

                }
            }
        });
    }

    getList(1);

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

    //点击进行操作按钮进行对应的操作
    $('.shopauth-wrap').on('click', 'a', function (e) {
        var target = $(e.currentTarget);
        //如果点击带有edit的a标签跳转到编辑页面
        if (target.hasClass('edit')) {
            window.location.href = '/O2O/shopAdmin/shopAuthEditPage?shopAuthId=' + e.currentTarget.dataset.authId;
        } else if (target.hasClass('status')) {
            //点击带有status的a标签进行权限操作
            changeStatus(e.currentTarget.dataset.authId, e.currentTarget.dataset.status);
        } else if (target.hasClass('delete')) {
            removeShopAuthMap(e.currentTarget.dataset.authId);
        }
    });

    //删除员工操作
    function removeShopAuthMap(id) {
        $.confirm('确定删除吗?删除后不可恢复', function () {
            $.ajax({
                url: removeUrl,
                type: 'POST',
                data: {
                    shopAuthId: id
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        //重新渲染页面
                        getList(pageNum);
                    } else {
                        $.toast('操作失败!' + data.errMsg);
                    }
                }
            });
        });
    }

    //授权操作
    function changeStatus(id, status) {
        var shopAuth = {};
        shopAuth.shopAuthId = id;
        shopAuth.enableStatus = status;
        var value = '确定取消授权吗?';
        if (status == 1) {
            value = '确定授权吗?';
        }
        $.confirm(value, function () {
            $.ajax({
                url: deleteUrl,
                type: 'POST',
                data: {
                    //json对象转为字符串
                    shopAuthMapStr: JSON.stringify(shopAuth),
                    statusChange: true
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        //重新渲染页面
                        getList(pageNum);
                    } else {
                        $.toast('操作失败!' + data.errMsg);
                    }
                }
            });
        });
    }


    //新增授权信息
    // $('#new').click(function () {
    //     window.location.href = '/O2O/shopAdmin/shopauthedit';
    // });
});