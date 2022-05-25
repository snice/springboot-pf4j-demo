package com.itfenbao.main;

import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.util.StringUtils;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.UrlTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.UrlTemplateResource;

import java.net.URL;
import java.util.Map;

public class PluginThymeleafTemplateResolver extends UrlTemplateResolver {

    private final SpringPluginManager pluginManager;

    public PluginThymeleafTemplateResolver(SpringPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        for (PluginWrapper plugin : pluginManager.getPlugins(PluginState.STARTED)) {
            ClassLoader classLoader = plugin.getPluginClassLoader();
            URL url = classLoader.getResource("templates/" + resourceName + end());
            if (url != null) return new UrlTemplateResource(url, characterEncoding);
        }
        return super.computeTemplateResource(configuration, ownerTemplate, template, templateResolutionAttributes);
    }

    private String end() {
        return StringUtils.hasLength(getSuffix()) ? getSuffix() : ".html";
    }
}
