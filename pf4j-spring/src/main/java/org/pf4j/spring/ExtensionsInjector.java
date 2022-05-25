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

import com.google.common.reflect.ClassPath;
import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Decebal Suiu
 */
public class ExtensionsInjector {

    private static final Logger log = LoggerFactory.getLogger(ExtensionsInjector.class);

    protected final String pluginId;
    protected final SpringPluginManager springPluginManager;
    protected final ApplicationContext applicationContext;
    protected final AbstractAutowireCapableBeanFactory beanFactory;

    public ExtensionsInjector(String pluginId, SpringPluginManager springPluginManager, ApplicationContext applicationContext) {
        this.pluginId = pluginId;
        this.springPluginManager = springPluginManager;
        this.applicationContext = applicationContext;
        this.beanFactory = (AbstractAutowireCapableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

    public void injectExtensions(boolean restart) {
        PluginWrapper pluginWrapper = springPluginManager.getPlugin(this.pluginId);
        ClassLoader classLoader = pluginWrapper.getPluginClassLoader();
        if (pluginWrapper.getPlugin() instanceof SpringPlugin) {
            try {
                String basePackage = ((SpringPlugin) pluginWrapper.getPlugin()).basePackage();
                ClassPath classPath = ClassPath.from(classLoader);
                List<Class> classes = classPath.getTopLevelClassesRecursive(basePackage).stream().filter(it -> it.load().getClassLoader() != getClass().getClassLoader()).map(it -> it.load()).collect(Collectors.toList());
                List<Class> filterClasses = classes.stream().filter(it -> isController(it) || isExtension(it)).collect(Collectors.toList());
                for (Class c : filterClasses) {
                    log.debug("Register extension '{}' as bean", c.getName());
                    registerExtension(c, restart);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            Set<String> extensionClassNames = springPluginManager.getExtensionClassNames(this.pluginId);
            for (String extensionClassName : extensionClassNames) {
                try {
                    log.debug("Register extension '{}' as bean", extensionClassName);
                    Class<?> extensionClass = classLoader.loadClass(extensionClassName);
                    registerExtension(extensionClass, restart);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public void uninjectExtensions() {
        PluginWrapper pluginWrapper = springPluginManager.getPlugin(this.pluginId);
        ClassLoader classLoader = pluginWrapper.getPluginClassLoader();
        if (pluginWrapper.getPlugin() instanceof SpringPlugin) {
            try {
                String basePackage = ((SpringPlugin) pluginWrapper.getPlugin()).basePackage();
                ClassPath classPath = ClassPath.from(classLoader);
                List<Class> classes = classPath.getTopLevelClassesRecursive(basePackage).stream().filter(it -> it.load().getClassLoader() != getClass().getClassLoader()).map(it -> it.load()).collect(Collectors.toList());
                List<Class> filterClasses = classes.stream().filter(it -> isController(it) || isExtension(it)).collect(Collectors.toList());
                for (Class c : filterClasses) {
                    log.debug("unRegister extension '{}' as bean", c.getName());
                    unregisterExtension(c);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            Set<String> extensionClassNames = springPluginManager.getExtensionClassNames(this.pluginId);
            for (String extensionClassName : extensionClassNames) {
                try {
                    log.debug("unRegister extension '{}' as bean", extensionClassName);
                    Class<?> extensionClass = classLoader.loadClass(extensionClassName);
                    unregisterExtension(extensionClass);
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Register an extension as bean.
     * Current implementation register extension as singleton using {@code beanFactory.registerSingleton()}.
     * The extension instance is created using {@code pluginManager.getExtensionFactory().create(extensionClass)}.
     * The bean name is the extension class name.
     * Override this method if you wish other register strategy.
     */
    protected void registerExtension(Class<?> extensionClass, boolean restart) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Map<String, ?> extensionBeanMap = springPluginManager.getApplicationContext().getBeansOfType(extensionClass);
        if (extensionBeanMap.isEmpty()) {
            Object extension = springPluginManager.getExtensionFactory().create(extensionClass);
            String beanName = StringUtils.uncapitalize(extensionClass.getSimpleName());
            beanFactory.registerSingleton(beanName, extension);
            if (restart) {
                if (isController(extensionClass)) {
                    registerController(extensionClass);
                }
            }
        } else {
            log.debug("Bean registeration aborted! Extension '{}' already existed as bean!", extensionClass.getName());
        }
    }

    protected void unregisterExtension(Class<?> extensionClass) {
        Map<String, ?> extensionBeanMap = springPluginManager.getApplicationContext().getBeansOfType(extensionClass);
        if (!extensionBeanMap.isEmpty()) {
            String beanName = StringUtils.uncapitalize(extensionClass.getSimpleName());
            if (isController(extensionClass)) {
                unregisterController(beanName);
            }
            try {
                Method method = beanFactory.getClass().getSuperclass().getDeclaredMethod("removeSingleton", String.class);
                method.setAccessible(true);
                method.invoke(beanFactory, beanName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isController(Class<?> extensionClass) {
        return extensionClass.getAnnotation(RestController.class) != null || extensionClass.getAnnotation(Controller.class) != null;
    }

    private boolean isExtension(Class<?> extensionClass) {
        return extensionClass.getAnnotation(Extension.class) != null;
    }

    /**
     * 注册controller
     *
     * @param extensionClass
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void registerController(Class<?> extensionClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String beanName = StringUtils.uncapitalize(extensionClass.getSimpleName());
        final RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        if (requestMappingHandlerMapping != null) {
            //注册Controller
            Method method = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredMethod("detectHandlerMethods", Object.class);
            //将private改为可使用
            method.setAccessible(true);
            method.invoke(requestMappingHandlerMapping, beanName);
        }
    }

    /**
     * 卸载controller
     *
     * @param beanName
     */
    public void unregisterController(String beanName) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        if (requestMappingHandlerMapping != null) {
            Object controller = this.applicationContext.getBean(beanName);
            final Class<?> targetClass = controller.getClass();
            ReflectionUtils.doWithMethods(targetClass, method -> {
                Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
                try {
                    Method createMappingMethod = RequestMappingHandlerMapping.class.getDeclaredMethod("getMappingForMethod", Method.class, Class.class);
                    createMappingMethod.setAccessible(true);
                    RequestMappingInfo requestMappingInfo = (RequestMappingInfo) createMappingMethod.invoke(requestMappingHandlerMapping, specificMethod, targetClass);
                    if (requestMappingInfo != null) {
                        requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, ReflectionUtils.USER_DECLARED_METHODS);
        }
    }

}
