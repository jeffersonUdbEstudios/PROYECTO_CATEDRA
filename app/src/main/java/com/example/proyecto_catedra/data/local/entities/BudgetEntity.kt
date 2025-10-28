package com.example.proyecto_catedra.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val userId: String,
    val category: String,
    val amount: Double,
    val month: Int, // 1-12
    val year: Int,
    val icon: String = "💰",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

