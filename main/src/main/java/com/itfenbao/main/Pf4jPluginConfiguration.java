package com.itfenbao.main;

import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class Pf4jPluginConfiguration implements BeanFactoryAware, PluginStateListener {

    private BeanFactory beanFactory;

    private final SpringPluginManager pluginManager;
    private final ApplicationContext applicationContext;

    @Autowired
    public Pf4jPluginConfiguration(SpringPluginManager pluginManager, ApplicationContext applicationContext) {
        this.pluginManager = pluginManager;
        this.applicationContext = applicationContext;
        this.pluginManager.addPluginStateListener(this);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void pluginInit() {
        // 支持thymeleaf加载plugin中的模板
        SpringTemplateEngine engine = beanFactory.getBean(SpringTemplateEngine.class);
        engine.addTemplateResolver(new PluginThymeleafTemplateResolver(pluginManager));
    }

    @PreDestroy
    public void cleanup() {
        pluginManager.stopPlugins();
    }

    @Override
    public void pluginStateChanged(PluginStateEvent event) {
        System.out.println("pluginStateChanged:" + event);
    }
}
