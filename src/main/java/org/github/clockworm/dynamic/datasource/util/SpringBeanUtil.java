package org.github.clockworm.dynamic.datasource.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class SpringBeanUtil implements BeanFactoryAware {

    public static DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        this.beanFactory = defaultListableBeanFactory;
    }

    public static void setBean(String beanName, Object object) {
        beanFactory.registerSingleton(beanName, object);
    }

    public static void removeSingletonBean(String beanName) {
        beanFactory.destroySingleton(beanName);
    }

    public static void setBean(String beanName, Class<?> clazz) {
        BeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClassName(clazz.getName());
        beanDefinition.setScope("prototype");
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public static void removeBean(String beanName) {
        beanFactory.removeBeanDefinition(beanName);
    }


}