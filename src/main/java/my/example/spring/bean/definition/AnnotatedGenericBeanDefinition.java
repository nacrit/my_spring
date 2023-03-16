package my.example.spring.bean.definition;

/**
 * @author mars
 * @description AnnotatedGenericBeanDefinition
 * @date 2023-02-20 20:34
 */
public class AnnotatedGenericBeanDefinition implements AnnotationBeanDefinition {
    private Class<?> clazz;
    private String scope;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
