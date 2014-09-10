<#assign system = "reportEngine">

<#assign tipId=0 />
<#macro renderHtmlHead>
	<head>
		<meta charset="utf-8">
		<title>${report.tiele!}</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		
		<#assign bootstrapCssList = ["bootstrap-cerulean.css","bootstrap-classic.css","bootstrap-cyborg.css","bootstrap-journal.css","bootstrap-redy.css","bootstrap-simplex.css","bootstrap-slate.css","bootstrap-spacelab.css","bootstrap-united.css"]/>
		<#assign randomCssIndex =  now?string('dd')?number % bootstrapCssList?size/>
		<!-- css -->
		<link href="${ctx}/css/skin/${bootstrapCssList[randomCssIndex]}" rel="stylesheet" id="bs-css">

		<link href="${ctx}/css/bootstrap-responsive.min.css" rel="stylesheet">
		<link href="${ctx}/css/bootstrap-sortable.css" rel="stylesheet">
		<link href="${ctx}/css/dynamic-page.css" rel="stylesheet">
		
		<!-- external javascript
		================================================== -->
		<!-- Placed at the end of the document so the pages load faster -->
	
		<script src="${ctx}/js/jquery.min.js"></script>
		<!-- 在dynamic-page.js中, 用到了jquery-ui的sortable拖曳特效 -->
		<script src="${ctx}/js/jquery-ui.custom.min.js"></script>
		<script src="${ctx}/js/bootstrap.min.js"></script>
		<script src="${ctx}/js/dynamic-page.js"></script>
		<script src="${ctx}/js/bootstrap-tooltip.js"></script>
		<script src="${ctx}/js/bootstrap-popover.js"></script>
		
		<!-- begin import highcharts -->
		<script src="${ctx}/js/highcharts/highcharts.js"></script>
	    <script src="${ctx}/js/highcharts/modules/exporting.js"></script>
	    <script src="${ctx}/js/highcharts/highchartsUtil.js"></script>
	    <!-- end import highcharts -->	
	    
	     <!-- my97 date picker -->
   		 <script src="${ctx}/js/My97DatePicker/WdatePicker.js"></script>
    
    	 <script src="${ctx}/js/bootstrap-sortable.js"></script>
    	 
    	 <!--monitor system js -->
    	 <script src="${ctx}/js/monitor.js"></script>
    
     	<script type="text/javascript">

		(function($){
    		$.getUrlParam = function(name)
   			 {
        		var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        		var r = window.location.search.substr(1).match(reg);
        		if (r!=null) return unescape(r[2]); return null; 
    			}
		})(jQuery);
        $(function () {
			var showTab = $.getUrlParam('showTab');
        	var hideTab = $.getUrlParam('hideTab');
         	 
         	if(showTab!=null && hideTab!=null){
         	
         	    var showTabArray = new Array();
         	    var hideTabArray = new Array();
         	    
         		showTabArray=showTab.split(","); //字符分割      
         		hideTabArray=hideTab.split(","); //字符分割    
         		
		 		for (i=0;i<showTabArray.length;i++ ){    
		         $('#'+showTabArray[i]).addClass('active');
		    	} 
         		  
		 		for (i=0;i<hideTabArray.length;i++ ){    
		         $('#'+hideTabArray[i]).removeClass('active');
		    	}          		

        	 }
        	        	       	        	
         })

         </script>
    
	    <style type="text/css">
			.div-space {
				margin-left: 15px;
				margin-right: 15px;
			}
	    </style>
	
	    <@block name="head"></@block>  
	</head>
</#macro>

<#macro renderBoxHeader title="" icon="icon-book" kpiTips=[]>
	<div class="box-header well">
			<h2>
				<i class="${icon}"></i>
				<span id='tTitle'>${title!}</span>
				<#if kpiTips?size!=0>
					<#assign kpiList = application.getAttribute("kpiList") />
					<a id="tip${tipId}" href="#" class="btn btn-mini btn-round" rel="popover" data-original-title="描述" data-content="
						<#list kpiTips as kpiTip>
							<#list kpiList as kpi>
								<#if kpiTip==kpi['kpi_code']>
									<font color=#D2691E><strong>${kpi['kpi_name']}:</strong></font>${kpi['remarks']}<br>
								</#if>
							</#list>
						</#list>
					">
					<i class="icon-question-sign"></i></a>
					<script type="text/javascript">
					  $('#tip${tipId}').popover();
					</script>
				</#if>
			</h2>
			<div class="box-icon">
				<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
			</div>
	</div>
	<#assign tipId=tipId+1 />
</#macro>

<#macro renderChartTableTabs tabs>
	<div class="tabbable">
		<ul class="nav nav-tabs">
			<#list tabs as tab>
				<#if tab_index=0>
					<li class="active"><a href="#Tab${tipId}${tab_index}" data-toggle="tab" onclick="showTab('Tab${tipId}${tab_index}');">${tab[0]}</a></li>
				<#else>
					<li><a href="#Tab${tipId}${tab_index}" data-toggle="tab" onclick="showTab('Tab${tipId}${tab_index}');">${tab[0]}</a></li>
				</#if>
			</#list>
		</ul>
		<div class="tab-content">
			<#list tabs as tab>
				<#if tab_index=0>
					<div class="tab-pane active" id="Tab${tipId}${tab_index}">
				<#else>
					<div class="tab-pane" id="Tab${tipId}${tab_index}">
				</#if>
					<@renderChart report.getElementById("${tab[1]}")/>
					<@renderTable report.getElementById("${tab[2]}")/>
					</div>						
			</#list>				
		</div>
	</div>		
	<#assign tipId=tipId+1 />		
</#macro>

<#macro renderChartTabs tabs>
	<div class="tabbable">
		<ul class="nav nav-tabs">
			<#list tabs as tab>
				<#if tab_index=0>
					<li class="active"><a href="#Tab${tab[0]}" data-toggle="tab" onclick="showTab('Tab${tab[0]}');">${tab[0]}</a></li>
				<#else>
					<li><a href="#Tab${tab[0]}" data-toggle="tab" onclick="showTab('Tab${tab[0]}');">${tab[0]}</a></li>
				</#if>
			</#list>
		</ul>
		<div class="tab-content">
			<#list tabs as tab>
				<#if tab_index=0>
					<div class="tab-pane active" id="Tab${tab[0]}">
				<#else>
					<div class="tab-pane" id="Tab${tab[0]}">
				</#if>
					<@renderChart report.getElementById("${tab[1]}")/>
					</div>						
			</#list>				
		</div>
	</div>			
</#macro>

<#macro renderTabs tabs>
	<div class="tabbable">
		<ul class="nav nav-tabs">
			<#list tabs as tab>
				<#if tab_index=0>
					<li class="active"><a href="#Tab${tipId}${tab_index}" data-toggle="tab" onclick="showTab('Tab${tipId}${tab_index}');">${tab[1]}</a></li>
				<#else>
					<li><a href="#Tab${tipId}${tab_index}" data-toggle="tab" onclick="showTab('Tab${tipId}${tab_index}');">${tab[1]}</a></li>
				</#if>
			</#list>
		</ul>
		<div class="tab-content">
			<#list tabs as tab>
				<#if tab_index=0>
					<div class="tab-pane active" id="Tab${tipId}${tab_index}">
				<#else>
					<div class="tab-pane" id="Tab${tipId}${tab_index}">
				</#if>
				<#if tab[0]=='chart'>
					<@renderChart report.getElementById("${tab[2]}")/>
				<#else>
					<@renderTable report.getElementById("${tab[2]}")/>
				</#if>
				</div>						
			</#list>				
		</div>
	</div>
	<#assign tipId=tipId+1 />	
</#macro>

<#macro renderTableList tables>
	<#list tables as table>
		<@renderTable report.getElementById("${table}") />
	</#list>
</#macro>

<#macro renderChartList charts>
	<#list charts as chart>
		<@renderChart report.getElementById("${chart}") />
	</#list>
</#macro>

<#macro renderChartTable chart table>
	<@renderChart report.getElementById("${chart}")/>
	<@renderTable report.getElementById("${table}")/>	
</#macro>

<#macro renderNav>
		<div>
			<ul class="breadcrumb" style="color:#08c;">
				<li>
					${report.title}
				</li>
			</ul>
		</div>
</#macro>