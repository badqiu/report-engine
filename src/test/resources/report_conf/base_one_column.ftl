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

		<div class="panel panel-default">
			<@renderBoxHeader "icon-picture"/>
			<div class="panel-body">
				<@block name="chart"></@block>
			</div>
		</div>

		<div class="panel panel-default">
			<@renderBoxHeader "icon-list-alt"/>
			<div class="panel-body">
				<@block name="table"></@block>
			</div>
		</div>
		<!--/row-->
		
		<div class="panel panel-default">
			<@renderBoxHeader "icon-book"/>
			<div class="panel-body">
				<@block name="help"></@block>
			</div>
		</div>
		<!--/row-->
	</div>
	</@block>  
</body>
</html>





