package com.zou.core;

/**
 * @author zou
 */
public abstract class AbstractApplicationContext {
    public abstract Object getBean(String name);
    public abstract String[] getBeanDefinitionNames();
}
