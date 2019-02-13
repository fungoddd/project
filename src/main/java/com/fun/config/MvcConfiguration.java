package com.fun.config;


import com.fun.interceptor.shopAdmin.ShopLoginInterceptor;
import com.fun.interceptor.shopAdmin.ShopPermissionInterceptor;
import com.google.code.kaptcha.servlet.KaptchaServlet;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.ServletRegistration;

/**
 * @Author: FunGod
 * @Date: 2018-12-11 20:03:40
 * @Desc: MVC配置
 */
@Configuration
@EnableWebMvc //等同于<mvc:annotation-driven/>配置视图解析器
public class MvcConfiguration extends WebMvcConfigurerAdapter implements ApplicationContextAware {
    //Spring容器
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 静态资源配置
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //windows系统
        //registry.addResourceHandler("/upload/**").addResourceLocations("file:C:/funProject/image/upload/");
        //linux系统
        registry.addResourceHandler("/upload/**").addResourceLocations("file:/home/funProject/image/upload/");
        //registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/");
    }

    /**
     * 定义默认请求处理器
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * 创建viewResolver视图解析器
     */
    @Bean(name = "viewResolver")
    public ViewResolver createViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        //设置Spring容器
        viewResolver.setApplicationContext(this.applicationContext);
        //取消缓存
        viewResolver.setCache(false);
        //设置视图解析前缀
        viewResolver.setPrefix("/WEB-INF/html/");
        //设置视图解析后缀
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    /**
     * 创建文件上传解析器
     */
    @Bean(name = "multipartResolver")
    public MultipartResolver createMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("utf-8");
        //最大内存20M
        multipartResolver.setMaxInMemorySize(20971520);
        //最大上次大小20M
        multipartResolver.setMaxUploadSize(20971520);
        return multipartResolver;
    }

    /**
     * Kaptcha验证码
     */
    @Value("${kaptcha.border}")
    private String border;

    @Value("${kaptcha.textproducer.font.color}")
    private String fontcolor;

    @Value("${kaptcha.image.width}")
    private String width;

    @Value("${kaptcha.image.height}")
    private String height;

    @Value("${kaptcha.textproducer.font.size}")
    private String size;

    @Value("${kaptcha.textproducer.char.string}")
    private String string;

    @Value("${kaptcha.noise.color}")
    private String color;

    @Value("${kaptcha.textproducer.char.length}")
    private String length;

    @Value("${kaptcha.textproducer.font.names}")
    private String names;

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servletBean = new ServletRegistrationBean(new KaptchaServlet(), "/Kaptcha");
        servletBean.addInitParameter("kaptcha.border", border);
        servletBean.addInitParameter("kaptcha.textproducer.font.color", fontcolor);
        servletBean.addInitParameter("kaptcha.image.width", width);
        servletBean.addInitParameter("kaptcha.image.height", height);
        servletBean.addInitParameter("kaptcha.textproducer.font.size", size);
        servletBean.addInitParameter("kaptcha.textproducer.char.string", string);
        servletBean.addInitParameter("kaptcha.noise.color", color);
        servletBean.addInitParameter("kaptcha.textproducer.char.length", length);
        servletBean.addInitParameter("kaptcha.textproducer.font.names", names);
        return servletBean;
    }

    /**
     * 添加自定义拦截器配置验证权限
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截路径
        String interceptorPath = "/shopAdmin/**";
        //注册拦截器,校验是否登录商家管理系统的拦截器
        InterceptorRegistration loginInterceptor = registry.addInterceptor(new ShopLoginInterceptor());
        //配置拦截器路径
        loginInterceptor.addPathPatterns(interceptorPath);
        //不拦截扫码添加商店授权信息,不需要登录操作
        loginInterceptor.excludePathPatterns("/shopAdmin/addShopAuthMap");
        loginInterceptor.excludePathPatterns("/shopAdmin/exchangeAward");
        loginInterceptor.excludePathPatterns("/shopAdmin/addUserProductMap");

        //注册第二个拦截器,否对该商店有操作权限的拦截器
        InterceptorRegistration operationInterceptor = registry.addInterceptor(new ShopPermissionInterceptor());
        operationInterceptor.addPathPatterns(interceptorPath);

        //配置不拦截路径
        //商家-商店列表页面:跳转,获取商店列表
        operationInterceptor.excludePathPatterns("/shopAdmin/shopList");
        operationInterceptor.excludePathPatterns("/shopAdmin/getShopListPageHelper");
        //注册新店跳转路由,注册,以及获取商店初始信息
        operationInterceptor.excludePathPatterns("/shopAdmin/shopOperation");
        operationInterceptor.excludePathPatterns("/shopAdmin/registerShop");
        operationInterceptor.excludePathPatterns("/shopAdmin/getShopInitInfo");
        //跳转商店管理路由以及验证商店管理
        operationInterceptor.excludePathPatterns("/shopAdmin/shopManagement");
        operationInterceptor.excludePathPatterns("/shopAdmin/getShopManagementInfo");
        //不拦截扫码添加商店授权信息,不需要登录操作
        operationInterceptor.excludePathPatterns("/shopAdmin/addShopAuthMap");
        operationInterceptor.excludePathPatterns("/shopAdmin/exchangeAward");
        operationInterceptor.excludePathPatterns("/shopAdmin/addUserProductMap");
    }

    /**
     * 输入域名自动跳转到主页
     */
    @Configuration
    public class DefaultView extends WebMvcConfigurerAdapter{
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("forward:/QRlogin");
            registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
            super.addViewControllers(registry);
        }
    }
}
