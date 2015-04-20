############################################
      report engine 主要结构描述
############################################

主要相关技术:
	xstream (xml parse)
	highchart (html5 chart)
	bootstrape (css framework)
	jackson (json)
	groovy
	spring (report可以引用spring定义的bean)

一. XML结构: 
	params 查询参数 
	dataSources 数据源
	querys 数据库SQL查询,查询完的结果集将放在context中
	groovy 脚本，在查询开始前及查询数据结果后执行
	charts 图形
	tables 表格

二. 执行
	1. 解析XML得到 Report对象
	2. 执行Report对象的 querys,及 groovy,得到 context
	3. 通过 context渲染freemarker报表

三. freemarker渲染报表
	renderParams(params)
	renderChart(chart)
	renderTable(table)

四. 工具类方法
	SelectorUtil,应用在 report.getElementById(id) 
		public static Object getElementById(Object root, String id)
		
	/**
	 * 报表引擎生命周期接口
	 * 
	 * @author badqiu
	 *
	 */
	public interface ReportEngineLifecycle {
		
		public void beforeQuery(Map<String,Object> context);
		
		public void afterQuery(Map<String,Object> context);
		
	}

############################################
      report engine 使用描述
############################################
1. maven依赖
	<dependency>
		<groupId>com.duowan.common</groupId>
		<artifactId>duowan-report-engine</artifactId>
		<version>0.1.1-SNAPSHOT</version>
	</dependency>	

2. web.xml配置
	<!-- Spring ApplicationContext配置文件的路径,可使用通配符，多个路径用,号分隔,此参数用于后面的Spring-Context loader -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath*:spring/report_engine/*.xml,</param-value>
	</context-param>

	<!--Spring ApplicationContext 载入 -->
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
	
	<servlet>
		<servlet-name>ReportEngineServlet</servlet-name>
		<servlet-class>com.github.reportengine.web.servlet.ReportEngineServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	
	<servlet-mapping>
		<servlet-name>ReportEngineServlet</servlet-name>
		<url-pattern>/ReportEngine/*</url-pattern>
	</servlet-mapping>

3. spring配置: report_engine.xml
	<bean id="reportEngine" class="com.github.reportengine.ReportEngine">
		<property name="baseReportDir" value="${report_baseReportDir}"></property>
		<!-- 引擎配置参数，可以在报表中引用定义的变量 -->
		<property name="engineContext" ref="applicationProperties"/>
	</bean>

4. spring属性 配置
	report_baseReportDir=classpath:report_conf
		frameset.ftl   用于将报表与参数联合展现在一起
		parameter.ftl  用于展现参数
		macro.ftl	        包含标准的macro函数,macro如下
			 renderParams 		渲染参数
			 renderChart  		渲染图形
			 renderTable  		渲染表格
			 renderPagination 	渲染分页
			 
5. 静态资源
	需要将reportEngine中css,img,js等静态资源拷贝至项目中
	

