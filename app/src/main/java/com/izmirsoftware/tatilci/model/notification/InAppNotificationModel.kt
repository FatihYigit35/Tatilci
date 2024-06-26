package com.izmirsoftware.tatilci.model.notification

import com.izmirsoftware.tatilci.util.NotificationType


class InAppNotificationModel(
    var itemId : String? = null,
    var userId : String? = null,
    var notificationType : NotificationType? = null,
    var notificationId : String? = null,
    var userName : String? = null,
    var title : String? = null,
    var message : String? = null,
    var userImage : String? = null,
    var imageUrl : String? = null,
    var userToken : String? = null,
    var time : String? = null,
)