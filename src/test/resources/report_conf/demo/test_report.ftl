<#include "/macro.ftl"/>
<@override name="body">
	<div class="row-fluid sortable">
		<!-- 切换皮肤需求引用 -->
		<input type="hidden" id="ctx" value="">	

		
		<div class="box rp-table">
			<@renderBoxHeader "多个表格" "icon-list-alt"/>
			<div class="box-content">
				<@renderTableList ['userTable','requeryBlogTable','table1'] />
			</div>
		</div>
		
		<div class="box rp-chart">
			<@renderBoxHeader "多个图表" "icon-list-alt"/>
			<div class="box-content">
				<@renderChartList ['barChart','pieChart','lineChartBreakByDate'] />
			</div>
		</div>
		
		<div class="box rp-chart">
			<@renderBoxHeader "图表-表格" "icon-list-alt"/>
			<div class="box-content">
				<@renderChartTable 'dynamicLineChart','requeryBlogTable' />
			</div>
		</div>
	</div>

</@override>

<#include "/base_report.ftl" />

