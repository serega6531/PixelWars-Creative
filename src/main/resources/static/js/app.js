"use strict";

/**
 * Свойства app:
 * loaded - загружена ли информация о канвасе
 * authorized - вошел ли пользователь через вк
 * pixels - массив цветов пикселей, номер пикселя = x * pixelsX + y
 * currentColor - выбранный цвет, отсчет начинается с 0
 * backgroundColor - hex цвета фона
 * currentPixelX, currentPixelY - координаты выделенного пикселя, без выделения равны -1
 * canvasOffsetX, canvasOffsetY - отступ канваса от краев страницы
 * lastDraw - Date последнего обновления пикселя
 * cooldown - промежуток между обновлением пикселей в секундах
 * canvasWidth, canvasHeight - размеры канваса в настоящих пикселях
 * isDragging - передвигает ли пользователь сейчас нарисованное на канвасе
 * prevZoom - последнее заданное состояние приближения
 * canvasViewCornerX, canvasViewCornerY - положение верхнего левого игрового пикселя относительно канваса, может быть отрицательным
 * prevX, prevY - последние координаты указателя мыши при передвижении на канвасе
 * gamePixelsX, gamePixelsY - количество игроых пикселей для отрисовки
 * pixelsInGamePixel - количество настоящих пикселей на сторону одного игрового
 * mouseDownCanvasX, mouseDownCanvasY - положение верхнего левого игрового пикселя относительно канваса во время последнего mousedown по канвасу
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

    if (queryDict.hasOwnProperty('error') && queryDict.hasOwnProperty('error_description')) {
        notifier.addNotification(queryDict['error'], queryDict['error_description'], 0);
    }

    var loadCanvas = queryDict.hasOwnProperty('load_canvas') ? queryDict['load_canvas'] === 'true' : true;
    if (!loadCanvas) {
        $('#canvas-loader').hide();
        return;
    }

    var login = document.getElementById('login-data');
    if (login) {
        app.authorized = true;

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
                        login.innerText = 'как {0} {1}'.f(data.first_name, data.last_name);
                    }
                },
                error: function (error) {
                    console.error(error);
                }
            });
        }, 300);
    } else {
        app.authorized = false;
    }

    document.body.onresize = function () {
        var canvas = document.getElementById('pixelwars-canvas');
        var content = document.getElementById('content');

        updateCanvasSize(canvas, content);

        if (app.loaded) {
            updatePixelSize(app.prevZoom, app.canvasWidth, app.canvasHeight,
                app.gamePixelsX, app.gamePixelsY)
        }
    };

    var slider = document.getElementById('zoom-slider');
    slider.oninput = function () {
        var relativeCenterX = window.innerWidth / 2 - app.canvasViewCornerX;
        var relativeCenterY = window.innerHeight / 2 - app.canvasViewCornerY;

        zoomCanvas(this.value,
            Math.floor(relativeCenterX / app.pixelsInGamePixel),
            Math.floor(relativeCenterY / app.pixelsInGamePixel));
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

    window.addEventListener('resize', function () {
        redrawImage();
    });

    var content = document.getElementById('content');
    var $content = $(content);

    updateCanvasSize(canvas, content);

    var $canvas = $(canvas);

    app.loaded = false;
    app.currentColor = 0;
    app.currentPixelX = -1;
    app.currentPixelY = -1;

    var offset = $content.offset();
    app.canvasOffsetX = offset.left;
    app.canvasOffsetY = offset.top;
    app.canvasWidth = canvas.width;
    app.canvasHeight = canvas.height;

    app.isDragging = false;
    app.prevZoom = 1;
    app.pixels = {};

    function handleMouseDown() {
        app.isDragging = true;
        app.mouseDownCanvasX = app.canvasViewCornerX;
        app.mouseDownCanvasY = app.canvasViewCornerY;
    }

    function handleMouseUp(e) {
        app.isDragging = false;
        if (Math.abs(app.mouseDownCanvasX - app.canvasViewCornerX) / app.pixelsInGamePixel < 1 &&
            Math.abs(app.mouseDownCanvasY - app.canvasViewCornerY) / app.pixelsInGamePixel < 1) {

            var clickedX = Math.floor((e.clientX - app.canvasOffsetX - app.canvasViewCornerX) /
                app.pixelsInGamePixel);
            var clickedY = Math.floor((e.clientY - app.canvasOffsetY - app.canvasViewCornerY) /
                app.pixelsInGamePixel);

            if (clickedX < 0 || clickedX >= app.gamePixelsX ||
                clickedY < 0 || clickedY >= app.gamePixelsY) {
                return;
            }

            if (app.currentPixelX !== -1 && app.currentPixelY !== -1) {
                redrawImage();
            }

            app.currentPixelX = clickedX;
            app.currentPixelY = clickedY;
            drawSelectionFrame();

            document.getElementById('selection-info-data').innerText =
                '({0}, {1})'.f(clickedX, clickedY);

            var updateButton = document.getElementById('update-pixel-button');
            if (updateButton.disabled && typeof app.lastDraw !== 'undefined' &&
                new Date() - app.lastDraw > app.cooldown) {

                updateButton.removeAttribute('disabled');
            }
        }
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

            if (offsetX > 0) {
                app.canvasViewCornerX = Math.min(app.canvasViewCornerX + offsetX,
                    app.canvasWidth - 0.1 * app.gamePixelsX * app.pixelsInGamePixel);
            } else if (offsetX < 0) {
                app.canvasViewCornerX = Math.max(app.canvasViewCornerX + offsetX,
                    -0.9 * app.gamePixelsX * app.pixelsInGamePixel);
            }

            if (offsetY > 0) {
                app.canvasViewCornerY = Math.min(app.canvasViewCornerY + offsetY,
                    app.canvasHeight - 0.1 * app.gamePixelsY * app.pixelsInGamePixel);
            } else if (offsetY < 0) {
                app.canvasViewCornerY = Math.max(app.canvasViewCornerY + offsetY,
                    -0.9 * app.gamePixelsY * app.pixelsInGamePixel);
            }

            redrawImage();
            drawSelectionFrame();
        }

        app.prevX = realX;
        app.prevY = realY;
    }

    $canvas.mousedown(handleMouseDown);
    $canvas.mousemove(handleMouseMove);
    $canvas.mouseup(handleMouseUp);
    $canvas.mouseout(handleMouseOut);

    document.getElementById('update-pixel-button').disabled = true;

    var sock = SockJS('/canvas');

    sock.onopen = function () {
        console.log("Connected to socket");
        sock.send('start');
    };

    sock.onerror = function (e) {
        console.error(e.message);

        notifier.addNotification('Connection error', e.message, 0);
    };

    sock.onmessage = function (e) {
        var data = JSON.parse(e.data);
        console.log(data);

        if (data.hasOwnProperty('pixels')) {   //initial
            handleInitialResponse(data);
        } else {  // pixel update
            handlePixelUpdate(data);
        }
    };

    function handleInitialResponse(data) {
        /** @namespace data.sizeX */
        /** @namespace data.sizeY */
        /** @namespace data.backgroundColor */
        /** @namespace data.pixels */
        /** @namespace data.colors */
        /** @namespace data.colorsAmount */
        /** @namespace data.cooldown */

        $('#canvas-loader').hide();
        $('#canvas-content').show();

        app.loaded = true;
        app.gamePixelsX = data.sizeX;
        app.gamePixelsY = data.sizeY;
        app.backgroundColor = intToHex(data.backgroundColor);
        app.cooldown = data.cooldown + 500;  // чтобы избежать проблем с задержкой сети

        updatePixelSize(app.prevZoom, app.canvasWidth, app.canvasHeight,
            app.gamePixelsX, app.gamePixelsY);

        app.canvasViewCornerX = Math.round((app.canvasWidth - app.pixelsInGamePixel * app.gamePixelsX) / 2);
        app.canvasViewCornerY = Math.round((app.canvasHeight - app.pixelsInGamePixel * app.gamePixelsY) / 2);

        ctx.strokeRect(app.canvasViewCornerX, app.canvasViewCornerY,
            app.gamePixelsX * app.pixelsInGamePixel, app.gamePixelsY * app.pixelsInGamePixel);

        var pixels = data.pixels;

        for (var x = 0; x < app.gamePixelsX; x++) {
            for (var y = 0; y < app.gamePixelsY; y++) {
                app.pixels[x * app.gamePixelsX + y] = app.backgroundColor;
            }
        }

        for (var pos in pixels) {
            if (pixels.hasOwnProperty(pos)) {
                app.pixels[pos] = intToHex(pixels[pos]);
            }
        }

        redrawImage();

        if (!app.authorized) {
            canvas.style.cursor = 'move';
        } else {
            //TODO проверить откат рисования
        }

        if (app.authorized) {
            var colors = data.colors;
            var colorsAmount = data.colorsAmount;
            var colorsBox = document.getElementById('color-pick-box');

            for (var i = 0; i < colorsAmount; i++) {
                var color = colors[i];

                var colorBox = document.createElement('div');
                colorBox.classList.add('color-box');
                if (i === 0) {
                    colorBox.classList.add('color-selected');
                }

                colorBox.setAttribute('data-color', i.toString());
                colorBox.style.backgroundColor = intToHex(color);

                colorsBox.appendChild(colorBox);
            }

            var $colorBox = $("#color-pick-box");

            $colorBox.show();

            $colorBox.on('click', '*', function (e) {
                var clicked = e.currentTarget;
                var color = clicked.attributes['data-color'].value;

                $("#color-pick-box").children('*').each(function () {
                    this.classList.remove('color-selected');
                });

                clicked.classList.add('color-selected');

                app.currentColor = color;
            });

            $.ajax({
                url: "/canvas/getCooldown",
                type: "GET",
                dataType: "json",
                success: function (data) {
                    if (data.success) {
                        /** @namespace data.lastDraw */
                        app.lastDraw = new Date(data.lastDraw);
                        var now = new Date();
                        if(app.lastDraw > now) {
                            runButtonCooldown(new Date(+now + app.cooldown))
                        }

                        $("#update-pixel-box").show();

                        $("#update-pixel-button").click(function () {
                            updatePixel(app.currentPixelX, app.currentPixelY, app.currentColor);

                            var updateButton = document.getElementById('update-pixel-button');
                            updateButton.innerText = "...";
                            updateButton.disabled = true;
                        });
                    } else {
                        notifier.addNotification('Ajax error', messages[data.reason], 3000);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.error(errorThrown);
                    notifier.addNotification('Ajax error', errorThrown, 3000);
                }
            });
        }
    }

    function handlePixelUpdate(data) {
        var pos = data.position;
        var posX = pos.x;
        var posY = pos.y;
        var color = intToHex(data.color);

        app.pixels[posX * app.gamePixelsX + posY] = color;
        drawPixel(posX, posY, color);

        if (Math.abs(app.currentPixelX - posX) + Math.abs(app.currentPixelY - posY) <= 2) {
            // если обновлен соседний с выделением пиксель
            drawSelectionFrame();
        }
    }

    function redrawImage() {
        ctx.clearRect(0, 0, app.canvasWidth, app.canvasHeight);

        ctx.fillStyle = app.backgroundColor;
        ctx.fillRect(app.canvasViewCornerX, app.canvasViewCornerY,
            app.gamePixelsX * app.pixelsInGamePixel, app.gamePixelsY * app.pixelsInGamePixel);

        ctx.lineWidth = 1;
        ctx.strokeRect(app.canvasViewCornerX, app.canvasViewCornerY,
            app.gamePixelsX * app.pixelsInGamePixel, app.gamePixelsY * app.pixelsInGamePixel);

        var pixels = app.pixels;
        for (var pos in pixels) {
            if (pixels.hasOwnProperty(pos)) {
                var hexColor = pixels[pos];

                var posX = Math.floor(pos / app.gamePixelsX);
                var posY = pos % app.gamePixelsX;

                drawPixel(posX, posY, hexColor);
            }
        }
    }

    function drawPixel(posX, posY, hexColor) {
        var canvasPosX = app.canvasViewCornerX + posX * app.pixelsInGamePixel;
        var canvasPosY = app.canvasViewCornerY + posY * app.pixelsInGamePixel;

        if (canvasPosX + app.pixelsInGamePixel >= 0 && canvasPosX <= app.canvasWidth &&
            canvasPosY + app.pixelsInGamePixel >= 0 && canvasPosY <= app.canvasHeight) {

            ctx.fillStyle = hexColor;
            ctx.fillRect(canvasPosX, canvasPosY, app.pixelsInGamePixel, app.pixelsInGamePixel);
        }
    }

    function drawSelectionFrame() {
        if (app.currentPixelX === -1 || app.currentPixelY === -1) {
            return;
        }

        var canvasPosX = app.canvasViewCornerX + app.currentPixelX * app.pixelsInGamePixel;
        var canvasPosY = app.canvasViewCornerY + app.currentPixelY * app.pixelsInGamePixel;

        ctx.strokeRect(canvasPosX, canvasPosY, app.pixelsInGamePixel, app.pixelsInGamePixel);
    }

    function updateCanvasSize(canvas, content) {
        canvas.width = content.clientWidth;
        canvas.height = content.clientHeight;

        app.canvasWidth = content.clientWidth;
        app.canvasHeight = content.clientHeight;
    }

    function zoomCanvas(zoom, targetX, targetY) {
        if (zoom !== app.prevZoom) {
            app.prevZoom = zoom;

            var oldPIGP = app.pixelsInGamePixel;
            updatePixelSize(zoom, app.canvasWidth, app.canvasHeight,
                app.gamePixelsX, app.gamePixelsY);
            var newPIGP = app.pixelsInGamePixel;

            var diffPIGP = newPIGP - oldPIGP;

            applyNewCorner(
                app.canvasViewCornerX - targetX * diffPIGP,
                app.canvasViewCornerY - targetY * diffPIGP);

            redrawImage();
            drawSelectionFrame();
        }
    }

    function applyNewCorner(newCornerX, newCornerY) {
        if (newCornerX > app.canvasWidth - 0.1 * app.gamePixelsX * app.pixelsInGamePixel) {
            app.canvasViewCornerX = app.canvasWidth - 0.1 * app.gamePixelsX * app.pixelsInGamePixel;
        } else if (newCornerX < -0.9 * app.gamePixelsX * app.pixelsInGamePixel) {
            app.canvasViewCornerX = -0.9 * app.gamePixelsX * app.pixelsInGamePixel;
        } else {
            app.canvasViewCornerX = newCornerX;
        }

        if (newCornerY > app.canvasHeight - 0.1 * app.gamePixelsY * app.pixelsInGamePixel) {
            app.canvasViewCornerY = app.canvasHeight - 0.1 * app.gamePixelsY * app.pixelsInGamePixel;
        } else if (newCornerY < -0.9 * app.gamePixelsY * app.pixelsInGamePixel) {
            app.canvasViewCornerY = -0.9 * app.gamePixelsY * app.pixelsInGamePixel;
        } else {
            app.canvasViewCornerY = newCornerY;
        }
    }

    function updatePixelSize(zoom, width, height, pixelsX, pixelsY) {
        var zoomCoef = 1 + zoom * 0.03;
        var pixelMinX = zoomCoef * width * 0.8 / pixelsX;
        var pixelMinY = zoomCoef * height * 0.8 / pixelsY;

        app.pixelsInGamePixel = Math.min(pixelMinX, pixelMinY);
    }

    function onWheel(e) {
        e = e || window.event;

        var delta = e.deltaY || e.detail || e.wheelDelta;

        var slider = document.getElementById('zoom-slider');
        if (delta < 0) {  // прокрутка вверх
            slider.value = Math.min(+slider.value + 5, 100);
        } else {  //прокрутка вниз
            slider.value = Math.max(+slider.value - 5, 0);
        }

        var x = Math.floor((e.clientX - app.canvasOffsetX - app.canvasViewCornerX) / app.pixelsInGamePixel);
        var y = Math.floor((e.clientY - app.canvasOffsetY - app.canvasViewCornerY) / app.pixelsInGamePixel);

        zoomCanvas(slider.value, x, y);
    }

    function updatePixel(x, y, color) {
        var pixel = {position: {x: x, y: y}, color: color};

        $.ajax({
            url: "/canvas/updatePixel",
            type: "POST",
            data: JSON.stringify(pixel),
            contentType: "application/json",
            success: function (data) {
                if(data.success) {
                    console.log(data);
                    app.lastDraw = new Date();

                    runButtonCooldown(new Date(+new Date() + app.cooldown));
                } else {
                    notifier.addNotification('Update error', messages[data.reason], 3000);

                    var updateButton = document.getElementById('update-pixel-button');
                    updateButton.innerText = "Отправить";
                    updateButton.removeAttribute('disabled');
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error(errorThrown);
                notifier.addNotification('Update error', errorThrown, 3000);

                var updateButton = document.getElementById('update-pixel-button');
                updateButton.innerText = "Отправить";
                updateButton.removeAttribute('disabled');
            }
        });
    }

    function runButtonCooldown(endDate) {
        var msDiff = endDate - Date.now();
        var updateButton = document.getElementById('update-pixel-button');
        var msUntilSecond = msDiff % 1000;
        var seconds = Math.floor(msDiff / 1000);

        var secondsLeft;
        if (msUntilSecond > 0) {
            secondsLeft = seconds + 1;
        } else {
            secondsLeft = seconds;
        }

        updateButton.innerText = secondsToTime(secondsLeft);
        updateButton.disabled = true;

        setTimeout(function () {
            secondsLeft--;
            updateButton.innerText = secondsToTime(secondsLeft);

            var task = setInterval(function () {
                secondsLeft--;

                if (secondsLeft > 0) {
                    updateButton.innerText = secondsToTime(secondsLeft);
                } else {
                    clearInterval(task);
                    updateButton.removeAttribute('disabled');
                    updateButton.innerText = 'Отправить';
                }
            }, 1000);
        }, msUntilSecond);
    }

    function secondsToTime(seconds) {
        var minutes = Math.floor(seconds / 60);
        seconds = seconds % 60;

        if(minutes > 0) {
            return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
        } else {
            return "" + seconds;
        }
    }

    function intToHex(i) {
        return '#' + i.toString(16).padStart(6, '0');
    }
});