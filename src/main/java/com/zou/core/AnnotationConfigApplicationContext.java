package com.zou.core;

import com.zou.annotation.Autowired;
import com.zou.annotation.Component;
import com.zou.annotation.Qualifier;
import com.zou.annotation.Value;
import com.zou.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author zou
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {
    //指定包
    private final String pack;
    //ioc容器
    private final Map<String, Object> ioc = new HashMap<>();
    //对bean的一个封装
    private Set<BeanDefinition> beanDefinitionSet = new HashSet<>();


    public AnnotationConfigApplicationContext(String pack) {
        this.pack = pack;
        initApplicationContext();
    }

    public void initApplicationContext() {

        //扫描包下并把class封装为beanDefinition对象,放入ioc容器
        transformClassToBeanDefinitions(pack);
        //对容器中的bean进行初始化，也就是装载，对bean属性赋值
        autoLoad();
    }

    private void autoLoad() {
        //非自定义对象基本数据类型自动装载
        basicFieldAutoLoad();
        //自定义对象数据类型自动装载
        objectFieldAutoLoad();
    }

    private void objectFieldAutoLoad() {
        try {
            for (BeanDefinition beanDefinition : beanDefinitionSet) {
                String beanName = beanDefinition.getBeanName();
                Class<?> beanclass = beanDefinition.getBeanclass();
                Field[] declaredFields = beanclass.getDeclaredFields();
                Object o = beanclass.getConstructor().newInstance();
                for (Field field : declaredFields) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        //byName注入
                        if (field.isAnnotationPresent(Qualifier.class)) {
                            Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                            String name = qualifierAnnotation.value();
                            if (ioc.containsKey(name)) {
                                field.setAccessible(true);
                                field.set(o, ioc.get(name));
                                //更新ioc
                                ioc.put(beanName, o);
                            } else {
                                throw new RuntimeException(String.format("bean of '%s' name not found", name));
                            }
                        }
                        //byType注入
                        Class<?> type = field.getType();
                        if (beanDefinitionSet.stream().filter(b -> b.getBeanclass().equals(type)).count() != 1){
                            throw new RuntimeException(String.format("The bean of '%s' has many type ", type.getName()));
                        }

                        for (BeanDefinition b : beanDefinitionSet) {
                            if (type.equals(b.getBeanclass())) {
                                Object o1 = ioc.get(b.getBeanName());
                                field.setAccessible(true);
                                field.set(o, o1);
                                //更新ioc
                                ioc.put(beanName, o);
                            }


                        }
                    }
                }
            }


        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void basicFieldAutoLoad() {
        try {
            for (BeanDefinition beanDefinition : beanDefinitionSet) {
                String beanName = beanDefinition.getBeanName();
                Class<?> beanclass = beanDefinition.getBeanclass();
                Field[] declaredFields = beanclass.getDeclaredFields();
                Object o = beanclass.getConstructor().newInstance();

                Arrays.stream(declaredFields).forEach((field) -> {
                            Value valueAnnotation = field.getAnnotation(Value.class);
                            if (valueAnnotation == null) {
                                return;
                            }
                            field.setAccessible(true);
                            try {
                                if (field.getType() == String.class) {
                                    field.set(o, valueAnnotation.value());
                                }
                                if (field.getType() == Integer.class || field.getType() == int.class) {
                                    field.set(o, Integer.parseInt(valueAnnotation.value()));
                                }
                                if (field.getType() == Long.class || field.getType() == long.class) {
                                    field.set(o, Long.parseLong(valueAnnotation.value()));
                                }
                                if (field.getType() == Short.class || field.getType() == short.class) {
                                    field.set(o, Short.parseShort(valueAnnotation.value()));
                                }
                                if (field.getType() == Double.class || field.getType() == double.class) {
                                    field.set(o, Double.parseDouble(valueAnnotation.value()));
                                }
                                if (field.getType() == Float.class || field.getType() == float.class) {
                                    field.set(o, Float.parseFloat(valueAnnotation.value()));
                                }
                                if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                                    field.set(o, Boolean.parseBoolean(valueAnnotation.value()));
                                }
                                if (field.getType() == Byte.class || field.getType() == byte.class) {
                                    field.set(o, Byte.parseByte(valueAnnotation.value()));
                                }
                                if (field.getType() == Character.class || field.getType() == char.class) {
                                    field.set(o, valueAnnotation.value().charAt(0));
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                );
                //注入基本类型初始化好的对象
                ioc.put(beanName, o);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private void transformClassToBeanDefinitions(String pack) {

        try {
            List<Class<?>> classes = ClassUtil.getClasses(pack);
            Iterator<Class<?>> classIterator = classes.iterator();
            while (classIterator.hasNext()) {
                Class<?> aClass = classIterator.next();
                Component component = aClass.getAnnotation(Component.class);
                if (component == null) {
                    continue;
                }
                //自定义bean的名称
                String beanName = component.value();
                if ("".equals(beanName)) {
                    //com.zou.entity.User
                    String[] split = aClass.getName().split("\\.");
                    // 如果component的value字段为空默认使用类名的首字母小写作为bean的名字
                    beanName = split[split.length - 1].substring(0, 1).toLowerCase() + split[split.length - 1].substring(1);
                    beanDefinitionSet.add(new BeanDefinition(beanName, aClass));
                } else {
                    beanDefinitionSet.add(new BeanDefinition(beanName, aClass));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object getBean(String name) {

        return ioc.get(name);
    }

    public Object getBeans() {

        return ioc.values();
    }

    @Override
    public Set<BeanDefinition> getBeanDefinitionNames() {
        return beanDefinitionSet;
    }
}

