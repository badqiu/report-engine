package com.github.reportengine.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

import com.github.reportengine.ReportEngine;
import com.github.reportengine.util.CookieUtil;
import com.github.reportengine.util.ResponseUtil;

import freemarker.ext.servlet.IncludePage;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ReportEngineServlet extends HttpServlet{

	private static final long serialVersionUID = 1;
	private ReportEngine reportEngine = null;
	
	/**
	 * 报表引擎保存的参数cookie的前缀
	 */
	private static String REPORT_ENGINE_COOKIE_PARAM_PREFIX = "rqp";
	
	@Override
	public void init() throws ServletException {
		super.init();
		ApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		reportEngine = wac.getBean("reportEngine",ReportEngine.class);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			service0(req, resp);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e); 
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			service0(req, resp);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e); 
		}
	}

	/**
	 * cookie失效时间，单位：秒
	 */
	int COOKIE_MAX_AGE = 3600 * 10; 
	private void service0(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html;charset=UTF-8");
		
		String method = resloveMethod(req);
		String reportPath = req.getParameter("reportPath");
		Map params = genParams(req, resp);
		
		Map<String, Object> rawParam = WebUtils.getParametersStartingWith(req, "");
		CookieUtil.saveParamInotCookie(excludeCookieMapByKeys(rawParam),resp,REPORT_ENGINE_COOKIE_PARAM_PREFIX,COOKIE_MAX_AGE);
		
		if("parameter".equals(method)) {
			parameter(reportPath,params,req, resp);
		}else if("report".equals(method)) {
			report(reportPath,params,req, resp);
		}else if("frameset".equals(method)) {
			frameset(reportPath,params,req, resp);
		}else if("download".equals(method)) {
			download(reportPath,params,req, resp);
		}else if("cleanParamCookies".equals(method)){
			cleanParamCookies(req, resp);
		}else if("cleanReportCache".equals(method)) {
			cleanReportCache(req, resp);
		}else {
			throw new RuntimeException("unknow method:"+method+" servletPath:"+req.getRequestURI());
		}
	}



	
	private void cleanReportCache(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		String cleanDate=req.getParameter("cleanDate");
		reportEngine.cleanReportCache(cleanDate);
		resp.getOutputStream().write("200".getBytes());
	}

	@SuppressWarnings("unchecked")
	protected Map genParams(HttpServletRequest req, HttpServletResponse resp) {
		Map param = new HashMap();
		
		//cookie param
		param.putAll(excludeCookieMapByKeys(CookieUtil.getCookieMap(req,REPORT_ENGINE_COOKIE_PARAM_PREFIX)));
		param.put("cookie", CookieUtil.getCookieMap(req,""));
		
		Map<String, Object> rawParam = WebUtils.getParametersStartingWith(req, "");
		param.putAll(rawParam);
		
		param.put("request", req);
		param.put("response", resp);
		param.put("ctx", req.getContextPath());
		param.put("param", param);
		param.put("application",getServletContext());
		param.put("session",req.getSession());
		param.put("include_page", new IncludePage(req,resp));
		
		return param;
	}
	
	private static Map excludeCookieMapByKeys(Map cookies) {
		String[] excludes = new String[]{"page"};
		Map result = new HashMap();
		for(Object key : cookies.keySet()) {
			if(ArrayUtils.contains(excludes, key)) {
				continue;
			}
			result.put(key, cookies.get(key));
		}
		return result;
	}
	
	/**
	 * extract method from "/services/serviceId/method.do" 
	 * @param request
	 * @return
	 */
	private String resloveMethod(HttpServletRequest request) {
		String[] array = StringUtils.split(request.getRequestURI(), '/');
		if(array.length < 1) {
			throw new IllegalArgumentException("cannot reslove method from requestURI:"+request.getRequestURI());
		}
		String method = array[array.length - 1];
		return method; 
	}
	
	/**
	 * 参数，报表整合在一起查看
	 * @param req
	 * @param resp
	 * @throws IOException 
	 * @throws TemplateException 
	 * @throws ServletException 
	 */
	private void frameset(String reportPath,Map params,HttpServletRequest req, HttpServletResponse resp) throws IOException, TemplateException, ServletException {
		Template template = reportEngine.getFreemarkerConfiguration().getTemplate("frameset.ftl");
		Map map = reportEngine.getTemplateModel(reportPath, params);
		String str = FreeMarkerTemplateUtils.processTemplateIntoString(template,map);
		ResponseUtil.writeString(resp, str);
	}

	/**
	 * 查看报表
	 * @param req
	 * @param resp
	 * @throws IOException 
	 */
	private void report(String reportPath,Map params,HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String report = reportEngine.renderReport(reportPath,params);
		ResponseUtil.writeString(resp, report);
	}

	/**
	 * 查看报表参数
	 * @param req
	 * @param resp
	 * @throws IOException 
	 */
	private void parameter(String reportPath,Map params,HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ResponseUtil.writeString(resp,reportEngine.renderParameter(reportPath, params));
	}
	
	/**
	 * 清除ReportEngine保留在cookie中的参数值
	 * @param req
	 * @param resp
	 * @throws IOException 
	 */
	private void cleanParamCookies(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		CookieUtil.cleanAllCookie(req, resp, REPORT_ENGINE_COOKIE_PARAM_PREFIX);
		
		String returnUrl = req.getParameter("returnUrl");
		if(StringUtils.isNotBlank(returnUrl)) {
			resp.sendRedirect(returnUrl);
		}
	}
	
	/**
	 * 下载报表
	 * @param req
	 * @param resp
	 * @throws IOException 
	 */
	private void download(String reportPath,Map params,HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String table = req.getParameter("table");
		Assert.hasText(table,"table parameter must be not empty");
		resp.setContentType("application/octet-stream");
		resp.setHeader("Content-Disposition", "attachment; filename="+table+".csv");
		
		ResponseUtil.writeString(resp,reportEngine.download(reportPath, params));
	}
	
}
