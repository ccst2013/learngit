Spring容器启动将自动注册实现BeanFactoryPostProcessors,BeanPostProcessors,ApplicationListeners的bean，且
BeanFactoryPostProcessors先于BeanPostProcessors调用，都由Spring容器自己调用。
	PropertyPlaceholderConfigurer <-- BeanFactoryPostProcessors
	CustomEditorConfigure <-- BeanFactoryPostProcessors
Spring容器刷新流程
	1.初始化Bean工厂	ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
						prepareBeanFactory(beanFactory);
						postProcessBeanFactory(beanFactory);

	2.调用工厂后处理器	invokeBeanFactoryPostProcessors(beanFactory);

	3.注册Bean后处理器	registerBeanPostProcessors(beanFactory);

	4.初始化消息源		initMessageSource();

	5.初始化事件广播器	initApplicationEventMulticaster();

	6.初始化子类其他特殊Bean	onRefresh();

	7.注册事件监听器	registerListeners();

	8.初始化所有单实例Bean,懒惰初始化除外	finishBeanFactoryInitialization(beanFactory);

1.初始化BeanFactory
	1.ResourceLoader 加载Spring配置信息，并使用Resource来表示资源
	2.BeanDefinitionReader 读取Resource指向资源，解析<bean>为BeanDefinition对象，保存到BeanDefinitionRegister中
	3.使用java反射机制，遍历BeanDefinitionReader中的BeanDefinition对象，识别出实现BeanFactoryPostProcessors的Bean对象，并用这些工厂后处理器来对BeanDefinitionRegister中的BeanDefinition进行加工处理：
		1.对占位符替换；
		2.把实现属性编辑器的Bean注册到Spring属性编辑器
	4.遍历BeanDefinitionReader中的BeanDefinition对象，BeanWrapper封装并实例化Bean
	5.调用Bean后处理器对设置好的Bean进行加工

Spring容器在启动过程中会去bean中查找实现了ApplicationEventMulticaster 且id为applicationEventMulticaster的bean来作为事件广播器;如果没找到,则使用默认的SimpleApplicationEventMulticaster
AbstractApplicationContext 
	-->initApplicationEventMulticaster(){
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
			this.applicationEventMulticaster =
					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
		}
		else {//使用默认的SimpleApplicationEventMulticaster
			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
		}
	}
AbstractBeanFactory 
	-->containsLocalBean(String name){
		String beanName = transformedBeanName(name);
		if (containsSingleton(beanName) || containsBeanDefinition(beanName)) {
			return (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(name));
		}
		// Not found -> check parent.
		BeanFactory parentBeanFactory = getParentBeanFactory();
		return (parentBeanFactory != null && parentBeanFactory.containsBean(originalBeanName(name)));
	}