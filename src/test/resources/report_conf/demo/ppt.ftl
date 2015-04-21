<#include "/macro.ftl"/>

<@renderParams report.params/>

<@override name="body">
	<@renderChart report.getElementById("userChart")/>
	<@renderTable report.getElementById("userTable")/>
</@override>

<#include "/base_one_column.ftl"/>

