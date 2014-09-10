<#include "/macro.ftl"/>

<@override name="table">
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

