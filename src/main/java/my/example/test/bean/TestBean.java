package my.example.test.bean;

import my.example.spring.annotation.Service;

/**
 * @author mars
 * @description TestBean
 * @date 2023-02-10 13:57
 */
@Service("testBeanxx")
public class TestBean {
	private String name = "hello world";

	public TestBean() {
	}

	public TestBean(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
