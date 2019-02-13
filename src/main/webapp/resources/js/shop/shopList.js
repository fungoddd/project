/**
 * 获取商店列表(分页)
 */
var getShopListByPageHelperUrl = "/O2O/shopAdmin/getShopListPageHelper"
//var getShopListUrl = "/O2O/shopAdmin/getShopList";
//商店名字,模糊搜索请求参数
var shopName = '';
//初始显示第一页记录
var pageNum = 1;

$(function () {
    //初始显示第页码的记录
    getShopListByPageHelper(pageNum);
    //点击搜索,默认第一页开始显示,发生输入传入商品名字模糊搜索
    $('#search').on('change', function (e) {
        shopName = e.target.value;
        getShopListByPageHelper(1);
    });


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


});

/**
 * 获取店家商店列表
 * @param pageIndex
 */
function getShopListByPageHelper(pageIndex) {
    $.ajax({
        url: getShopListByPageHelperUrl,
        type: "GET",
        cache: false,
        data: {
            pageIndex: pageIndex,
            pageSize: 5,//默认每页五条数据
            shopName: shopName
        },
        dataType: "json",
        success: function (data) {
            if (data.success) {
                //显示用户名
                handleUser(data.user);
                //显示退出系统
                //var html = '<a href="#" id="log-out" onclick="loginout()" userType="2" class="button button-big button-fill button-danger">退出系统</a>'
                //$("#logindiv").html(html);

                if (data.count != 0) {
                    //显示商店列表
                    handleList(data.shopList);
                    //var count = Math.ceil(data.count / 6);
                    pageCount = data.count;
                    $(".dianji").remove();
                    //动态添加页码,每次点击页码调用点击方法,传入自身对象
                    for (var i = 1; i <= pageCount; i++) {
                        $('<li><a href="#" class="dianji" onclick="dianji(this)">' + i + '</a></li>').insertBefore($("#page"));
                    }
                    //显示上下页
                    $('#up').attr("style", "");
                    $('#low').attr("style", "");
                } else {
                    $.alert("商店在火星请去注册^_^")
                }

            }
        }
    });
};

/**
 * 退出登录
 */

/*
function loginout() {
    $.ajax({
        url: "/O2O/localAuth/loginOut",
        type: "POST",
        async: false,
        cache: false,
        dataType: 'json',
        success: function (data) {
            if (data.success) {
                //拼接上userType
                var userType = $('#log-out').attr('userType');
                //退回到登录界面
                window.location.href = '/O2O/localAuth/login?userType=' + userType;
                return false;
            }
        }, error: function (data, error) {
            $.toast(error)
        }
    })
}*/

/**
 * 点击页码传入页码获取当前页码的数据
 * @param value
 */
function dianji(value) {
    //获取被点击对象的值
    pageNum = value.textContent;
    //重新获取商品列表渲染页面,传入页码值
    getShopListByPageHelper(pageNum);
};

/**
 * 显示用户名
 * @param user
 */
function handleUser(user) {
    $("#username").text("欢迎回来!" + user.name);
    //显示注册新店按钮
    //$("#register").attr("style", "");

};

/**
 * 显示店家个人商店列表信息
 * @param shopList
 */
function handleList(shopList) {
    var tempHtml = '';
    shopList.map(function (item) {
        tempHtml +=
            '<div class="row row-shop">'
            + '<div class="col-40" >' + item.shopName + '</div>'
            + '<div class="col-40" >' + shopStatus(item.enableStatus) + '</div>'//商店状态
            + '<div class="col-20" >' + goShop(item.enableStatus, item.shopId) + '</div>'//进入商店管理界面
            + '</div>'
    });
    //添加div
    $('.shop-wrap').html(tempHtml);

    //店铺状态信息,如果状态为0显示审核中
    function shopStatus(status) {
        if (status == 0) {
            return '审核中';
        } else if (status == 1) {
            return '<font color="#32cd32">审核通过</font>';
        } else {
            return '<font color="red">非法店铺</font>';
        }
    };

    //进入商店管理界面,传入状态和shopId,如果状态不为1无法操作
    function goShop(status, shopId) {
        if (status == 1) {
            return '<a href="/O2O/shopAdmin/shopManagement?shopId=' + shopId + '">管理</a>';
            //return '<a href="/shopAdmin/shopManagement/' + shopId + '">管理</a>';
        } else {
            return "禁止";
        }
    };
};


/**
 * 获取搜索框的值为商店名字
 * @returns {*}
 */
/*
function getShopName() {
    var shopNameSearch = $("#search").val();
    //如果搜索框为空不为空,商店名参数就为输入的值,进行ajax请求后台,否则传入null
    if (shopNameSearch != null) {
        return shopNameSearch;
    } else {
        return null;
    }
}*/

/*
function getShopList(pageIndex) {

    $.ajax({
        url: getShopListUrl,
        type: "GET",
        data: {pageIndex: pageIndex},
        dataType: "json",
        success: function (data) {
            if (data.success) {
                //显示用户名
                handleUser(data.user);
                //显示商店列表
                handleList(data.shopList);

                var count = data.count / 2// Math.ceil(data.count / 6);
                //alert(count);
                $(".dianji").remove();
                //动态添加页码,每次点击页码调用点击方法,传入自身对象
                for (var i = 1; i <= count; i++) {
                    $('<li><a href="#" class="dianji" onclick="dianji(this)">' + i + '</a></li>').insertBefore($("#page"));
                }

            }
        }
    });
};
*/