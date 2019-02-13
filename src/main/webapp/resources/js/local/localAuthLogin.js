/**
 * 本地账号登录
 */
$(function () {
    //登录请求的url
    var loginUrl = '/O2O/localAuth/localAuthLogin';
    //地址栏获取到userType,1用户其余店家
    var userType = getSelectIdString('userType');
    //登录次数,累计三次弹出验证码
    var loginCount = 0;

    $('#submit').click(function () {
        var username = $('#username').val();
        var password = $('#password').val();
        var verifyCode = $('#kaptcha').val();
        var needVerify = false;
        //如果登录三次需要输入验证码
        if (loginCount >= 3) {
            if (verifyCode == null || verifyCode.replace(/(^\s*)|(\s*$)/g, "") == "") {
                $.toast("请输入验证码");
                return;
            } else {
                needVerify = true;
            }
        }
        if (username == null || username.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请输入用户名");
            return;
        }
        else if (password == null || password.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请输入密码");
            return;
        }
        $.ajax({
            url: loginUrl,
            async: false,
            cache: false,
            type: "post",
            dataType: 'json',
            data: {
                username: username,
                password: password,
                verifyCode: verifyCode,
                needVerify: needVerify
            },
            success: function (data) {
                if (data.success) {
                    $.toast('登录成功!');
                    //如果用户是在前端展示系统绑定的返回前端主页
                    if (userType == 1) {
                        setTimeout('window.location.href = "/O2O/front/index"', 1500);
                    } else {//否则跳转到店家系统
                        setTimeout('window.location.href = "/O2O/shopAdmin/shopList"', 1500);
                    }
                } else {
                    $.toast('登录失败!' + data.errMsg);

                    loginCount++;
                    //超过三次显示验证码
                    if (loginCount >= 3) {
                        $('#verifyPart').show();
                    }
                }
                $('#kaptchaImg').click();
            }
        });
    });
})