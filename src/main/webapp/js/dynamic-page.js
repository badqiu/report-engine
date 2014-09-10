$(document).ready(function(){
	var ctx = $("#ctx").val();
	
/** begin change theme/skin **/
	var bsCss = $(window.parent.document).find("#bs-css").attr("href");
	var currentTheme = bsCss.match(/bootstrap-.+\.css$/g)[0].replace("bootstrap-", "").replace(".css", "");
	switchTheme(currentTheme);
	
	$('#themes a[data-value="'+currentTheme+'"]').find('i').addClass('icon-ok');
				 
	$('#themes a').click(function(e) {
		e.preventDefault();
		currentTheme = $(this).attr('data-value');
		switchTheme(currentTheme);
		$('#themes i').removeClass('icon-ok');
		$(this).find('i').addClass('icon-ok');
	});
	
	function switchTheme(themeName) {
		var themePath = ctx + "/css/skin/bootstrap-" + themeName + ".css";
		$('#bs-css').attr('href', themePath);
		if($(window.frames["mainFrame"]).length != 0) {
			$(window.frames["mainFrame"].document).find("#bs-css").attr('href', themePath);
		}
	}
/** end change theme/skin **/
	
	
/** begin 小窗口右上角按钮 **/
	$('.btn-close').click(function(e){
		e.preventDefault();
		$(this).parent().parent().parent().fadeOut();
	});
	
	$('.btn-minimize').click(function(e){
		e.preventDefault();
		var $target = $(this).parent().parent().next('.box-content');
		if($target.is(':visible')) $('i',$(this)).removeClass('icon-chevron-up').addClass('icon-chevron-down');
		else 					   $('i',$(this)).removeClass('icon-chevron-down').addClass('icon-chevron-up');
		$target.slideToggle();
	});
	
	$('.btn-setting').click(function(e){
		e.preventDefault();
		$('#myModal').modal('show');
	});
/** end 小窗口右上角按钮 **/
	
	
/** begin 小窗口拖动特效 **/
	//makes elements soratble, elements that sort need to have id attribute to save the result
	$('.sortable').sortable({
		revert:true,
		cancel:'.btn,.box-content,.nav-header',
		update:function(event,ui){
			//line below gives the ids of elements, you can make ajax call here to save it to the database
			//console.log($(this).sortable('toArray'));
		}
	});
/** end 小窗口拖动特效 **/
	
	
/** begin 左侧菜单栏特效 **/
	$('ul.main-menu li a').each(function(){
		if($($(this))[0].href==String(window.location))
			$(this).parent().addClass('active');
	});
	
	//animating menus on hover
	$('ul.main-menu li:not(.nav-header)').hover(function(){
		$(this).animate({'margin-left':'+=5'},300);
	},
	function(){
		$(this).animate({'margin-left':'-=5'},300);
	});
/** end 左侧菜单栏特效 **/
	
});
