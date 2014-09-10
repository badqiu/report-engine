<#include "/macro.ftl"/>

<!DOCTYPE html>
<html lang="en">
<@renderHtmlHead/>

<body>
	<@block name="body">
	<div class="row-fluid sortable">
		<!-- 切换皮肤需求引用 -->
		<input type="hidden" id="ctx" value="">	
		<div class="rp-query">
		</div>
        
        <div class="row-fluid sortable rp-pic">
			<div class="box span6">
				<div class="box-header well" data-original-title>
					<h2>pic1</h2>
					<div class="box-icon">
						<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a> <a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a> <a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
					</div>
				</div>
	            <@block name="leftChart"></@block>  
			</div>

			<div class="box span6">
				<div class="box-header well" data-original-title>
					<h2>pic2</h2>
					<div class="box-icon">
						<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a> <a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a> <a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
					</div>
				</div>
                <@block name="rightChart"></@block>
			</div>
		</div>
		
		<div class="box rp-chart">
			<div class="box-header well">
				<h2>
					<i class="icon-picture"></i>
				</h2>
				<div class="box-icon">
					<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a> <a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a> <a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
				</div>
			</div>
			<@block name="chart"></@block>  
		</div>

		<div class="row-fluid sortable rp-table">
			<div class="box span6">
				<div class="box-header well" data-original-title>
					<h2>table1</h2>
					<div class="box-icon">
						<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a> <a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a> <a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
					</div>
				</div>
				<div class="box-content">
                    <@block name="leftTable"></@block>
				</div>
			</div>
			<!--/span-->

			<div class="box span6">
				<div class="box-header well" data-original-title>
					<h2>table2</h2>
					<div class="box-icon">
						<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a> <a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a> <a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
					</div>
				</div>
				<div class="box-content">
                    <@block name="rightTable"></@block>
				</div>
			</div>
			<!--/span-->
		</div>
		<!--/row-->
		
		<div class="box rp-table">
			<div class="box-header well" data-original-title>
				<h2>
					<i class="icon-list-alt"></i>
				</h2>
				<div class="box-icon">
					<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a> <a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a> <a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
				</div>
			</div>
			<div class="box-content">
				 <@block name="table"></@block>
			</div>
		</div>
		<!--/row-->
		
		<div class="box rp-help">
			<div class="box-header well">
				<h2>
					<i class="icon-book"></i>帮助文档
				</h2>
				<div class="box-icon">
					<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a> <a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a> <a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
				</div>
			</div>
			<div class="box-content">
				<@block name="help"></@block>
			</div>
		</div>
		<!--/row-->
	</div>
	</@block>  
</body>
</html>





