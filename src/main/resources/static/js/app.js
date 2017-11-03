$(document).ready(function () {
    var queryDict = {};
    decodeURI(location.search).substr(1).split("&").forEach(function (item) {
        var spl = item.split("=");
        queryDict[spl[0]] = spl[1]
    });

    var error = $("#error-box");
    if (queryDict.hasOwnProperty('error') && queryDict.hasOwnProperty('error_description')) {
        error.html("Ошибка " + queryDict['error'] + ": " + queryDict['error_description']);
        error.show();
    }

    document.body.onresize = function () {
        var canvas = document.getElementById('pixelwars-canvas');
        var content = document.getElementById('content');

        updateCanvasSize(canvas, content);
    };

    var slider = document.getElementById('zoom-slider');
    slider.oninput = function () {
        console.log(this.value); //TODO
    };

    var canvas = document.getElementById('pixelwars-canvas');
    var ctx = canvas.getContext('2d');

    if (canvas.addEventListener) {
        if ('onwheel' in document) {
            canvas.addEventListener("wheel", onWheel);
        } else if ('onmousewheel' in document) {
            canvas.addEventListener("mousewheel", onWheel);
        } else {
            canvas.addEventListener("MozMousePixelScroll", onWheel);
        }
    } else {
        canvas.attachEvent("onmousewheel", onWheel);
    }

    var content = document.getElementById('content');
    updateCanvasSize(canvas, content);

    ctx.beginPath();
    ctx.arc(80, 100, 56, 3/4 * Math.PI, 1/4 * Math.PI, true);
    ctx.fill();
    ctx.moveTo(40, 140);
    ctx.lineTo(20, 40);
    ctx.lineTo(60, 100);
    ctx.lineTo(80, 20);
    ctx.lineTo(100, 100);
    ctx.lineTo(140, 40);
    ctx.lineTo(120, 140);
    ctx.stroke();
});

function updateCanvasSize(canvas, content) {
    canvas.width = content.clientWidth;
    canvas.height = content.clientHeight;
}

function onWheel(e) {
    e = e || window.event;

    var delta = e.deltaY || e.detail || e.wheelDelta;

    var slider = document.getElementById('zoom-slider')
    if(delta < 0){  // прокрутка вверх
        slider.value = Math.max(+slider.value - 5, 1);
    } else {  //прокрутка вниз
        slider.value = Math.min(+slider.value + 5, 100);
    }
}