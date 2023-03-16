package my.example.spring.context;

import my.example.spring.bean.definition.AnnotationBeanDefinition;
import my.example.spring.bean.definition.BeanDefinitionRegistry;
import my.example.spring.bean.factory.DefaultListableBeanFactory;

/**
 * @author mars
 * @description GenericApplicationContext
 * @date 2023-02-20 17:46
 */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

    private final DefaultListableBeanFactory beanFactory;

    public GenericApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    @Override
    public void registerBeanDefinition(String beanName, AnnotationBeanDefinition beanDefinition) {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public DefaultListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }
}
