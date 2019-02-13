//URL中获取商品id
var productId = getSelectIdString("productId");
//获取商品信息
var getProductInfoUrl = "/O2O/shopAdmin/getProductById?productId=" + productId;
//获取当前商店的商品类别列表
var getProductCategoryListUrl = "/O2O/shopAdmin/getProductCategoryList";
//更改商品信息
var productUpdateUrl = "/O2O/shopAdmin/updateProduct";
//添加商品信息
var addProductUrl = "/O2O/shopAdmin/addProduct";
//验证url中是否有商品id,有修改没有添加
var isUpdate = productId ? true : false;


$(function () {
    //如果url中存在商品id
    if (productId) {
        $("#t1").html("商品编辑");
        $("#t2").html("商品编辑");
        //则进行页面更改操作
        getProductInfo();

    } else {//否则页面是商品类别,添加加商品
        getCategoryList();

    }

    //获取商品类别
    getCategoryList();

    //如果上传的图片数小于五才能继续添加
    $('.detail-img-div').on('change', '.detail-img:last-child', function () {
        if ($('.detail-img').length < 5) {
            $('#detail-img').append('<input type="file" class="detail-img">');
        }
    });
    //点击提交
    $("#submit").click(function () {
        if (!isUpdate) {
            var res = validateAdd();
            //如果验证结果为true则进行注册请求
            if (res) {
                addProductOrUpdate();
            }
        }
        else {//如果是修改则直接进入修改验证
            var res = validateUpdate();
            if (res) {
                addProductOrUpdate();
            }
        }
    });

});


/**
 * 获取商品信息
 */
function getProductInfo() {
    $.getJSON(getProductInfoUrl, function (data) {
        if (data.success) {
            //从返回的json对象中获取商品对象信息添加给表单
            var product = data.product;
            $('#productName').val(product.productName);
            $('#productDesc').val(product.productDesc);
            $('#priority').val(product.priority);
            $('#point').val(product.point);
            $('#normalPrice').val(product.normalPrice);
            $('#promotionPrice').val(product.promotionPrice);
            var optionHtml = '';
            var optionArr = data.productCategoryList;
            var optionSelected = product.productCategory.productCategoryId;
            optionArr.map(function (item) {
                var isSelect = optionSelected === item.productCategoryId ? 'selected' : '';
                optionHtml += '<option data-value="'
                    + item.productCategoryId
                    + '"'
                    + isSelect
                    + '>'
                    + item.productCategoryName
                    + '</option>';
            });
            $('#category').html(optionHtml);
        } else {
            $toast("要访问的数据在火星^_^:" + data.errMsg);
        }
    });
};

/**
 * 为商品添加操作提供该商店下可选的商品类别列表
 */
function getCategoryList() {
    $.getJSON(getProductCategoryListUrl, function (data) {
        if (data.success) {
            var productCategoryList = data.data;
            if (productCategoryList.length <= 0) {
                $.toast("请先添加商品类别在添加商品");
                setTimeout('window.location.href="/O2O/shopAdmin/shopManagement"', 2000);
            }
            var optionHtml = '';
            optionHtml += "<option value='' style='display: none'>请选择</option>";
            productCategoryList.map(function (item) {
                optionHtml += '<option data-value="' + item.productCategoryId + '">' + item.productCategoryName + '</option>';
            });
            $('#category').html(optionHtml);
        } else {
            $.toast("要访问的数据在火星^_^" + data.errMsg);
        }
    });
};

/**
 * 添加商品操作
 */
function addProductOrUpdate() {
    var product = {};//获取前端输入的值封装成对象给后台
    product.productName = $('#productName').val();
    var productDesc = $('#productDesc').val();
    product.productDesc = productDesc;
    if (productDesc == null || productDesc.replace(/(^\s*)|(\s*$)/g, "") == "") {
        product.productDesc = "该店家很懒什么也没留下";
    }
    var point = $('#point').val();
    product.point = point;
    if (point == null || point.replace(/(^\s*)|(\s*$)/g, "") == "") {
        product.point = 0;
    }
    product.priority = $('#priority').val();
    product.normalPrice = $('#normalPrice').val();
    product.promotionPrice = $('#promotionPrice').val();
    product.productCategory = {
        productCategoryId: $('#category').find('option').not(function () {
            return !this.selected;
        }).data('value')
    };
    product.productId = productId;

    var thumbnail = $('#smallImg')[0].files[0];

    var formData = new FormData();

    formData.append('thumbnail', thumbnail);
    //遍历要提交的图片转成文件流添加到fromData对象中
    $('.detail-img').map(
        function (index) {//大于0才进行操作
            if ($('.detail-img')[index].files.length > 0) {
                formData.append('productImg' + index,
                    $('.detail-img')[index].files[0]);
            }
        });
    formData.append('productStr', JSON.stringify(product));
    var verifyCode = $('#kaptcha').val();
    if (!verifyCode) {
        $.toast('请输入验证码！');
        return;
    }
    formData.append("verifyCode", verifyCode);
    $.ajax({//如果是更改(url中有商品id)进行修改请求,否则添加请求
        url: (isUpdate ? productUpdateUrl : addProductUrl),
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        cache: false,
        success: function (data) {
            if (data.success) {
                $.toast('提交成功!');
                $('#kaptchaImg').click();
            } else {
                $.toast('提交失败:' + data.errMsg);
                $('#kaptchaImg').click();
            }
            //每次提交后更换验证码
            $("#kaptchaImg").click()
        }
    });
};

/**
 * 修改页验证
 * @returns {boolean}
 */
function validateUpdate() {
    var validateInfo = true;
    var productName = $('#productName').val();
    var category = $('#category').val();
    var priority = $('#priority').val();
    var normalPrice = $('#normalPrice').val();
    var promotionPrice = $('#promotionPrice').val();
    var smallImg = $("#smallImg").val();
    var productImg = $(".detail-img").val();
    var kapCode = $("#kaptcha").val();
    if (productName == null || productName.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入商品名");
        validateInfo = false;
    } else if (category == null || category.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请选择商品分类");
        validateInfo = false;
    } else if (priority == null || priority.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入优先级");
        validateInfo = false;
    }
    else if (priority < 0 || priority > 100) {
        $.toast("优先级在0-100");
        validateInfo = false;
    } else if (normalPrice == null || normalPrice.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入原价");
        validateInfo = false;
    }
    else if (normalPrice < 0 || promotionPrice < 0) {
        $.toast("请输入正确的价格");
        validateInfo = false;
    } else if (promotionPrice == null || promotionPrice.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入现价");
        validateInfo = false;
    }
    else if (kapCode == null || kapCode.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("未输入验证码");
        validateInfo = false;
    }
    return validateInfo;
}

/**
 * 添加页验证
 * @returns {boolean}
 */
function validateAdd() {
    var validateInfo = true;

    var productName = $('#productName').val();
    var category = $('#category').val();
    var priority = $('#priority').val();
    var normalPrice = $('#normalPrice').val();
    var promotionPrice = $('#promotionPrice').val();
    var smallImg = $("#smallImg").val();
    var productImg = $(".detail-img").val();
    var kapCode = $("#kaptcha").val();
    if (productName == null || productName.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入商品名");
        validateInfo = false;
    } else if (category == null || category.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请选择商品分类");
        validateInfo = false;
    } else if (priority == null || priority.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入优先级");
        validateInfo = false;
    }
    else if (priority < 0 || priority > 100) {
        $.toast("优先级在0-100");
        validateInfo = false;
    } else if (normalPrice == null || normalPrice.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入原价");
        validateInfo = false;
    }
    else if (normalPrice < 0 || promotionPrice < 0) {
        $.toast("请输入正确的价格");
        validateInfo = false;
    } else if (promotionPrice == null || promotionPrice.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入现价");
        validateInfo = false;
    } else if (smallImg == null || smallImg.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请上传商品缩略图");
        validateInfo = false;
    }
    else if (productImg == null || productImg.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请上传至少一张商品详情图");
        validateInfo = false;
    }
    else if (kapCode == null || kapCode.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("未输入验证码");
        validateInfo = false;
    }
    return validateInfo;
}