1.基于JUnit4的Spring测试框架
	@RunWith(SpringJUnit4ClassRunner.class) //基于JUnit4的Spring测试框架
	@ContextConfiguration(locations="beans.xml") //启动Spring容器
	public class TestSpring {
		@Autowired
		private Person person;
		@Test
		public void test() {
			fail("Not yet implemented");
		}
	}

2.Spring web容器监听器org.springframework.web.context.ContextLoaderListener
*This listener should be registered after org.springframework.web.util.Log4jConfigListener
* in web.xml, if the latter is used.
* Create a new ContextLoaderListener that will create a web application
* context based on the "contextClass" and "contextConfigLocation" servlet
* context-params. 
//指定Spring配置文件，多个文件用逗号或空格分隔
<context-param>
	<param-name>contextConfigLocation</param-name>
	<param-value>classpath:beans.xml</param-value>
</context-param>
//负责启动Spring容器的web容器监听器，当监听web容器启动时，执行Spring容器的启动，并加载contextConfigLocation指定的配置文件
<listener>
	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>

3.Spring MVC的主控制器org.springframework.web.servlet.DispatcherServlet（Spring MVC通过<servlet>来截获url请求）
* Calling #setContextConfigLocation init-param 'contextConfigLocation'
* will dictate which XML files will be loaded by the
* #DEFAULT_CONTEXT_CLASS default XmlWebApplicationContext
* Calling #setContextClass init-param 'contextClass' overrides the
* default XmlWebApplicationContext and allows for specifying an alternative class,
* such as AnnotationConfigWebApplicationContext.
* Calling #setContextInitializerClasses init-param 'contextInitializerClasses'
* indicates which {@code ApplicationContextInitializer} classes should be used to
* further configure the internal application context prior to refresh().
<servlet>
	<servlet-name>dispatcher</servlet-name>
	<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
</servlet>
<servlet-mapping>
	<servlet-name>dispatcher</servlet-name>
	<url-pattern>/*</url-pattern>
</servlet-mapping>
Spring MVC拥有一个Spring配置文件，默认采用<servlet Name>-servlet.xml
