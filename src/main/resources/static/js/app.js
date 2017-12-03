"use strict";

/**
 * Свойства app:
 * canvasOffsetX, canvasOffsetY - отступ канваса от краев страницы
 * canvasWidth, canvasHeight - размеры канваса в настоящих пикселях
 * isDragging - передвигает ли пользователь сейчас нарисованное на канвасе
 * prevZoom - последнее заданное состояние приближения
 * canvasViewCornerX, canvasViewCornerY - положение верхнего левого игрового пикселя относительно канваса, может быть отрицательным
 * prevX, prevY - последние координаты указателя мыши при передвижении на канвасе
 * gamePixelsX, gamePixelsY - количество игроых пикселей для отрисовки
 * pixelsInGamePixel - количество настоящих пикселей на сторону одного игрового
 */

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
        error.html('Ошибка {0}: {1}'.f(queryDict['error'], queryDict['error_description']));
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
                        login.html('как {0} {1}'.f(data.first_name, data.last_name));
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
            // noinspection JSUnresolvedFunction
            canvas.addEventListener("MozMousePixelScroll", onWheel);
        }
    } else {
        // noinspection JSUnresolvedFunction
        canvas.attachEvent("onmousewheel", onWheel);
    }

    var content = document.getElementById('content');
    updateCanvasSize(canvas, content);

    var $canvas = $(canvas);
    var offset = $canvas.offset();

    app.canvasOffsetX = offset.left;
    app.canvasOffsetY = offset.top;
    app.canvasWidth = canvas.width;
    app.canvasHeight = canvas.height;

    app.isDragging = false;
    app.prevZoom = 1;
    app.pixels = {};

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
        var realX = e.clientX - app.canvasOffsetX;
        var realY = e.clientY - app.canvasOffsetY;

        if (app.isDragging) {
            var offsetX = realX - (app.prevX || realX); // >0 = движение вправо
            var offsetY = realY - (app.prevY || realY); // >0 = движение вниз

            console.log("x=" + offsetX + " y=" + offsetY);

            //TODO
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
        success: function (data) {
            $('#canvas-loader').hide();
            $('#canvas-content').show();

            console.log(data);

            /** @namespace data.sizeX */
            /** @namespace data.sizeY */
            /** @namespace data.pixels */

            app.gamePixelsX = data.sizeX;
            app.gamePixelsY = data.sizeY;

            updatePixelSize(app.prevZoom, app.canvasWidth, app.canvasHeight,
                app.gamePixelsX, app.gamePixelsY);

            app.canvasViewCornerX = Math.round((app.canvasWidth - app.pixelsInGamePixel * app.gamePixelsX) / 2);
            app.canvasViewCornerY = Math.round((app.canvasHeight - app.pixelsInGamePixel * app.gamePixelsY) / 2);

            ctx.strokeRect(app.canvasViewCornerX, app.canvasViewCornerY,
                app.gamePixelsX * app.pixelsInGamePixel, app.gamePixelsY * app.pixelsInGamePixel);

            var pixels = data.pixels;

            for (var pos in pixels) {
                if (pixels.hasOwnProperty(pos)) {
                    var color = pixels[pos];

                    var posX = pos / data.sizeX;
                    var posY = pos % data.sizeX;

                    var hexColor = '#' + color.toString(16).padStart(6, "0");
                    app.pixels[pos] = hexColor;

                    var canvasPosX = app.canvasViewCornerX + posX * app.pixelsInGamePixel;
                    var canvasPosY = app.canvasViewCornerY + posY * app.pixelsInGamePixel;

                    if (canvasPosX + app.pixelsInGamePixel >= 0 && canvasPosX <= app.canvasWidth &&
                        canvasPosY + app.pixelsInGamePixel >= 0 && canvasPosY <= app.canvasHeight) {

                        ctx.fillStyle = hexColor;
                        ctx.fillRect(canvasPosX, canvasPosY, app.pixelsInGamePixel, app.pixelsInGamePixel);
                    }
                }
            }

            setInterval(function () {
                // noinspection JSUnusedGlobalSymbols
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

    function updatePixelSize(zoom, width, height, pixelsX, pixelsY) {
        //TODO use zoom
        var pixelMinX = width * 0.8 / pixelsX;
        var pixelMinY = height * 0.8 / pixelsY;

        app.pixelsInGamePixel = Math.min(pixelMinX, pixelMinY);
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
});