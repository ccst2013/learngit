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
	<context:property-placeholder location="classpath:XXX/jdbc.properties"/>
	(备注:需要引入名字空间)


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
