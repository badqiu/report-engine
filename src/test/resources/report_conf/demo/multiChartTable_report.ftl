<#include "/macro.ftl"/>

<@override name="leftChart">
	<@renderChart report.getElementById("lineChart")/>
</@override>

<@override name="rightChart">
	<@renderChart report.getElementById("pieChart")/>
</@override>

<@override name="chart">
	<@renderChart report.getElementById("barChart")/>
</@override>

<@override name="leftTable">
	<@renderTable report.getElementById("leftTable")/>
</@override>

<@override name="rightTable">
	<@renderTable report.getElementById("rightTable")/>
</@override>

<@override name="table">
	<@renderTable report.getElementById("table1")/>
</@override>

<@override name="help">
	${report.help}
</@override>

<@extends name="/base_two_column.ftl"/>

