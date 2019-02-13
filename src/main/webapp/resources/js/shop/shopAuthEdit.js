/**
 * 商店员工授权编辑
 */
$(function () {
    var shopAuthId = getSelectIdString('shopAuthId');
    //通过主键获取指定商店授权信息url
    var infoUrl = '/O2O/shopAdmin/getShopAuthMapById?shopAuthId=' + shopAuthId;
    //进行编辑操作
    var shopAuthUpdateUrl = '/O2O/shopAdmin/updateShopAuthMap';

    if (shopAuthId) {
        getInfo(shopAuthId);
    } else {
        window.location.href = '/O2O/shopAdmin/shopManagement';
    }

    //获取员工信息
    function getInfo(id) {
        $.getJSON(infoUrl, function (data) {
            if (data.success) {
                var shopAuthMap = data.shopAuthMap;
                $('#shopauth-name').val(shopAuthMap.name);
                $('#title').val(shopAuthMap.title);
            }
        });
    }

    $('#submit').click(function () {
        var shopAuth = {};
        shopAuth.name = $('#shopauth-name').val();
        shopAuth.title = $('#title').val();
        shopAuth.shopAuthId = shopAuthId;
        var verifyCode = $('#kaptcha').val();
        if (shopAuth.name == null || shopAuth.name.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast('请输入员工名');
            return;
        } else if (shopAuth.title == null || shopAuth.title.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast('请输入职位');
            return;
        }
        else if (verifyCode == null || verifyCode.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast('请输入验证码');
            return;
        }

        $.ajax({
            url: shopAuthUpdateUrl,
            type: 'POST',
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {
                shopAuthMapStr: JSON.stringify(shopAuth),
                verifyCode: verifyCode
            },
            success: function (data) {
                if (data.success) {
                    $.toast('提交成功!');
                    $('#kaptchaImg').click();
                } else {
                    $.toast('提交失败!' + data.errMsg);
                    $('#kaptchaImg').click();
                }
            }
        });
    });

});