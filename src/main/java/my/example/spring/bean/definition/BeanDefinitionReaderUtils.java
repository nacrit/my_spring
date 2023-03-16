package my.example.spring.bean.definition;

/**
 * @author mars
 * @description BeanDefinitionReaderUtils
 * @date 2023-02-20 20:43
 */
public class BeanDefinitionReaderUtils {

    public static void registerBeanDefinition(AnnotationBeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        String beanName = (((AnnotatedGenericBeanDefinition) beanDefinition)).getClazz().getSimpleName();
        beanName = beanName.substring(0,1).toLowerCase() + beanName.substring(1);
        // 交给bean工厂净行bean注册
        registry.registerBeanDefinition(beanName, beanDefinition);
    }
}
