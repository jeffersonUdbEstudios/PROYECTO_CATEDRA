package com.example.proyecto_catedra.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String, // Firebase UID
    
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    
    // Datos académicos del estudiante
    val universidad: String? = null,
    val carrera: String? = null,
    val semestre: String? = null, // "1er Semestre", "2do Año", etc.
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

