<#macro renderCrosstab cube >
	<#assign maxLevel = cube.dimensionSize - 1>
	<table class="table table-striped table-bordered table-hover table-condensed">
	<thead>
	<#list 0..maxLevel as i>
		<tr>
		<#list cube.getDimListMapByLevel(i) as row>
			<#list row?keys as key>
				<th colspan="${cube.getDimAllChildsSize(row[key],maxLevel-i)}">${key?string}</th>
			</#list>
		</#list>
		</tr>
	</#list>
	
		<tr>
			<#list cube.getDimListMapByLevel(maxLevel+1) as row>
				<#list row?keys as key>
					<th>${key}</th>
				</#list>
			</#list>
		</tr>
	</thead>
	
	
	<tbody>	
	<tr>
		<#list cube.getDimListMapByLevel(maxLevel+1) as row>
			<#list row?keys as key>
				<td>${row[key]?string}</td>
			</#list>
		</#list>
	</tr>
	</tbody>	
	</table>
</#macro>


<#macro renderCrosstab cube >
	<#assign maxLevel = cube.dimensionSize - 1>
	<table class="table table-striped table-bordered table-hover table-condensed">
	<#list 0..maxLevel as i>
		<tr>
		<#list cube.getDimListMapByLevel(i) as row>
			<#list row?keys as key>
				<th colspan="${cube.getDimAllChildsSize(row[key],maxLevel-i)}">${key?string}</th>
			</#list>
		</#list>
		</tr>
	</#list>
	
		<tr>
			<#list cube.getDimListMapByLevel(maxLevel+1) as row>
				<#list row?keys as key>
					<th>${key}</th>
				</#list>
			</#list>
		</tr>
	
	
	<tr>
		<#list cube.getDimListMapByLevel(maxLevel+1) as row>
			<#list row?keys as key>
				<td>${row[key]?string}</td>
			</#list>
		</#list>
	</tr>
	</table>
</#macro>

<#macro renderCrosstab2 cube factKey>
	<#assign cubeMap = cube.cube>
	<table class="table table-striped table-bordered table-hover table-condensed">
		<tr>
			<th>日期\游戏</th>
	<#list cubeMap?keys as dim1Key>
				<th>${dim1Key}</th>
	</#list>
		</tr>

		<tr>
	<#list cubeMap?keys as dim1key>
		<#list cubeMap[dim1key]?keys as dim2key> 
		<td>${dim2key}</td>
		<!--
		<#list cubeMap[dim1key][dim2key]?keys as fact2key> 
				<td>${cubeMap[dim1key][dim2key][fact2key]?string}</td>
		</#list>
		-->
		<td>${cubeMap[dim1key][dim2key][factKey]?string}</td>
		</#list>
	</#list>
		</tr>
	
	</table>
</#macro>