package my.example.spring.bean.factory;

import my.example.spring.annotation.Component;
import my.example.spring.annotation.ComponentScan;
import my.example.spring.annotation.Scope;
import my.example.spring.bean.definition.AnnotatedGenericBeanDefinition;
import my.example.spring.bean.definition.AnnotationBeanDefinition;
import my.example.spring.bean.definition.BeanDefinitionRegistry;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mars
 * @description DefaultListableBeanFactory
 * @date 2023-02-20 17:49
 */
public class DefaultListableBeanFactory implements BeanDefinitionRegistry, BeanFactory {
    // bean定义缓存
    private final Map<String, AnnotationBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    // bean名字集合
    private final List<String> beanDefinitionNames = new ArrayList<>(256);

    // 一级缓存,存储完整的对象
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    // 二级缓存，存储不完整的对象
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(256);
    // 三级缓存，存储要代理的对象
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);


    public DefaultListableBeanFactory() {
    }

    @Override
    public void registerBeanDefinition(String beanName, AnnotationBeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(beanName, beanDefinition);

    }

    @Override
    public <T> T getBean(Class<T> tClass) {
        String bn = null;
        if (tClass.isAnnotationPresent(Component.class)) {
            bn = tClass.getAnnotation(Component.class).value();
        }
        if (bn == null || "".equals(bn.trim())) {
            bn = getBeanName(tClass);
        }
        return (T) getBean(bn);
    }

    private static String getBeanName(Class<?> tClass) {
        return tClass.getSimpleName().substring(0, 1).toLowerCase() + tClass.getSimpleName().substring(1);
    }

    @Override
    public Object getBean(String beanName) {
        return doGetBean(beanName);
    }

    public Object doGetBean(final String beanName) {
        Object bean = getSingleton(beanName);
        if (bean == null) {
            bean = getSingleton(beanName, () -> createBean(beanName, beanDefinitionMap.get(beanName)));
        }
        return bean;
    }

    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            singletonObject = singletonFactory.getObject();
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
        }
        return singletonObject;
    }

    public Object getSingleton(String beanName) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null) {
            singletonObject = earlySingletonObjects.get(beanName);
            if (singletonObject == null) {
                ObjectFactory<?> objectFactory = singletonFactories.get(beanName);
                if (objectFactory != null) {
                    singletonObject = objectFactory.getObject();
                    // 三级缓存中的bean放入二级缓存
                    earlySingletonObjects.put(beanName, singletonObject);
                    // 清除三级缓存
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    public void doScan() {
        for (String beanName : beanDefinitionMap.keySet()) {
            AnnotatedGenericBeanDefinition abd = (AnnotatedGenericBeanDefinition) beanDefinitionMap.get(beanName);
            if (abd.getClazz().isAnnotationPresent(ComponentScan.class)) {
                ComponentScan componentScan = abd.getClazz().getAnnotation(ComponentScan.class);
                String basePackage = componentScan.basePackage();
                if (basePackage == null || "".equals(basePackage.trim())) {
                    basePackage = this.getClass().getPackage().getName();
                }
                String path = basePackage.replace(".", "/");
                URL url = this.getClass().getClassLoader().getResource(path);
                if (url != null) {
                    File basePackageFile = new File(url.getPath());
                    // 递归扫描该路径下的所有bean
                    doScanFile(basePackageFile, basePackage);
                }
            }

        }
    }

    private void doScanFile(File basePackageFile, String basePackage) {
        if (basePackageFile.isDirectory() && basePackageFile.listFiles() != null) {
            for (File beanFile : Objects.requireNonNull(basePackageFile.listFiles())) {
                if (beanFile.isDirectory()) {
                    doScanFile(beanFile, basePackage + "." + beanFile.getName());
                    continue;
                }
                try {
                    String fn = beanFile.getName().split("\\.")[0];
                    Class<?> beanClazz = this.getClass().getClassLoader()
                            .loadClass(basePackage.concat(".").concat(fn));
//                                Class<?> beanClazz = Class.forName(basePackage + "." + basePackageFile.getName().replace(".class", ""));
                    if (beanClazz.isAnnotationPresent(Component.class)) {
                        Component componentAnn = beanClazz.getAnnotation(Component.class);
                        String bn = componentAnn.value();
                        if (bn == null || "".equals(bn.trim())) {
                            bn = getBeanName(beanClazz);
                        }
                        AnnotatedGenericBeanDefinition newAbd = new AnnotatedGenericBeanDefinition();
                        newAbd.setClazz(beanClazz);
                        if (beanClazz.isAnnotationPresent(Scope.class)) {
                            newAbd.setScope(beanClazz.getAnnotation(Scope.class).value());
                        } else {
                            newAbd.setScope("singleton");
                        }
                        // 注册BeanDefinition
//                                    BeanDefinitionReaderUtils.registerBeanDefinition(newAbd, this);
                        registerBeanDefinition(bn, newAbd);
                        beanDefinitionNames.add(bn);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void preInstantiateSingletons() {
        List<String> list = new ArrayList<>(beanDefinitionNames);
        for (String bdn : list) {
            AnnotatedGenericBeanDefinition abd = (AnnotatedGenericBeanDefinition) beanDefinitionMap.get(bdn);
            if ("singleton".equals(abd.getScope())) {
                createBean(bdn, abd);
            }
        }
        this.earlySingletonObjects.clear();
        this.singletonFactories.clear();
    }

    private Object createBean(String beanName, AnnotationBeanDefinition abd) {
        try {
            // 给代理类一个机会
            Object bean = resolveBeforeInstantiation(beanName, abd);
            if (bean != null) {
                return bean;
            }
            // 创建bean
            bean = doCreateBean(beanName, abd);
            // 单例bean放入单例池中
            if ("singleton".equals(((AnnotatedGenericBeanDefinition) abd).getScope())) {
                this.singletonObjects.put(beanName, bean);
            }
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object resolveBeforeInstantiation(String beanName, AnnotationBeanDefinition abd) {
        Object singleton = getSingleton(beanName);
        if (singleton == null) {
            // todo 代理类一个机会 ...
//            this.singletonFactories.put(beanName, () -> createBean(beanName, abd));
        }
        return singleton;
    }

    private Object doCreateBean(String beanName, AnnotationBeanDefinition abd) {
        try {
            // 创建bean
            Object bean = ((AnnotatedGenericBeanDefinition) abd).getClazz().newInstance();
            // 属性填充
            populateBean(beanName, abd, bean);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void populateBean(String beanName, AnnotationBeanDefinition abd, Object bean) {
        Class<?> clazz = ((AnnotatedGenericBeanDefinition) abd).getClazz();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Resource.class)) {
                // 解决循环依赖
                this.earlySingletonObjects.put(beanName, bean);
                Resource resAnno = field.getAnnotation(Resource.class);
                String name = resAnno.name();
                if (name == null || "".equals(name.trim())) {
                    name = getBeanName(field.getType());
                }
                try {
                    Object fieldObj = getBean(name);
                    field.setAccessible(true);
                    field.set(bean, fieldObj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
