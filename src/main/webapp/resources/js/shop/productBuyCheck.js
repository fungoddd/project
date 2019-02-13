/**
 * 顾客消费记录
 */

$(function () {
    //商品名
    var productName = '';
    var pageNum = 1;
    //点击上一页重新渲染页面页码-1请求
    $("#up").click(function () {
        if (pageNum > 1) {
            getList(--pageNum);
        }
    });
    //点击一下页页码+1请求,当前页小分页页数才能进行下一页
    $("#low").click(function () {
        if (pageNum < pageCount) {
            getList(++pageNum);
        }
    });

    getList(1);

    //获取用户消费记录列表
    function getList(pageIndex) {
        var listUrl = '/O2O/shopAdmin/getUserProductMapByShop';
        $.ajax({
            url: listUrl,
            type: "GET",
            cache: false,
            data: {
                pageIndex: pageIndex,
                pageSize: 5,//默认每页五条数据
                productName: productName

            },
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    var userProductMapList = data.userProductMapList;
                    var tempHtml = '';
                    if (userProductMapList.length != 0) {
                        userProductMapList.map(function (item) {
                            tempHtml += ''
                                + '<div class="row row-productbuycheck">'
                                + '<div class="col-20" style="text-align: center">' + item.product.productName + '</div>'
                                + '<div class="col-40 productbuycheck-time" style="text-align: center">' + new Date(item.createTime).Format("yyyy-MM-dd hh:mm:ss") + '</div>'
                                + '<div class="col-15" style="text-align: center">' + item.user.name + '</div>'
                                + '<div class="col-10" style="text-align: center">' + item.point + '</div>'
                                + '<div class="col-15" style="text-align: center">' + item.operator.name + '</div>'
                                + '</div>';
                        });
                        $('.productbuycheck-wrap').html(tempHtml);
                        pageCount = data.count;
                        $(".dianji").remove();
                        //动态添加页码,每次点击页码调用点击方法,传入自身对象
                        for (var i = 1; i <= pageCount; i++) {
                            $('<li><a href="#" class="dianji" onclick="dianji(this)">' + i + '</a></li>').insertBefore($("#page"));
                        }
                        //查到的页数不为零显示上一页下一页
                        $('#up').attr("style", "");
                        $('#low').attr("style", "");

                    } else {
                        $.alert("还没有消费记录^_^");
                    }

                } else {
                    $.toast("获取失败!" + data.errMsg);
                }
            }
        });
    }

    /**
     * 点击页码传入页码获取当前页码的数据
     * @param value
     */
    dianji = function (value) {
        //获取被点击对象的值
        pageNum = value.textContent;
        //重新获取商品列表渲染页面,传入页码值
        getList(pageNum);
    };

    $('#search').on('change', function (e) {
        //点击搜索通过商品名模糊查询
        productName = e.target.value;
        //清空购买记录列表
        //$('.productbuycheck-wrap').empty();
        //渲染页面
        getList(1);
    });

    /**
     * 获取七天的销量
     */
    function getProductSellDailyList() {
        var gerProductSellDailyListUrl = '/O2O/shopAdmin/getProductSellDailyList';
        $.getJSON(gerProductSellDailyListUrl, function (data) {
            var myChart = echarts.init(document.getElementById('chart'));
            //生成静态的Echart信息部分
            var option = generateStaticEchartPart();
            //遍历销量统计列表,动态设置Echart的值
            option.legend.data = data.legendData;
            option.xAxis = data.xAxis;
            option.series = data.series;
            myChart.setOption(option)
        })
    }

    getProductSellDailyList();

    /*echarts*/
    //获取页面的chart元素
    //var myChart = echarts.init(document.getElementById('chart'));
    function generateStaticEchartPart() {

        var option = {
            //提示框,鼠标悬浮交互时的信息提示
            tooltip: {
                trigger: 'axis',
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'shadow'        // 鼠标移动到轴承的时候,显示阴影,默认为直线,可选为：'line' | 'shadow'
                }
            },
            //图例,每个图片最多一个图例
            legend: {

                //图例内容数组,数组通常为String,每一项代表一个系列的名字
                //data: ['奶茶', '咖啡', '牛奶']
            },
            //直角坐标系内绘图网络
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            //直角坐标系中横轴数组,数组中每一项代表一条横轴坐标
            xAxis: [
                {
                    /*
                    //类目型:需要制定类目列表,坐标轴内仅有这些指定类目标
                    type: 'category',
                    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                    */
                }
            ],
            //直角坐标系中纵轴数组,数组中每一项代表一条纵轴坐标
            yAxis: [
                {
                    type: 'value'
                }
            ],
            //驱动图表生成的数据内容数组,数组中每一项为一个系列的选项及数据
            series: [
                /*{
                    name: '奶茶',
                    type: 'bar',
                    data: [120, 132, 101, 134, 290, 230, 220]
                },
                {
                    name: '咖啡',
                    type: 'bar',
                    data: [60, 72, 71, 74, 190, 130, 110]
                },
                {
                    name: '牛奶',
                    type: 'bar',
                    data: [62, 82, 91, 84, 109, 110, 120]
                }*/
            ]
        }
        return option;
    }

    //myChart.setOption(option);

});