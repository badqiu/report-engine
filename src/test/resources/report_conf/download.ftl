<#macro renderCsv table>
	<#compress>
	<#list table.columns as col>${col.label}<#if col_has_next>,</#if></#list>
	<#if table.query??>
		<#local sum = table.query.autoSumResult! /> 
	<#else>
		<#local sum = context[table.refDataList + 'Sum']! /> 
	</#if>
	<#list table.dataList as row>
		<#list table.columns as col><#assign rowValue = col.value?interpret><@rowValue /><#if col_has_next>,</#if></#list>
	</#list>
	</#compress>
</#macro>
<@renderCsv report.elements[table]/>