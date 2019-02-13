/*
公用页面
 */



//通过url传过来的key(shopId)获取shopId的值
function getSelectIdString(name) {
    //通过正则匹配出参数名
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)")
    var r = window.location.search.substr(1).match(reg)
    if (r != null) {
        return decodeURIComponent(r[2])
    }
    return '';
}

/**
 * 获取url中最后一个斜杠后的内容
 * @returns {string}
 */

function getId() {
    //	content css3
    var htmlHref = window.location.href;
    htmlHref = htmlHref.replace(/^http:\/\/[^/]+/, "");
    var addr = htmlHref.substr(htmlHref.lastIndexOf('/', htmlHref.lastIndexOf('/') - 1) + 1);
    var index = addr.lastIndexOf("\/");
    //js 获取字符串中最后一个斜杠后面的内容
    var addrLast = decodeURI(addr.substring(index + 1, addr.length));
    //js 获取字符串中最后一个斜杠前面的内容
    //var str = decodeURI(addr.substring(0, index));
    if (addrLast != null) {
        return addrLast;
    } else {
        return 0;
    }
}

/**
 * 验证码
 * @param img
 */
function changeVerifyCode(img) {
    img.src = "../Kaptcha?" + new Date();
}

/**
 * 获取工程的ContextPath修正图片路由
 * @returns {string}
 */
function getContextPath() {
    return "/O2O";
}

/**
 * 格式化日期
 * @param fmt
 * @returns {*}
 * @constructor
 */
Date.prototype.Format = function (fmt) {
    var o = {
        // 月份
        "M+": this.getMonth() + 1,
        // 日
        "d+": this.getDate(),
        // 小时
        "h+": this.getHours(),
        // 分
        "m+": this.getMinutes(),
        // 秒
        "s+": this.getSeconds(),
        // 季度
        "q+": Math.floor((this.getMonth() + 3) / 3),
        // 毫秒
        "ms": this.getMilliseconds()
    }
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length))
    }
    for (var k in o) {
        if (new RegExp('(' + k + ')').test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ?
                (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)))
        }
    }
    return fmt
}