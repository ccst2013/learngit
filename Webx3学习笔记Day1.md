## Webx3 学习笔记 ##
# MAVEN管理 #
## pom.xml ##
- `<modelVersion>4.0.0</modelVersion>` 默认版本为4.0.0
- `<groupId>com.alibaba.hello</groupId>` 组ID
- `<artifactId>hello</artifactId>` 框架Id 
- `<version>0.0.1-SNAPSHOT</version>` 版本（SNAPSHOT代表测试版）
- `<packaging>pom</packaging>` 打包类型（pom，jar，war），一般父模块指定pom，子模块非web的指定jar，子模块web指定war
- `<modules><module>helloDao</module></modules>` 父模块包含下级子模块
>     <parent>
	     <groupId>com.alibaba.hello</groupId>
	     <artifactId>hello</artifactId>
	     <version>0.0.1-SNAPSHOT</version>
	     <relativePath>../pom.xml</relativePath> 
>     </parent> <!-- 用在子模块中来包含父模块信息 -->

>     <properties>
  		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  		<java.version>1.5</java.version>
  		<java.Encoding>UTF-8</java.Encoding>
  		<webx.version>3.2.4</webx.version>
  		<spring.version>3.2.7.RELEASE</spring.version>
  		<springext-plugin-version>1.2</springext-plugin-version>
  		<junit.version>4.11</junit.version>
  		<velocity.version>1.7</velocity.version>
  		<mybatis.version>1.1.0</mybatis.version>
>     </properties> <!-- 属性配置用来管理jar包版本 -->
>     <dependencies>
  			<dependency>
  				<groupId>junit</groupId>
  				<artifactId>junit</artifactId>
  			</dependency>
  			<dependency>
				<groupId>org.slf4j</groupId>
				<artifactId>slf4j-api</artifactId>
			</dependency>
			<dependency>
				<groupId>ch.qos.logback</groupId>
				<artifactId>logback-classic</artifactId>
			</dependency> 
>     </dependencies> <!-- 引入依赖的jar包，其版本由dependencyManagement统一管理；子模块可以继承父模块的jar包 -->
>      <dependencyManagement>
  	<dependencies>
  	<!-- 单元测试junit -->
  		<dependency>
  			<groupId>junit</groupId>
  			<artifactId>junit</artifactId>
  			<version>${junit.version}</version>
  		</dependency>
  	<!-- webx框架依赖 -->
  		<dependency>
  			<groupId>com.alibaba.citrus</groupId>
  			<artifactId>citrus-webx-all</artifactId>
  			<version>${webx.version}</version>
  		</dependency>
>     </dependencyManagement> <!-- 统一管理父模块和子模块需要引入的jar包，放在父模块中 -->
>      <build>
  		<plugins>
  			<!-- maven编译插件指定为1.5版本，支持注解 -->
  			<plugin>
  				<groupId>org.apache.maven.plugins</groupId>
  				<artifactId>maven-compiler-plugin</artifactId>
  				<configuration>
  					<source>${java.version}</source>
  					<target>${java.version}</target>
  				</configuration>
  			</plugin>
		</plugins> <!-- maven编译插件引入 -->
  		<pluginManagement>
			<plugins>
				<plugin>
					<artifactId>maven-antrun-plugin</artifactId>
					<version>1.6</version>
				</plugin>
			</plugins>
		</pluginManagement> <!-- maven编译插件管理 -->
>     </build>