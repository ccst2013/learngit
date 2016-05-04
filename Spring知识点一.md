1.控制反转和依赖注入
	控制反转: 将对象的创建和管理交给spring容器；
	依赖注入: 在运行时将外部容器管理的对象注入到应用之中；
2.spring通过XML文件来配置框架
	1.XML文件解析(dom4j) + Java反射技术
	2.<bean>节点的子节点<property>主要完成属性注入，有三种方式:
		构造器注入，属性setter方法注入，注解方式注入
		setter方法对类属性的注入:
			1.使用property节点的ref；
			2.使用property节点子节点的<bean>进行内部bean配置
		setter方法对基本属性的注入:
			使用节点的value属性
		构造器注入:
			<bean id="person" class="com.uuu.Person" />
			<bean id="personImpl" class=com.uuu.PersonImpl>
				<constructor-arg index="0" type="com.uuu.Person" ref="person" />
				<constructor-arg index="1" type="java.lang.String" value="test" />
			</bean>
3.Spring自动装配(byName,byType)
	byName: 通过属性的名字的方式查找JavaBean依赖的对象并为其注入。比如说类Computer有个属性printer，指定其autowire属性为byName后，Spring IoC容器会在配置文件中查找id/name属性为printer的bean，然后使用Seter方法为其注入.
	byType: 通过属性的类型查找JavaBean依赖的对象并为其注入。比如类Computer有个属性printer，类型为Printer，那么，指定其autowire属性为byType后，Spring IoC容器会查找Class属性为Printer的bean，使用Seter方法为其注入.
	No: 即不启用自动装配。Autowire默认的值.
	default：由上级标签<beans>的default-autowire属性确定.
	优点：1.减少配置的数量；2.配置与java代码同步更新；
	没有自动装配的配置文件
	 <!-- Definition for textEditor bean -->
   <bean id="textEditor" class="com.yiibai.TextEditor">
      <property name="spellChecker" ref="spellChecker" />
      <property name="name" value="Generic Text Editor" />
   </bean>
   <!-- Definition for spellChecker bean -->
   <bean id="spellChecker" class="com.yiibai.SpellChecker">
   </bean>
   有自动装配的配置文件
   <!-- Definition for textEditor bean -->
   <bean id="textEditor" class="com.yiibai.TextEditor" 
      autowire="byName">
      <property name="name" value="Generic Text Editor" />
   </bean>
   <!-- Definition for spellChecker bean -->
   <bean id="spellChecker" class="com.yiibai.SpellChecker">
   </bean>
4.Bean的基本概念及容器中Bean作用域
	1.概念：<bean>是<beans>的子元素，在spring中，每个<bean>可以定义一个Bean实例，对应spring容器中java实例；
	2.容器中Bean的作用域：
		Singleton：单例模式。在整个SpringIoC容器中，使用singleton定义的Bean将只有一个实例。（默认）
        Prototype：原型模式。每次通过容器的getBean方法获取prototype定义的Bean时，都将产生一个新的Bean实例。
        request：  对于每次HTTP请求，使用request定义的Bean都将产生一个新的实例，即每次HTTP请求都会产生不同的Bean实例。当然只有在WEB应用中使用Spring时，该作用域才真正有效。
        session：  对于每次HTTPSession，使用session定义的Bean都将产生一个新的实例时，即每次HTTP Session都将产生不同的Bean实例。同HTTP一样，只有在WEB应用才会有效。
        global session：每个全局的HTTPSession对应一个Bean实例。仅在portlet Context的时候才有效。
    3.设置Bean的作用域是通过scope属性来指定：
    	<!-- 配置一个singleton Bean实例：默认 -->  
    		<bean id="bean1" class="com.Bean1" />  
   	 	<!-- 配置一个prototype Bean实例 -->  
    		<bean id="bean2" class="com.Bean2" scope="prototype"/>  
    4.对于每次HTTP请求，Spring容器都会Bean实例，且该Bean实例仅在当前HTTP Request内有效。
        对于session作用域相同。只不过有效范围不同而已。
        request和session作用域只在web应用中才会有效，并且必须在Web应用中增加额外配置才会生效。为了能够让request和session两个作用域生效，必须将HTTP请求对象绑定到位该请求提供的服务线程上，这使得具有request和session作用的Bean实例能够在后面的调用链中被访问到。
        因此我们可以采用两种配置方式：采用Listener配置或者采用Filter配置，在web.xml中
        <listener>  
    		<listener-class>  
        		org.springframework.web.context.request.RequestContextListener  
   	 		</listener-class>  
		</listener>  
		or
		<filter>  
		    <filter-name>requestContextFilter</filter-name>  
		    <filter-class>org.springframework.web.filter.RequestContextFilter</filter-class>  
		</filter>  
		<filter-mapping>  
		    <filter-name>requestContextFilter</filter-name>  
		    <url-pattern>/*</url-pattern>  
		</filter-mapping>  
		 一旦在web.xml中增加上面两种配置中的一种，程序就可以在Spring配置文件中使用request或者session作用域了.
		  <bean id="p" class="com.app.Person" scope="request"/> 
		  bean配置了一个实现类Person的Bean，指定它的作用域为request。这样Spring容器会为每次的HttP请求生成一个Person的实例，当该请求响应结束时，该实例也会被注销。
5.Spring组件扫描<context:component-scan/>
	1.需要引入context名字空间
		xmlns:context="http://www.springframework.org/schema/context"
		xsi:schemaLocation="...
			http://www.springframework.org/schema/context   
    		http://www.springframework.org/schema/context/spring-context-2.5.xsd"
    2.<context:component-scan />的base-package属性指定了需要扫描的类包
    	<context:component-scan base-package="com.uuu.Service" />
    3.<context:component-scan>提供两个子标签：<context:include-filter>和<context:exclude-filter>各代表引入和排除的过滤。
    	<context:component-scan base-package="com.uuu.Service" >
			<context:include-filter type="regex" expression=".service.*"/>
		</context:component-scan>
	4.默认情况下，Spring将把组件Class的第一个字母变成小写，来作为自动扫描组件的名称,你可以像下边这样，创建自定义的组件名称
		@Service("AAA")
		public class CustomerService { ... }
	5.使用@Scope来定义Bean的作用范围
		@Scope ( "session" )  
		@Component ()  
		public class CustomerService { ... }
6.Spring配置项<context:annotation-config/>
	作用:显示的向 Spring 容器注册AutowiredAnnotationBeanPostProcessor、CommonAnnotationBeanPostProcessor、PersistenceAnnotationBeanPostProcessor 以及 RequiredAnnotationBeanPostProcessor这4个BeanPostProcessor。注册这4个 BeanPostProcessor的作用，就是为了你的系统能够识别相应的注解。
	<bean class="org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor"/> 对应@Autowired注解；
	<bean class="org.springframework.beans.factory.annotation.CommonAnnotationBeanPostProcessor"/> 对应@Resource 、@PostConstruct、@PreDestroy等注解
	<bean class="org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor"/> 对应@Required注解
	<bean class="org.springframework.beans.factory.annotation.PersistenceAnnotationBeanPostProcessor"/> 对应@PersistenceContext注解