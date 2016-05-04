## Webx3 学习笔记 ##
# logback.xml配置 #

    logback.xml

> 1.根节点`<configuration>`包含的属性
   
-  **scan**: 当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。
-  **scanPeriod**:
设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。
- **debug**:
  当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。

> 	<configuration scan="true" scanPeriod="60 seconds" 	debug="false">  
      <!-- 其他配置省略-->  
	</configuration>  

> 2.根节点`<configuration>`的子节点
 
![](http://localhost/MarkDownResource/basicSyntax.png)

- **设置上下文名称`<contextName>`**: 每个logger都关联到logger上下文，默认上下文名称为“default”。但可以使用`<contextName>`设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
>     <configuration scan="true" scanPeriod="60 seconds" debug="false">
  		<contextName>myAppName</contextName >
         	<!-- 其他配置省略-->
	</configuration> 

- **设置变量`<property>`**: 用来定义变量值的标签,定义变量后，可以使“${}”来使用变量。

>     <configuration scan="true" scanPeriod="60 seconds" debug="false">  
      <property name="APP_Name" value="myAppName" />   
      <contextName>${APP_Name}</contextName>  
      <!-- 其他配置省略-->  
    </configuration>   

- **获取时间戳字符串`<timestamp>`**: 两个属性 key:标识此`<timestamp>`的名字；datePattern：设置将当前时间（解析配置文件的时间）转换为字符串的模式，遵循java.txt.SimpleDateFormat的格式。

>     <configuration scan="true" scanPeriod="60 seconds" debug="false">  
      <timestamp key="bySecond" datePattern="yyyyMMdd'T'HHmmss"/>   
      <contextName>${bySecond}</contextName>  
      <!-- 其他配置省略-->  
    </configuration>   

# **`<loger>`** 
用来设置某一个包或者具体的某一个类的日志打印级别、以及指定`<appender>`。`<loger>`仅有一个name属性，一个可选的level和一个可选的addtivity属性。

- **name**:
用来指定受此loger约束的**某一个包**或者**具体的某一个类**。
- **level**:
用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，还有一个特俗值INHERITED或者同义词NULL，代表强制执行上级的级别。
如果未设置此属性，那么当前loger将会继承上级的级别。
- **addtivity**:
是否向上级loger传递打印信息。默认是true。
> **`<appender-ref>`**: 
`<loger>`可以包含零个或多个`<appender-ref>`元素，标识这个appender将会添加到这个loger。

>     <appender name="FILE"
		class="ch.qos.logback.core.rolling.RollingFileAppender">
		<file>${loggingRoot}/all.log</file>
		<append>true</append>
		<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<!-- daily rollover -->
			<fileNamePattern>${loggingRoot}/all.%d{yyyy-MM-dd}.log
			</fileNamePattern>
			<!-- keep 30 days' worth of history -->
			<maxHistory>300</maxHistory>
		</rollingPolicy>
		<!-- encoders are assigned the type ch.qos.logback.classic.encoder.PatternLayoutEncoder 
			by default -->
		<encoder>
			<pattern>%-4relative [%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] %-5level - %logger - %msg%n</pattern>
		</encoder>
	</appender>
    <logger name="org.apache" level="INFO"
		additivity="false">
		<appender-ref ref="FILE" />
	</logger>
# **`<root>`** 
也是`<loger>`元素，但是它是根loger。只有一个level属性，应为已经被命名为"root".

- **level**:
用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，**不能**设置为INHERITED或者同义词NULL。
默认是DEBUG。
`<root>`可以包含零个或多个`<appender-ref>`元素，标识这个appender将会添加到这个loger。
# **`<appender>`** 

![](http://localhost/MarkDownResource/appenderSyntax.png)

`<appender>`是`<configuration>`的子节点，是负责写日志的组件。有两个必要属性name和class。name指定appender名称，class指定appender的全限定名。

- 1.**ch.qos.logback.core.ConsoleAppender**:
把日志添加到控制台，有以下子节点：
> `<encoder>`：对日志进行格式化。

> `<target>`：字符串 System.out 或者 System.err ，默认 System.out ；


>     <configuration>  
      <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">  
      <encoder>  
          <pattern>%-4relative [%thread] %-5level %logger{35} - %msg %n</pattern>  
      </encoder>  
      </appender>  
      <root level="DEBUG">  
         <appender-ref ref="STDOUT" />  
      </root>  
    </configuration>

- 2.**ch.qos.logback.core.FileAppender**:
把日志添加到文件，有以下子节点：

> `<file>`：被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值。

> `<append>`：如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true。

> `<encoder>`：对记录事件进行格式化。

> `<prudent>`：如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false。


>     <configuration>  
     <appender name="FILE" class="ch.qos.logback.core.FileAppender">  
     <file>testFile.log</file>  
     <append>true</append>  
     <encoder>  
         <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>  
     </encoder>  
     </appender>          
     <root level="DEBUG">  
         <appender-ref ref="FILE" />  
     </root>  
    </configuration>  

- 3.**ch.qos.logback.core.rolling.RollingFileAppender**:
滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件。有以下子节点：

> `<file>`：被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值。

> `<append>`：如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true。

> `<encoder>`：对记录事件进行格式化。

> `<rollingPolicy>`:当发生滚动时，决定 RollingFileAppender 的行为，涉及文件移动和重命名。

> `<triggeringPolicy>`: 告知 RollingFileAppender 合适激活滚动。

> `<prudent>`：当为true时，不支持FixedWindowRollingPolicy。支持TimeBasedRollingPolicy，但是有两个限制，1不支持也不允许文件压缩，2不能设置file属性，必须留空。

>     //每天生成一个日志文件，保存30天的日志文件
     <configuration>   
      <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">     
      <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">   
        <fileNamePattern>logFile.%d{yyyy-MM-dd}.log</fileNamePattern>   
        <maxHistory>30</maxHistory>    
      </rollingPolicy>   
      <encoder>   
        <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>   
      </encoder>   
     </appender>     
     <root level="DEBUG">   
        <appender-ref ref="FILE" />   
     </root>   
    </configuration>  

- 

>     //按照固定窗口模式生成日志文件，当文件大于20MB时，生成新的日志文件。窗口大小是1到3，当保存了3个归档文件后，将覆盖最早的日志
    <configuration>   
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">   
    <file>test.log</file>      
    <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">   
      <fileNamePattern>tests.%i.log.zip</fileNamePattern>   
      <minIndex>1</minIndex>   
      <maxIndex>3</maxIndex>   
    </rollingPolicy>      
    <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">   
      <maxFileSize>5MB</maxFileSize>   
    </triggeringPolicy>   
    <encoder>   
      <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>   
    </encoder>   
    </appender>           
    <root level="DEBUG">   
    <appender-ref ref="FILE" />   
    </root>   
    </configuration>  

# **`<encoder>`** 
负责两件事，一是把日志信息转换成字节数组，二是把字节数组写入到输出流。
目前PatternLayoutEncoder 是唯一有用的且默认的encoder ，有一个`<pattern>`节点，用来设置日志的输入格式。使用“%”加“转换符”方式，如果要输出“%”，则必须用“\”对“\%”进行转义。

>     <encoder>   
       <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>   
    </encoder  
- relative 输出从程序启动到创建日志记录的时间，单位是毫秒
- thread 输出产生日志的线程名
- level 输出日志级别
- logger 输出日志的logger名，可有一个整形参数，功能是缩短logger名
- msg 输出应用程序提供的信息
- n 输出平台先关的分行符“\n”或者“\r\n”

>         %-4relative 表示，将输出从程序启动到创建日志记录的时间 进行左对齐 且最小宽度为4