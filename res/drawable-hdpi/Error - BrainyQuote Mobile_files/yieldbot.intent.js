var ybotq=ybotq||[],yieldbot=yieldbot||{};
(function(){var ta=function(){return(0|36*Math.random()).toString(36)},D=function(){return((+new Date).toString(36)+"xxxxxxxxxx".replace(/[x]/g,ta)).toLowerCase()},$=Array.prototype.indexOf,Q=function(a,b){var c,d;if(null==a)return-1;if($&&a.indexOf===$)return a.indexOf(b);c=0;for(d=a.length;c<d;c++)if(c in a&&a[c]===b)return c;return-1},n=function(a,b){null==b&&(b=s);a?m("_debug",a,b):p("_debug")},t=function(){var a=Array.prototype.slice.call(arguments);yieldbot.framework_window.yieldbot._history.push(a);
yieldbot.framework_window.console&&k("_debug")&&yieldbot.framework_window.console.log(a)},E=function(a,b){return function(){var c=(new Date).getTime(),d=Array.prototype.slice.call(arguments);d.unshift(c-yieldbot.framework_window.yieldbot._historyStart);d.unshift(a);t.apply(null,d);return b.apply(null,arguments)}},ua=function(){if(yieldbot.framework_window.console){yieldbot.framework_window.console.group("Yieldbot History");for(var a=0,b=yieldbot.framework_window.yieldbot._history.length;a<b;a++)yieldbot.framework_window.console.log(yieldbot.framework_window.yieldbot._history[a]);
yieldbot.framework_window.console.groupEnd()}},va=/^\s\s*/,wa=/\s\s*$/,q=function(a){return a.replace(va,"").replace(wa,"")},r=window,R=window.top,aa=escape,xa=unescape,s=36E5,ba=1E3,ca=!1,S=!1,F=[],G=[],v={},T={},H,I,x="i",U=!1,da=!1,J=!1,K=function(a,b){if(0===arguments.length){x&&(a="//"+x+".yldbt.com/m/");var c=k("c",!0);c&&(a=c);"http://"===a.slice(0,7)&&(a=a.slice(5));"//"===a.slice(0,2)&&(a=("https:"===document.location.protocol?"https:":"http:")+a);return a}m("c",a,s);b&&(ca=b)},ea=function(a,
b,c){var d=a.length;b=b||";";var e=c||"=";for(c=0;c<d;c++)1===a[c].length?a[c]=a[c][0]:2===a[c].length&&(a[c]=a[c][0]+e+aa(a[c][1]));return a.join(b)},L=function(a){ca&&a.push(["_url_prefix",K()]);a.push(["e"]);return ea(a,"&")},ya=function(){J=!0},za=function(){J=!1},z=function(a){if(0===arguments.length)return H||k("b",!0);yieldbot.framework_window.yieldbot._initialized||(H=q(""+a),m("b",H,s))},Aa=function(a){H=q(""+a)},Ba=function(a){ba=a},Ca=function(a){I=a},fa=function(a){if(0===arguments.length)return x||
k("d",!0);x=q(""+a);m("d",x,s)},Da=function(a){x=a},Ea=function(a){da=a},ga=function(a,b){var c=new Image(1,1);c.onload=function(){};c.src=K()+z()+"/"+a+"?"+b},ha=function(a,b,c){c=K()+z()+"/"+b+"?"+c;J?(b=a.document.createElement("script"),b.src=c,a=a.document.getElementsByTagName("script")[0],a.parentNode.insertBefore(b,a)):a.document.write('<script type="text/javascript" src="'+c+'">\x3c/script>')},u=function(a){ga("info.gif",L(a))},k=function(a,b){var c="__ybot"+a;try{var d=RegExp("(^|;)[ ]*"+
c+"=([^;]*)").exec(yieldbot.framework_window.document.cookie);return d?xa(d[2]):void 0}catch(e){return b||(d=[],d.push(["v","v2013-10-28|a38349c"]),d.push(["op","getCookie"]),d.push(["ts",+new Date]),d.push(["k",c]),d.push(["m",e?e.message||e:"_info"]),u(d)),!1}},m=function(a,b,c,d,e,g){a="__ybot"+a;try{var f;c&&(f=new Date,f.setTime(f.getTime()+c));yieldbot.framework_window.document.cookie=a+"="+aa(b)+(c?";expires="+f.toGMTString():"")+";path="+(d||"/")+(e?";domain="+e:"")+(g?";secure":"")}catch(h){c=
[],c.push(["v","v2013-10-28|a38349c"]),c.push(["op","setCookie"]),c.push(["ts",+new Date]),c.push(["k",a]),c.push(["ev",b]),c.push(["m",h?h.message||h:"_info"]),u(c)}},p=function(a,b,c,d){m(a,"",-1,b,c,d)},Fa=function(a){a?(m("n","1",s),p("a"),p("e")):p("n")},ia=function(){var a=k("s");if(a)return a.split(".")[0]},M=function(){if(I)return I;var a=k("s");if(a)return a.split(".")[2]},Ga=function(){var a,b,c,d,e,g,f,h,l,V,n=new Date,A=yieldbot.framework_window,r=A.document,q=A.screen,y=A.navigator,u=
/[ +]/g,t=function(a){return a.replace(u,"%20")};b=D();V=n.getTime();l=k("v");c=k("u")||D();e=k("s");f=k("n");e?(a=e.split("."),e=a[0],g=1+parseInt(a[3],10),h=parseInt(a[1],10),a=a[2]):(e=D(),g=1,h=l?0:1);d=[e,h,b,g].join(".");m("u",c,31556E7);m("v",V,2592E6);m("s",d,s);f&&m("n",f,s);I=b;p("a");p("p");p("e");d=[];d.push(["v","v2013-10-28|a38349c"]);d.push(["vi",c]);d.push(["si",e]);d.push(["pvi",b]);d.push(["pvd",g]);a&&d.push(["lpvi",a]);h&&d.push(["nv"]);(function(){if("boolean"===N(y.cookieEnabled))return!y.cookieEnabled;
var a;m("t","1");a="1"===k("t")?!1:!0;return a})()&&d.push(["cd"]);G.length&&d.push(["sn",G.join("|")]);f&&d.push(["sb"]);d.push(["lo",t(r.location.href)]);d.push(["r",t(function(){var a="";try{a=R.document.referrer}catch(b){if(A.parent)try{a=A.parent.document.referrer}catch(c){a=""}}""===a&&(a=r.referrer);return a}())]);d.push(["sd",q.width+"x"+q.height]);d.push(["to",n.getTimezoneOffset()/60]);d.push(["la",y.language||y.userLanguage]);d.push(["np",y.platform]);d.push(["ua",y.userAgent]);l&&d.push(["lpv",
V-parseInt(l,10)]);return L(d)},Ha=function(a,b,c,d,e,g){var f,h,l;f=a.createElement("div");h=a.createElement("div");l=a.createElement("a");f.className="overlay";h.className="x-out";f.style.width="100%";f.style.height="100%";f.style.position="fixed";f.style.top="0";f.style.left="0";f.style.background="rgba(0, 0, 0, 0.4)";h.style.position="absolute";h.style.top="-12px";h.style.right="-15px";l.style.textDecoration="none";l.style.font="18px/18px Verdana,sans-serif";l.style.color="#888";l.style.padding=
"0 5px";l.style.background="#fff";l.style.border="solid 2px #444";l.style.borderRadius="13px";l.style.backgroundClip="padding-box";l.href=document.location.href;l.innerHTML="X";l.onclick=f.onclick=function(){b.style.display="none";b.innerHTML="";return!1};h.appendChild(l);c.appendChild(h);f.appendChild(c);b.appendChild(f);b.style.position="fixed";b.style.zIndex=1000002;d.position="absolute";d.border="solid 2px #444";(function(){var b=a.documentElement;d.left=(b.clientWidth-e)/2+"px";d.top=(b.clientHeight-
g)/2+"px"})()},ja=function(a,b,c,d,e,g,f){var h;e=e.createElement("iframe");e.frameBorder="0";e.width=g;e.height=f;e.scrolling="no";e.id=c;d&&(e.src="javascript:false");e.allowTransparency="true";a.appendChild(e);try{e.contentWindow.document.open()}catch(l){h="javascript:var d=document.open();d.domain='"+yieldbot.framework_window.document.domain+"';",e.src=h+"void(0);"}try{var k=e.contentWindow.document;k.write(b);k.close()}catch(n){e.src=h+'d.write("'+b.replace(/"/g,String.fromCharCode(92)+'"')+
'");d.close();'}},Ia=function(a){var b=a.size[0],c=a.size[1],d=a.html,e=a.style,g=a.interstitial,f=!a.silent,h=a.request_id,l=a.delay;a=a.wrapper_id||"ybot-"+h;var k=r.document,n=k.getElementById(a),m=k.createElement("div"),p=m.style;m.className="ybot-creative creative-wrapper";g?Ha(k,n,m,p,b,c):n.appendChild(m);p.width=b+"px";p.height=c+"px";f&&(d+='<script type="text/javascript">var y=window.parent.yieldbot;y.impression("'+h+'");\x3c/script>');var q="<!DOCTYPE html><head><meta charset=utf-8><style>"+
e+"</style></head><body>"+d+"</body>",s=/MSIE[ ]+6/.test(r.navigator.userAgent),t=a+"-iframe";null!=l?setTimeout(function(){ja(m,q,s,t,k,b,c)},l):ja(m,q,s,t,k,b,c)},Ja=function(){var a=k("u");a&&m("u",a,s);p("v")},Ka=function(a,b,c){-1===Q(G,a)&&G.push(a);null==b?delete v[a]:v[a]=b;null==c?delete T[a]:T[a]=c},ka=function(a){m("a",a.join("."),s)},W=function(){var a=k("a");return"."===k("a")?[]:a?a.split("."):[]},la=function(){var a=k("e");return a?a.split("."):[]},La=function(a){m("e",a.join("."),
s)},Ma=function(a){U=a},ma=function(a,b,c){c=c||"ybot_";var d=[];d.push(["y"]);d.push([c+"slot",a]);d.push([c+"psn",z()]);d.push([c+"pvi",M()]);d.push([c+"subdomain",fa()]);return ea(d,b)},O=function(a){var b,c,d;c=W();var e=la();if(!a)return!1;a.split&&(d=a.split(","));d||(d=a);if((b=d.length)&&"."===k("a"))return c=Math.round(Math.random()*(b-1)),q(d[c]);for(a=0;a<b;a++)if(-1<Q(c,q(d[a])))return U&&(p("a"),p("e")),q(d[a]);if(e.length)for(a=0;a<b;a++)if(-1<Q(e,q(d[a])))return U&&(p("a"),p("e")),
q(d[a]);return!1},na=function(a,b,c){return(a=O(a))?ma(a,b,c):"n"},B=function(a){a=O(a);return da?a?ma(a).slice(12):!1:a},Na=function(a){return B(a)?"y":"n"},Oa=function(a,b){B(b)&&googletag.cmd.push(function(){googletag.pubads().setTargeting(a,"y")})},Pa=function(a,b){B(b)&&GA_googleAddAttr(a,"y")},Qa=function(){yieldbot.framework_window.yieldbot._initialized||null==z()||(yieldbot.framework_window.yieldbot._initialized=!0,yieldbot.framework_window.yieldbot._initStart=(new Date).getTime(),ha(yieldbot.framework_window,
"init.js",Ga()),S=!0)},C=function(a,b){var c,d,e,g=[];d=D();g.push(["v","v2013-10-28|a38349c"]);g.push(["vi",k("u")]);g.push(["si",ia()]);g.push(["pvi",M()]);g.push(["ri",d]);b&&g.push(["wi",b]);for(c in a)if(a.hasOwnProperty(c)&&(g.push([c,a[c]]),("slot"===c||"ad_slot"===c)&&"."!==k("a"))){p("e");e=W();for(var f=a[c],h=void 0,l=void 0,m=[],h=0,l=e.length;h<l;h++)e[h]!==f&&m.push(e[h]);e=m;ka(e)}J||b||r.document.write('<div id="ybot-'+d+'"></div>');ha(r,"ad/creative.js",L(g))},oa=function(a,b){var c=
r.document,d;if(R!==r||yieldbot.framework_window.yieldbot._initialized){var e=T[a]||2E3,g=O(a);g&&!W()&&v[a]?(d=c.getElementById(v[a]),d.innerHTML="",C({slot:g},v[a])):la().length?setTimeout(function(){(g=O(a))?(d=c.getElementById(v[a]),d.innerHTML="",C({slot:g},v[a])):b()},e):b()}else F.push(function(){setTimeout(function(){oa(a,b)},0)})},Ra=function(a){var b=[];b.push(["v","v2013-10-28|a38349c"]);b.push(["vi",k("u")]);b.push(["si",ia()]);b.push(["pvi",M()]);b.push(["ri",a]);r.document.getElementById("ybot-frame-"+
a);ga("ad/impression.gif",L(b))},X=!1,w=[],pa={dfp_sb_manager:2,psn:1,ad:1,init:0},qa={},N=function(a){return null==a?String(a):qa[Object.prototype.toString.call(a)]||"object"},Sa=Array.isArray||function(a){return"array"===N(a)},ra=function(a){var b,c,d,e,g;g=a.toString();b=(new Date).getTime();t.apply(null,["ybotq.push."+yieldbot.framework_window.yieldbot._pushCount,b-yieldbot.framework_window.yieldbot._historyStart,g]);yieldbot.framework_window.yieldbot._pushCount+=1;if("function"===N(a))b=a;else{if(!Sa(a))if(w.push(a),
e=w[0],pa.hasOwnProperty(e))pa[e]===w.length-1?(X=!0,a=w):a=["noop"];else{a=[];a.push(["v","v2013-10-28|a38349c"]);a.push(["ts",+new Date]);a.push(["api_error",e+" not supported with unwrapped call"]);g=w.length;for(e=0;e<g;e++)a.push(["arg_stack",w[e]]);u(a);a=["noop"]}e=a[0];g=a.slice(1);if(yieldbot.hasOwnProperty(e))b=yieldbot[e],g&&(d=g);else{var f=[];f.push(["v","v2013-10-28|a38349c"]);f.push(["op",e]);f.push(["ts",+new Date]);f.push(["api_error",e+" function not available"]);c=g.length;for(b=
0;b<c;b++)f.push(["arg",g[b]]);u(f);b=yieldbot.noop}X&&(X=!1,w=[])}if(S&&"resume"!==e)F.push(a);else try{b.apply(yieldbot,d||[])}catch(h){f=[];f.push(["v","v2013-10-28|a38349c"]);f.push(["ts",+new Date]);k("_debug")&&(yieldbot.framework_window.console.log("Caught error in ybotq.push"),yieldbot.framework_window.console.log(h.stack||h.stackTrace||"Error in ybotq.push with no stack trace"));f.push(["apie",h.message||h]);try{u(f)}catch(l){}}},Y=function(a){var b,c;if(!a.framework)for(c=a.length,b=0;b<
c;b++)ra(a[b])},Ta=function(){S=!1;Y(F);F=[];var a=[],b=(new Date).getTime(),c=b-yieldbot.framework_window.yieldbot._initStart,d=b-yieldbot.framework_window.yieldbot._historyStart;isFinite(c)&&c>ba&&(a.push(["v","v2013-10-28|a38349c"]),a.push(["op","resumeCall"]),a.push(["ts",b]),a.push(["pvi",M()]),a.push(["t0",c]),a.push(["t1",d]),u(a))},Z="Boolean Number String Function Array Date RegExp Object".split(" "),P=0;for(;P<Z.length;P++)qa["[object "+Z[P]+"]"]=Z[P].toLowerCase();if(!yieldbot.framework){yieldbot.framework=
!0;yieldbot.enableAsync=yieldbot.enable_async=E("yieldbot.enableAsync",ya);yieldbot.enableSync=yieldbot.enable_sync=za;yieldbot.data_collection_opt_out=Ja;yieldbot.dfp_sb_manager=Oa;yieldbot.gam_manager=Pa;yieldbot.noop=function(){};yieldbot.pub=yieldbot.psn=E("yieldbot.pub",z);yieldbot.psn_iframe=Aa;yieldbot.subdomain=fa;yieldbot.subdomain_iframe=Da;yieldbot.pvi_iframe=Ca;yieldbot.resume=Ta;yieldbot.run_queue=Y;yieldbot.defineSlot=Ka;yieldbot.set_slots=ka;yieldbot.set_alternate_slots=La;yieldbot.ad_serve_first_slot_only=
Ma;yieldbot.adAvailable=Na;yieldbot.slot_available=B;yieldbot.alternateSlot=oa;yieldbot.ad_params=na;yieldbot.params=na;yieldbot.support_pre_ad_params=Ea;yieldbot._erase_cookie=p;yieldbot._get_cookie=k;yieldbot._set_cookie=m;yieldbot._render=Ia;yieldbot._url_prefix=K;yieldbot._block_session=Fa;yieldbot._info=u;yieldbot._info_init_time_limit=Ba;yieldbot.type=N;yieldbot.debug=n;yieldbot.log=t;yieldbot.dumpLog=ua;yieldbot.go=yieldbot.enablePub=yieldbot.track=yieldbot.init=E("yieldbot.go",Qa);yieldbot.ad=
C;yieldbot.renderAd=function(a,b){C({slot:a},b)};var sa=!1;yieldbot.renderIfAvailable=function(a,b){B(a)&&(sa=!0,C({slot:a},b))};yieldbot.adNotAvailable=function(a){sa||a()};yieldbot.impression=E("yieldbot.impression",Ra);yieldbot.framework_window=r;yieldbot._initialized=!1;yieldbot._history=[];yieldbot._pushCount=0;yieldbot._historyStart=(new Date).getTime();for(n=window;n!==R;)try{n=n.parent,n.ybotq&&n.ybotq.framework&&(yieldbot.framework_window=n)}catch(Ua){yieldbot.unfriendly_iframe="In unfriendly iframe tag document.domain is: "+
r.document.domain}yieldbot.framework_window.performance&&yieldbot.framework_window.performance.timing?r===yieldbot.framework_window?t("history offset",yieldbot._historyStart-yieldbot.framework_window.performance.timing.navigationStart):t("subsequent yieldbot.intent tag load",yieldbot._historyStart-yieldbot.framework_window.yieldbot._historyStart):r===yieldbot.framework_window?t("history offset","unknown"):t("subsequent yieldbot.intent tag load","unknown");Y(ybotq);ybotq={push:function(){var a,b=arguments.length;
for(a=0;a<b;a++)ra(arguments[a])},framework:!0};yieldbot.unfriendly_iframe&&(n=[],n.push(["v","v2013-10-28|a38349c"]),n.push(["op","iframeAccess"]),n.push(["ts",+new Date]),n.push(["m",yieldbot.unfriendly_iframe]),u(n))}})();