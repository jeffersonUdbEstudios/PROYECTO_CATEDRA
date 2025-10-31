package com.example.proyecto_catedra.data.local.dao

import androidx.room.*
import com.example.proyecto_catedra.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE uid = :userId")
    suspend fun getUserByIdSync(userId: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE uid = :userId")
    suspend fun deleteUser(userId: String)
    
    // Método para actualizar datos académicos del perfil
    @Query("""
        UPDATE users 
        SET name = :name, 
            photoUrl = :photoUrl,
            universidad = :universidad, 
            carrera = :carrera, 
            semestre = :semestre,
            updatedAt = :updatedAt
        WHERE uid = :userId
    """)
    suspend fun updateUserProfile(
        userId: String,
        name: String,
        photoUrl: String?,
        universidad: String?,
        carrera: String?,
        semestre: String?,
        updatedAt: Long = System.currentTimeMillis()
    )
}

