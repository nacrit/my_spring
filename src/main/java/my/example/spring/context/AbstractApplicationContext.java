package my.example.spring.context;

import my.example.spring.bean.factory.DefaultListableBeanFactory;

/**
 * @author mars
 * @description AbstractApplicationContext
 * @date 2023-02-20 17:30
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
    @Override
    public void refresh() {
        // 1.获取bean工厂
        DefaultListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // 2.将扫配置路径下的bean定义注册到bean工厂
        invokeBeanFactoryPostProcessors(beanFactory);

        // 3.根据bean定义，初始化单例bean，放入缓存中
        finishBeanFactoryInitialization(beanFactory);
    }

    private void finishBeanFactoryInitialization(DefaultListableBeanFactory beanFactory) {
        // 初始化单例bean
        beanFactory.preInstantiateSingletons();
    }

    private void invokeBeanFactoryPostProcessors(DefaultListableBeanFactory beanFactory) {
        // 扫描路径，初始化beanDefinition
        beanFactory.doScan();
    }

    protected DefaultListableBeanFactory obtainFreshBeanFactory() {
//        refreshBeanFactory();
        return getBeanFactory();
    }

    public abstract DefaultListableBeanFactory getBeanFactory();


    @Override
    public <T> T getBean(Class<T> classBean) {
        return getBeanFactory().getBean(classBean);
    }

    @Override
    public Object getBean(String beanName) {
        return getBeanFactory().getBean(beanName);
    }
}
