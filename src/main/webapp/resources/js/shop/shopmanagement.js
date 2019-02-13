/**
 * 获取用户管理信息
 */
$(function () {
    //解析url中的shopId
    var shopId = getSelectIdString("shopId");
    //var shopId = getId();

    //请求用户管理信息handler,拼接解析到的shopId
    var shopManagementInfoUrl = "/O2O/shopAdmin/getShopManagementInfo?shopId=" + shopId;
    //var shopManagementInfoUrl = "/shopAdmin/getShopManagementInfo/" + shopId;

    $.getJSON(shopManagementInfoUrl, function (data) {
        //如果非法操作返回redirect进行重定向
        if (data.redirect) {
            window.location.href = data.url;
        } else {//如果当前shop对象的shopId不为空
            if (data.shopId != null && data.shopId != undefined) {
                shopId = data.shopId;
                //商店信息的链接拼接商店id进行修改操作(没有商店id就是注册)
                $("#shopInfo").attr("href", "/O2O/shopAdmin/shopOperation?shopId=" + shopId);
                //$("#shopInfo").attr("href", "/shopAdmin/shopOperation/" + shopId);
            }
        }
    });
});