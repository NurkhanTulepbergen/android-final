package com.example.dms.models

data class LocalRepairRequest(
    val id: String,
    val location: String,
    val issue: String,
    val status: String
)
