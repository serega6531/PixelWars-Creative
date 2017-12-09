'use strict';

var notifier = {};

$(function () {
    var wrap = elementWithClass('div', 'notification-wrap');
    var notification = elementWithClass('div', 'notification');

    var head = elementWithClass('div', 'notification-head');
    var close = elementWithClass('a', 'notification-close');
    close.innerHTML = '&#10006;';
    var title = elementWithClass('h4', 'notification-title');

    var body = elementWithClass('div', 'notification-body');
    var message = elementWithClass('div', 'notification-message');

    head.appendChild(close);
    head.appendChild(title);

    body.appendChild(message);

    notification.appendChild(head);
    notification.appendChild(body);
    wrap.appendChild(notification);

    var notificationsBox = document.getElementById('notifications-box');

    notifier.addNotification = function (title, text, timeout) {
        var cloned = wrap.cloneNode(true);
        var $cloned = $(cloned);

        $cloned.find('.notification-title').html(title);
        $cloned.find('.notification-message').html(text);

        $cloned.find('.notification-close').click(function () {
            $cloned.fadeOut(500, function () {
                $cloned.remove();
            })
        });

        notificationsBox.appendChild(cloned);

        if (timeout !== 0) {
            setInterval(function () {
                $cloned.fadeOut(500, function () {
                    $cloned.remove();
                })
            }, timeout);
        }
    };

    function elementWithClass(el, cl) {
        var element = document.createElement(el);
        element.classList.add(cl);
        return element;
    }
});