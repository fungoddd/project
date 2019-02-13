/*
商店的相关操作
 */

//获取商店ID,shopId不为空返回true为空返回false,如果传来了shopId就是修改没有就是注册店铺
var shopId = getSelectIdString("shopId");
//var  shopId=getId();
var isUpdate = shopId ? true : false;

//商店初始信息请求路径
var initUrl = "/O2O/shopAdmin/getShopInitInfo";

//商店注册请求路径
var resistShop = "/O2O/shopAdmin/registerShop";

//通过商店id获取商店信息
var getShopInfoById = "/O2O/shopAdmin/getShopById?shopId=" + shopId;

//修改商店信息的请求路由
var updateShopUrl = "/O2O/shopAdmin/updateShop";

$(function () {

    //如果不是修改就调用初始全部店铺信息的函数进行注册
    if (!isUpdate) {
        getShopInitInfo();
    } else {//否则调用通过店铺id获取本用户的店铺信息进行修改操作
        $("#t1").html("商店编辑");
        $("#t2").html("商店编辑");
        getShopInfo();
    }
    //给提交按钮绑定点击事件
    $("#submit").click(function () {
        //如果当前页不是修改状态,进行注册验证
        if (!isUpdate) {
            var res1 = validateRegisterHtml();
            //如果验证结果为true则进行注册请求
            if (res1) {
                registerOrUpdateShop();
            }
        }
        else {//如果是修改则直接进入修改验证
            var res2 = validateUpdateHtml();
            if (res2) {
                registerOrUpdateShop();
            }
        }
    });
});

/**
 * 通过商店id获取商店信息函数
 */
function getShopInfo() {

    $.getJSON(getShopInfoById, function (data) {
        if (data.success) {
            var shop = data.shop;
            $("#shopName").val(shop.shopName);
            $("#shopAddr").val(shop.shopAddr);
            $("#shopPhone").val(shop.phone);
            $("#shopDesc").val(shop.shopDesc);
            var shopCategory = '<option data-id="' + shop.shopCategory.shopCategoryId + '">' + shop.shopCategory.shopCategoryName + '</option>'
            var tempAreaHtml = '';
            //从返回的modelMap对象中获取到数据进行动态添加
            data.areaList.map(function (item) {
                tempAreaHtml += '<option data-id="' + item.areaId + '">' + item.areaName + '</option>'
            });
            $("#shopCategory").html(shopCategory);
            //店铺类别设置不能选择
            $("#shopCategory").attr("disabled", "disabled");
            $("#area").html(tempAreaHtml);
            //默认选择现在的店铺区域信息
            $("#area option[data-id='" + shop.area.areaId + "']").attr("selected", "selected");
        }
        else {
            $.toast(data.errMsg);
        }
    });
}

/**
 * 获取店铺初始信息函数
 */
function getShopInitInfo() {
    $.getJSON(initUrl, function (data) {
        if (data.success) {
            var tempHtml = '';
            var tempAreaHtml = '';
            //从返回的modelMap对象中获取到数据进行动态添加
            tempHtml += "<option value='' style='display: none'>请选择</option>";
            tempAreaHtml += "<option value='' style='display: none'>请选择</option>";
            data.shopCategoryList.map(function (item) {
                tempHtml += '<option data-id="' + item.shopCategoryId + '">' + item.shopCategoryName + '</option>'

            });
            data.areaList.map(function (item) {
                tempAreaHtml += '<option data-id="' + item.areaId + '">' + item.areaName + '</option>'
            });
            $("#shopCategory").html(tempHtml);
            $("#area").html(tempAreaHtml);
        } else {
            $.toast("发生了未知的错误" + data.errMsg);
        }
    });
}

/**
 * 商店注册函数
 */
function registerOrUpdateShop() {

    //把前端页面input中的数据进行封装成shop对象
    var shop = {};
    //如果是修改当前对象的shopId为url传来的id
    if (isUpdate) {
        shop.shopId = shopId
    }
    shop.shopName = $("#shopName").val();
    shop.shopAddr = $("#shopAddr").val();
    shop.phone = $("#shopPhone").val();
    //商店描述
    var shopDesc = $("#shopDesc").val();
    shop.shopDesc = shopDesc;
    if (shopDesc == null || shopDesc.replace(/(^\s*)|(\s*$)/g, "") == "") {
        shop.shopDesc = "该商家很懒什么也没留下";
    }
    //获取选中的商店分类和区域分类的id
    shop.shopCategory = {//双重否定=肯定
        shopCategoryId: $("#shopCategory").find('option').not(function () {
            return !this.selected;
        }).data('id')
    };
    shop.area = {
        areaId: $("#area").find('option').not(function () {
            return !this.selected;
        }).data('id')
    };
    //图片文件流
    var shopImg = $('#shopImg')[0].files[0];
    //新建一个表单对象
    var formData = new FormData();
    //将文件流和shop转为String添加到formData
    formData.append('shopImg', shopImg);

    formData.append('shopStr', JSON.stringify(shop));//把数组先转为字符串

    var verifyCode = $("#kaptcha").val();
    if (!verifyCode) {
        $.toast("请输入验证码!");
        return;
    }
    //验证码添加到
    formData.append("verifyCode", verifyCode);
    $.ajax({//如果是更改请求更改路由否则请求注册路由
        url: (isUpdate ? updateShopUrl : resistShop),
        type: "POST",
        data: formData,
        contentType: false,
        processData: false,//默认为false,设置为true提交时不会序列化data直接使用，processData设置为false之后，再去序列化一下参数
        cache: false,//默认true,false时不缓存页面
        async: true,//默认true为异步请求
        dataType: "json",
        success: function (data) {
            if (data.success) {
                $.toast("提交成功!");
                $("#kaptchaImg").click();
                //alert("提交成功")
            } else {
                $.toast("提交失败:" + data.errMsg);
                $("#kaptchaImg").click();
                //alert("提交失败")
            }
            //每次提交后更换验证码
            $("#kaptchaImg").click();
        },
        /*complete: function () {
        },
        error: function () {
        }*/
    });

}


/**
 * 验证前端用户输入信息是否正确
 * 验证结果默认为true,如果有一个信息不正确都会返回false
 */
function validateRegisterHtml() {
    var shopName = $("#shopName").val();
    var shopCategory = $("#shopCategory").val();
    var area = $("#area").val();
    var shopAddr = $("#shopAddr").val();
    var kapCode = $("#kaptcha").val();
    var telPhone = $("#shopPhone").val();
    var shopImg = $("#shopImg").val();
    var validateInfo = true;
    if (shopName == null || shopName.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入商店名");
        validateInfo = false;
    }
    else if (shopCategory == null || shopCategory.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请选择商店类别");
        validateInfo = false;
    }
    else if (area == null || area.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请选择所在区域");
    }
    else if (shopAddr == null || shopAddr.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请填写详细地址");
        validateInfo = false;
    }
    else if (telPhone == null || telPhone.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请填写联系电话");
        validateInfo = false;
    }
    else if (!/^[1][3,4,5,7,8][0-9]{9}$/.test(telPhone)) {
        $.toast("手机号码格式错误!");
        validateInfo = false;
    }
    else if (shopImg == null || shopImg.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请上传商店图片");
        validateInfo = false;
    }
    else if (kapCode == null || kapCode.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("未输入验证码");
        validateInfo = false;
    }
    return validateInfo;
}

/**
 * 修改页验证
 */
function validateUpdateHtml() {
    var shopName = $("#shopName").val();
    var shopCategory = $("#shopCategory").val();
    var area = $("#area").val();
    var shopAddr = $("#shopAddr").val();
    var kapCode = $("#kaptcha").val();
    var telPhone = $("#shopPhone").val();
    var shopImg = $("#shopImg").val();
    var validateInfo = true;
    if (shopName == null || shopName.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入商店名");
        validateInfo = false;
    }
    else if (shopCategory == null || shopCategory.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请选择商店类别");
        validateInfo = false;
    }
    else if (area == null || area.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请选择所在区域");
    }
    else if (shopAddr == null || shopAddr.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请填写详细地址");
        validateInfo = false;
    }
    else if (telPhone == null || telPhone.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请填写联系电话");
        validateInfo = false;
    }
    else if (!/^[1][3,4,5,7,8][0-9]{9}$/.test(telPhone)) {
        $.toast("手机号码格式错误!");
        validateInfo = false;
    }
    else if (kapCode == null || kapCode.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("未输入验证码");
        validateInfo = false;
    }
    return validateInfo;
}