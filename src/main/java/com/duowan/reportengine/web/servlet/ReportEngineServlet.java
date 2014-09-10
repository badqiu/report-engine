package com.duowan.reportengine.web.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

import com.duowan.common.util.DateConvertUtils;
import com.duowan.reportengine.ReportEngine;
import com.duowan.reportengine.util.CookieUtil;
import com.duowan.reportengine.util.ResponseUtil;

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
	/**
	 * 报表数据请求的密钥
	 */
	private final static String MONITOR_KEY = "efWQoq48saerDeVuGj75wd";
	
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
			doGet0(req, resp);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e); 
		}
	}

	private void doGet0(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html;charset=UTF-8");
		
		String method = resloveMethod(req);
		String reportPath = req.getParameter("reportPath");
		Map params = genParams(req, resp);
		
		Map<String, Object> rawParam = WebUtils.getParametersStartingWith(req, "");
		CookieUtil.saveParamInotCookie(excludeCookieMapByKeys(rawParam),resp,REPORT_ENGINE_COOKIE_PARAM_PREFIX);
		
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
		}else if("monitorArg".equals(method)){
			String metadataId = req.getParameter("metadataId");
			String metadataType = req.getParameter("metadataType");
			String isRetainedData = req.getParameter("isRetainedData");
			formatQueryDate(isRetainedData,params);
			metadata(reportPath, metadataType, metadataId, params, req, resp);
		}else if("monitorReport".equals(method)){
			String enc = req.getParameter("monitorReportEnc");
			String time = req.getParameter("monitorReportTime");
			String passport = req.getParameter("monitorReportPassport");
			isValidRequest(time, enc, passport);
			req.getSession().setAttribute("passport", passport);
			String metadataId = req.getParameter("metadataId");
			String metadataType = req.getParameter("metadataType")+".xml";
			metadata(reportPath, metadataType, metadataId, params, req, resp);
		}else{
			throw new RuntimeException("unknow method:"+method);
		}
	}
	/**
	 * 格式化查询时间,非用户留存数据
	 */
	@SuppressWarnings("rawtypes")
	void formatQueryDate(String isRetainedData,Map params){
		if(StringUtils.isBlank(isRetainedData) || isRetainedData.toString().equals("false")){
			String tdate = DateConvertUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
			Object startDate = params.get("startDate");
			Object endDate = params.get("endDate");
			if(startDate!=null && endDate!=null && startDate.toString().equalsIgnoreCase(endDate.toString())){
				tdate = startDate.toString();
			}else {
				//时间类型
				Object tdateType = params.get("tdateType");
				if(tdateType != null ){
					String spanType = tdateType.toString();
					if(spanType.equalsIgnoreCase("week")){
						tdate = com.duowan.reportengine.util.DateUtils.getBeginDayOfLastWeek(new Date());
					}else if (spanType.equalsIgnoreCase("month")) {
						tdate = com.duowan.reportengine.util.DateUtils.getFirstDayOfLastMonth(new Date());
					}else if (spanType.equalsIgnoreCase("hour")) {
						tdate = com.duowan.reportengine.util.DateUtils.getBeginTimeOfLastHour(new Date());
					}else if (spanType.equalsIgnoreCase("minute")) {
						tdate = com.duowan.reportengine.util.DateUtils.getBeginTimeOfLast5Minute(new Date());
					}
				}
			}
			params.put("startDate", tdate);
			params.put("endDate", tdate);
		}
	}
	/**
	 * 参数非法则抛出异常
	 * @param time
	 * @param enc
	 * @throws IllegalArgumentException
	 */
	void isValidRequest(String time,String enc,String passport) throws IllegalArgumentException{
		if(StringUtils.isBlank(enc)||StringUtils.isBlank(time)
			||Math.abs(System.currentTimeMillis()-Long.valueOf(time))/(1000*60*60)>24
			||!DigestUtils.md5Hex(passport + time + MONITOR_KEY).equalsIgnoreCase(enc)){
			throw new IllegalArgumentException("Illegal Argument: passport="+passport+", enc="+enc+",time="+time);
		}
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
		param.put("queryParams", buildParamStringFromMap(param));
		param.put("monitor_report_passport",req.getSession().getAttribute("monitor_report_passport"));
		
		return param;
	}
	private String buildParamStringFromMap(Map param){
		String paramString = "";
		Object paramKeys[] = param.keySet().toArray();
		for(int i = 0; i < param.size(); i++) {
			if(param.get(paramKeys[i]) instanceof String && filterString((String)paramKeys[i],"startDate","endDate","reportPath")){
				paramString += "&"+paramKeys[i].toString();
				paramString += "=";
				paramString += param.get(paramKeys[i]);
			}
		}
		return paramString.substring(1);
	}
	
	private boolean filterString(String source,String ... filters){
		for(String filter:filters){
			if(filter.equalsIgnoreCase(source)){
				return false;
			}
		}
		return true;
	}

	private Map excludeCookieMapByKeys(Map cookies) {
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
	 * 参数，报表整合在一起查看
	 * @param req
	 * @param resp
	 * @throws IOException 
	 * @throws TemplateException 
	 * @throws ServletException 
	 */
	private void metadata(String reportPath,String metadataType,String metadataId,Map params,HttpServletRequest req, HttpServletResponse resp) throws IOException, TemplateException, ServletException {
		Template template = reportEngine.getFreemarkerConfiguration().getTemplate(metadataType+".ftl");
		params.put("metadataId", metadataId);
		params.put("reportUrl", req.getRequestURL()+"?reportPath="+reportPath);
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
	 */
	private void download(String reportPath,Map params,HttpServletRequest req, HttpServletResponse resp) {
	}
	
}
