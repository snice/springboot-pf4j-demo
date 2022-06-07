package org.pf4j.spring.inject;

import org.pf4j.Extension;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Lazy
@Component
public class DefaultSpringInjector extends ISpringInjector {

    public DefaultSpringInjector(ApplicationContext applicationContext, SpringPluginManager springPluginManager) {
        super(applicationContext, springPluginManager);
    }

    @Override
    public boolean isSupport(Class c) {
        return isController(c) || isExtension(c);
    }

    @Override
    public void register(Class c) throws Exception {
        Map<String, ?> extensionBeanMap = springPluginManager.getApplicationContext().getBeansOfType(c);
        if (extensionBeanMap.isEmpty()) {
            Object extension = springPluginManager.getExtensionFactory().create(c);
            String beanName = StringUtils.uncapitalize(c.getSimpleName());
            beanFactory.registerSingleton(beanName, extension);
            if (isController(c)) {
                unregisterController(beanName);
                registerController(c);
            }
        }
    }

    @Override
    public void unregister(Class c) throws Exception {
        Map<String, ?> extensionBeanMap = springPluginManager.getApplicationContext().getBeansOfType(c);
        if (!extensionBeanMap.isEmpty()) {
            String beanName = StringUtils.uncapitalize(c.getSimpleName());
            if (isController(c)) {
                unregisterController(beanName);
            }
            Method method = beanFactory.getClass().getSuperclass().getDeclaredMethod("removeSingleton", String.class);
            method.setAccessible(true);
            method.invoke(beanFactory, beanName);
        }
    }

    /**
     * 注册controller
     *
     * @param extensionClass
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void registerController(Class<?> extensionClass) throws Exception {
        String beanName = StringUtils.uncapitalize(extensionClass.getSimpleName());
        final RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        if (requestMappingHandlerMapping != null) {
            Method method = ReflectionUtils.findMethod(requestMappingHandlerMapping.getClass(), "detectHandlerMethods", Object.class);
            method.setAccessible(true);
            method.invoke(requestMappingHandlerMapping, beanName);
        }
    }

    /**
     * 卸载controller
     *
     * @param beanName
     */
    private void unregisterController(String beanName) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        if (requestMappingHandlerMapping != null) {
            Object controller = this.applicationContext.getBean(beanName);
            if (controller == null) return;
            final Class<?> targetClass = controller.getClass();
            ReflectionUtils.doWithMethods(targetClass, method -> {
                Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
                try {
                    Method createMappingMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "getMappingForMethod", Method.class, Class.class);
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

    private boolean isExtension(Class c) {
        return AnnotatedElementUtils.hasAnnotation(c, Extension.class);
    }

    private boolean isController(Class c) {
        return AnnotatedElementUtils.hasAnnotation(c, Controller.class) || AnnotatedElementUtils.hasAnnotation(c, RequestMapping.class);
    }
}
