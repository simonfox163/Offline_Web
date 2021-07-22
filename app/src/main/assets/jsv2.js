window.addEventListener('load', () => {
  if (window.parent !== window) { return }

  function getCssJsLoadTime() {
    try {
      var per = performance.getEntriesByType("resource");
      var cssTotalTime = 0;
      var jsTotalTime = 0;
      per.forEach(function (key) {
        if (key.initiatorType === 'link' && key.name.indexOf('.css') > -1) {
          cssTotalTime += key.duration;
        }

        if ((key.initiatorType === 'link' || key.initiatorType === 'script') && key.name.indexOf('.js')) {
          jsTotalTime += key.duration;
        }
      });
      return {
        cssTotalTime: cssTotalTime,
        jsTotalTime: jsTotalTime
      };
    } catch (err) {
      return {
        cssTotalTime: 0,
        jsTotalTime: 0
      };
    }
  }


  var timing = performance.timing;
  var p = {};
  p.dns = timing.domainLookupEnd - timing.domainLookupStart;
  p.tcp = timing.connectEnd - timing.connectStart;

  var loadTime = getCssJsLoadTime();
  p.jsTotalTime = parseInt(loadTime.jsTotalTime);
  p.cssTotalTime = parseInt(loadTime.cssTotalTime);

  function setParameter() {
    p.load = timing.loadEventEnd - timing.fetchStart;
    p.whitescreen = timing.domInteractive - timing.fetchStart;
    p.fpt = timing.responseEnd - timing.fetchStart;

    p.request = timing.responseEnd - timing.responseStart;
    p.ttfb = timing.responseStart - timing.navigationStart;
  }

  function objToSearchParams(obj) {
    var result = ''
    Object.keys(obj).forEach((key, index) => {
        if (index !== 0) {result += '&'}
        result += key + '=' + obj[key]
    })
    return result
  }

  function submitData() {
    console.log(p)
    var searchStr = objToSearchParams(p)
    window.location.href= 'yunji://common/performanceAnalyze?' + searchStr
  }

  if (timing.loadEventEnd === 0 || timing.domInteractive === 0 || timing.responseEnd === 0) {
    var timer = null;
    timer = setInterval(function () {
      if (timing.loadEventEnd > 0 && timing.domInteractive > 0) {
        setParameter();
        clearInterval(timer);
        submitData();
      }
    }, 500);
  } else {
    setParameter();
    submitData();
  }
})
