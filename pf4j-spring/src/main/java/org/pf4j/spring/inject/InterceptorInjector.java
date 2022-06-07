package org.pf4j.spring.inject;

import org.pf4j.spring.SpringPluginManager;
import org.pf4j.spring.annotation.Path;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InterceptorInjector extends ISpringInjector {

    public InterceptorInjector(SpringPluginManager springPluginManager, ApplicationContext applicationContext, AbstractAutowireCapableBeanFactory beanFactory) {
        super(springPluginManager, applicationContext, beanFactory);
    }

    @Override
    public boolean isSupport(Class c) {
        return c.getAnnotation(Path.class) != null && HandlerInterceptor.class.isAssignableFrom(c);
    }

    @Override
    public void register(Class c) throws Exception {
        Map<String, ?> extensionBeanMap = springPluginManager.getApplicationContext().getBeansOfType(c);
        if (extensionBeanMap.isEmpty()) {
            Object extension = springPluginManager.getExtensionFactory().create(c);
            String beanName = StringUtils.uncapitalize(c.getSimpleName());
            beanFactory.registerSingleton(beanName, extension);
        }
        RequestMappingHandlerMapping handlerMapping = beanFactory.getBean(RequestMappingHandlerMapping.class);
        Field field = ReflectionUtils.findField(RequestMappingHandlerMapping.class, "adaptedInterceptors");
        Method method = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "adaptInterceptor", Object.class);
        field.setAccessible(true);
        method.setAccessible(true);
        List<HandlerInterceptor> list = (List<HandlerInterceptor>) ReflectionUtils.getField(field, handlerMapping);
        Path path = (Path) c.getAnnotation(Path.class);
        HandlerInterceptor h = (HandlerInterceptor) beanFactory.getBean(c);
        MappedInterceptor interceptor1 = new MappedInterceptor(path.value(), path.exclude(), h, null);
        HandlerInterceptor interceptor = (HandlerInterceptor) ReflectionUtils.invokeMethod(method, handlerMapping, interceptor1);
        list.add(interceptor);
        ReflectionUtils.setField(field, handlerMapping, list);
    }

    @Override
    public void unregister(Class c) throws Exception {
        RequestMappingHandlerMapping handlerMapping = beanFactory.getBean(RequestMappingHandlerMapping.class);
        Field field = ReflectionUtils.findField(RequestMappingHandlerMapping.class, "adaptedInterceptors");
        Method method = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "adaptInterceptor", Object.class);
        field.setAccessible(true);
        method.setAccessible(true);
        List<HandlerInterceptor> list = (List<HandlerInterceptor>) ReflectionUtils.getField(field, handlerMapping);
        List<MappedInterceptor> mapList = list.stream().filter(it -> it instanceof MappedInterceptor).map(it -> (MappedInterceptor) it).collect(Collectors.toList());
        for (MappedInterceptor i : mapList) {
            if (i.getInterceptor().getClass() == c) {
                list.remove(i);
            }
        }
        ReflectionUtils.setField(field, handlerMapping, list);

        Map<String, ?> extensionBeanMap = springPluginManager.getApplicationContext().getBeansOfType(c);
        if (!extensionBeanMap.isEmpty()) {
            String beanName = StringUtils.uncapitalize(c.getSimpleName());
            Method removeSingletonMethod = beanFactory.getClass().getSuperclass().getDeclaredMethod("removeSingleton", String.class);
            removeSingletonMethod.setAccessible(true);
            removeSingletonMethod.invoke(beanFactory, beanName);
        }
    }
}
