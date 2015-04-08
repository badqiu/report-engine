var monitorUrl = "http://statmonitor.game.yy.com/monitor/report/setReportMonitorTaskArg.do";
var subscribeUrl = "http://statmonitor.game.yy.com/monitor/subscribe/setReportSubscribekArg.do";

function checkAllBox(tableid){
	$("#" + tableid + " tr").each(function () {
		$(this).children("td").eq(0).find("input:checkbox").prop("checked",true);
	});
}
	
function uncheckAllBox(tableid){
	$("#" + tableid + " tr").each(function () {
		if($(this).children("td").eq(0).find("input:checkbox").prop("checked")==true){
			$(this).children("td").eq(0).find("input:checkbox").prop("checked",false)
		}else{
			$(this).children("td").eq(0).find("input:checkbox").prop("checked",true)
		}
	});
}
	
function isCheckedTable(tableid){
	var isChecked = false;
	$("#" + tableid + " tr").each(function () {
		if($(this).children("td").eq(0).find("input:checkbox").prop("checked") == true){
			isChecked = true;
		}
	});
	return isChecked;
}
	
function getCheckedInfo(tableid){
	var monitorString = "";
	var lastString = "";
	if(isCheckedTable(tableid)){
		monitorString = "[";
		$("#" + tableid + " tr").each(function (){
			if($(this).children("td").eq(0).find("input:checkbox").prop("checked") == true){
				var col = $(this).children("td");
				monitorString += "{\"id\":\"" + $(this).index() + "\",\"value\":\"";
				var valueStr = "";
				for(var i=1; i<col.length-1; i++){
					var value = $(this).children("td").eq(i).text();
					valueStr += "#" + formatStr(value);
				}
				if(valueStr.length>0){
					valueStr = valueStr.substr(1);
				}
				monitorString += valueStr + "\"},";
			}
		});
		lastString = monitorString.substring(0,monitorString.length-1);
		lastString += "]";
	}
	return lastString;
}
/**
 * 如果为a标签，则获取其text
 * @param str
 */
function formatStr(str){
	if(!isNull(str)){
		if(str.length>1&&str.indexOf("<a")==0){
			var reg =/<a\s(?:\s*\w*?\s*=\s*".+?")*(?:\s*href\s*=\s*".+?")(?:\s*\w*?\s*=\s*".+?")*\s*>([\s\S]*?)<\/a>/; 
			str = str.replace(reg,'$1');
		}
		//替换<br/>为空格
		str = str.replace("<br/>", " ");
		//多个空格替换成一个空格
		str = str.replace(/\s{2,}/g, " ");
		//删除前后空格
		str = str.replace(/(^\s*)|(\s*$)/g, "");
	}
	return str;
}
//报表监控form表单提交
function submitForm(tableid){
	var temp = document.createElement("form"); 
	temp.action = monitorUrl; 
	temp.method = "post";
	temp.target="_blank";
	temp.style.display = "none";
	temp.appendChild(createInputById("reportPath"));
	temp.appendChild(createInputById("metadataId"));
	temp.appendChild(createInputById("metadataTitle"));
	temp.appendChild(createInputById("system"));
	temp.appendChild(createInputById("startDate"));
	temp.appendChild(createInputById("endDate"));
	temp.appendChild(createInputById("queryParams"));
	if(document.getElementById("autoAppend").checked){
		temp.appendChild(createInputById("autoAppend"));
	}
	var dateInput = createInputById("dateTitle");
	temp.appendChild(dateInput);
	
	var passportInput = createInputById("monitorReportPassport");
	if(isNull(passportInput.value)){
		alert("用户通行证不能为空！");
		return;
	}else{
		if((passportInput.value).toLowerCase().substring(0,3)!='dw_'){
			alert("通行证必须以dw_开头！");
			passportInput.focus();
			return;
		}
		if(passportInput.value.length<=3){
			alert(passportInput.value+" 为非常规通行证！");
			passportInput.focus();
			return;
		}
	}
	temp.appendChild(passportInput);
	
	var valueTitleObj = document.getElementById("valueTitle");
	var valueTitleValue = valueTitleObj.options[valueTitleObj.options.selectedIndex].value;
	if(isNull(valueTitleValue)){
		alert("数据值列为空，无法提交监控！");
		return;
	}
	temp.appendChild(createInputByValue("valueTitle",formatStr(valueTitleValue)));
	
	var keyTitlesArr=document.getElementsByName("keyTitles");
	if(!isCheckedTable(tableid)){
		alert("未选择需要监控的维度值,请先选择！");
		return;
	}
	var keyTitles="";
	for(var i = 0; i < keyTitlesArr.length; i++){
		keyTitles += "#"+formatStr(keyTitlesArr[i].value);
	}
	if(keyTitles.length>1){
		keyTitles = keyTitles.substr(1);
	}
	temp.appendChild(createInputByValue("keyTitles",keyTitles));
	
	var kpiKeysJson = getCheckedInfo(tableid);
	temp.appendChild(createInputByValue("kpiKeys",kpiKeysJson));
	
	document.body.appendChild(temp);        
	temp.submit(); 
	document.body.removeChild(temp)
}

function createInputById(id){
	var myInput = document.createElement("input");   
	myInput.setAttribute("name", id) ;
	var obj = document.getElementById(id);
	if(obj!=null){
		myInput.setAttribute("value", document.getElementById(id).value);
	}
	return myInput;
}

function createInputByValue(name,value){
	var myInput = document.createElement("input");   
	myInput.setAttribute("name", name) ;   
	myInput.setAttribute("value", value);
	return myInput;
}

function isNull(str) {
    if(str==null||str.length==0) {
        return true;
    }else {
        return false;
    }
}

/**报表订阅表单提交*/
function submitSubscribeForm(tableid){
	var temp = document.createElement("form"); 
	temp.action = subscribeUrl; 
	temp.method = "post";
	temp.style.display = "none";
	temp.appendChild(createInputById("reportPath"));
	temp.appendChild(createInputById("metadataId"));
	temp.appendChild(createInputById("startDate"));
	temp.appendChild(createInputById("endDate"));
	temp.appendChild(createInputById("queryParams"));
	
	var passportInput = createInputById("monitorReportPassport");
	if(isNull(passportInput.value)){
		alert("用户通行证不能为空！");
		return;
	}else{
		if((passportInput.value).toLowerCase().substring(0,3)!='dw_'){
			alert("通行证必须以dw_开头！");
			passportInput.focus();
			return;
		}
		if(passportInput.value.length<=3){
			alert(passportInput.value+" 为非常规通行证！");
			passportInput.focus();
			return;
		}
	}
	temp.appendChild(passportInput);
	
	document.body.appendChild(temp);        
	temp.submit(); 
	document.body.removeChild(temp)
}

	