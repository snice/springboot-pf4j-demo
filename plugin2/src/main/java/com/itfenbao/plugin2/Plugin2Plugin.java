package com.itfenbao.plugin2;

import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Plugin2Plugin extends SpringPlugin {
    public Plugin2Plugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        ApplicationContext appContext = ((SpringPluginManager) getWrapper().getPluginManager()).getApplicationContext();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.setParent(appContext);
        applicationContext.refresh();
        return applicationContext;
    }

    @Override
    public String basePackage() {
        return "com.itfenbao.plugin2";
    }
}
