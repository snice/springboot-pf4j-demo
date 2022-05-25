package com.itfenbao.main;

import org.pf4j.spring.SpringPluginManager;
import org.pf4j.update.UpdateManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

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

}
