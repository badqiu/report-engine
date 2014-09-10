<#include "/macro.ftl"/>

<!DOCTYPE html>
<html lang="en">

<head>
	<title>${report.tiele!}</title>
</head>

<body>

	<div class="row-fluid sortable">
		<!-- content starts -->

		<@renderNav />
		
		<div class="rp-query">
			<#include "/parameter.ftl"/>
		</div>
		<div class="rp-report">
			<#include "${reportPath}.ftl"/>
		</div>
		
	</div>	
	
	<!--
	<script>
	$(document).ready(function() {
		$("#queryForm").submit();
	});
	</script>
	-->
	
	
	
</body>
</html>













