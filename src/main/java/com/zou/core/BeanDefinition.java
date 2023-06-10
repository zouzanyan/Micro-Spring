package com.zou.core;

import java.util.Objects;

/**
 * @author zou
 * @Description 把对象封装为bean,进行对bean生命周期的管理
 */
public class BeanDefinition {
    private String beanName;
    private Class<?> beanclass;

    public BeanDefinition(String beanName, Class<?> beanclass) {
        this.beanName = beanName;
        this.beanclass = beanclass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getBeanclass() {
        return beanclass;
    }

    public void setBeanclass(Class<?> beanclass) {
        this.beanclass = beanclass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanDefinition that = (BeanDefinition) o;
        return Objects.equals(beanName, that.beanName) && Objects.equals(beanclass, that.beanclass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, beanclass);
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanName='" + beanName + '\'' +
                ", beanclass=" + beanclass +
                '}';
    }
}
