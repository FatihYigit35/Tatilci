package com.androiddevelopers.villabuluyorum.viewmodel.host.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toNotification
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HostNotificationViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val currentUserId = auth.currentUser?.uid.toString()

    private var _notificationMessage = MutableLiveData<Resource<Boolean>>()
    val notificationMessage: LiveData<Resource<Boolean>>
        get() = _notificationMessage

    private var _notifications = MutableLiveData<List<InAppNotificationModel>>()
    val notifications: LiveData<List<InAppNotificationModel>>
        get() = _notifications

    init {
        loadNotifications()
    }

    private fun loadNotifications() = viewModelScope.launch {
        _notificationMessage.value = Resource.loading(true)
        firebaseRepo.getAllNotifications(currentUserId,20)
            .addOnSuccessListener { querySnapshot ->
                val notificationList = mutableListOf<InAppNotificationModel>()
                for (document in querySnapshot.documents) {
                    document.toNotification()?.let { notification ->
                        notificationList.add(notification)
                    }
                }
                _notifications.value = notificationList
                _notificationMessage.value = if (notificationList.isNotEmpty()) {
                    Resource.success(true)
                } else {
                    Resource.success(false)
                }
            }.addOnFailureListener { exception ->
                _notificationMessage.value = Resource.error(
                    "Bildirim alınamadı. Hata: $exception",
                    null
                )
                println("exception : "+exception)
            }
    }

}