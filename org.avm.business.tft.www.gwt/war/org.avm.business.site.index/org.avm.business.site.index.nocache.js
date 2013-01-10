function org_avm_business_site_index(){
  var $intern_0 = '', $intern_35 = '" for "gwt:onLoadErrorFn"', $intern_33 = '" for "gwt:onPropertyErrorFn"', $intern_21 = '"><\/script>', $intern_10 = '#', $intern_43 = '&', $intern_78 = '.cache.html', $intern_12 = '/', $intern_65 = '4A79DC25515007F625DDAB085AED7BC7', $intern_66 = '4D157A792905C8817A5F08A47E51F912', $intern_67 = '633518171DE3CAD88F42DB929FB6D2A0', $intern_68 = '6FC7DF2FE21F4FF7C1ED4279327B6DA4', $intern_69 = '804885871B1F6E8DBE2E7305D5861F20', $intern_70 = '98FC50E9B3BE8B2EDA3AFE442C53E3C3', $intern_71 = '9EAF731F4BD1028A3AD987DAC5A42FF5', $intern_77 = ':', $intern_27 = '::', $intern_85 = '<script defer="defer">org_avm_business_site_index.onInjectionDone(\'org.avm.business.site.index\')<\/script>', $intern_20 = '<script id="', $intern_81 = '<script language="javascript" src="', $intern_30 = '=', $intern_11 = '?', $intern_51 = 'ActiveXObject', $intern_72 = 'BAD81F6E23213F6DB809FA1C7A9AFCF3', $intern_73 = 'BEAD480D777B46A7A3B85F7255B6BB2A', $intern_32 = 'Bad handler "', $intern_52 = 'ChromeTab.ChromeFrame', $intern_79 = 'DOMContentLoaded', $intern_74 = 'F08480E032E704FCC1771672C32DE438', $intern_75 = 'FD516C1BE82E5D9C7C1DD05278EC1556', $intern_76 = 'FE125EC3BEB686F1EE5F4FE1437D144C', $intern_22 = 'SCRIPT', $intern_46 = 'Unexpected exception in locale detection, using default: ', $intern_45 = '_', $intern_44 = '__gwt_Locale', $intern_19 = '__gwt_marker_org.avm.business.site.index', $intern_23 = 'base', $intern_15 = 'baseUrl', $intern_4 = 'begin', $intern_3 = 'bootstrap', $intern_50 = 'chromeframe', $intern_14 = 'clear.cache.gif', $intern_29 = 'content', $intern_41 = 'default', $intern_64 = 'en', $intern_9 = 'end', $intern_58 = 'gecko', $intern_59 = 'gecko1_8', $intern_5 = 'gwt.codesvr=', $intern_6 = 'gwt.hosted=', $intern_7 = 'gwt.hybrid', $intern_34 = 'gwt:onLoadErrorFn', $intern_31 = 'gwt:onPropertyErrorFn', $intern_28 = 'gwt:property', $intern_62 = 'hosted.html?org_avm_business_site_index', $intern_57 = 'ie6', $intern_56 = 'ie8', $intern_55 = 'ie9', $intern_36 = 'iframe', $intern_13 = 'img', $intern_37 = "javascript:''", $intern_61 = 'loadExternalRefs', $intern_40 = 'locale', $intern_42 = 'locale=', $intern_24 = 'meta', $intern_39 = 'moduleRequested', $intern_8 = 'moduleStartup', $intern_54 = 'msie', $intern_25 = 'name', $intern_48 = 'opera', $intern_1 = 'org.avm.business.site.index', $intern_17 = 'org.avm.business.site.index.nocache.js', $intern_26 = 'org.avm.business.site.index::', $intern_38 = 'position:absolute;width:0;height:0;border:none', $intern_80 = 'ressources/scriptaculous-js-1.7.0/lib/prototype.js', $intern_82 = 'ressources/scriptaculous-js-1.7.0/lib/prototype.js"><\/script>', $intern_83 = 'ressources/scriptaculous-js-1.7.0/src/scriptaculous.js', $intern_84 = 'ressources/scriptaculous-js-1.7.0/src/scriptaculous.js"><\/script>', $intern_53 = 'safari', $intern_16 = 'script', $intern_63 = 'selectingPermutation', $intern_2 = 'startup', $intern_18 = 'undefined', $intern_60 = 'unknown', $intern_47 = 'user.agent', $intern_49 = 'webkit';
  var $wnd = window, $doc = document, $stats = $wnd.__gwtStatsEvent?function(a){
    return $wnd.__gwtStatsEvent(a);
  }
  :null, $sessionId = $wnd.__gwtStatsSessionId?$wnd.__gwtStatsSessionId:null, scriptsDone, loadDone, bodyDone, base = $intern_0, metaProps = {}, values = [], providers = [], answers = [], softPermutationId = 0, onLoadErrorFunc, propertyErrorFunc;
  $stats && $stats({moduleName:$intern_1, sessionId:$sessionId, subSystem:$intern_2, evtGroup:$intern_3, millis:(new Date).getTime(), type:$intern_4});
  if (!$wnd.__gwt_stylesLoaded) {
    $wnd.__gwt_stylesLoaded = {};
  }
  if (!$wnd.__gwt_scriptsLoaded) {
    $wnd.__gwt_scriptsLoaded = {};
  }
  function isHostedMode(){
    var result = false;
    try {
      var query = $wnd.location.search;
      return (query.indexOf($intern_5) != -1 || (query.indexOf($intern_6) != -1 || $wnd.external && $wnd.external.gwtOnLoad)) && query.indexOf($intern_7) == -1;
    }
     catch (e) {
    }
    isHostedMode = function(){
      return result;
    }
    ;
    return result;
  }

  function maybeStartModule(){
    if (scriptsDone && loadDone) {
      var iframe = $doc.getElementById($intern_1);
      var frameWnd = iframe.contentWindow;
      if (isHostedMode()) {
        frameWnd.__gwt_getProperty = function(name){
          return computePropValue(name);
        }
        ;
      }
      org_avm_business_site_index = null;
      frameWnd.gwtOnLoad(onLoadErrorFunc, $intern_1, base, softPermutationId);
      $stats && $stats({moduleName:$intern_1, sessionId:$sessionId, subSystem:$intern_2, evtGroup:$intern_8, millis:(new Date).getTime(), type:$intern_9});
    }
  }

  function computeScriptBase(){
    function getDirectoryOfFile(path){
      var hashIndex = path.lastIndexOf($intern_10);
      if (hashIndex == -1) {
        hashIndex = path.length;
      }
      var queryIndex = path.indexOf($intern_11);
      if (queryIndex == -1) {
        queryIndex = path.length;
      }
      var slashIndex = path.lastIndexOf($intern_12, Math.min(queryIndex, hashIndex));
      return slashIndex >= 0?path.substring(0, slashIndex + 1):$intern_0;
    }

    function ensureAbsoluteUrl(url){
      if (url.match(/^\w+:\/\//)) {
      }
       else {
        var img = $doc.createElement($intern_13);
        img.src = url + $intern_14;
        url = getDirectoryOfFile(img.src);
      }
      return url;
    }

    function tryMetaTag(){
      var metaVal = __gwt_getMetaProperty($intern_15);
      if (metaVal != null) {
        return metaVal;
      }
      return $intern_0;
    }

    function tryNocacheJsTag(){
      var scriptTags = $doc.getElementsByTagName($intern_16);
      for (var i = 0; i < scriptTags.length; ++i) {
        if (scriptTags[i].src.indexOf($intern_17) != -1) {
          return getDirectoryOfFile(scriptTags[i].src);
        }
      }
      return $intern_0;
    }

    function tryMarkerScript(){
      var thisScript;
      if (typeof isBodyLoaded == $intern_18 || !isBodyLoaded()) {
        var markerId = $intern_19;
        var markerScript;
        $doc.write($intern_20 + markerId + $intern_21);
        markerScript = $doc.getElementById(markerId);
        thisScript = markerScript && markerScript.previousSibling;
        while (thisScript && thisScript.tagName != $intern_22) {
          thisScript = thisScript.previousSibling;
        }
        if (markerScript) {
          markerScript.parentNode.removeChild(markerScript);
        }
        if (thisScript && thisScript.src) {
          return getDirectoryOfFile(thisScript.src);
        }
      }
      return $intern_0;
    }

    function tryBaseTag(){
      var baseElements = $doc.getElementsByTagName($intern_23);
      if (baseElements.length > 0) {
        return baseElements[baseElements.length - 1].href;
      }
      return $intern_0;
    }

    var tempBase = tryMetaTag();
    if (tempBase == $intern_0) {
      tempBase = tryNocacheJsTag();
    }
    if (tempBase == $intern_0) {
      tempBase = tryMarkerScript();
    }
    if (tempBase == $intern_0) {
      tempBase = tryBaseTag();
    }
    if (tempBase == $intern_0) {
      tempBase = getDirectoryOfFile($doc.location.href);
    }
    tempBase = ensureAbsoluteUrl(tempBase);
    base = tempBase;
    return tempBase;
  }

  function processMetas(){
    var metas = document.getElementsByTagName($intern_24);
    for (var i = 0, n = metas.length; i < n; ++i) {
      var meta = metas[i], name = meta.getAttribute($intern_25), content;
      if (name) {
        name = name.replace($intern_26, $intern_0);
        if (name.indexOf($intern_27) >= 0) {
          continue;
        }
        if (name == $intern_28) {
          content = meta.getAttribute($intern_29);
          if (content) {
            var value, eq = content.indexOf($intern_30);
            if (eq >= 0) {
              name = content.substring(0, eq);
              value = content.substring(eq + 1);
            }
             else {
              name = content;
              value = $intern_0;
            }
            metaProps[name] = value;
          }
        }
         else if (name == $intern_31) {
          content = meta.getAttribute($intern_29);
          if (content) {
            try {
              propertyErrorFunc = eval(content);
            }
             catch (e) {
              alert($intern_32 + content + $intern_33);
            }
          }
        }
         else if (name == $intern_34) {
          content = meta.getAttribute($intern_29);
          if (content) {
            try {
              onLoadErrorFunc = eval(content);
            }
             catch (e) {
              alert($intern_32 + content + $intern_35);
            }
          }
        }
      }
    }
  }

  function __gwt_isKnownPropertyValue(propName, propValue){
    return propValue in values[propName];
  }

  function __gwt_getMetaProperty(name){
    var value = metaProps[name];
    return value == null?null:value;
  }

  function unflattenKeylistIntoAnswers(propValArray, value){
    var answer = answers;
    for (var i = 0, n = propValArray.length - 1; i < n; ++i) {
      answer = answer[propValArray[i]] || (answer[propValArray[i]] = []);
    }
    answer[propValArray[n]] = value;
  }

  function computePropValue(propName){
    var value = providers[propName](), allowedValuesMap = values[propName];
    if (value in allowedValuesMap) {
      return value;
    }
    var allowedValuesList = [];
    for (var k in allowedValuesMap) {
      allowedValuesList[allowedValuesMap[k]] = k;
    }
    if (propertyErrorFunc) {
      propertyErrorFunc(propName, allowedValuesList, value);
    }
    throw null;
  }

  var frameInjected;
  function maybeInjectFrame(){
    if (!frameInjected) {
      frameInjected = true;
      var iframe = $doc.createElement($intern_36);
      iframe.src = $intern_37;
      iframe.id = $intern_1;
      iframe.style.cssText = $intern_38;
      iframe.tabIndex = -1;
      $doc.body.appendChild(iframe);
      $stats && $stats({moduleName:$intern_1, sessionId:$sessionId, subSystem:$intern_2, evtGroup:$intern_8, millis:(new Date).getTime(), type:$intern_39});
      iframe.contentWindow.location.replace(base + initialHtml);
    }
  }

  providers[$intern_40] = function(){
    var locale = null;
    var rtlocale = $intern_41;
    try {
      if (!locale) {
        var queryParam = location.search;
        var qpStart = queryParam.indexOf($intern_42);
        if (qpStart >= 0) {
          var value = queryParam.substring(qpStart + 7);
          var end = queryParam.indexOf($intern_43, qpStart);
          if (end < 0) {
            end = queryParam.length;
          }
          locale = queryParam.substring(qpStart + 7, end);
        }
      }
      if (!locale) {
        locale = __gwt_getMetaProperty($intern_40);
      }
      if (!locale) {
        locale = $wnd[$intern_44];
      }
      if (locale) {
        rtlocale = locale;
      }
      while (locale && !__gwt_isKnownPropertyValue($intern_40, locale)) {
        var lastIndex = locale.lastIndexOf($intern_45);
        if (lastIndex < 0) {
          locale = null;
          break;
        }
        locale = locale.substring(0, lastIndex);
      }
    }
     catch (e) {
      alert($intern_46 + e);
    }
    $wnd[$intern_44] = rtlocale;
    return locale || $intern_41;
  }
  ;
  values[$intern_40] = {'default':0, en:1};
  providers[$intern_47] = function(){
    var ua = navigator.userAgent.toLowerCase();
    var makeVersion = function(result){
      return parseInt(result[1]) * 1000 + parseInt(result[2]);
    }
    ;
    if (function(){
      return ua.indexOf($intern_48) != -1;
    }
    ())
      return $intern_48;
    if (function(){
      return ua.indexOf($intern_49) != -1 || function(){
        if (ua.indexOf($intern_50) != -1) {
          return true;
        }
        if (typeof window[$intern_51] != $intern_18) {
          try {
            var obj = new ActiveXObject($intern_52);
            if (obj) {
              obj.registerBhoIfNeeded();
              return true;
            }
          }
           catch (e) {
          }
        }
        return false;
      }
      ();
    }
    ())
      return $intern_53;
    if (function(){
      return ua.indexOf($intern_54) != -1 && $doc.documentMode >= 9;
    }
    ())
      return $intern_55;
    if (function(){
      return ua.indexOf($intern_54) != -1 && $doc.documentMode >= 8;
    }
    ())
      return $intern_56;
    if (function(){
      var result = /msie ([0-9]+)\.([0-9]+)/.exec(ua);
      if (result && result.length == 3)
        return makeVersion(result) >= 6000;
    }
    ())
      return $intern_57;
    if (function(){
      return ua.indexOf($intern_58) != -1;
    }
    ())
      return $intern_59;
    return $intern_60;
  }
  ;
  values[$intern_47] = {gecko1_8:0, ie6:1, ie8:2, ie9:3, opera:4, safari:5};
  org_avm_business_site_index.onScriptLoad = function(){
    if (frameInjected) {
      loadDone = true;
      maybeStartModule();
    }
  }
  ;
  org_avm_business_site_index.onInjectionDone = function(){
    scriptsDone = true;
    $stats && $stats({moduleName:$intern_1, sessionId:$sessionId, subSystem:$intern_2, evtGroup:$intern_61, millis:(new Date).getTime(), type:$intern_9});
    maybeStartModule();
  }
  ;
  processMetas();
  computeScriptBase();
  var strongName;
  var initialHtml;
  if (isHostedMode()) {
    if ($wnd.external && ($wnd.external.initModule && $wnd.external.initModule($intern_1))) {
      $wnd.location.reload();
      return;
    }
    initialHtml = $intern_62;
    strongName = $intern_0;
  }
  $stats && $stats({moduleName:$intern_1, sessionId:$sessionId, subSystem:$intern_2, evtGroup:$intern_3, millis:(new Date).getTime(), type:$intern_63});
  if (!isHostedMode()) {
    try {
      unflattenKeylistIntoAnswers([$intern_64, $intern_48], $intern_65);
      unflattenKeylistIntoAnswers([$intern_64, $intern_53], $intern_66);
      unflattenKeylistIntoAnswers([$intern_41, $intern_53], $intern_67);
      unflattenKeylistIntoAnswers([$intern_41, $intern_55], $intern_68);
      unflattenKeylistIntoAnswers([$intern_64, $intern_59], $intern_69);
      unflattenKeylistIntoAnswers([$intern_41, $intern_56], $intern_70);
      unflattenKeylistIntoAnswers([$intern_41, $intern_59], $intern_71);
      unflattenKeylistIntoAnswers([$intern_64, $intern_56], $intern_72);
      unflattenKeylistIntoAnswers([$intern_41, $intern_57], $intern_73);
      unflattenKeylistIntoAnswers([$intern_64, $intern_57], $intern_74);
      unflattenKeylistIntoAnswers([$intern_64, $intern_55], $intern_75);
      unflattenKeylistIntoAnswers([$intern_41, $intern_48], $intern_76);
      strongName = answers[computePropValue($intern_40)][computePropValue($intern_47)];
      var idx = strongName.indexOf($intern_77);
      if (idx != -1) {
        softPermutationId = Number(strongName.substring(idx + 1));
        strongName = strongName.substring(0, idx);
      }
      initialHtml = strongName + $intern_78;
    }
     catch (e) {
      return;
    }
  }
  var onBodyDoneTimerId;
  function onBodyDone(){
    if (!bodyDone) {
      bodyDone = true;
      maybeStartModule();
      if ($doc.removeEventListener) {
        $doc.removeEventListener($intern_79, onBodyDone, false);
      }
      if (onBodyDoneTimerId) {
        clearInterval(onBodyDoneTimerId);
      }
    }
  }

  if ($doc.addEventListener) {
    $doc.addEventListener($intern_79, function(){
      maybeInjectFrame();
      onBodyDone();
    }
    , false);
  }
  var onBodyDoneTimerId = setInterval(function(){
    if (/loaded|complete/.test($doc.readyState)) {
      maybeInjectFrame();
      onBodyDone();
    }
  }
  , 50);
  $stats && $stats({moduleName:$intern_1, sessionId:$sessionId, subSystem:$intern_2, evtGroup:$intern_3, millis:(new Date).getTime(), type:$intern_9});
  $stats && $stats({moduleName:$intern_1, sessionId:$sessionId, subSystem:$intern_2, evtGroup:$intern_61, millis:(new Date).getTime(), type:$intern_4});
  if (!__gwt_scriptsLoaded[$intern_80]) {
    __gwt_scriptsLoaded[$intern_80] = true;
    document.write($intern_81 + base + $intern_82);
  }
  if (!__gwt_scriptsLoaded[$intern_83]) {
    __gwt_scriptsLoaded[$intern_83] = true;
    document.write($intern_81 + base + $intern_84);
  }
  $doc.write($intern_85);
}

org_avm_business_site_index();
