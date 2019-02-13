/**
 * 本地用户修改密码
 */
$(function () {
    //修改密码的url
    var changePwdUrl = '/O2O/localAuth/updateLocalAuthPwd';
    //获取地址栏URL中的userType,userType为1就是前端展示系统
    var userType = getSelectIdString('userType');

    $('#submit').click(function () {
        var username = $('#username').val();
        var password = $('#password').val();
        var newpassword = $('#newpassword').val();
        var repassword = $('#repassword').val();
        var verifyCode = $('#kaptcha').val();

        if (username == null || username.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请输入用户名");
            return;
        }
        else if (password == null || password.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请输入原密码");
            return;
        } else if (username == newpassword) {
            $.toast("用户名和密码不能相同")
            return;
        } else if (newpassword == null || newpassword.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请输入新密码");
            return;
        } else if (password == newpassword) {
            $.toast("新密码不能和旧密码相同")
            return;
        } else if (!/^[a-zA-Z0-9_]{6,12}$/.test(newpassword)) {
            $.toast("新密码格式不正确")
            return;
        } else if (repassword == null || repassword.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请确认新密码");
            return;
        }
        else if (repassword != newpassword) {
            $.toast("两次密码输入不一致");
            return;
        }
        else if (verifyCode == null || verifyCode.replace(/(^\s*)|(\s*$)/g, "") == "") {
            $.toast("请输入验证码");
            return;
        }
        $.ajax({
            url: changePwdUrl,
            async: false,
            cache: false,
            type: "post",
            dataType: 'json',
            data: {
                username: username,
                password: password,
                newPassword: newpassword,
                verifyCode: verifyCode
            },
            success: function (data) {
                if (data.success) {
                    $.toast('修改成功!将为您跳转至登录界面');
                    //如果修改成功需要重新登录,跳转到登录界面,并拼接对应用户的userType
                    if (userType == 1) {
                        setTimeout('window.location.href = "/O2O/localAuth/login?userType=' + userType + '"', 2000);
                    } else {
                        setTimeout('window.location.href = "/O2O/localAuth/login?userType=' + userType + '"', 2000);
                    }

                } else {
                    $.toast('修改失败!' + data.errMsg);
                    $('#kaptchaImg').click();
                }
            }
        });
    });

});