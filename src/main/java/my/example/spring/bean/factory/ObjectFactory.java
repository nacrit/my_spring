package my.example.spring.bean.factory;

/**
 * @author mars
 * @description ObjectFactory
 * @date 2023-07-22 16:02
 */
@FunctionalInterface
public interface ObjectFactory<T> {

    /**
     * 返回bean实例
     */
    T getObject();

}