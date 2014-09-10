var dashboard = {
		"onLoadIframe" : function(iframe) {
			iframe.height = iframe.contentWindow.document.documentElement.scrollHeight;
		},
		
		/**
		 * 重新加载弹出框的内容
		 */
		"reloadPopframe" : function(targetPos, url, sourcePos) {
			$("#popFrame").attr("src", url);
			//点击确定，将弹出框的内容重新加载进dashboard
			$("#modalSureBtn").attr("onclick", "dashboard.reloadPopToPage('" + targetPos + "', '" + url + "')");
		},
		
		/**
		 * 获取弹出框的查询条件，并将条件加到targetPos内的iframe的src中，重新加载
		 */
		"reloadPopToPage" : function(targetPos, url) {
			var formVals = $(window.frames['popFrame'].document).find('#queryForm').serialize();
			$("#" + targetPos + "Frame").show();
			$("#" + targetPos + "Frame").attr("src", url + "?" + formVals);
		},
	
		/**
		 * 从iframe的url页面中获取sourcePos位置的内容, 插入到该jsp的targetPos位置
		 */
		"insertContentFromIframe" : function(targetPos, iframe, url, sourcePos) {
			var doc = iframe.contentWindow.document;
			//从url获取sourcePos位置的内容
			var content = $(doc).find(sourcePos);
			//将targetPos内的内容清空，并将结果插到入targetPos位置
			var targetDiv = $("#" + targetPos + " > .box-content");
			$(targetDiv).html("");
			var clumnWidthClass = "span" + 12 / content.length;
			for(var i=0; i<content.length; i++) {
				$(content[i]).addClass(clumnWidthClass);
				$(targetDiv).append($(content[i]));		
			}
	    	$(iframe).hide();
			//弹出框显示url的全部内容
			var pop = $("#" + targetPos + " > .box-header > .box-icon > .btn-setting");
			$(pop).attr("onclick", "dashboard.reloadPopframe('" + targetPos + "', '"+ url + "', '" + sourcePos + "')");
		},
		
		/**
		 * 创建一个iframe，加载url的内容
		 */
		"insertContentFromPage" : function(targetPos, url, sourcePos) {
			var iframeName = targetPos + "Frame";
			var iframeStr = "<iframe width='" + $("#" + targetPos).width() + "px' id='" + iframeName + "' src='" + url + 
				"' scrolling='no' name='" + iframeName + 
				"' frameborder='0' height='50' padding='0' margin='0' " + 
				"onload=\"dashboard.insertContentFromIframe('" + targetPos + "', this, '" + url + "', '" + sourcePos + "')\"></iframe>";
			$("body").append(iframeStr);
		},
		
		/**
		 * 1、加载url的图片内容至该 jsp的targetPos位置
		 * 2、url只有单列图片内容
		 */
		"insertPic" : function(targetPos, url) {
			dashboard.insertContentFromPage(targetPos, url, ".rp-pic > .box-content");
		},
		
		/**
		 * 1、加载url的图片内容至该 jsp的targetPos位置
		 * 2、url只有单列表格内容
		 */
		"insertTable" : function(targetPos, url) {
			dashboard.insertContentFromPage(targetPos, url, ".rp-table > .box-content");
		},
		
		/**
		 * 1、加载url的图片内容至该 jsp的targetPos位置
		 * 2、url有多列图片内容
		 */
		"insertMutiPic" : function(targetPos, url) {
			dashboard.insertContentFromPage(targetPos, url, ".rp-pic > .box > .box-content");
		},
		
		/**
		 * 1、加载url的图片内容至该 jsp的targetPos位置
		 * 2、url有多列表格内容
		 */
		"insertMutiTable" : function(targetPos, url) {
			dashboard.insertContentFromPage(targetPos, url, ".rp-table > .box > .box-content");
		}
}