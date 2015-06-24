<#include "/beta_macro.ftl"/>

<#assign tipId=0 />
<#macro renderHtmlHead>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		
		<title>${report.tiele!}</title>
		
		<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
    	<link href="${ctx}/css/bootstrap-theme.css"  rel="stylesheet" />
    	
		<script src="${ctx}/js/jquery.min.js"></script>
		<script src="${ctx}/js/bootstrap.min.js"></script>
		
		<script src="${ctx}/js/bootstrap-tooltip.js"></script>
		<script src="${ctx}/js/bootstrap-popover.js"></script>
		<script src="${ctx}/js/bootstrap-tab.js"></script>
		
		<!-- bootstrap-sortable -->
    	<script src="${ctx}/js/bootstrap-sortable.js"></script>
		<link href="${ctx}/css/bootstrap-sortable.css" rel="stylesheet">
		
		<!-- multiple-select -->
		<link href="${ctx}/js/multiple-select/multiple-select.css" rel="stylesheet"/>
		<script src="${ctx}/js/multiple-select/jquery.multiple.select.js"></script>
		
		<!-- highcharts -->
		<script src="${ctx}/js/highcharts/highcharts.js"></script>
	    <script src="${ctx}/js/highcharts/modules/exporting.js"></script>
	    <script src="${ctx}/js/highcharts/highchartsUtil.js"></script>
	    
	    <!-- my97 date picker -->
   		<script src="${ctx}/js/My97DatePicker/WdatePicker.js"></script>

	    <style type="text/css">
			.div-space {
				margin-left: 15px;
				margin-right: 15px;
			}
			#queryPanel {
				height : 32px;
			}
	    </style>
	
	    <@block name="head"></@block>  
	</head>
</#macro>

<#macro renderBoxHeader title="" icon="icon-book" kpis=[]>
		<div class="panel-heading">
				<i class="${icon}"></i>
				${title!}
				
				<#if kpis?size!=0>
					<#assign kpiMap = application.getAttribute("kpiMap") />
					<a id="tip${tipId}" href="#" class="btn btn-mini btn-round" rel="popover" data-original-title="描述" data-content="
						<#list kpis as kpi>
							<font color=#D2691E><strong>${kpiMap[kpi].kpiName}: </strong>${kpiMap[kpi].kpiDesc}</font><br>
						</#list>
					">
					<i class="icon-question-sign"></i></a>
					<script type="text/javascript">
					  $('#tip${tipId}').popover();
					</script>
					
				</#if>
		</div>
	<#assign tipId=tipId+1 />
</#macro>

<#-- 渲染tabs, 参数示例: tabs : ['chart1','chart2','chart3'] -->
<#macro renderTabs tabs>
	<div class="tabbable">
	
		<ul class="nav nav-tabs">
			<#list tabs as tab>
				<#assign tabTitle = report.getElementById(tab).title/>
				<#assign tabId = report.getElementById(tab).id/>
				<li <#if tab_index=0> class="active" </#if> ><a href="#tab_${tabId}" data-toggle="tab">${tabTitle}</a></li>
			</#list>
		</ul>
		
		<div class="tab-content">
			<#list tabs as tab>
				<#assign tabId = report.getElementById(tab).id/>
				<div class="tab-pane <#if tab_index=0> active </#if>" id="tab_${tabId}">
					<@renderObject tab/>
				</div>						
			</#list>				
		</div>
		
	</div>
</#macro>




