<#include "/custom_macro.ftl"/>

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

<#function filter things name value>
    <#local result = []>
    <#list things as thing>
        <#if thing[name] == value>
            <#local result = result + [thing]>
        </#if>
    </#list>
    <#return result>
</#function>

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
					<#if !defaultParamValue??>
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
	<a href="/ReportEngine/monitorArg?reportPath=${reportPath}&metadataId=${table.id}&metadataType=table&isRetainedData=${table.isRetainedData?string('true','false')}" target="_blank" style="background-color: #DDDDDD;background-image: linear-gradient(#DDDDDD, #DDDDDD 5%, #DDDDDD);margin-bottom: 4px;" class="btn btn-round" title="点击设置报表数据监控">我要监控</a>
	<a href="/ReportEngine/subscribeArg?reportPath=${reportPath}&metadataId=${table.id}&metadataType=table_subscribe" target="_blank" style="background-color: #DDDDDD;background-image: linear-gradient(#DDDDDD, #DDDDDD 5%, #DDDDDD);margin-bottom: 4px;" class="btn btn-round" title="点击订阅报表">我要订阅</a>
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

<#macro renderMonitorForTable table>
	<a href="#" class="btn btn-round" title="监控"><i class="icon-bell" onclick="$('#monitor-${table.id}').show(); $('#monitor-${table.id}').modal('show')"></i></a>
	 <div id="monitor-${table.id}" class="div-middle modal hide  fade"  tabindex="-1"  role="dialog"  aria-hidden="true">
		<form class="form-horizontal">
		
		  <fieldset>
    		<legend>监控</legend>
		  <div class="control-group">
			    <label class="control-label" for="report_input">报表</label>
			    <div class="controls">
			      <input type="text" id="report_input" placeholder="Email" value="${reportPath}">
			    </div>
		  </div>
		  
		  <div class="control-group">
			    <label class="control-label" for="inputPassword">表格</label>
			    <div class="controls">
			      <input type="password" id="inputPassword" placeholder="${table.id}">
			    </div>
		  </div>
		  
		  <div class="control-group">
			  	<label class="control-label" for="inputPassword">指标列</label>
			    <div class="controls">
			        <#list table.columns as col>
			 			<label class="checkbox" for="checkbox_${col.label}"><input type="checkbox" value="${col.label}" id="checkbox_${col.label}" /> ${col.label}</label>
					</#list>
			    </div>
		  </div>
		  
		  <div class="control-group">
		  	<div class="controls">
		  	<button type="submit" class="btn">提交</button>
		  	</div>
		  </div>
		  
		  </fieldset>
		</form>
		
	 </div>
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


<#macro renderCrosstab cube >
	<#assign maxLevel = cube.dimensionSize - 1>
	<table class="table table-striped table-bordered table-hover table-condensed">
	<thead>
	<#list 0..maxLevel as i>
		<tr>
		<#list cube.getDimListMapByLevel(i) as row>
			<#list row?keys as key>
				<th colspan="${cube.getDimAllChildsSize(row[key],maxLevel-i)}">${key?string}</th>
			</#list>
		</#list>
		</tr>
	</#list>
	
		<tr>
			<#list cube.getDimListMapByLevel(maxLevel+1) as row>
				<#list row?keys as key>
					<th>${key}</th>
				</#list>
			</#list>
		</tr>
	</thead>
	
	
	<tbody>	
	<tr>
		<#list cube.getDimListMapByLevel(maxLevel+1) as row>
			<#list row?keys as key>
				<td>${row[key]?string}</td>
			</#list>
		</#list>
	</tr>
	</tbody>	
	</table>
</#macro>


<#macro renderCrosstab cube >
	<#assign maxLevel = cube.dimensionSize - 1>
	<table class="table table-striped table-bordered table-hover table-condensed">
	<#list 0..maxLevel as i>
		<tr>
		<#list cube.getDimListMapByLevel(i) as row>
			<#list row?keys as key>
				<th colspan="${cube.getDimAllChildsSize(row[key],maxLevel-i)}">${key?string}</th>
			</#list>
		</#list>
		</tr>
	</#list>
	
		<tr>
			<#list cube.getDimListMapByLevel(maxLevel+1) as row>
				<#list row?keys as key>
					<th>${key}</th>
				</#list>
			</#list>
		</tr>
	
	
	<tr>
		<#list cube.getDimListMapByLevel(maxLevel+1) as row>
			<#list row?keys as key>
				<td>${row[key]?string}</td>
			</#list>
		</#list>
	</tr>
	</table>
</#macro>

<#macro renderCrosstab2 cube factKey>
	<#assign cubeMap = cube.cube>
	<table class="table table-striped table-bordered table-hover table-condensed">
		<tr>
			<th>日期\游戏</th>
	<#list cubeMap?keys as dim1Key>
				<th>${dim1Key}</th>
	</#list>
		</tr>

		<tr>
	<#list cubeMap?keys as dim1key>
		<#list cubeMap[dim1key]?keys as dim2key> 
		<td>${dim2key}</td>
		<!--
		<#list cubeMap[dim1key][dim2key]?keys as fact2key> 
				<td>${cubeMap[dim1key][dim2key][fact2key]?string}</td>
		</#list>
		-->
		<td>${cubeMap[dim1key][dim2key][factKey]?string}</td>
		</#list>
	</#list>
		</tr>
	
	</table>
</#macro>




<#macro renderMonitorTable table> 
	<div class="input-prepend">
		<span class="add-on">数据值列:</span>
		<select name="valueTitle" id="valueTitle" onchange="changeCheckedKpi();">
			<#list table.columns as col>
				<#if !col.isKpi?? || col.isKpi> 
		          <option value ="${col.label}">${col.label}</option>
		        </#if>
			</#list>
		</select>
		<a id="tip0" data-placement="bottom" class="btn btn-mini btn-round" style="border-radius: 40px;" data-content=" <font color=#D2691E><strong>数据值列:</strong></font>所监控报表的指标列<br> <font color=#D2691E><strong>监控所有维度:</strong></font>自动监控展现的所有维度及后续增加的维度<br> <font color=#D2691E><strong>维度值:</strong></font>报表中除指标列之外的其他列，与进入此页面之前报表显示的数据相同<br> <font color=#D2691E><strong>维度值中的日期:</strong></font>与查询时间及当前时间有关，监控时数据日期会随着监控时间顺移，并非固定不变<br> " data-original-title="说明" rel="popover" href="javascript:void(0);">
			<i class="icon-question-sign"></i>
		</a>
		<script type="text/javascript">
		  $('#tip0').popover();
		</script>
		<span style="float: right !important;margin-right:30px;">
	    <input class="btn btn-primary" style="border-radius: 3px 3px 3px 3px;" type="button" value="提交" onclick="submitForm('${table.id}')"/>&nbsp;&nbsp;
	    <input class="btn btn-primary" style="border-radius: 3px 3px 3px 3px;" type="button" name="back" value="关闭" onclick="javascript:window.opener=null;window.open('','_self');window.close();"/>
	    &nbsp;&nbsp;
	    </span>
    </div>
    <div class="input-prepend">
    	<span class="add-on">监控所有维度:</span>&nbsp;&nbsp;<input id="autoAppend" name="autoAppend" type="checkbox" value="1"/><span style="background:transparent;height:10px;font-size:80%;font-weight:500;color:rgb(128,128,128);">&nbsp;&nbsp;（勾选后监控所有维度,包括新增维度）</span>
    </div>
	<#list table.columns as col>
		<#if col.isKpi?? && !col.isKpi>
	          <input type="hidden" name="keyTitles" value="${col.label}"/>
		</#if>
    </#list>
    <#if startDate??>
    	<input type="hidden" id="startDate" name="startDate" value="${startDate}"/>
	</#if>
	 <#if endDate??>
	    <input type="hidden" id="endDate" name="endDate" value="${endDate}"/>
	</#if>
    <input type="hidden" id="reportPath" name="reportPath" value="${reportUrl}"/>
    <input type="hidden" id="metadataId"  name="metadataId" value="${table.id}"/>
    <input type="hidden" id="system"  name="system" value="${system}"/>
    <input type="hidden" id="queryParams" name="queryParams" value="${queryParams}"/>
    <input type="hidden" id="monitorReportPassport"  name="monitorReportPassport" value="${monitor_report_passport}"/>
    <div class="input-prepend">
    	<span class="add-on">&nbsp;&nbsp;&nbsp;维度值:</span><span style="background:transparent;height:10px;font-size:80%;font-weight:500;color:rgb(128,128,128);">（注:请点击上面<i class="icon-question-sign"></i>图标查看说明）</span>
    </div>
    </br>
	<table id="${table.id}" class="table table-striped table-bordered table-hover table-condensed  scrolltable ">
		<thead>
			<tr>
			<th>勾选监控  <a id="checkAllBoxTag" href="javascript:void(0);" onclick="checkAllBox('${table.id}')">全选</a>/<a id="uncheckAllBoxTag" href="javascript:void(0);" onclick="uncheckAllBox('${table.id}')">反选</a></th>
			<#list table.columns as col>
				<#if col.isKpi?? && !col.isKpi>
			          <th>${col.label}</th>
				</#if>
            </#list>
            <th>选中的指标</th>
			</tr>
		</thead>
		
		<tbody>
			<#list table.dataList as row>
				<tr>
				<td class="center" style="width: 130px;"><input type="checkbox" name="key" ></td>
				<#list table.columns as col>
					<#if col.isKpi?? && !col.isKpi>
				          <td class="center"><#assign rowValue = col.value?interpret><@rowValue /></td>
					</#if>
            	</#list>
            	<td class="center" name="checkedKpi"></td>
				</tr>
			</#list>
		</tbody>
	</table>
</#macro>

<#macro renderMonitorTableXml table>
<table id="${table.id}" >
	<#list table.dataList as row>
		<row>
			<#list table.columns as col>
			<rowData isKpi="<#if col.isKpi?? && !col.isKpi>false<#else>true</#if>" >
				<label><![CDATA[${col.label}]]></label>
				<value><#assign rowValue = col.value?interpret> <![CDATA[<@rowValue />]]></value>
			</rowData>
			</#list>
		</row>
	</#list>
</table>
</#macro>

<#macro renderTableSubscribe table>
	<!-- 报表订阅  -->
	<#if startDate??>
		<input type="hidden" id="startDate" name="startDate" value="${startDate}"/>
	</#if>
	 <#if endDate??>
	    <input type="hidden" id="endDate" name="endDate" value="${endDate}"/>
	</#if>
	<input type="hidden" id="reportPath" name="reportPath" value="${reportUrl}"/>
	<input type="hidden" id="metadataId"  name="metadataId" value="${table.id}"/>
	<input type="hidden" id="queryParams" name="queryParams" value="${queryParams}"/>
	<input type="hidden" id="monitorReportPassport"  name="monitorReportPassport" value="${monitor_report_passport}"/>
</#macro>

<#macro renderTableSubscribeHtml table> 
<table id='${table.id}' class='table'>
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
				<td class='center'><#assign rowValue = col.value?interpret><@rowValue /></td>
		</#list>
			</tr>
		</#list>
	</tbody>
</table>
</#macro>