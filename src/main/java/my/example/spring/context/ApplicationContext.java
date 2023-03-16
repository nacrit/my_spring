package my.example.spring.context;

import my.example.spring.bean.factory.BeanFactory;

/**
 * @author mars
 * @description ApplicationContext
 * @date 2023-02-20 17:30
 */
public interface ApplicationContext extends BeanFactory {
    void refresh();
}
