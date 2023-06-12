package com.zou.core;

import java.util.Set;

/**
 * @author zou
 */
public abstract class AbstractApplicationContext {
    public abstract Object getBean(String name);
    public abstract Set<BeanDefinition> getBeanDefinitionNames();
}
