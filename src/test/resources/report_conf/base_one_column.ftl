<#include "/macro.ftl"/>

<!-- 单列模板，用于其它报表继承 -->


<!DOCTYPE html>
<html lang="en">
<@renderHtmlHead/>

<body>
	<@block name="body">
	<div class="row-fluid sortable">
		<!-- 切换皮肤需求引用 -->
		<input type="hidden" id="ctx" value="">	
		<div class="rp-query">
			<@renderParams report.params/>
		</div>

		<div class="box rp-chart">
			<@renderBoxHeader "icon-picture"/>
			<@block name="chart"></@block>  
		</div>

		<div class="box rp-table">
			<@renderBoxHeader "icon-list-alt"/>
			<div class="box-content">
				<@block name="table"></@block>
			</div>
		</div>
		<!--/row-->
		
		<div class="box rp-help">
			<@renderBoxHeader "icon-book"/>
			<div class="box-content">
				<@block name="help"></@block>
			</div>
		</div>
		<!--/row-->
	</div>
	</@block>  
</body>
</html>





