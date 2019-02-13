/**
 * 用户退出
 */
$(function () {

    $("#log-out").click(function () {
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
    })
    //获取session对象
    $(function () {
        $.ajax({
            url: "/O2O/localAuth/getSession",
            type: "POST",
            cache: false,
            dataType: "json",
            success: function (data) {
                if (data.success) {

                } else {
                    $("#l1").hide();
                    $("#l2").hide();
                    $("#l3").hide();
                    $("#l4").hide();
                    $("#l5").hide();
                    $("#log-out").text("用户登录");
                }
            }
        });
    });


});