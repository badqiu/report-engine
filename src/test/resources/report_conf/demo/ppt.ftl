<#include "/macro.ftl"/>

<@renderParams report.params/>

<@override name="body">
	<@renderChart report.getElementById("userChart")/>
	<h1>111111111111111111</h1>
	<@renderTable report.getElementById("userTable")/>
</@override>

<#include "/base_one_column.ftl"/>

