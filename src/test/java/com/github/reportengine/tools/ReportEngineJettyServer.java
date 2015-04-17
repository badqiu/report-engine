package com.github.reportengine.tools;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.github.reportengine.ReportEngineTest;
/**
 * 开发调试使用的 Jetty Server
 * @author badqiu
 *
 */
public class ReportEngineJettyServer {
	
	public static void main(String[] args) throws Exception {
		ReportEngineTest.reportEngineEnvSetting();
		
		
		Server server = buildNormalServer(80, "/");
		server.start();
	}
	
	/**
	 * 创建用于正常运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
	 */
	public static Server buildNormalServer(int port, String contextPath) {
		Server server = new Server(port);
		WebAppContext webContext = new WebAppContext("src/main/webapp", contextPath);
		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		webContext.setDefaultsDescriptor("src/test/resources/jetty-webdefault.xml"); // 避免windows lock,设置useFileMappedBuffer=false
		server.setHandler(webContext);
		server.setStopAtShutdown(true);
		return server;
	}


}
