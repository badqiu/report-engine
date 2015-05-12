<#include "/custom_macro.ftl"/>

<#function filter things name value>
    <#local result = []>
    <#list things as thing>
        <#if thing[name] == value>
            <#local result = result + [thing]>
        </#if>
    </#list>
    <#return result>
</#function>

<#macro renderParamsBoxHeader>
	<div class="box">
		<div class="box-header well">
			<h2>
				<i class="icon-search"></i> ${report.title!} 报表作者:${report.author!}
			</h2>
			<div class="box-icon">
			    <!--
				<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a>
				<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a> 
				-->
				<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a> 
			</div>
			
			<div id="beforeN15Div" class="pull-right div-space" style="display:none;">
				<input type="button" class="btn btn-primary" value="最近30天" onclick="getBeforeDay(-30);this.form.submit();">
			</div>					
			<div id="beforeN7Div" class="pull-right div-space" style="display:none;">
				<input type="button" class="btn btn-primary" value="最近7天" onclick="getBeforeDay(-7);this.form.submit();">
			</div>
			<div id="nextDateDiv" class="pull-right div-space">
				<input type="button" class="btn btn-primary" value="后一天" onclick="scrollStartDateEndDate(1);this.form.submit();">
			</div>
			<div id="preDateDiv" class="pull-right div-space">
				<input type="button" class="btn btn-primary" value="前一天" onclick="scrollStartDateEndDate(-1);this.form.submit();">
			</div>
			<div id="queryDiv" class="pull-right div-space">
				<input type="button" class="btn btn-primary" onclick="this.form.submit();" value="查    询">
			</div>
			
			<script type="text/javascript">
			 //前N天
			  Date.prototype.format = function(fmt)
			  {
				    var o = {
				      "M+" : this.getMonth()+1,                 //月份
				      "d+" : this.getDate(),                    //日
				      "h+" : this.getHours(),                   //小时
				      "m+" : this.getMinutes(),                 //分
				      "s+" : this.getSeconds(),                 //秒
				      "q+" : Math.floor((this.getMonth()+3)/3), //季度
				      "S"  : this.getMilliseconds()             //毫秒
				    };
				    if(/(y+)/.test(fmt))
				      fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
				    for(var k in o)
				      if(new RegExp("("+ k +")").test(fmt))
				    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
				    return fmt;
			  }
			  
			  function getDate(day){
				      var edate=new Date( new Date().getTime()  + (day*24*60*60*1000) ).format("yyyy-MM-dd");
				      return edate;
			  }
			
			 /* 
			   将String类型解析为Date类型. 
			   parseDate('2006-1-1') return new Date(2006,0,1) 
			   parseDate(' 2006-1-1 ') return new Date(2006,0,1) 
			   parseDate('2006-1-1 15:14:16') return new Date(2006,0,1,15,14,16) 
			   parseDate(' 2006-1-1 15:14:16 ') return new Date(2006,0,1,15,14,16); 
			   parseDate('2006-1-1 15:14:16.254') return new Date(2006,0,1,15,14,16,254) 
			   parseDate(' 2006-1-1 15:14:16.254 ') return new Date(2006,0,1,15,14,16,254) 
			   parseDate('不正确的格式') retrun null 
			 */  
			 function parseDate(str){  
				   if(typeof str == 'string'){  
				     var results = str.split("-");
				     if(results && results.length>=3)  {
				      return new Date(parseFloat(results[0]),parseFloat(results[1]) -1,parseFloat(results[2]));   
				     }
				   }  
				   throw new Error("cannot parse str for date:"+str);  
			 }
			 
			  /**
			   *函数功能 :前N天
			   */
			 function getBeforeDay(days){
					 if(document.getElementById('startDate')!=null){
				     	document.getElementById('startDate').value = getDate(days);
					 }
				     if(document.getElementById('endDate')!=null){
				     	document.getElementById('endDate').value = getDate(-1);
					 }
			   }
			  
			  /**
			   * 函数功能 :startDate,endDate滚动n天, n可以为正负数
			   */
			 function scrollStartDateEndDate(ndays){
				   if(document.getElementById('startDate')!=null){
				      var sDate= parseDate(document.getElementById('startDate').value);
				      var sLastDate=sDate.getTime() + ndays * (1*24*60*60*1000);
				      var sFormatLastDay=new Date(sLastDate).format("yyyy-MM-dd");
				     document.getElementById('startDate').value = sFormatLastDay;
				   }
				
				   if(document.getElementById('endDate')!=null){
					   var eDate= parseDate(document.getElementById('endDate').value);
					   var eLastDate=eDate.getTime() + ndays * (1*24*60*60*1000);
					   var eFormatLastDay=new Date(eLastDate).format("yyyy-MM-dd");
					   document.getElementById('endDate').value = eFormatLastDay;
				   }
			   
			   }
			   
			   $(document).ready(function(){
			   		if( document.getElementById("startDate") && document.getElementById("endDate") ) {
			   			$('#beforeN15Div').show();
			   			$('#beforeN7Div').show();
			   		}
			   })
   		</script>			
	   </div>
</#macro>

<#macro renderParams paramDefs>
		<@renderParamsBoxHeader/>
		
		<div id="queryConditions" class="box-content container-fluid">
				<#list filter(paramDefs,'hidden',true) as paramDef>
					<@renderParam paramDef/>
				</#list>
				
				<table border="0">
				<#list filter(paramDefs,'hidden',false)?chunk(3) as paramDefRow>
					<tr>
					<#list paramDefRow as paramDef>
					<td>
					<@renderParam paramDef/>
					</td>
				</#list>
				</tr>
			</#list>
			</table>
		</div>	
	</div>
</#macro>

<#macro renderParam paramDef>
	<#if paramDef.defaultValue?has_content >
			<#assign defaultParamValueExpr = paramDef.defaultValue?interpret>
			<#assign defaultParamValue><@defaultParamValueExpr/></#assign>
	<#else>
			<#assign defaultParamValue = "">
	</#if>
	
	<#assign paramValue = paramDef.rawValue!defaultParamValue!>
	
	<#if paramDef.hidden>
		<input type="hidden" id="${paramDef.id}" name="${paramDef.id}" value="${paramValue}" />
	<#else>
		<div id="param_${paramDef.id}" class="input-prepend pull-left div-space">
		
		<#if paramDef.label??>
		<span class="add-on">${paramDef.label}</span>
		</#if>
		
		<#if paramDef.displayType == 'select'>
				<select name="${paramDef.id}" placeholder="${paramDef.label}" onchange="this.form.submit();" <#if paramDef.readonly>disabled="disabled"</#if> >
					<#if defaultParamValue??>
						<option value ="${defaultParamValue}"></option>
					</#if>
					<#list paramDef.dataList as row>
						<#assign valueInterpret = row[paramDef.valueExpr]>
						<#assign labelInterpret = row[paramDef.labelExpr]>
						<option <#if paramValue?string == valueInterpret?string >selected="selected"</#if> value="${valueInterpret}">${labelInterpret}</option> 
					</#list>
				</select>
		<#elseif paramDef.displayType == 'radio'>
			<div class="btn-group" data-toggle="buttons-radio" style="width:${paramDef.dataList?size * 40}px">
				<input type="hidden"  id="${paramDef.id}" name="${paramDef.id}" value="${paramValue}" />
				<#list paramDef.dataList as row>
					<#assign valueInterpret = row[paramDef.valueExpr]>
					<#assign labelInterpret = row[paramDef.labelExpr]>
				   <button type="button" class="btn btn-primary  <#if paramValue?string == valueInterpret?string >active</#if>" name="${paramDef.id}" id="radio_${paramDef.id}" value="${valueInterpret}" onclick="$('#${paramDef.id}').val(this.value); this.form.submit();">${labelInterpret}</button>
				</#list>	
			</div>									
		<#elseif paramDef.displayType == 'radio'>
				<#list paramDef.dataList as row>
					<#assign valueInterpret = row[paramDef.valueExpr]>
					<#assign labelInterpret = row[paramDef.labelExpr]>
					<input name="${paramDef.id}" type="${paramDef.displayType}" <#if paramValue?string == valueInterpret?string >checked='checked'</#if> value="${valueInterpret}" onclick="this.form.submit();">${labelInterpret}</input>
				</#list>
		<#elseif paramDef.displayType == 'checkbox'>
				<#list paramDef.dataList as row>
					<#assign valueInterpret = row[paramDef.valueExpr]>
					<#assign labelInterpret = row[paramDef.labelExpr]>
					<#assign checked>
						<#compress>
						<#if paramValue?is_string>
							<#if paramValue?string == valueInterpret?string>checked='checked'</#if>
						<#else> 
							<#if paramValue?seq_contains(valueInterpret)>checked='checked'</#if> 
						</#if>
						</#compress>
					</#assign>
					<input name="${paramDef.id}" type="${paramDef.displayType}" value="${valueInterpret}" ${checked}  >${labelInterpret}</input>
				</#list>
		<#elseif paramDef.displayType == 'date'>
				<input id="${paramDef.id}" name="${paramDef.id}" type="text" value="${paramValue}" title="${paramDef.help!}" placeholder="${paramDef.label}" onfocus="WdatePicker({isShowWeek:true})" onchange="this.form.submit();">
				<!--
				<img onclick="WdatePicker({el:'${paramDef.id}'})" src="${ctx}/js/My97DatePicker/skin/datePicker.gif" width="16" height="22" align="absmiddle">
				-->
		<#else>
				<input name="${paramDef.id}" type="${paramDef.displayType}" value="${paramValue}" title="${paramDef.help!}" placeholder="${paramDef.label}" onchange="this.form.submit();" <#if paramDef.readonly>readonly="readonly"</#if>>
		</#if>
		
		</div>	
		
     </#if>		
</#macro>

<#macro renderChart chart>
		<div class="box-content rp-chart" id="${chart.id}" style="width:96%;">
		</div>
		<script type="text/javascript">
			$(function () {
				var jsonStr = ${report.getElementById('${chart.id}').toJson()};
				highchartsUtil.chart("${chart.id}",jsonStr); 
		    });
		</script> 
</#macro>

<#macro renderTable table>
	<table id="${table.id}" class="table table-striped table-bordered table-hover table-condensed  scrolltable sortable">
		<thead>
			<tr>
			<#list table.columns as col>
				<th>${col.label}</th>
			</#list>
			</tr>
		</thead>
		
		<tbody>
			<#list table.dataList as row>
				<tr>
			<#list table.columns as col>
					<td class="center"><#assign rowValue = col.value?interpret><@rowValue /></td>
			</#list>
				</tr>
			</#list>
		</tbody>
	</table>
	
	<#if table.pageable>
		<@renderPagination table.paginator/>
	</#if>
</#macro>

<#macro renderPagination paginator> 
	<div class="pagination pagination-centered">
		<ul>
			<#if !paginator.firstPage>
			<li><a href="${ctx}${request.requestURI}?page=1&${request.queryString?replace("page=","")}">First</a></li>
			</#if>
			<#if paginator.hasPrePage>
			<li><a href="${ctx}${request.requestURI}?page=${paginator.prePage}&${request.queryString?replace("page=","")}">Prev</a></li>
			</#if>
			
			<#list paginator.slider(5) as pageNum>
			<li <#if paginator.page == pageNum>class="active"</#if> ><a href="${ctx}${request.requestURI}?page=${pageNum}&${request.queryString?replace("page=","")}">${pageNum}</a></li>
			</#list>
			<#if paginator.hasNextPage>
			<li><a href="${ctx}${request.requestURI}?page=${paginator.nextPage}&${request.queryString?replace("page=","")}">Next</a></li>
			</#if>
			<#if !paginator.lastPage>
			<li><a href="${ctx}${request.requestURI}?page=${paginator.totalPages}&${request.queryString?replace("page=","")}">Last</a></li>
			</#if>
		</ul>
	</div>
</#macro>


<#macro renderObject id>
	<#assign obj = report.getElementById("${id}")/>
	<#if obj.class.simpleName = 'Chart'>
		<@renderChart obj/>
	<#elseif obj.class.simpleName = 'Table'>
		<@renderTable obj/>
	<#else>
		render error,unknow object type for render,id:${id},object class: ${item.class.simpleName},support renderObject is [Chart,Table]
	</#if>
</#macro>

<#macro renderTables tables>
	<#list tables as table>
		<@renderTable report.getElementById("${table}") />
	</#list>
</#macro>

<#macro renderCharts charts>
	<#list charts as chart>
		<@renderChart report.getElementById("${chart}") />
	</#list>
</#macro>

