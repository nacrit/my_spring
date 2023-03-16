package my.example.spring.bean.factory;

/**
 * @author mars
 * @description BeanFactory
 * @date 2023-02-20 17:47
 */
public interface BeanFactory {
    <T> T getBean(Class<T> tClass);
    Object getBean(String beanName);

}
