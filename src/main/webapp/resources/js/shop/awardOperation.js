//URL中获取奖品id
var awardId = getSelectIdString("awardId");
//获取奖品信息
var getAwardInfoUrl = "/O2O/shopAdmin/getAwardById?awardId=" + awardId;
//更改奖品信息
var AwardUpdateUrl = "/O2O/shopAdmin/updateAward";
//添加奖品信息
var addAwardUrl = "/O2O/shopAdmin/addAward";
//验证url中是否有奖品id,有修改没有添加
var isUpdate = awardId ? true : false;


$(function () {
    //如果url中存在商品id
    if (awardId) {
        $("#t1").html("奖品编辑");
        $("#t2").html("奖品编辑");
        //则进行页面更改操作
        getAwardInfo();

    }
    //点击提交
    $("#submit").click(function () {
        if (!isUpdate) {
            var res = validateAdd();
            //如果验证结果为true则进行注册请求
            if (res) {
                addAwardOrUpdate();
            }
        }
        else {//如果是修改则直接进入修改验证
            var res = validateUpdate();
            if (res) {
                addAwardOrUpdate();
            }
        }
    });

});

/**
 * 获取商品信息
 */
function getAwardInfo() {
    $.getJSON(getAwardInfoUrl, function (data) {
        if (data.success) {
            //从返回的json对象中获取奖品对象信息添加给表单
            var award = data.award;
            $('#awardName').val(award.awardName);
            $('#awardDesc').val(award.awardDesc);
            $('#priority').val(award.priority);
            $('#point').val(award.point);

        } else {
            $toast("要访问的数据在火星^_^:" + data.errMsg);
        }
    });
};

/**
 * 添加奖品操作
 */
function addAwardOrUpdate() {
    var award = {};//获取前端输入的值封装成对象给后台
    award.awardName = $('#awardName').val();
    var awardDesc = $('#awardDesc').val();
    award.awardDesc = awardDesc;
    if (awardDesc == null || awardDesc.replace(/(^\s*)|(\s*$)/g, "") == "") {
        award.awardDesc = "该店家很懒什么也没留下";
    }
    var point = $('#point').val();
    award.point = point;
    if (point == null || point.replace(/(^\s*)|(\s*$)/g, "") == "") {
        award.point = 0;
    }
    award.priority = $('#priority').val();

    award.awardId = awardId;

    var thumbnail = $('#smallImg')[0].files[0];

    var formData = new FormData();

    formData.append('thumbnail', thumbnail);

    formData.append('awardStr', JSON.stringify(award));

    var verifyCode = $('#kaptcha').val();
    if (!verifyCode) {
        $.toast('请输入验证码！');
        return;
    }
    formData.append("verifyCode", verifyCode);
    $.ajax({//如果是更改(url中有奖品id)进行修改请求,否则添加请求
        url: (isUpdate ? AwardUpdateUrl : addAwardUrl),
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
    var awardName = $('#awardName').val();
    var priority = $('#priority').val();
    var smallImg = $("#smallImg").val();
    var kapCode = $("#kaptcha").val();
    if (awardName == null || awardName.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入奖品名");
        validateInfo = false;
    } else if (priority == null || priority.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入优先级");
        validateInfo = false;
    }
    else if (priority < 0 || priority > 100) {
        $.toast("优先级在0-100");
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

    var awardName = $('#awardName').val();

    var priority = $('#priority').val();

    var smallImg = $("#smallImg").val();

    var kapCode = $("#kaptcha").val();
    if (awardName == null || awardName.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入奖品名");
        validateInfo = false;
    } else if (priority == null || priority.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请输入优先级");
        validateInfo = false;
    }
    else if (priority < 0 || priority > 100) {
        $.toast("优先级在0-100");
        validateInfo = false;
    } else if (smallImg == null || smallImg.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("请上传奖品缩略图");
        validateInfo = false;
    }
    else if (kapCode == null || kapCode.replace(/(^\s*)|(\s*$)/g, "") == "") {
        $.toast("未输入验证码");
        validateInfo = false;
    }
    return validateInfo;
}