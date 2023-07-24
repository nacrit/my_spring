# 手写Spring源码

## 1.spring启动类AnnotationConfigApplicationContext
### 构造方法：this() register(componentClasses) refresh()
1. this()：
  * 初始化bean reader
  * 环境（systemProperties、envProperties）
  * 对一些内部spring bean 读取和注册，初始化beanFactory、为beanFactory设置针对@Order、@Primary、@Lazy的Processer
  * 添加有关的@Configuration、@Autowired、@Resource，检测JPA添加存在的Listener、ListenerFactory
2. register(componentClasses): 注册配置类（@ComponentScan）bean信息到bean工厂
3. refresh()：
    * 