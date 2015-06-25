<#include "/macro.ftl"/>

<@override name="table">
	<table class="table table-bordered " style="text-align: center;">
		<tr>
			<td>
				<b class="title">当前在线</b><br/>
				<b class="count">100</b>
			</td>
			<td>
				<b class="title">最高在线</b><br/>
				<b class="count">100</b>
			</td>
			<td>
				<b class="title">活跃帐号</b><br/>
				<b class="count">100</b>
			</td>
			<td>
				<b class="title">滚服新增帐号</b><br/>
				<b class="count">100</b>
			</td>
		</tr>
		<tr>
			<td>
				<b class="title">付费帐号</b><br/>
				<b class="count">100</b>
			</td>
			<td>
				<b class="title">付费金额</b><br/>
				<b class="count">100</b>
			</td>
			<td>
				<b class="title">付费率</b><br/>
				<b class="count">100</b>
			</td>
			<td>
				<b class="title">ARPPU</b><br/>
				<b class="count">100</b>
			</td>
		</tr>
	</table>
			
	<@renderTable report.getElementById("table1")/>
	<@renderTable report.getElementById("userTable")/>
	<@renderTable report.getElementById("requeryBlogTable")/>
</@override>


<@override name="help">
	<@renderCrosstab2 report.getElementById("cubeTdateGame") "行数" />
	<@renderCrosstab report.getElementById("cubeTdateGame")  />
	<@renderCrosstab report.getElementById("cubeBlog") />
	${report.help}
</@override>



<#include "/base_one_column.ftl"/>

