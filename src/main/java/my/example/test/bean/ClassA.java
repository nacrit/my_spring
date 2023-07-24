package my.example.test.bean;

import my.example.spring.annotation.Component;

import javax.annotation.Resource;

/**
 * @author mars
 * @description ClassA
 * @date 2023-07-22 16:17
 */
@Component
public class ClassA {
    @Resource
    private ClassB classB;

    public ClassA() {
        System.out.println("I am ClassA");
    }

    public ClassB getClassB() {
        return classB;
    }

    public void setClassB(ClassB classB) {
        this.classB = classB;
    }

    @Override
    public String toString() {
        return "ClassA{" + this.hashCode() + ", " +
                "classB=" + classB.hashCode() +
                '}';
    }
}
