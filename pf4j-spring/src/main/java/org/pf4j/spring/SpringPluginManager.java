/*
 * Copyright (C) 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pf4j.spring;

import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginState;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Decebal Suiu
 */
public class SpringPluginManager extends DefaultPluginManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, ExtensionsInjector> extensionsInjectorMap = new HashMap<>();

    public SpringPluginManager() {
        super();
    }

    public SpringPluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    public SpringPluginManager(List<Path> pluginsRoots) {
        super(pluginsRoots);
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * This method load, start plugins and inject extensions in Spring
     */
    @PostConstruct
    public void init() {
        loadPlugins();
        startPlugins();

        for (String pluginId : getPlugins().stream().map(it -> it.getPluginId()).collect(Collectors.toList())) {
            extensionsInjectorMap.put(pluginId, new ExtensionsInjector(pluginId, this, this.applicationContext));
        }
        extensionsInjectorMap.values().forEach(it -> it.injectExtensions(false));
    }

    @Override
    public String loadPlugin(Path pluginPath) {
        String pluginId = super.loadPlugin(pluginPath);
        if (!extensionsInjectorMap.containsKey(pluginId)) {
            ExtensionsInjector injector = new ExtensionsInjector(pluginId, this, this.applicationContext);
            extensionsInjectorMap.put(pluginId, injector);
        }
        return pluginId;
    }

    @Override
    public boolean unloadPlugin(String pluginId) {
        if (extensionsInjectorMap.containsKey(pluginId)) extensionsInjectorMap.remove(pluginId);
        return super.unloadPlugin(pluginId);
    }

    @Override
    public PluginState startPlugin(String pluginId) {
        boolean isStarted = getPlugin(pluginId).getPluginState() == PluginState.STARTED;
        PluginState state = super.startPlugin(pluginId);
        if (!isStarted && extensionsInjectorMap.get(pluginId) != null)
            extensionsInjectorMap.get(pluginId).injectExtensions(true);
        return state;
    }

    @Override
    public PluginState stopPlugin(String pluginId) {
        if (extensionsInjectorMap.get(pluginId) != null) extensionsInjectorMap.get(pluginId).uninjectExtensions();
        return super.stopPlugin(pluginId);
    }

}
