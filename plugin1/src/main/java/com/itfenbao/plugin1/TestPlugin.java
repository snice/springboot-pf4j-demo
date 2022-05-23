package com.itfenbao.plugin1;

import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestPlugin extends SpringPlugin {

    public TestPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("Spring Sample plugin.start()");
    }

    @Override
    public void stop() {
        log.info("Spring Sample plugin.stop()");
        super.stop(); // to close applicationContext
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        ApplicationContext appContext = ((SpringPluginManager) getWrapper().getPluginManager()).getApplicationContext();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.setParent(appContext);
        applicationContext.register(PluginConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }

    @Override
    public String basePackage() {
        return "com.itfenbao.plugin1";
    }

}
