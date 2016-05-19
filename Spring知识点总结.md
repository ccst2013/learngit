Spring 属性和属性文件加载(PropertyPlaceholderConfigurer)
Visit each bean definition in the given bean factory and attempt to replace ${...} property
placeholders with values from the given properties.
1.方法一: 直接把属性以key/value配置在.xml文件中
	<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="properties">
			<props>
				<prop key="testBean.name">Rob Harrop</prop>
				<prop key="testBean.age">100</prop>
				<prop key="scopeName">myScope</prop>
			</props>
		</property>
	</bean>
2.方法二: 把单个.properties文件引入
	<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="location">
			<value>classpath: XXX/jdbc.properties</value>
		</property>
		<!-- 指定外部文件编码 -->
		<property name="fileEncoding">
      		<value>UTF-8</value>
    	</property>
	</bean>
3.方法三: 把多个.properties文件引入
	<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="locations">
			<list>
				<value>/WEB-INF/mail.properties</value>
    			<value>classpath: XXX/jdbc.properties</value>
			</list>
		</property>
		<!-- 指定外部文件编码 -->
		<property name="fileEncoding">
      		<value>UTF-8</value>
    	</property>
	</bean>
4.方法四: 使用PropertyPlaceholderConfigurer的实例<context:property-placeholder/>
	<context:property-placeholder location="classpath:XXX/jdbc.properties" file-encoding="utf8"/>
	此处file-encoding="utf-8"会报异常，需声明为字符串
	<bean id="utf8" class="java.lang.String">
		<constructor-arg value="utf-8" />
	</bean>
	(备注:需要引入名字空间)

5.实现加密属性文件两个方法任选一个实现
	public calss EncryptPropertyPlaceholder extends PropertyPlaceholderConfigurer {
		@Override
		protected String convertProperty(String propertyName, String propertyValue){
			//实现加密/解密
		}
		@Override
		protected void convertProperties(Properties props) {
			//实现加密/解密
		}
	}
Spring Filter代理bean解决Filter中无法注入bean问题(DelegatingFilterProxy)
Proxy for a standard Servlet Filter, delegating to a Spring-managed bean that
implements the Filter interface. Supports a "targetBeanName" filter init-param
in web.xml, specifying the name of the target bean in the Spring
application context.
NOTE: The lifecycle methods defined by the Servlet Filter interface
will by default not be delegated to the target bean, relying on the
Spring application context to manage the lifecycle of that bean. Specifying
the "targetFilterLifecycle" filter init-param as "true" will enforce invocation
of the Filter.init and Filter.destroy lifecycle methods
on the target bean, letting the servlet container manage the filter lifecycle.
1.普通filter配置
	<filter>
		<filter-name>myFilter</filter-name>
		<filter-class>com.XXX.YYY.filter.MyFilter</filter-class>
	</filter>
	<filter-mapping>
	    <filter-name>myFilter</filter-name>
	    <url-pattern>/*</url-pattern>
	</filter-mapping>
因为filter比bean先加载，也就是spring会先加载filter指定的类到container中，这样filter中注入的spring bean就为null了。
2.Filter代理bean配置一:
	<filter>
		<filter-name>myBean</filter-name>//需要被代理的bean的name
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
	</filter>
	<filter-mapping>
	    <filter-name>myBean</filter-name>
	    <url-pattern>/*</url-pattern>
	</filter-mapping>
	Spring bean配置文件加入：
	<bean id="myBean" class="com.XXX.YYYY.TestClass" />
	(备注:filter-name和bean name要一样)
3.Filter代理bean配置二:
	<filter>
		<filter-name>myTest</filter-name> //此处可以为自定义的过滤名
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		<!-- 指定需要代理的bean -->
		<init-param>
			<param-name>targetBeanName</init-param>
			<param-value>myBean</param-value>
		</init-param>
		<!-- 是否需要执行代理的bean Filter中的init和destroy,默认false -->
		<init-param>
        	<param-name>targetFilterLifecycle</param-name>
        	<param-value>true</param-value>
    	</init-param>
	</filter>
	<filter-mapping>
	    <filter-name>myBean</filter-name>
	    <url-pattern>/*</url-pattern>
	</filter-mapping>
	Spring bean配置文件加入：
	<bean id="myBean" class="com.XXX.YYYY.TestClass" />

Spring字符编码org.springframework.web.filter.CharacterEncodingFilter（解决不同编码导致乱码问题）
 * Servlet Filter that allows one to specify a character encoding for requests.
 * This is useful because current browsers typically do not set a character
 * encoding even if specified in the HTML page or form.
 *(以上表明在html页面中设置的编码是无效的，显示编码实际上在response中设定)
 * This filter can either apply its encoding if the request does not already
 * specify an encoding, or enforce this filter's encoding in any case
 * ("forceEncoding"="true"). In the latter case, the encoding will also be
 * applied as default response encoding (although this will usually be overridden
 * by a full content type set in the view).
在源码中:
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
			request.setCharacterEncoding(this.encoding);
			if (this.forceEncoding) {
				response.setCharacterEncoding(this.encoding);
			}
		}
		filterChain.doFilter(request, response);
	}
说明encoding是用来设置request编码格式，forceencoding是用来决定是否把response也设置为encoding编码。
使用方法一:
<filter>
	<filter-name>setCharacterEncoding</filter-name>
	<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
	<init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
    <init-param>
        <param-name>forceEncoding</param-name>  <!-- default false -->
        <param-value>true</param-value>
    </init-param>
</filter>
<filter-mapping>
	<filter-name>setCharacterEncoding</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
使用方法二:(继承Disaptcher)
1.继承DispatcherServlet添加实现设置编码 
DispatcherServlet: 将所有请求进行识别，分发给对应的处理器进行处理，如同中央控制器
public class DispatchEncodingServlet extends DispatcherServlet {
	private String encoding;
	private Boolean forceEncoding = false;

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setForceEncoding(Boolean forceEncoding) {
		this.forceEncoding = forceEncoding;
	}

	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//参考CharacterEncodingFilter
		if (encoding != null
				&& (forceEncoding || request.getCharacterEncoding() == null)) {
			request.setCharacterEncoding(encoding);
			if (forceEncoding) {
				response.setCharacterEncoding(encoding);
			}
		}
		super.doService(request, response);
	}
}
web.xml文件中配置 
<servlet>
    <servlet-name>appServlet</servlet-name>
    <servlet-class>com.alibaba.meeting.common.DispatchEncodingServlet</servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>/WEB-INF/spring/appServlet/servlet-context.xml</param-value>
    </init-param>
    <init-param>
  		<param-name>encoding</param-name>
  		<param-value>UTF-8</param-value>
  	</init-param>
  	<init-param>
  		<param-name>forceEncoding</param-name>
  		<param-value>true</param-value>
  	</init-param>
  </servlet>

Spring的BeanPostProcessor接口org.springframework.beans.factory.config.BeanPostProcessor
 * ApplicationContexts can autodetect BeanPostProcessor beans in their
 * bean definitions and apply them to any beans subsequently created.
 * Plain bean factories allow for programmatic registration of post-processors,
 * applying to all beans created through this factory
配置和初始化bean后添加一些自己的逻辑处理;
BeanPostProcessor的作用域是容器级的，它只和所在容器有关。如果你在容器中定义了BeanPostProcessor,它仅仅对此容器中的bean进行后置。它不会对定义在另一个容器中的bean进行任何处理.BeanFactory和ApplicationContext对待bean后置处理器稍有不同。ApplicationContext会自动检测在配置文件中实现了BeanPostProcessor接口的所有bean，并把它们注册为后置处理器，然后在容器创建bean的适当时候调用它,BeanFactory实现的时候，bean 后置处理器必须通过下面类似的代码显式地去注册;
public class BeanPostPrcessorImpl implements BeanPostProcessor {
    // Bean 实例化之前进行的处理
    public Object postProcessBeforeInitialization(Object bean, String beanName)
           throws BeansException {
       System.out.println("对象" + beanName + "开始实例化");
       return bean;
    }
    // Bean 实例化之后进行的处理
    public Object postProcessAfterInitialization(Object bean, String beanName)
           throws BeansException {
       System.out.println("对象" + beanName + "实例化完成");
       return bean;
    }
}
将这个BeanPostProcessor接口的实现定义到容器中:
<bean id="beanPostProcressorImpl" class="springTest.BeanPostProcressorImpl">
BeanFactory实现:
ConfigurableBeanFactory bf = new XmlBeanFactory(new ClassPathResource(
				"beans.xml"));
		BeanPostProcressorImpl bpp = (BeanPostProcressorImpl) bf
				.getBean("beanPostProcressorImpl");
		bf.addBeanPostProcessor(bpp);

Java注解(Annotation)详解及实现（利用反射机制）
1.java元注解(meta-Annotation)包含4个：@Target,@Retention,@Documented,@Inherited
@Target:说明了Annotation修饰对象范围，可用于packages，types(类，接口，枚举，Annotation类型)，类型成员(方法，构造方法，成员变量，枚举值)，方法参数和本地变量(循环变量和catch参数)
取值ElementType：
		1.CONSTRUCTOR:用于描述构造器
　　　　2.FIELD:用于描述域
　　　　3.LOCAL_VARIABLE:用于描述局部变量
　　　　4.METHOD:用于描述方法
　　　　5.PACKAGE:用于描述包
　　　　6.PARAMETER:用于描述参数
　　　　7.TYPE:用于描述类、接口(包括注解类型) 或enum声明

@Retention：定义该Annotation被保留时间长短
取值RetentionPoicy：
　　　　1.SOURCE:在源文件中有效（即源文件保留）
　　　　2.CLASS:在class文件中有效（即class保留）
　　　　3.RUNTIME:在运行时有效（即运行时保留）

@Documented：用于描述其它类型的annotation应该被作为被标注的程序成员的公共API，因此可以被例如javadoc此类的工具文档化

@Inherited：元注解是一个标记注解，@Inherited阐述了某个被标注的类型是被继承的。如果一个使用了@Inherited修饰的annotation类型被用于一个class，则这个annotation将被用于该class的子类

2.自定义注解(Annotation)
定义注解格式：
　　public @interface 注解名 {定义体}

注解参数的可支持数据类型：
　　　　1.所有基本数据类型（int,float,boolean,byte,double,char,long,short)
　　　　2.String类型
　　　　3.Class类型
　　　　4.enum类型
　　　　5.Annotation类型
　　　　6.以上所有类型的数组
//Example:
	@Target({ElementType.Field})
	@Retention(RetentionPoicy.RUNTIME)
	@Documented
	public @interface MyAnnotation {
		public String name() default "";
		int age() default 0;
	}
Annotation类型里面的参数该怎么设定:
　　第一,只能用public或默认(default)这两个访问权修饰.例如,String value();这里把方法设为defaul默认类型；　 　
　　第二,参数成员只能用基本类型byte,short,char,int,long,float,double,boolean八种基本数据类型和 String,Enum,Class,annotations等数据类型,以及这一些类型的数组.例如,String value();这里的参数成员就为String;　　
　　第三,如果只有一个参数成员,最好把参数名称设为"value",后加小括号.例:下面的例子FruitName注解就只有一个参数成员。

3.自定义注解的使用
//Person.java
	public class Person {
		@MyAnnotation(name="YYY")
		private String name;
		@MyAnnotation(age=23)
		private int age;
		...
	}

//MyAnnotation.java
	@Target({ElementType.Field})
	@Retention(RetentionPoicy.RUNTIME)
	@Documented
	public @interface MyAnnotation {
		public String name() default "";
		int age() default 0;
	}
//PersonTest.java
	public void personTest() {
		Person p = new Person();
		Class<? extends Person> pClass = p.getClass();
		Field[] fileds = pClass.getDeclaredFields();//获取已有字段，不包含继承的
		if (fileds.length > 0) {
			for (int i = 0; i < fileds.length; i++) {

			//判断字段是否可访问，不可访问的话设置为可访问
				boolean isAccess = false;
				if (!fileds[i].isAccessible()) {
					isAccess = true;
					fileds[i].setAccessible(true);
				}

			//判断当前字段是否包含MyClass类注解，若有，这执行相关赋值操作
				if (fileds[i].isAnnotationPresent(MyClass.class)) {
					try {
						MyClass classType = fileds[i]
								.getDeclaredAnnotation(MyClass.class);
						Method method = MyClass.class.getMethod(
								fileds[i].getName(), null);
						fileds[i].set(p, method.invoke(classType, null));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			//设置字段属性为不可见
				if (isAccess) {
					fileds[i].setAccessible(false);
				}
			}
		}
		System.out.println(p.getName());
	}

Spring自定义属性编辑器org.springframework.beans.factory.config.CustomEditorConfigurer
用于注册属性编辑器实例
<bean class="org.springframework.beans.factory.config.CustomEditorConfigurer">
	<property name="customEditors">
		<map>
			<entry key="com.xxx.Car" value="com.xxx.CarEditor" />
		</map>
	</property>
</bean>
<bean id="boss" class="com.xxx.boss">
	<property name="car" value="120,CA7789,Bule" />
	...
</bean>

public class Car {
	private int speed;
	private String brand;
	private String color; 
	...
}

public class CarEditor extends java.beans.PropertyEditorSupport {
	public void setAsText(String text){
		String[] infos = text.split(",");
		Car car = new Car();
		car.setSpeed(Integer.parseInt(infos[0]));
		car.setBrand(infos[1]);
		car.setColor(infos[2]);
		setValue(car); //置换转换后的对象
	}
}

