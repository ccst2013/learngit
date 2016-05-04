## Webx3 学习笔记 ##
# Web.xml配置 #

    webx.xml


>     <web-app  version="2.4" xmlns="http://java.sun.com/xml/ns/j2ee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="
             http://java.sun.com/xml/ns/j2ee  http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd
         ">
  	<display-name>Archetype Created Web Application</display-name>
  	<!-- 初始化日志系统 的参数-->
  	<context-param>
  		<param-name>loggingRoot</param-name>
  		<param-value>/tmp/logs/</param-value>
  	</context-param>
  	<context-param>
  		<param-name>loggingLevel</param-name>
  	<param-value>INFO</param-value>
  	</context-param>
  	<!-- 初始化日志系统 -->
  	<listener>
  		<listener-class>com.alibaba.citrus.logconfig.LogConfiguratorListener</listener-class>
  	</listener>
  	<!-- 装载/WEB-INF/webx.xml, /WEB-INF/webx-*.xml -->
  	<listener>
		<listener-class>com.alibaba.citrus.webx.context.WebxContextLoaderListener</listener-class>
  	</listener>
  	<!-- 通过SLF4J MDC(Mapped Diagnostic Context)来记录用户和请求的信息,处理不同源 -->
  	<filter>
  		<filter-name>mdc</filter-name>
  		<filter-class>com.alibaba.citrus.webx.servlet.SetLoggingContextFilter</filter-class>
  	</filter>
  	<filter-mapping>
  		<filter-name>mdc</filter-name>
  		<url-pattern>/*</url-pattern>
 	</filter-mapping>
  	<!-- 初始化spring容器的filter -->
  	<filter>
  		<filter-name>webx</filter-name>
  		<filter-class>com.alibaba.citrus.webx.servlet.WebxFrameworkFilter</filter-class>
  		<init-param>
  			<param-name>excludes</param-name>
  			<param-value><!-- 需要被“排除”的URL路径，以逗号分隔，如/static, *.jpg。适合于映射静态页面、图片。 --></param-value>
  		</init-param>
  		<init-param>
  			<param-name>passthru</param-name>
  			<param-value><!-- 需要被“略过”的URL路径，以逗号分隔，如/myservlet, *.jsp。适用于映射servlet、filter。 
					对于passthru请求，webx的request-contexts服务、错误处理、开发模式等服务仍然可用。 --></param-value>
  		</init-param>
  	</filter>
  	<filter-mapping>
  		<filter-name>webx</filter-name>
  		<url-pattern>/*</url-pattern>
  	</filter-mapping>
>     </web-app>

web.xml中主要做的事情：
    
- 初始化日志系统（日志路径和日志级别等---com.alibaba.citrus.logconfig.LogConfiguratorListener）
- 装载/WEB-INF/webx.xml,/WEB-INF/webx-*.xml (com.alibaba.citrus.webx.context.WebxContextLoaderListener)
- 配置过滤器通过SLF4J MDC（Mapped Diagnostic Context）来记录用户和请求的信息，处理不同源请求
- 初始化spring容器的filter（com.alibaba.citrus.webx.servlet.WebxFrameworkFilter）
##  ##
> LogConfiguratorListener实现ServletContextListener接口，在Servlet容器启动时执行contextInitialized()方法，获取以log开头的init params来配置日志文件

    //LogConfiguratorListener.java
    public class LogConfiguratorListener implements ServletContextListener {
      private static final String LOG_CONFIGURATION = "logConfiguration";
      private static final String LOG_SYSTEM        = "logSystem";
      private static final String LOG_PREFIX        = "log";
      private LogConfigurator[] logConfigurators;
      public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();

        // 取得所有以log开头的init params。
        Map<String, String> params = getLogInitParams(servletContext);

        // 从context init-param中取得logSystem的值，可能为null。
        String[] logSystems = getLogSystems(params);

        // 取得指定名称的logConfigurator，如未取得，则抛出异常，listener失败。
        logConfigurators = LogConfigurator.getConfigurators(logSystems);

        for (LogConfigurator logConfigurator : logConfigurators) {
            String logSystem = logConfigurator.getLogSystem();

            // 取得指定logConfigurator的配置文件。
            String logConfiguration = getLogConfiguration(params, logSystem);

            servletContext.log(String.format("Initializing %s system", logSystem));

            // 取得log配置文件。
            URL logConfigurationResource;

            try {
                logConfigurationResource = servletContext.getResource(logConfiguration);
            } catch (MalformedURLException e) {
                logConfigurationResource = null;
            }

            // 如未找到配置文件，则用默认的值来配置，否则配置之。
            if (logConfigurationResource == null) {
                servletContext
                        .log(String
                                     .format("Could not find %s configuration file \"%s\" in webapp context.  Using default configurations.",
                                             logSystem, logConfiguration));

                logConfigurator.configureDefault();
            } else {
                Map<String, String> props = logConfigurator.getDefaultProperties();
                initProperties(props);
                props.putAll(params);

                logConfigurator.configure(logConfigurationResource, props);
            }
        }
    }
LogConfiguratorListener:
> 1.获取以LOG_PREFIX开头的初始化参数
 
> 2.获取LOG_SYSTEM指定的xml文件来配置日志容器，若未指定，则采用默认可用日志容器（logback）

> 3.配置日志系统
##  ##


> WebxContextLoaderListener用来启动root context的listener，和Spring的ContextLoaderListener类似。listener将读取/WEB-INF/web.xml中context param contextClass所指定的类名，作为root ApplicationContext的实现类，假如未指定则使用WebxApplicationContext
    
    //WebxContextLoaderListener.java
    public class WebxContextLoaderListener extends ContextLoaderListener {
	@Override
    protected final ContextLoader createContextLoader() {
        return new WebxComponentsLoader() {

            @Override
            protected Class<? extends WebxComponentsContext> getDefaultContextClass() {
                Class<? extends WebxComponentsContext> defaultContextClass = WebxContextLoaderListener.this
                        .getDefaultContextClass();

                if (defaultContextClass == null) {
                    defaultContextClass = super.getDefaultContextClass();
                }

                return defaultContextClass;
            }
        };
    }

    protected Class<? extends WebxComponentsContext> getDefaultContextClass() {
        return null;
    }
    }
WebxApplicationContext:
> 1.继承了ContextLoaderListener，并重载了createContextLoader（）方法；

> 2.在createContextLoader中new了一个WebxComponentsLoader对象，WebxComponentsLoader对象继承了WebxApplicationContext，取得默认配置文件/WEB-INF/webx.xml，/WEB-INF/webx-*.xml
##  ##
> SetLoggingContextFilter通过SLF4J MDC来记录用户和请求的信息

    //SetLoggingContextFilter.java
    public class SetLoggingContextFilter extends FilterBean {
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        SetLoggingContextHelper helper = new SetLoggingContextHelper(request);

        try {
            helper.setLoggingContext();

            chain.doFilter(request, response);
        } finally {
            helper.clearLoggingContext();
        }
    }
    }
SetLoggingContextFilter:
> 1.继承了FilterBean，在FilterBean的init方法中将init-params注入到filter中，将request和response转换成 <code>HttpServletRequest</code>和<code>HttpServletResponse</code>

> 2.重载方法doFilter，通过SetLoggingContextHelper对象实现日志的记录