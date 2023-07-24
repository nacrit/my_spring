package my.example.test.bean;

import my.example.spring.annotation.Component;

import javax.annotation.Resource;

/**
 * @author mars
 * @description ClassA
 * @date 2023-07-22 16:17
 */
@Component
public class ClassB {
    @Resource
    private ClassA classA;

    public ClassB() {
        System.out.println("I am ClassB");
    }

    public ClassA getClassA() {
        return classA;
    }

    public void setClassA(ClassA classA) {
        this.classA = classA;
    }

    @Override
    public String toString() {
        return "ClassB{" + this.hashCode() + ", " +
                "classA=" + classA.hashCode() +
                '}';
    }
}
