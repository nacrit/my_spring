package my.example.spring.context;

import my.example.spring.bean.definition.AnnotatedBeanDefinitionReader;

/**
 * @author mars
 * @description AnnotationConfigApplicationContext
 * @date 2023-02-20 17:31
 */
public class AnnotationConfigApplicationContext extends GenericApplicationContext {
    private final AnnotatedBeanDefinitionReader reader;

    public AnnotationConfigApplicationContext() {
//        super(); // 默认先调用父类构造
        reader = new AnnotatedBeanDefinitionReader(this);
    }

    public AnnotationConfigApplicationContext(Class<?> componentClass) {
        // 初始化环境变量，初始化BeanDefinitionReader、BeanFactory
        // BeanDefinitionReader 用来读取bean定义信息
        // BeanFactory 用来注册、创建bean
        this();

        // 将配置类的beanDefinition注册到bean工厂
        register(componentClass);

        // 解析配置类扫描包路径，遍历路径下所有bean，注册到bean工厂（单例bean初始化）
        refresh();
    }

    private void register(Class<?> componentClass) {
        this.reader.register(componentClass);
    }
}
