/**
 * @author xinwuyi <xinwuyi@gmail.com>
 * @version 1.0.0
 * 
 * 使用示例:
 * $(document).ready(function () {
 *		setTimeout(function (){
 *			tableSmartFloat("tableId",1);
 *		},1000);
 * });
 * 
 * @param tableId
 * @param colNum 固定列数，如果为0代表不固定列数
 * @param colsArr 可选参数，指定的固定列对象，td,th的数组，如果指定了此参数colNum将不作用
 */
function tableSmartFloat(tableId,colNum,colsArr){
	//设置指定td,th实现列固定
	var smartFloatLeft=function(colsObj) { 
        $(window).scroll(function() { //侦听滚动时 
            var scrolls = $(this).scrollLeft(); 
            if (scrolls > 0) { //如果滚动到页面超出了当前元素element的相对页面顶部的高度 
            	var bgcolor="#FFFFFF";//设置为白色
                if (window.XMLHttpRequest) { //如果不是ie6 
                   	colsObj.css({ //设置css 
                           position: "relative", //固定定位,即不再跟随滚动 
                           "background-color":bgcolor,
                           "border-right":"1px solid #dddddd",
                           "z-index":90,
                           left: scrolls //距离页面顶部为0
                       });
                } else { //如果是ie6 
                   colsObj.css({ //设置css 
                           position: "relative", //固定定位,即不再跟随滚动 
                           "background-color":bgcolor,
                           "border-right":"1px solid #dddddd",
                           "z-index":90,
                           left: scrolls //距离页面顶部为0
                       });
                } 
            }else {
            	colsObj.css({ //设置css 
            		 left: 0,
            		 "border-bottom":"0px solid #dddddd",
                     "background-color":""
                });
            } 
        }); 
    }; 
    //实现表头固定
    var smartFloatTop=function(table) {
    	var element=table.children("thead");
    	if(element.length==0){
    		return ;
    	}
        var pos = element.css("position"); //当前元素距离页面document顶部的距离
        //获得框架在父窗口的偏移量，如果有多个嵌套，这个需要累加
        var offsetObj=getTopWindowOffsetTop(window);
        var iframeOffsetTop=offsetObj.top;
        $(window.top).scroll(function() { //侦听滚动时 
            var scrolls = $(this).scrollTop(); 
            var top = table.offset().top; //当前元素对象element距离浏览器上边缘的距离 
             //console.log(table.attr("id")+",top:"+top);
            if (scrolls > top&&top>0) { //如果滚动到页面超出了当前元素element的相对页面顶部的高度 
            	if(element.css("position")==pos){ //第一次要设置宽度，否则会宽度会为0
            		element.css({width:element.css("width"),height:element.css("height")});
			        var whArr=new Array(element.children().children().length);
			        var i=0;
			        var start=new Date().getTime();
			        //先获得每列的宽度，用于设置后面的子列
			        element.children().children().each(function(index,e){
			        	var obj = $(e);
			        	var wh={width:parseInt(obj.css("width")),height:parseInt(obj.css("height"))};
			        	whArr[i]=wh;
			        	obj.css({width:wh.width});
			        	i++;
				        });
				     var trs=table.find("tr");
				     trs.each(function(index,e){
				        	var j=0;
				        	var tr = $(e);
				        	tr.children().each(function(index,e){
				        	var obj = $(e);
				        	var wh=whArr[j];
				        	obj.css({width:wh.width});
				        	j++;
				        });
			        });
			        //console.log("css time:"+(new Date().getTime()-start));
            	}
                if (window.XMLHttpRequest) { //如果不是ie6 
                    element.css({ //设置css 
                        position: "absolute", //固定定位,即不再跟随滚动 
                        "background-color":"#FFFFFF",
                        "border-bottom":"1px solid #dddddd",
                        "z-index":100,
                        top: scrolls-top-iframeOffsetTop //距离页面顶部为0 
                    })
                } else { //如果是ie6 
                    element.css({ 
                        position: "absolute", //固定定位,即不再跟随滚动 
                        "background-color":"#FFFFFF",
                        "border-bottom":"1px solid #dddddd",
                        "z-index":100,
                        top: scrolls
                    });     
                } 
            }else { 
                element.css({ //如果当前元素element未滚动到浏览器上边缘，则使用默认样式 
                    position: pos, 
                    top: top,
                    "border-bottom":"0px solid #dddddd",
                    "background-color":""
                })//.removeClass("shadow");//移除阴影样式.shadow 
            } 
        }); 
    }; 
    //获得偏移量，主要用于带框架的系统
    var getTopWindowOffsetTop=function (win){
		var xtop=0;
		var xleft=0;
		if(win.frameElement){
			xtop+=$(win.frameElement).offset().top;
			xleft+=$(win.frameElement).offset().left;
		}else{
			return {top:xtop,left:xleft};
		}
		if(win.parent){
			tl=getTopWindowOffsetTop(win.parent);
			xtop+=tl.top;
			xleft+=tl.left;
		}
		return {top:xtop,left:xleft};
	};
	//获得固定的列对象
	var getFixCols=function(table,colNum){
		var trs=table.find("tr");
		var cols=new Array(trs.length*colNum);
		//计算哪些列需要固定
		if(!colNum&&colNum!=0){
			colNum=1;
		}
		if(colNum==0){
			return cols;
		}
		trs.each(function(index,e){
			var tr=$(e);
			var i=0;
			var tds=tr.children();
			for(var i=0;i<tds.length;i++){
				if(i<colNum){
					cols.push(tds[i]);
				}
			}
		});
		return cols;
	}
	
	var init=function(tableId,colNum){
		var table=$("#"+tableId);
		if(table.length==0){
			return ;
		}
		//设置table css, 此处很重要，
		if(table.css("position")!="relative"){
			table.css({position:"relative"});
		}
		//置顶
		smartFloatTop(table);
		var cols=colsArr;
		if(!cols){
			cols=getFixCols(table,colNum);
		}
		//置列
		smartFloatLeft($(cols));
	};
	init(tableId,colNum);
}