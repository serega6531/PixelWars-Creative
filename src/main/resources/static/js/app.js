"use strict";
var app = {};

if (typeof $ === 'undefined') {
    throw new Error('JQuery not loaded!');
}

$(function () {
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

    var login = $("#login-data");
    if (login) {
        setTimeout(function () {
            // noinspection SpellCheckingInspection
            $.ajax({
                url: "/user/fullname",
                type: "GET",
                dataType: "json",
                beforeSend: function () {
                    $('#name-loader').show();
                },
                complete: function () {
                    $('#name-loader').hide();
                },
                success: function (data) {
                    /** @namespace data.last_name */
                    /** @namespace data.first_name */

                    if (data.id !== -1) {
                        login.html('как ' + data.first_name + ' ' + data.last_name);
                    }
                },
                error: function (error) {
                    console.error(error);
                }
            });
        }, 300);
    }

    var loadCanvas = queryDict.hasOwnProperty('load_canvas') ? queryDict['load_canvas'] === 'true' : true;
    if (!loadCanvas) {
        $('#canvas-loader').hide();
        return;
    }

    document.body.onresize = function () {
        var canvas = document.getElementById('pixelwars-canvas');
        var content = document.getElementById('content');

        updateCanvasSize(canvas, content);
    };

    var slider = document.getElementById('zoom-slider');
    slider.oninput = function () {
        zoomCanvas(this.value);
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

    var img = new Image();
    img.onload = function () {
        ctx.drawImage(img, 0, 0);
    };
    img.src = "https://pp.userapi.com/c635104/v635104989/23d24/utoKLhwl-eA.jpg";

    var $canvas = $(canvas);
    var offset = $canvas.offset();

    app.canvasOffsetX = offset.left;
    app.canvasOffsetY = offset.top;
    app.canvasWidth = canvas.width;
    app.canvasHeight = canvas.height;

    app.isDragging = false;

    app.prevZoom = 1;

    function handleMouseDown() {
        app.isDragging = true;
    }

    function handleMouseUp() {
        app.isDragging = false;
    }

    function handleMouseOut() {
        app.isDragging = false;
    }

    function handleMouseMove(e) {
        var realX = parseInt(e.clientX - app.canvasOffsetX);
        var realY = parseInt(e.clientY - app.canvasOffsetY);

        if (app.isDragging) {
            var offsetX = realX - (app.prevX || realX); // >0 = движение вправо
            var offsetY = realY - (app.prevY || realY); // >0 = движение вниз

            console.log("x=" + offsetX + " y=" + offsetY);

            ctx.clearRect(0, 0, app.canvasWidth, app.canvasHeight);
            ctx.drawImage(img, realX - 128 / 2, realY - 120 / 2, 128, 120);
        }

        app.prevX = realX;
        app.prevY = realY;
    }

    $canvas.mousedown(function (e) {
        handleMouseDown(e);
    });
    $canvas.mousemove(function (e) {
        handleMouseMove(e);
    });
    $canvas.mouseup(function (e) {
        handleMouseUp(e);
    });
    $canvas.mouseout(function (e) {
        handleMouseOut(e);
    });

    $.ajax({
        url: "/canvas/getAllPixels",
        type: "GET",
        dataType: "json",
        timeout: 8000,
        complete: function () {
            $('#canvas-loader').hide();
            $('#canvas-content').show();

            setInterval(function () {
                $.ajax({
                    url: "/canvas/getUpdates",
                    type: "GET",
                    dataType: "json",
                    timeout: 8000,  //TODO проверить, что не пришло более раннее обновление после позднего
                    success: function (data) {
                        console.log(data);
                    },
                    error: function (xhr, status, text) {
                        //TODO show error
                        console.error(xhr);
                    }
                });
            }, 1000);
        },
        success: function (data) {
            console.log(data);
        },
        error: function (xhr, status, text) {
            if (!String.prototype.includes) {
                String.prototype.includes = function () {
                    'use strict';
                    return String.prototype.indexOf.apply(this, arguments) !== -1;
                };
            }

            var href = window.location.href;

            href = (href.includes('?') ? href.substring(0, href.lastIndexOf('?')) : href) +
                '?error=' + (text || "Network error") +
                '&error_description=' + status + '&load_canvas=false';
            window.location.href = href;

            console.error(xhr);
        }
    });
});

function updateCanvasSize(canvas, content) {
    canvas.width = content.clientWidth;
    canvas.height = content.clientHeight;
}

function zoomCanvas(zoom) {
    if (zoom !== app.prevZoom) {
        console.log(zoom);

        //TODO

        app.prevZoom = zoom;
    }
}

function onWheel(e) {
    e = e || window.event;

    var delta = e.deltaY || e.detail || e.wheelDelta;

    var slider = document.getElementById('zoom-slider');
    if (delta < 0) {  // прокрутка вверх
        slider.value = Math.min(+slider.value + 5, 100);
    } else {  //прокрутка вниз
        slider.value = Math.max(+slider.value - 5, 1);
    }

    zoomCanvas(slider.value);
}

function updatePixel(x, y, color) {
    var pixel = {position: {x: x, y: y}, color: color};

    $.ajax({
        url: "/canvas/updatePixel",
        type: "POST",
        data: JSON.stringify(pixel),
        contentType: "application/json",
        success: function (data) {
            console.log(data);
        },
        error: function (error) {
            console.error(error);
        }
    });
}