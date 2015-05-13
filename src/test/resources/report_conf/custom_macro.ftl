<#assign system = "reportEngine">
<#include "/beta_macro.ftl"/>

<#assign tipId=0 />
<#macro renderHtmlHead>
	<head>
		<meta charset="utf-8">
		<title>${report.tiele!}</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		
		<!-- css -->
		<link href="${ctx}/css/skin/bootstrap-redy.css" rel="stylesheet" id="bs-css">

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
		<!--
		<script src="${ctx}/js/dynamic-page.js"></script>
		-->
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
         -->
    
	    <style type="text/css">
			.div-space {
				margin-left: 15px;
				margin-right: 15px;
			}
	    </style>
	
	    <@block name="head"></@block>  
	</head>
</#macro>

<#macro renderBoxHeader title="" icon="icon-book" kpis=[]>
	<div class="box-header well">
			<h2>
				<i class="${icon}"></i>
				<span id='tTitle'>${title!}</span>
				
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
			</h2>
			<div class="box-icon">
				<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
			</div>
	</div>
	<#assign tipId=tipId+1 />
</#macro>

<#-- 渲染tabs, 参数示例: tabs : ['chart1','chart2','chart3'] -->
<#macro renderTabs tabs>
	<div class="tabbable">
		<ul class="nav nav-tabs">
			<#list tabs as tab>
				<#assign tabTitle = report.getElementById(tab).title/>
				<#if tab_index=0>
					<li class="active"><a href="#Tab${tab}" data-toggle="tab" onclick="showTab('Tab${tab}');">${tabTitle}</a></li>
				<#else>
					<li><a href="#Tab${tab}" data-toggle="tab" onclick="showTab('Tab${tab}');">${tabTitle}</a></li>
				</#if>
			</#list>
		</ul>
		<div class="tab-content">
			<#list tabs as tab>
				<#if tab_index=0>
					<div class="tab-pane active" id="Tab${tab}">
				<#else>
					<div class="tab-pane" id="Tab${tab}">
				</#if>
					<@renderObject tab/>
				</div>						
			</#list>				
		</div>
	</div>
</#macro>




