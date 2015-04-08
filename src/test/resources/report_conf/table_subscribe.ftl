<#include "/macro.ftl"/>

<!DOCTYPE html>
<html lang="en">
<@renderHtmlHead/>

<body>
	<@block name="body">
	<div class="row-fluid sortable">
		<!-- 切换皮肤需求引用 -->
		<input type="hidden" id="ctx" value="">	

		<div class="box rp-table">
			<div class="box-header well">
		</div>
			<div class="box-content">
				<@block name="table"><@renderTableSubscribe report.getElementById("${metadataId}")/></@block>
			</div>
		</div>
		<!--/row-->
		
		<!--/row-->
	</div>
	<script type="text/javascript">
		$(function () {
			submitSubscribeForm();
		});
	</script>
	</@block> 
</body>
</html>


