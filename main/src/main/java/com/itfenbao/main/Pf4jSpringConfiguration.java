package com.itfenbao.main;

import org.pf4j.PluginManager;
import org.pf4j.spring.SpringPluginManager;
import org.pf4j.update.UpdateManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

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
    public SimpleUrlHandlerMapping pluginUrlHandleMapping(ServletContext servletContext, PluginManager pluginManager) {
        return new PluginResourceHandlerMapping(servletContext, pluginManager);
    }

}
