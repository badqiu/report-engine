Object.getProperty = function(o, s) {
    s = s.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
    s = s.replace(/^\./, '');           // strip a leading dot
    var a = s.split('.');
    while (a.length) {
        var n = a.shift();
        if (n in o) {
            o = o[n];
        } else {
            return;
        }
    }
    return o;
}


var highchartsUtil = {
		"chart" : function(containerId, obj) {
			var chart;
			this.containerId = containerId;
			var width = obj['width'];
			var height = obj['height'];
			var title = obj['title'];
			var xTitle = obj['xTitle'];
			var yTitle = obj['yTitle'];
			var chartType = obj['chartType'];
			var x = obj['x'];
			var dataList = obj['dataList'];

			// 从dataList里面取得x轴的值xfieldValueList
			// var xfieldValueList = ["2013-8-1","2013-8-2","2013-8-3"];
			var xfieldValueList = obj['xValues'];

			var xAxisTickInterval = Math.ceil(xfieldValueList.length / 10);
			/**
			 * 饼型图
			 */
			if (chartType == "pie") {
				chart = new Highcharts.Chart({
					chart : {
						renderTo : containerId,
						width : width,
						hight : height,
						type : chartType
					},
					/*
					lang:{ 
						exportButtonTitle: '导出',
						printButtonTitle: '打印',
						downloadJPEG:"下载JPEG 图片",
						downloadPDF: "下载PDF文档",
						downloadPNG: "下载PNG 图片",
						downloadSVG: "下载SVG 矢量图"
					},
					*/
					exporting:{ 
                     enabled:false, //用来设置是否显示‘打印’,'导出'等功能按钮，不设置时默认为显示 
                    },
					credits : {
						enabled : false
					},
					title : {
						text : title
					},
					xAxis : {
						categories : xfieldValueList,
						labels : {
							align : 'center',
							formatter : function() {
								return this.value;
							},
							rotation : 10,
							staggerLines : 1
						},
						tickInterval : xAxisTickInterval,
						title : {
							text : xTitle
						}
					},
					yAxis : {
						labels : {
							align : 'right',
							formatter : function() {
								return this.value;
							}
						},
					//	tickInterval : 3,
						title : {
							text : yTitle
						}
					},
					//图注属性
					legend: {
			                layout: 'vertical',
			                align: 'right',
			                verticalAlign: 'middle',
			                borderWidth: 0
			            },
					tooltip: {
						formatter: function() {
				            return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.percentage, 1) +'%';
				         },
				         useHTML:true
		            },
					plotOptions : {
						dataLabels: {
	                        enabled: true,
	                        color: '#000000',
	                        connectorColor: '#000000',
	                        formatter: function() {
	                            return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
	                        }
	                    },
	                    pie:{
	                        // 是否允许扇区点击
	                        allowPointSelect: true,
	                        // 点击后，滑开的距离
	                        slicedOffset: 5,
	                        // 饼图的中心坐标
	                        // 饼图的大小
	                        // 数据标签
	                        dataLabels: {
	                            // 是否允许标签
	                            enabled: true,
	                            // 标签与图像元素之间的间距
	                            distance: 10
	                        },
	                        // 是否忽略隐藏的项
	                        ignoreHiddenPoint: true,
	                       
	                        // 是否在图注中显示。
	                        showInLegend: true,
	                        // 调整图像顺序关系
	                        zIndex: 0
	                    }
	                },    
					series : []       
				});
			}else{
				chart = new Highcharts.Chart({
					chart : {
						renderTo : containerId,
						width : width,
						hight : height,
						type : chartType
					},
					/**
					lang:{ 
						exportButtonTitle: '导出',
						printButtonTitle: '打印',
						downloadJPEG:"下载JPEG 图片",
						downloadPDF: "下载PDF文档",
						downloadPNG: "下载PNG 图片",
						downloadSVG: "下载SVG 矢量图"
					},*/
					exporting:{ 
                     enabled:false, //用来设置是否显示‘打印’,'导出'等功能按钮，不设置时默认为显示 
                    },
					credits : {
						enabled : false
					},
					title : {
						text : title
					},
					xAxis : {
						categories : xfieldValueList,
						labels : {
							align : 'left',
							formatter : function() {
								return this.value;
							},
							staggerLines : 1
						},
						tickInterval : xAxisTickInterval,
						title : {
							text : xTitle
						}
					},
					yAxis : {
						labels : {
							align : 'right',
							formatter : function() {
								return this.value;
							}
						},
						//tickInterval : 30,
						title : {
							text : yTitle
						}
					},
					//图注属性
					legend: {
			                layout: 'vertical',
			                align: 'right',
			                verticalAlign: 'middle',
			                borderWidth: 0
			            },
					plotOptions : {
	
						series : {
							cursor : 'pointer',
							pointWidth : 20,
							pointPadding: 0.2
						},
					},
					series : []
				});
			}
			//给图表赋值	
			highchartsUtil.setData(chart,obj);
		},

		"setData" : function(chart,data) {
			
			var dataList = data['dataList'];
			var sers = data['sers'];
			var x = data['x'];
			var chartType = data['chartType'];
			
			if (chartType == "pie"){
				var pieData = new Array();
				for(var i=0;i<dataList.length;i++){
					var row = dataList[i];
					var valueKey = sers[0].y;
					var keyValue = [row[x],row[valueKey]];
					pieData.push(keyValue);
				}
				chart.addSeries({data:pieData});
			}else{
				for(var i = 0;i<sers.length;i++){
					var b = { name: sers[i].title, data: sers[i].values };
					chart.addSeries(b);
				}
			}
		
		}
		
}



