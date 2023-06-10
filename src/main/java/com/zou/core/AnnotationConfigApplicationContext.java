package com.zou.core;

import com.zou.annotation.AutoWired;
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
    private final String pack;
    private final Map<String, Object> ioc = new HashMap<>();

    private Set<BeanDefinition> beanDefinitionSet = new HashSet<>();


    public AnnotationConfigApplicationContext(String pack) {
        this.pack = pack;
    }

    public void initApplicationContext() {

        //扫描包下并把class封装为beanDefinition对象,放入ioc容器
        transformClassToBeanDefinitions(pack);
        //对容器中的bean进行初始化，也就是装载，对bean属性赋值
        autoLoad();
    }

    private void autoLoad() {
        Iterator<BeanDefinition> beanDefinitionIterator = beanDefinitionSet.iterator();
        while (beanDefinitionIterator.hasNext()) {
            BeanDefinition beanDefinition = beanDefinitionIterator.next();
            String beanName = beanDefinition.getBeanName();
            Class<?> beanclass = beanDefinition.getBeanclass();

            //非自定义对象基本数据类型自动装载
            basicFieldAutoLoad(beanName, beanclass);
            //自定义对象数据类型自动装载
            objectFieldAutoLoad(beanName, beanclass);


        }
    }

    private void objectFieldAutoLoad(String beanName, Class<?> beanclass) {
        try {
            Object o = beanclass.getConstructor().newInstance();
            Field[] declaredFields = beanclass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(AutoWired.class)) {
                    //byName注入
                    if (field.isAnnotationPresent(Qualifier.class)) {
                        field.setAccessible(true);
                        field.set(o, ioc.get(beanName));
                    }
                    //byType注入
//                    for(String name : ioc.keySet()){
//                        if (getBean(name).getClass() == field.getType()) {
//
//                        }
//                    }
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

    private void basicFieldAutoLoad(String beanName, Class<?> beanclass) {
        try {
            Object o = beanclass.getConstructor().newInstance();
            Field[] declaredFields = beanclass.getDeclaredFields();

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
                                field.set(o, Long.parseLong(valueAnnotation.value()));
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
                                field.set(o, Long.parseLong(valueAnnotation.value()));
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
                String beanName = component.value();
                if ("".equals(beanName)) {
                    //com.zou.entity.User
                    String[] split = aClass.getName().split("\\.");
                    // 如果component的value字段为空默认使用类名的首字母小写作为bean的名字
                    beanName = split[split.length - 1].substring(0, 1).toLowerCase() + split[split.length - 1].substring(1);
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

    @Override
    public String[] getBeanDefinitionNames() {
        return null;
    }
}

