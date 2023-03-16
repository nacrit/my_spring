package my.example.spring.bean.definition;

/**
 * @author mars
 * @description BeanDefinitionRegistry
 * @date 2023-02-20 19:39
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String beanName, AnnotationBeanDefinition beanDefinition);
}
