/**
 * 用户绑定本平台账号
 */
$(function () {
    //绑定本地账号的url
    var bindUrl = '/O2O/localAuth/bindLocalAuth';
    //获取地址栏URL中的userType,userType为1就是前端展示系统
    var userType = getSelectIdString('userType');

    $('#submit').click(function () {
        var username = $('#username').val();
        var password = $('#password').val();
        var repassword = $('#repassword').val();
        var verifyCode = $('#kaptcha').val();

        if (username == null || username.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请输入用户名");
            return;
        } else if (!/^[a-zA-Z0-9_]{6,12}$/.test(username)) {
            $.toast("用户名格式不正确")
            return;
        }
        else if (password == null || password.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请输入密码");
            return;
        } else if (!/^[a-zA-Z0-9_]{6,12}$/.test(password)) {
            $.toast("密码格式不正确")
            return;
        } else if (username == password) {
            $.toast("用户名和密码不能相同")
            return;
        } else if (repassword == null || repassword.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请确认密码");
            return;
        }
        else if (repassword != password) {
            $.toast("两次密码输入不一致");
            return;
        }
        else if (verifyCode == null || verifyCode.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请输入验证码");
            return;
        }
        $.ajax({
            url: bindUrl,
            async: false,
            cache: false,
            type: "post",
            dataType: 'json',
            data: {
                username: username,
                password: password,
                verifyCode: verifyCode

            },
            success: function (data) {
                if (data.success) {
                    $.toast('绑定成功!');
                    //如果用户是在前端展示系统绑定的返回前端主页
                    if (userType == 1) {
                        window.location.href = '/O2O/front/index';
                    } else {//否则跳转到店家系统
                        window.location.href = '/O2O/shopAdmin/shopList';
                    }
                } else {
                    $.toast('绑定失败!' + data.errMsg);
                    $('#kaptchaImg').click();
                }
            }
        });
    });
});