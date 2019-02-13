//获取url中商品id
//var shopId = getSelectIdString('shopId');

//获取商品列表url
var getProductListUrl = '/O2O/shopAdmin/getProductListByShop'//?shopId=' + shopId;
//下架上架操作修改状态
var removeProductUrl = '/O2O/shopAdmin/updateProduct';
//删除商品操作url
var deleteProductUrl = '/O2O/shopAdmin/deleteProduct';
//商品名字,模糊搜索请求参数
var productName = '';
//初始显示第一页记录
var pageNum = 1;
$(function () {
    //点击搜索,默认第一页开始显示,发生输入传入商品名字模糊搜索
    $('#search').on('change', function (e) {
        productName = e.target.value;
        getProductList(1);
    });
    //初始获取第一页记录
    getProductList(1);

    //点击上一页重新渲染页面页码-1请求
    $("#up").click(function () {
        if (pageNum > 1) {
            getProductList(--pageNum);
        }
    });

    //点击一下页页码+1请求,当前页码小于分页的页码才能进行上一页
    $("#low").click(function () {
        if (pageNum < pageCount) {
            getProductList(++pageNum);
        }
    });
});

/**
 * 获取商品列表
 * @param productList
 */
function handelList(productList) {
    var tempHtml = '';
    productList.map(function (item) {
        //商品添加后默认上架状态1,上下架操作显示为下架,设置操作状态0
        var textOp = "下架";
        var contraryStatus = 0;
        //如果该商品状态为0显示上架,操作
        if (item.enableStatus == 0) {
            textOp = "上架";
            contraryStatus = 1;
        } else {
            contraryStatus = 0;
        }
        tempHtml += '' + '<div class="row row-product">'
            + '<div class="col-33" style="text-align: center">'
            + item.productName
            + '</div>'
            + '<div class="col-33" style="text-align: center">'
            + item.point//积分 //商品的优先级priority
            + '</div>'
            + '<div class="col-33" style="text-align: center">'  //点击编辑跳转到商品编辑页面,传入当前行该商品id
            + '<a href="#" class="edit" onclick="update(' + item.productId + ')" data-id="'
            + item.productId
            + '" data-status="'
            + item.enableStatus
            + '">编辑</a>'                //点击上下架操作按钮触发函数,传入该行商品信息的商品id和状态上下架操作
            + '<a href="#" class="delete" onclick="removeProduct(' + item.productId + ',' + contraryStatus + ')" data-id="'
            + item.productId
            + '" data-status="'
            + contraryStatus //商品的状态
            + '">'
            + textOp//上架下架的信息
            + '</a>'
            + '<a href="#" class="preview" data-id="'
            + item.productId
            + '" data-status="'
            + item.enableStatus
            + '">预览</a>'
            + '<a href="#" class="remove" onclick="deleteProduct(' + item.productId + ')" data-id="'
            + item.productId
            + '" data-status="'
            + item.enableStatus
            + '">删除</a>'
            + '</div>'
            + '</div>';
    });
    $('.product-wrap').html(tempHtml);
}

/**
 * 点击页码传入页码获取当前页码的数据
 * @param value
 */
function dianji(value) {
    //获取被点击对象的值
    pageNum = value.textContent;
    //重新获取商品列表渲染页面,传入页码值
    getProductList(pageNum);
};

/**
 * 通过传入页码获取商品列表
 * @param pageIndex
 */
function getProductList(pageIndex) {
    $.ajax({
        url: getProductListUrl,
        type: "GET",
        cache: false,
        data: {
            pageIndex: pageIndex,
            pageSize: 5,//每页显示记录数
            productName: productName//商品名模糊搜索,触发此函数
            //productCategoryId:22,
            //productName:'珍珠'
        },
        dataType: "json",
        success: function (data) {
            if (data.success) {
                if (data.count != 0) {
                    //获取商品列表
                    handelList(data.productList);
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
                } else {
                    $.alert("商品不在地球上^_^")
                }
            } else {
                $.toast("获取失败:" + data.errMsg);
            }
        }
    });
}

/**
 * 点击编辑跳转到商品编辑页面,url拼接上该商品id
 * @param productId
 */
function update(productId) {
    window.location.href = "/O2O/shopAdmin/productOperation?productId=" + productId;
};

/**
 * 商品上下架操作,修改商品状态
 * @param id
 * @param enableStatus
 */
function removeProduct(id, enableStatus) {
    var product = {};
    product.productId = id;//传入当前商品id和状态进行修改
    product.enableStatus = enableStatus;

    var confir = "确定下架吗?";
    if (enableStatus != 0) {
        confir = "确定上架吗?";
    }
    $.confirm(confir, function () {
        $.ajax({//请求修改
            url: removeProductUrl,
            type: 'POST',
            cache: false,
            data: {
                productStr: JSON.stringify(product),
                statusChange: true//上下架默认不需要验证码验证,验证码验证设为true
            },
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    getProductList(pageNum);
                } else {
                    $.toast("操作失败" + data.errMsg);
                }
            }
        });
    });
}

/**
 * 删除当前商店下指定商品
 * @param productId
 */
function deleteProduct(productId) {
    var confir = "确定删除吗?";
    $.confirm(confir, function () {
        $.ajax({//请求修改
            url: deleteProductUrl,
            type: 'POST',
            cache: false,
            data: {
                productId: productId
            },
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    getProductList(pageNum);
                } else {
                    $.toast("操作失败" + data.errMsg);
                }
            }
        });
    });
}

/**
 * 获取搜索框的值为商品名,模糊搜索
 * @returns {*}
 */
/*
function getProductName() {
    var productNameSeach = $("#search").val();
    //如果搜索框为空不为空,商品名参数就为输入的值,进行ajax请求后台,否则传入null
    if (productNameSeach != null) {
        return productNameSeach;
    } else {
        return null;
    }
}*/

/*$('.product-wrap')
     .on(
         'click',
         'a',
         function (e) {
             var target = $(e.currentTarget);
             if (target.hasClass('edit')) {//点击编辑跳转到商品编辑,url中拼接商品id
                 window.location.href = '/shopAdmin/productOperation?productId='
                     + e.currentTarget.dataset.id;
             } else if (target.hasClass('preview')) {//点击预览跳转到商品预览,url中拼接商品id
                 window.location.href = '/frontend/productdetail?productId='
                     + e.currentTarget.dataset.id;
             }
             else if (target.hasClass('delete')) {
                 removeProduct(e.currentTarget.dataset.id,
                     e.currentTarget.dataset.status);
             }

         });
           //点击新增跳转到商品添加,(添加到当前商店下,session中有当前商店对象信息)
    $("#new").click(function () {
        window.location.href = '/shopAdmin/productOperation';
    });
*/
