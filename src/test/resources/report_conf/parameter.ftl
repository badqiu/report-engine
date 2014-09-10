<#include '/macro.ftl'/>
<script src="${ctx}/js/jquery.min.js"></script>
<input type="hidden" id="ctx" value="">
<form id="queryForm" action="${ctx}/ReportEngine/frameset">
	<input id="reportPath" name="reportPath" value="${reportPath}" type="hidden"/>
	<input id="ctx" value="${ctx}" type="hidden"/>
	<@renderParams report.params/>
</form>
