package my.example.spring.bean.definition;

import my.example.spring.annotation.Scope;

/**
 * @author mars
 * @description bean定义信息
 * @date 2023-02-20 17:50
 */
public class AnnotatedBeanDefinitionReader implements AnnotationBeanDefinition {
    private final BeanDefinitionRegistry registry;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }
    public void register(Class<?> componentClass) {
        registerBean(componentClass);
    }
    private void registerBean(Class<?> componentClass) {
        doRegisterBean(componentClass);
    }

    private void doRegisterBean(Class<?> componentClass) {
        // 把componentClass读取为一个bean定义
        AnnotatedGenericBeanDefinition bd = new AnnotatedGenericBeanDefinition();
        bd.setClazz(componentClass);
        if (componentClass.isAnnotationPresent(Scope.class)) {
            bd.setScope(componentClass.getAnnotation(Scope.class).value());
        } else {
            bd.setScope("singleton");
        }
        // 注册BeanDefinition
        BeanDefinitionReaderUtils.registerBeanDefinition(bd, registry);
    }
}
