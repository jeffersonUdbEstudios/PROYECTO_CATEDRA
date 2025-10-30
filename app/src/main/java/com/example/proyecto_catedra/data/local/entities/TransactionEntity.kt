package com.example.proyecto_catedra.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val userId: String, // Firebase UID
    val amount: Double,
    val description: String,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String,
    val paymentMethod: String = "Efectivo", // Método de pago: Efectivo, Tarjeta Débito, Tarjeta Crédito, Transferencia, Billetera Digital
    val date: Long, // timestamp
    val createdAt: Long = System.currentTimeMillis()
)

