package com.itfenbao.main;

import org.pf4j.spring.SpringPluginManager;
import org.pf4j.update.UpdateManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.net.URI;
import java.nio.file.Path;

@Configuration
public class Pf4jSpringConfiguration {
    @Bean
    public SpringPluginManager pluginManager() {
        return new SpringPluginManager(Path.of(URI.create("file:///Users/itfenbao/Documents/pf4j-plugins")));
    }

    @Bean
    @DependsOn("pluginManager")
    public UpdateManager updateManager(SpringPluginManager pluginManager) {
        return new UpdateManager(pluginManager);
    }


    @Bean
    @DependsOn("pluginManager")
    public FreeMarkerConfigurer freemarkerConfig(SpringPluginManager pluginManager) {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:/templates/");
        configurer.setPostTemplateLoaders(new PluginFreemarkerTemplateLoader(pluginManager));
        configurer.setDefaultEncoding("UTF-8");
        return configurer;
    }

    @Bean
    @DependsOn("pluginManager")
    public PluginResourceHandlerMapping pluginResourceHandlerMapping(ServletContext servletContext, SpringPluginManager pluginManager) {
        return new PluginResourceHandlerMapping(servletContext, pluginManager);
    }

//    @Bean
//    @DependsOn("pluginManager")
//    public JFinalViewResolver jfinalViewResolver() {
//
//        // 创建用于整合 spring boot 的 ViewResolver 扩展对象
//        JFinalViewResolver jfr = new JFinalViewResolver();
//
//        // 对 spring boot 进行配置
//        jfr.setSuffix(".html");
//        jfr.setContentType("text/html;charset=UTF-8");
//        jfr.setOrder(0);
//
//        // 设置在模板中可通过 #(session.value) 访问 session 中的数据
//        jfr.setSessionInView(true);
//
//        // 获取 engine 对象，对 enjoy 模板引擎进行配置，配置方式与前面章节完全一样
//        Engine engine = JFinalViewResolver.engine;
//
//        // 热加载配置能对后续配置产生影响，需要放在最前面
//        engine.setDevMode(true);
//
//        // 使用 ClassPathSourceFactory 从 class path 与 jar 包中加载模板文件
//        engine.setToClassPathSourceFactory();
//
//        // 在使用 ClassPathSourceFactory 时要使用 setBaseTemplatePath
//        // 代替 jfr.setPrefix("/view/")
//        engine.setBaseTemplatePath("/view/");
//
//        // 添加模板函数
////        engine.addSharedFunction("/common/_layout.html");
////        engine.addSharedFunction("/common/_paginate.html");
//
//        // 更多配置与前面章节完全一样
//        // engine.addDirective(...)
//        // engine.addSharedMethod(...);
//
//        return jfr;
//    }

}
