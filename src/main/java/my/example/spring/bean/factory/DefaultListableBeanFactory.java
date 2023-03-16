package my.example.spring.bean.factory;

import my.example.spring.annotation.ComponentScan;
import my.example.spring.annotation.Scope;
import my.example.spring.annotation.Service;
import my.example.spring.bean.definition.AnnotatedGenericBeanDefinition;
import my.example.spring.bean.definition.AnnotationBeanDefinition;
import my.example.spring.bean.definition.BeanDefinitionRegistry;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mars
 * @description DefaultListableBeanFactory
 * @date 2023-02-20 17:49
 */
public class DefaultListableBeanFactory implements BeanDefinitionRegistry, BeanFactory {
    private final Map<String, AnnotationBeanDefinition> beanDefinitionMap;
    private final List<String> beanDefinitionNames;

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    public DefaultListableBeanFactory() {
        this.beanDefinitionMap = new ConcurrentHashMap<>(256);
        this.beanDefinitionNames = new ArrayList<>(256);
    }

    @Override
    public void registerBeanDefinition(String beanName, AnnotationBeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(beanName, beanDefinition);

    }

    @Override
    public <T> T getBean(Class<T> tClass) {
        String bn = null;
        if (tClass.isAnnotationPresent(Service.class)) {
            bn = tClass.getAnnotation(Service.class).value();
        }
        if (bn == null || "".equals(bn.trim())) {
            bn = tClass.getSimpleName().substring(0, 1).toLowerCase() + tClass.getSimpleName().substring(1);
        }
        return (T) getBean(bn);
    }

    @Override
    public Object getBean(String beanName) {
        Object bean = this.singletonObjects.get(beanName);
        if (bean == null) {
            bean = createBean(beanName, beanDefinitionMap.get(beanName));
        }
        return bean;
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
                    if (beanClazz.isAnnotationPresent(Service.class)) {
                        Service serviceAnn = beanClazz.getAnnotation(Service.class);
                        String bn = serviceAnn.value();
                        if (bn == null || "".equals(bn.trim())) {
                            bn = fn.substring(0, 1).toLowerCase() + fn.substring(1);
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
    }

    private Object createBean(String bdn, AnnotationBeanDefinition abd) {
        try {
            Object bean = doCreateBean(bdn, abd);
            // 单例bean放入单例池中
            if ("singleton".equals(((AnnotatedGenericBeanDefinition) abd).getScope())) {
                this.singletonObjects.put(bdn, bean);
            }
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object doCreateBean(String bdn, AnnotationBeanDefinition abd) {
        try {
            return ((AnnotatedGenericBeanDefinition) abd).getClazz().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
