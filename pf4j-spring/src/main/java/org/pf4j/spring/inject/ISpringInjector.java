package org.pf4j.spring.inject;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public abstract class ISpringInjector {
    protected final SpringPluginManager springPluginManager;
    protected final ApplicationContext applicationContext;
    protected final AbstractAutowireCapableBeanFactory beanFactory;

    public ISpringInjector(ApplicationContext applicationContext, SpringPluginManager springPluginManager) {
        this.applicationContext = applicationContext;
        this.springPluginManager = springPluginManager;
        this.beanFactory = (AbstractAutowireCapableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

    public abstract boolean isSupport(Class c);

    public abstract void register(Class c) throws Exception;

    public abstract void unregister(Class c) throws Exception;
}
