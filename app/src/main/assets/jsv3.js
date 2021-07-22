/*
 * @Descriptions:
 * @Author: yaxiong wu
 * @Date: 2021-07-15 08:51:31
 * @LastEditTime: 2021-07-16 14:54:21
 * @LastEditors: yaxiong wu
 */
(function () {
  window.addEventListener('load', () => {
    // iframe里不需要执行
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
    p.dns = timing.domainLookupEnd - timing.domainLookupStart; // DNS解析时间
    p.tcp = timing.connectEnd - timing.connectStart; // TCP链接耗时

    var loadTime = getCssJsLoadTime();
    p.jsTotalTime = parseInt(loadTime.jsTotalTime); // 加载js总耗时
    p.cssTotalTime = parseInt(loadTime.cssTotalTime); // 加载css总耗时

    function setParameter() {
      p.load = timing.loadEventEnd - timing.fetchStart;
      p.whitescreen = timing.domInteractive - timing.fetchStart; //  白屏时间
      p.fpt = timing.responseEnd - timing.fetchStart; // fpt

      p.request = timing.responseEnd - timing.responseStart; // request时间
      p.ttfb = timing.responseStart - timing.navigationStart; // 获取首请求首字节耗时
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
})()