package com.example.dms.models

data class FinanceEntry(
    val id: String,
    val title: String,
    val dueDate: String,
    val amount: String,
    val status: String
)
