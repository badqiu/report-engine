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
				<@block name="table"><@renderMonitorTable report.getElementById("${metadataId}")/></@block>
			</div>
		</div>
		<!--/row-->
		
		<!--/row-->
	</div>
	<script type="text/javascript">
		$(function () {
			var checkAllBoxTag = document.getElementById("checkAllBoxTag");
			checkAllBoxTag.click();
			changeCheckedKpi();
		});
		function changeCheckedKpi(){
			var obj = document.getElementById("valueTitle");
			var value = obj.options[obj.selectedIndex].value;
			var kpiArr = document.getElementsByName("checkedKpi");
			for(var i=0;i<kpiArr.length;i++){
				kpiArr[i].innerHTML=value;
			}
		}
	</script>
	</@block> 
</body>
</html>


