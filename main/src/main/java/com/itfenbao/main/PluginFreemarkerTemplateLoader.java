package com.itfenbao.main;

import com.github.snice.spring.pf4j.SpringPluginManager;
import freemarker.cache.URLTemplateLoader;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

import java.net.URL;

public class PluginFreemarkerTemplateLoader extends URLTemplateLoader {

    private final SpringPluginManager pluginManager;

    public PluginFreemarkerTemplateLoader(SpringPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    protected URL getURL(String name) {
        for (PluginWrapper plugin : pluginManager.getPlugins(PluginState.STARTED)) {
            URL url = plugin.getPluginClassLoader().getResource("templates/" + name);
            if (url != null) return url;
        }
        return null;
    }

}
