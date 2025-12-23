package com.example.dms.models

data class LocalLiveRequest(
    val id: String,
    val room: String,
    val floor: String,
    var status: String
)
