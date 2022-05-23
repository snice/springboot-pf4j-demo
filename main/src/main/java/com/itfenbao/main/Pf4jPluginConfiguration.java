package com.itfenbao.main;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class Pf4jPluginConfiguration implements BeanFactoryAware {

    private final SpringPluginManager pluginManager;
    private final ApplicationContext applicationContext;
    private BeanFactory beanFactory;

    @Autowired
    public Pf4jPluginConfiguration(SpringPluginManager pluginManager, ApplicationContext applicationContext) {
        this.pluginManager = pluginManager;
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @PreDestroy
    public void cleanup() {
        pluginManager.stopPlugins();
    }
}
