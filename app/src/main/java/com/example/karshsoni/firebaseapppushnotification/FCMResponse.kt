package com.example.karshsoni.firebaseapppushnotification

data class FCMResponse(
	val notification: Notification? = null,
	val data: Data? = null,
	val to: String? = null
)
