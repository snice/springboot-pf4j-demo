package org.pf4j.spring.inject;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public abstract class ISpringInjector {
    protected final SpringPluginManager springPluginManager;
    protected final ApplicationContext applicationContext;
    protected final AbstractAutowireCapableBeanFactory beanFactory;

    public ISpringInjector(SpringPluginManager springPluginManager, ApplicationContext applicationContext, AbstractAutowireCapableBeanFactory beanFactory) {
        this.springPluginManager = springPluginManager;
        this.applicationContext = applicationContext;
        this.beanFactory = beanFactory;
    }

    public abstract boolean isSupport(Class c);

    public abstract void register(Class c) throws Exception;

    public abstract void unregister(Class c) throws Exception;
}
