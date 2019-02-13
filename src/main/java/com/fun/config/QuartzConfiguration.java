package com.fun.config;


import com.fun.service.ProductSellDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @Author: FunGod
 * @Date: 2018-12-17 20:07:27
 * @Desc: Quartz定时任务
 */
@Configuration
public class QuartzConfiguration {

    @Autowired
    private ProductSellDailyService productSellDailyService;
    @Autowired
    private MethodInvokingJobDetailFactoryBean jobDetailFactory;
    @Autowired
    private CronTriggerFactoryBean productSellDailyTriggerFactory;

    /**
     * 创建jobDetailFactory
     *
     * @return
     */
    @Bean(name = "jobDetailFactory")
    public MethodInvokingJobDetailFactoryBean createJobDetail() {
        //创建JobDetailFactory对象,此工程主要用来制作一个jobDetail,即制作一个任务
        //现在要制定的定时任务本质上就是执行方法,所以用此工厂
        MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
        //设置jobDetail名字
        jobDetailFactoryBean.setName("product_sell_daily_job");
        //设置jobDetail组名
        jobDetailFactoryBean.setGroup("job_product_sell_daily_group");
        //相同的jobDetail,当指定多个Trigger时,可能第一个job完成前,第二个家就开始了。
        //指定concurrent为false,多个job不会并发运
        jobDetailFactoryBean.setConcurrent(false);
        //指定运行任务的类
        jobDetailFactoryBean.setTargetObject(productSellDailyService);
        //指定运行任务的方法
        jobDetailFactoryBean.setTargetMethod("dailyCalculate");
        return jobDetailFactoryBean;
    }

    /**
     * 创建cronTriggerFactory
     *
     * @return
     */
    @Bean(name = "productSellDailyTriggerFactory")
    public CronTriggerFactoryBean createProductSellDailyTrigger() {
        //创建triggerFactory实例,用来创建trigger
        CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
        //设置triggerFactory名字
        triggerFactoryBean.setName("product_sell_daily_trigger");
        //绑定jobDetail
        triggerFactoryBean.setJobDetail(jobDetailFactory.getObject());
        //设定cron表达式,每天0点执行一次
        //triggerFactoryBean.setCronExpression("0/3 * * * * ? *");
        triggerFactoryBean.setCronExpression("0 0 0 * * ? *");
        return triggerFactoryBean;
    }

    /**
     * 创建调度工厂
     *
     * @return
     */
    @Bean(name = "schedulerFactory")
    public SchedulerFactoryBean createSchedulerFactory() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTriggers(productSellDailyTriggerFactory.getObject());
        return schedulerFactoryBean;
    }
}
