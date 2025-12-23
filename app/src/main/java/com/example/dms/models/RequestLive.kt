package com.example.dms.models

import com.google.gson.annotations.SerializedName

data class RequestLive(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("room_id")
    val roomId: Int,
    val status: String, // pending, accepted, rejected
    val documents: List<String>? = null,
    val room: Room? = null,
    val student: Student? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class Room(
    val id: Int,
    @SerializedName("floor_id")
    val floorId: Int,
    @SerializedName("room_number")
    val roomNumber: String,
    val capacity: Int,
    @SerializedName("live_cap")
    val liveCap: Int,
    val floor: Floor? = null
)

data class Floor(
    val id: Int,
    @SerializedName("building_id")
    val buildingId: Int,
    @SerializedName("floor_number")
    val floorNumber: Int,
    val building: Building? = null
)

data class Building(
    val id: Int,
    val address: String,
    @SerializedName("total_floors")
    val totalFloors: Int
)

data class Student(
    val id: Int,
    val name: String? = null,
    val email: String? = null
)

data class RequestLiveCreate(
    @SerializedName("room_id")
    val roomId: Int,
    val documents: List<String>? = null
)

data class ApiResponse<T>(
    val data: T
)

