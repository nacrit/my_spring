package my.example.test.bean;

import my.example.spring.annotation.Scope;
import my.example.spring.annotation.Component;

/**
 * @author mars
 * @description TestBean
 * @date 2023-02-10 13:57
 */
@Scope("prototype")
@Component
public class TestBean2 {
	private String name = "hello world";

	public TestBean2() {
	}

	public TestBean2(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
