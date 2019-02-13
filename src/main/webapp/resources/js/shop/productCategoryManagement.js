//var shopId = getShopIdString("shopId");
var productCategoryListUrl = "/O2O/shopAdmin/getProductCategoryList";//?shopId=" + shopId
var addProductCategoryUrl = "/O2O/shopAdmin/addProductCategories";
var removeProductCategoryUrl = "/O2O/shopAdmin/removeProductCategory";

$(function () {
    getProductCategoryList();
    add();
    addProductCategories();
    remove();
    //removeProductCategory();
});

/**
 * 获取商品类别列表
 */
function getProductCategoryList() {
    $.getJSON(productCategoryListUrl, function (data) {
            if (data.success) {//如果操作成功得到Result中的data

                var dataList = data.data;
                $(".category-wrap").html("");
                var tempHtml = "";
                if (dataList.length != 0) {
                    dataList.map(function (item) {
                        tempHtml += ''
                            + '<div class="row row-product-category now">'
                            + '<div class="col-33 product-category-name" style="text-align: center">'
                            + item.productCategoryName
                            + '</div>'
                            + '<div class="col-33" style="text-align: center">'
                            + item.priority
                            + '</div>'                                       //点击删除操作触发函数请求删除操作
                            + '<div class="col-33" style="text-align: center" onclick="removeProductCategory(' + item.productCategoryId + ')"><a href="#" class="button delete" data-id="'
                            + item.productCategoryId
                            + '">删除</a></div>' + '</div>';
                    });
                    $(".category-wrap").append(tempHtml);
                } else {
                    /* $(".category-wrap").html("");
                    var tempHtml = '<div class="row row-product-category now"><div class="col-33 product-category-name">无</div><div class="col-33">无</div><div class="col-33">无</div></div>';
                    $.toast("该商店下还未添加商品");
                    $(".category-wrap").append(tempHtml);*/
                    /*tempHtml += '<div class="row row-product-category " id="div1">'
                        + '<div class="col-33" style="text-align: center">无</div>'
                        + '<div class="col-33" style="text-align: center">无</div>'
                        + '<div class="col-33" style="text-align: center">无</div></div>';
                    $(".category-wrap").append(tempHtml);*/
                    //$("#div1").attr("style", " ");
                    $.toast("该商店下未添加商品分类");
                }

            }
            else {
                $.toast("数据跑到火星上去了^_^" + data.errCode + " " + data.errMsg);
            }
        }
    );
};

/**
 * 批量操作
 */

function add() {
    //每次点击新增添加一行
    $("#add").click(function () {
        // $("#div1").remove();
        //temp是新增的
        var tempHtml = '<div class="row row-product-category temp" >'
            + '<div class="col-33"><input class="category-input category" type="text" placeholder="输入商品类别" maxlength="20"></div>'
            + '<div class="col-33"><input class="category-input priority" type="number" placeholder="输入优先级" maxlength="3"></div>'
            + '<div class="col-33"><a href="#" class="button delete">删除</a></div>'
            + '</div>';
        $('.category-wrap').append(tempHtml);
    });
}

/**
 *批量添加商品类别
 */
function addProductCategories() {
    $("#submit").click(function () {
        var validate = true;
        var categoryValue = $(".category").val();
        var priority = $(".priority").val();
        if (categoryValue == null || categoryValue.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请添加商品类别")
            validate = false;
        } else if (priority == null || priority.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请添加商品类优先级")
            validate = false;
        } else if (priority < 0 || priority > 100) {
            $.toast("优先级在0-100");
            validate = false;
        }
        if (validate) {
            var tempArrList = $(".temp");
            var productCategoryList = [];
            //遍历新增行
            tempArrList.map(function (index, value) {
                var tempObject = {};
                tempObject.productCategoryName = $(value).find(".category").val()
                tempObject.priority = $(value).find(".priority").val();
                if (tempObject.productCategoryName && tempObject.priority) {
                    productCategoryList.push(tempObject);
                }
            });

            $.ajax({
                url: addProductCategoryUrl,
                type: "POST",
                data: JSON.stringify(productCategoryList),
                cache: false,
                contentType: "application/json",//发送信息至服务器时的内容编码
                success: function (data) {
                    if (data.success) {
                        $.toast("添加成功");
                        //重新渲染列表
                        getProductCategoryList();

                    }
                    else {
                        $.toast("添加失败:" + data.errMsg);
                    }
                }

            });
        }
    });
}

/**
 * 删除新增行
 */
function remove() {
    $('.category-wrap').on('click', '.row-product-category.temp .delete',
        function () {

            $(this).parent().parent().remove();
        });
}

/**
 * 删除指定商店下已有商品类别,通过类别id
 * @param productCategoryId
 */
function removeProductCategory(productCategoryId) {


    $.confirm('确定删除吗?', function () {
        $.ajax({
            url: removeProductCategoryUrl,
            type: 'POST',
            cache: false,
            data: {
                productCategoryId: productCategoryId,//target.dataset.id,
                //shopId: shopId
            },
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    // $.toast('删除成功！');
                    getProductCategoryList();

                } else {
                    $.toast('删除失败!');
                }
            }
        });
    });
}

