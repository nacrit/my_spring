package my.example;

import my.example.spring.context.AnnotationConfigApplicationContext;
import my.example.spring.context.ApplicationContext;
import my.example.test.bean.ClassA;
import my.example.test.bean.TestBean2;
import my.example.test.config.AppConfig;
import my.example.test.bean.TestBean;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        // 获取bean工厂
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        TestBean testBean = applicationContext.getBean(TestBean.class);

        // 单例bean
        System.out.println("testBean = " + testBean);
        System.out.println("testBean = " + applicationContext.getBean(TestBean.class));

        System.out.println(" ===================================== ");
        // 多例bean
        System.out.println("testBean2 = " + applicationContext.getBean(TestBean2.class));
        System.out.println("testBean2 = " + applicationContext.getBean(TestBean2.class));
        System.out.println("testBean2 = " + applicationContext.getBean(TestBean2.class));

        System.out.println(" ===================================== ");
        // 循环依赖解决
        ClassA classA = applicationContext.getBean(ClassA.class);
        System.out.println("classA = " + classA);
        System.out.println("classB = " + applicationContext.getBean("classB"));
    }
}
