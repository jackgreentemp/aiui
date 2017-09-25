function show(type)
{
    count = 0;
    for (var key in methods) {
        var row = document.getElementById(key);
        if ((methods[key] &  type) != 0) {
            row.style.display = '';
            row.className = (count++ % 2) ? rowColor : altColor;
        }
        else
            row.style.display = 'none';
    }
    updateTabs(type);
}

function updateTabs(type)
{
    for (var value in tabs) {
        var sNode = document.getElementById(tabs[value][0]);
        var spanNode = sNode.firstChild;
        if (value == type) {
            sNode.className = activeTableTab;
            spanNode.innerHTML = tabs[value][1];
        }
        else {
            sNode.className = tableTab;
            spanNode.innerHTML = "<a href=\"javascript:show("+ value + ");\">" + tabs[value][1] + "</a>";
        }
    }
}


/*********************************	自定义函数开始	***************************************/
function isEmpty(strValue){
	return null==strValue || 0>=strValue.length();
}

var gIsIfly=true;

function gIsIflytek(){
	return gIsIfly;
}

var gAIUIVer=false;
function gIsAIUIVersion(){
	return gAIUIVer;
}

// 作者
var gIflyAuthorName = "iFlytek";
var gIflyAuthorLink = "http://www.xfyun.cn";
var gIflyAuthorLinkName = "讯飞语音云";

var gLxyAuthorName = "CMCC";
var gLxyAuthorLink = "http://dev.10086.cn";
var gLxyAuthorLinkName = "中国移动灵犀云";
 
function gShowAuthor(){
	var name = gIflyAuthorName;
	var link = gIflyAuthorLink;
	var linkName = gIflyAuthorLinkName;
	
	if(  !gIsIflytek() ){
		name = gLxyAuthorName;
		link = gLxyAuthorLink;
		linkName = gLxyAuthorLinkName;
	}
	
     document.write( name+"&nbsp;&nbsp;&nbsp;<a href="+link+" target=\"_blank\">"+linkName+"</a>" );
}

// 语法指南
var gIflyGrammarGuideName = "语法编写指南";
var gIflyGrammarGuideLink = "http://club.voicecloud.cn/forum.php?mod=viewthread&tid=7595";

var gLxyGrammarGuideName = "语法编写指南";
var gLxyGrammarGuideLink = "http://dev.10086.cn/cmdn/bbs/thread-97556-1-1.html ";	

function gShowGrammarGuide(){
	var name = gIflyGrammarGuideName;
	var link = gIflyGrammarGuideLink;
	if( !gIsIflytek() ){
		name = gLxyGrammarGuideName;
		link = gLxyGrammarGuideLink;
	}
	
	document.write( "关于语法文件的编写，请参考 <a href="+link+" target=\"_blank\">"+name+"</a>" );
}

//开发者网站
var gIflyDeveloperWebsiteLink = "http://open.voicecloud.cn/developer.php";
var gIflyDeveloperWebsiteName = gIflyDeveloperWebsiteLink;

var gLxyDeveloperWebsiteLink = "http://dev.10086.cn"; 
var gLxyDeveloperWebsiteName = gLxyDeveloperWebsiteLink;

function gShowDeveloperWebsite(){
	var name = gIflyDeveloperWebsiteName;
	var link = gIflyDeveloperWebsiteLink;
	if( !gIsIflytek() ){
		name = gLxyDeveloperWebsiteName;
		link = gLxyDeveloperWebsiteLink;
	}
	document.write( "<a href="+link+" target=\"_blank\">"+name+"</a>" );
}

// Java 常见问题
var gIflyJavaQnAName = "Java 常见问题解答"; 
var gIflyJavaQnALink = "http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9864&pid=43664";

var gLxyJavaQnAName = "Java 常见问题解答";
var gLxyJavaQnALink = "http://dev.10086.cn/cmdn/bbs/viewthread.php?tid=97561";

function gShowJavaQnA(){
	var name = gIflyJavaQnAName;
	var link = gIflyJavaQnALink;
	if( !gIsIflytek() ){
		name = gLxyJavaQnAName;
		link = gLxyJavaQnALink;
	}
	
	document.write( "<a href="+link+" target=\"_blank\">"+name+"</a>" );
}

//语义配置
var gIflySchWebsiteName = "AIUI开放平台";
var gIflySchWebsiteLink = "http://aiui.xfyun.cn";

var gLxySupportEmail = "lingxicloud@139.com";

function gShowSchApply(){
	if( gIsIflytek() ){
		document.write( "申请SDK时，默认未开通语义功能，如需使用请在<a href="+gIflySchWebsiteLink+" target=\"_blank\">"+gIflySchWebsiteName+"</a>上进行业务配置" );
	}else{
		document.write( "申请SDK时，默认未开通语义功能，如需使用请联系 "+gLxySupportEmail+" 申请" );
	}
}

//服务器地址
var gIflyServerurlLink = "http://dev.voicecloud.cn/msp.do";
var gLxyServerLink = "http://cmcc.lingxicloud.com/msp.do";
function gShowServerurl(){
	var link = gIflyServerurlLink;
	if( !gIsIflytek() ){
		link = gLxyServerLink;
	}
	document.write( link );
}

//页底文字
var gIflyBottom = "IFLYTEK";
var gLxyBottom = "CMCC";
function gShowBottom(){
	var bottom = gIflyBottom;
	if( !gIsIflytek() ){
		bottom = gLxyBottom;
	}
	document.write( "<center>"+bottom+"</center>" );
}

var gIflyCompanyName = "科大讯飞";
var gLxyCompanyName = "中国移动";
function gShowCompanyName(){
	var name = gIflyCompanyName;
	if( !gIsIflytek() ){
		name = gLxyCompanyName;
	}
	document.write(  name );
}

var gIflyPlatformName = "语音云";
var gLxyPlatformName = "灵犀云";
function gShowPlatformName(){
	var name = gIflyPlatformName;
	if( !gIsIflytek() ){
		name = gLxyPlatformName;
	}
	document.write(  name );
}

function gShowPlatformOrCompanyNameAuto(){
	var name = gIflyCompanyName;
	if( !gIsIflytek() ){
		name = gLxyPlatformName;
	}
	document.write(  name );
}

function gShowServiceCheck(){
	if( gIsIflytek() ){
		document.write(  "<p>判断手机中是否安装了讯飞语记。在引擎模式为语记时，需要安装语记才能正常使用。</p>" );
	}else{
		document.write(  "<p>暂不支持</p>" );
	}
}

function gShowServiceVersion(){
	if( gIsIflytek() ){
		document.write(  "<p>获取手机中已安装的讯飞语记的版本号</p>" );
	}else{
		document.write(  "<p>暂不支持</p>" );
	}
}

function gShowEngineMode(){
	if( gIsIflytek() ){
		document.write(
			"<p>"
			+ 		"设置使用的引擎模式：语记、MSC、自动。此参数只在Android和iOS平台有用。"
	 		+"</p>"
	 		+"<p>"
	 		+"		在Android和iOS平台，通过语记可以免费使用本地功能。关于语记的介绍，请"
	 		+" 参考我们的官网。与语记中服务通信方式相比，另一种即直接通过SDK提供的接口和"
	 		+" 共享库使用语音服务，我们称之为MSC方式。在引擎选择有三种模式可选，<br>"
			+"auto：表示云端优先使用MSC，本地优先使用语记；<br>"
			+" msc：只使用MSC；<br>"
	 		+" plus：只使用语记。"
			+"</p>"
			+"<p>是否必须设置：否</p>"
			+"<p>默认值：\"auto\"  ——没有离线资源的SDK中， 有离线资源的SDK则默认为 msc</p>"
			+"<p>值范围：{ \"msc\", \"plus\", \"auto\" }</p>"
			);
	}else{
		document.write(  "<p>暂不支持</p>" );
	}
}

function gShowEngineTypeUsePlus(){
	if( gIsIflytek() ){
		document.write(  "或使用语记方式时，" );
	}
}

function gShowEngineTypeIntallPlus(){
	if( gIsIflytek() ){
		document.write(  "或安装“语记”（安卓或iOS平台）" );
	}
}

function gShowModePlus(){
	if( gIsIflytek() ){
		document.write(  "常量值：语记模式" );
	}else{
		document.write(  "暂不支持" );
	}
}

gTts = "合成";
gAsr = "识别";
gIvw = "唤醒";
gAll = "所有";

function gShowPlusLocal( type ){
	if( gIsIflytek() ){
		document.write(  "<p>在使用语记时，用于获取语记的本地"+type+"资源。</p>" );
	}else{
		document.write(  "<p>暂不支持</p>" );
	}
}

function gShowOpenEngineSettings(){
	if( gIsIflytek() ){
		document.write(  "<p>传入引擎名称，打开引擎设置。若引擎名称参数为空，则默认打开语记设置主界面</p>" );
	}else{
		document.write(  "<p>暂不支持</p>" );
	}
}

function gShowGetPlusLocalInfoTitle(){
	if( gIsIflytek() ){
		document.write(  "获取语记本地资源信息" );
	}else{
		document.write(  "获取本地资源信息（暂不支持）" );
	}
}

function gShowUnsupport(){
	document.write( "<p><b>此功能暂不支持</b></p>" );
}

function gShowExtraFace(){
	if( !gIsIflytek() ){
		gShowUnsupport();
	}
}

function gShowExtraIdentity(){
	if( !gIsIflytek() ){
		gShowUnsupport();
	}
}

function gShowExtraTranscripter(){
	if( !gIsIflytek() ){
		gShowUnsupport();
	}
}

function gShowErrorCodeLink(){
	var iflyErrorCodeLink = "<a href=http://www.xfyun.cn/doccenter/faq target=\"_blank\">http://www.xfyun.cn/doccenter/faq</a>";
	if( gIsIflytek() ){
		document.write(  "<p>关于错误码更详细的解析，请参考 "+iflyErrorCodeLink+" 。</p>" );
	}
}

function gShowTitle(){
	var title = "科大讯飞";
	if( !gIsIflytek() ){
		title = "灵犀云";
	}
	document.write( title );
}

function gShowHelp(){
	if( gIsIflytek() ){
		document.write(  "<p>在集成过程遇到问题，可通过 <b><a href=http://bbs.xfyun.cn target=\"_blank\">语音云开发者论坛</a></b> 搜索相关的问题答案，并与其他开发者交流。</p>" );
	}
}

function gUnsupportTip(){
	document.write( "此功能暂不支持" );
}

function gShowSchDefaultStatus(){
	if( gIsIflytek() ){
		document.write(  "SDK申请时，默认未开通语义理解功能（使用时报10402错误），可通过 <b><a href="+gIflySchWebsiteLink+" target=\"_blank\">"+gIflySchWebsiteName+"</a></b> 开通，并选择需要的语义场景。</p>" );
	}else{
		document.write(  "SDK申请时，默认未开通语义理解功能（使用时报10402错误），可联系我们开通。" );
	}
}

function gShowUserGuideFile(){
	if( gIsIflytek() ){
		if(  gIsAIUIVersion() ){
			document.write( "<b><a href=../../../../MSC%20Develop%20Manual%20for%20Android.pdf target=\"_blank\">科大讯飞MSC集成指南</a></b>" );
		}else{
			document.write( "<b><a href=http://doc.xfyun.cn/msc_android target=\"_blank\">科大讯飞MSC集成指南</a></b>" );
		}
	}else{
		document.write( "开发指南PDF文档" );
	}
}

function gShowUnderstanderFile(){
	if( gIsIflytek() ){
		document.write( "<b><a href=http://aiui.xfyun.cn/info/protocol target=\"_blank\">语义结果说明文档</a></b>" );
	}else{
		document.write( "《Open Semantic Platform API  Documents.pdf》" );
	}
}

function gShowIseResultFile(){
	document.write( "《Speech Evaluation API Documents.pdf》" );
}

function gShowIsePaperFile(){
	gShowIseResultFile();
}
/*********************************	自定义函数结束	***************************************/