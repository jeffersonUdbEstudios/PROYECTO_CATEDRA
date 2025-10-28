package com.example.proyecto_catedra.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyecto_catedra.data.local.dao.BudgetDao
import com.example.proyecto_catedra.data.local.dao.TransactionDao
import com.example.proyecto_catedra.data.local.dao.UserDao
import com.example.proyecto_catedra.data.local.entities.BudgetEntity
import com.example.proyecto_catedra.data.local.entities.TransactionEntity
import com.example.proyecto_catedra.data.local.entities.UserEntity

@Database(
    entities = [UserEntity::class, TransactionEntity::class, BudgetEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create budgets table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS budgets (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        category TEXT NOT NULL,
                        amount REAL NOT NULL,
                        month INTEGER NOT NULL,
                        year INTEGER NOT NULL,
                        icon TEXT NOT NULL,
                        description TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to budgets table
                database.execSQL("ALTER TABLE budgets ADD COLUMN icon TEXT NOT NULL DEFAULT '💰'")
                database.execSQL("ALTER TABLE budgets ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}

